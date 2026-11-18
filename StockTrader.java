/*
lowest/highest price table and answers here.
File                 Lowest Price  When       Highest Price  When
---------------------------------------------------------------------
tiny.txt             $30.00        Mar 2020   $70.00         Jul 2020
rising.txt           $20.00        Jan 2010   $48.14         Dec 2014
falling.txt          $30.36        Dec 2014   $100.00        Jan 2010
zigzag.txt           $35.00        Jul 2010   $65.00         Mar 2010
aapl_2000_2010.txt   $7.07         Mar 2003   $223.02        Mar 2010
amzn_2000_2010.txt   $5.97         Sep 2001   $135.91        Nov 2009
msft_2000_2010.txt   $15.81        Feb 2009   $43.22         Mar 2000
ibm_2000_2010.txt    $53.01        Sep 2002   $130.32        Dec 2009
goog_2004_2010.txt   $102.37       Aug 2004   $707.00        Oct 2007
sbux_2007_2016.txt   $4.09         Nov 2008   $62.16         Oct 2015
sp500_1925_1945.txt  $4.77         Jun 1932   $31.30         Sep 1929
sp500_1990_2025.txt  $307.12       Oct 1990   $6,853.03      Dec 2025
*/

/*
Step E table and answers here.
File                 Buy and Hold   Dip Buyer     Moving Average   My Strategy
-----------------------------------------------------------------------------
tiny.txt             $12,975.00     $14,975.00    $10,000.00       $8,480.00
rising.txt           $24,031.86     $10,000.00    $20,411.92       $22,637.70
tiny.txt             $12,975.00     $14,975.00    $10,000.00       $8,480.00
rising.txt           $24,031.86     $10,000.00    $20,411.92       $22,637.70
falling.txt          $3,095.64      $3,159.28     $3,986.34        $10,000.00
zigzag.txt           $12,101.39     $38,071.99    $9,970.00        $2,824.96
aapl_2000_2010.txt   $85,865.80     $36,610.49    $149,655.56      $46,165.65
amzn_2000_2010.txt   $19,886.04     $14,192.52    $184,343.04      $68,285.66
msft_2000_2010.txt   $7,237.50      $4,273.15     $11,988.80       $9,031.61
ibm_2000_2010.txt    $12,487.97     $27,998.86    $12,444.21       $11,310.53
goog_2004_2010.txt   $54,398.54     $13,655.41    $19,252.62       $23,631.70
sbux_2007_2016.txt   $36,280.60     $15,759.50    $69,796.12       $86,326.58
*/


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class StockTrader {

    public static void main(String[] args) throws FileNotFoundException {
        String fileName = "tiny.txt";
        if (args.length > 0) {
            fileName = args[0];
        }
        Scanner in = new Scanner(new File(fileName));
        in.useLocale(java.util.Locale.US);
        String symbol = in.next();               
        int n = in.nextInt();

		int[] years = new int[n];
		int[] months = new int[n];
		double[] prices = new double[n];
        loadData(in, years, months, prices);
        in.close();
        printData(years, months, prices);
        int lowest = indexOfLowest(prices);
        int highest = indexOfHighest(prices);
		System.out.println(symbol + " lowest: $" + prices[lowest] + " on " + monthName(months[lowest]) + " " + years[lowest]);
		System.out.println(symbol + " highest: $" + prices[highest] + " on " + monthName(months[highest]) + " " + years[highest]);
		double result = simulate(years, months, prices);
		System.out.printf("Final value: $%,.2f%n", result);
    }
    
	public static void loadData(Scanner in, int[] years, int[] months, double[] prices) {
		 for (int i = 0; i < years.length; i++) {
			years[i] = in.nextInt();
			months[i] = in.nextInt();
			prices[i] = in.nextDouble();
		 }
	}
	public static String monthName(int month) {
		String[] monthsNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		return monthsNames[month-1];
	}
	public static void printData(int[] years, int[] months, double[] prices) {
		for (int i = 0; i < months.length; i++) {
			System.out.println(" " + monthName(months[i]) + "  " + years[i] + " : " + "$" + prices[i]);
		}
	}
	public static int indexOfLowest(double[] prices) {
    int min = 0;
		for (int i = 1; i < prices.length; i++) {
			if (prices[i] < prices[min]) {
				min = i;
			}
		}
		return min;
	}

	public static int indexOfHighest(double[] prices) {
    int max = 0;
		for (int i = 1; i < prices.length; i++) {
			if (prices[i] > prices[max]) {
				max = i;
			}
		}
		return max;
	}
	
	public static final double START_CASH = 10000.0;
	public static final double FEE = 10.0;

	public static double simulate(int[] years, int[] months, double[] prices) {
    double cash = START_CASH;
    int shares = 0;
    double boughtAt = 0.0;

    for (int i = 0; i < prices.length; i++) {
        double[] history = Arrays.copyOf(prices, i + 1);
        String action = decide(history, shares > 0, boughtAt);

        if (action.equals("BUY") && shares == 0) {
            int canBuy = (int) ((cash - FEE) / prices[i]);
            if (canBuy > 0) {
                shares = canBuy;
                cash = cash - shares * prices[i] - FEE;
                boughtAt = prices[i];
                System.out.printf("%s %d: BUY %d shares at $%.2f%n",
                        monthName(months[i]), years[i], shares, prices[i]);
            }
        } else if (action.equals("SELL") && shares > 0) {
            cash = cash + shares * prices[i] - FEE;
            System.out.printf("%s %d: SELL %d shares at $%.2f%n",
                    monthName(months[i]), years[i], shares, prices[i]);
            shares = 0;
            boughtAt = 0.0;
        }
    }
    return cash + shares * prices[prices.length - 1];
}
	public static String decide(double[] history, boolean holding, double boughtAt) {
		return myStrategy(history, holding, boughtAt);   // change this line to switch strategies
		
	}
	public static String buyAndHold(double[] history, boolean holding, double boughtAt) {
		if (holding == false) {
			return "BUY";
		}
		return "HOLD";
	}
	public static String dipBuyer(double[] history, boolean holding, double boughtAt) {
		if (holding == false && history.length >= 3 && history[history.length-1] < history[history.length-2] && history[history.length-2] < history[history.length-3]) {
			return "BUY";
		} else if (holding == true && history[history.length-1] >= boughtAt + boughtAt / 10) {
			return "SELL";
		} else {
			return "HOLD";
		}
	}
	public static String movingAverage(double[] history, boolean holding, double boughtAt) {
		if (history.length < 12) {
			return "HOLD";
		} 
		double average = 0;
		for (int i = history.length - 12; i < history.length; i++) {
			average += history[i];
		}
		average /= 12;
		if (holding == false && history[history.length - 1] > average) {
			return "BUY";
		} else if (holding == true && history[history.length - 1] < average) {
			return "SELL";
		} else {
			return "HOLD";
		}
	}
	public static String myStrategy(double[] history, boolean holding, double boughtAt) {
		// Wait until we have at least 5 prices
		if (history.length < 5) {
			return "HOLD";
		}
		// Use the last 5 prices to estimate the recent trend.
		double average = 0;
		for (int i = history.length - 5; i < history.length; i++) {
			average += history[i];
		}
		average /= 5;
		double currentPrice = history[history.length - 1];
		// Buy above the average to follow a rising price.
		if (holding == false && currentPrice > average) {
			return "BUY";
			// Sell after a 5% drop from the buy price to limit losses.
		} else if (holding == true && currentPrice <= boughtAt * 0.95) {
			return "SELL";
		} else {
			return "HOLD";
		}
	}
	
}

