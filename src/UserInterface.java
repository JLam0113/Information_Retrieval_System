/* Jessye Lam
 * 500702091
 * CPS 842 - Assignment 2
 * 10/20/2020
 */

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;


/*
 * UI Class to handle user inputs for queries.
 * Also creates the index and does evaluation.
 */
public class UserInterface {
	static Invert inverter = new Invert();
	static Search search = new Search();

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		System.out.print("Hi, please enter yes or no for stopwords: ");
		Scanner sc = new Scanner(System.in);
		String s = sc.nextLine();

		while (!s.equals("yes") & !s.equals("no")) {
			System.out.println("Incorrect input, please enter yes or no.");
			s = sc.nextLine();
		}

		if (s.equals("yes")) {
			System.out.println("Stopwords enabled");
			inverter.setStopWord(true);
			search.setStopWord(true);
		} else if (s.equals("no")) {
			System.out.println("Stopwords disabled");
			inverter.setStopWord(false);
			search.setStopWord(false);
		}

		System.out.print("Please enter yes or no for stemming: ");
		s = sc.nextLine();

		while (!s.equals("yes") & !s.equals("no")) {
			System.out.println("Incorrect input, please enter yes or no.");
			s = sc.nextLine();
		}

		if (s.equals("yes")) {
			System.out.println("Stemming enabled");
			inverter.setStem(true);
			search.setStemming(true);
		} else if (s.equals("no")) {
			System.out.println("Stemming disabled");
			inverter.setStem(false);
			search.setStemming(false);
		}

		inverter.start();
		search.setInverter(inverter);
		// Comment this out if evaluation is taking too long or you just want to search queries
		System.out.println("Starting Evaluation Part");
		Eval eval = new Eval(search);
		eval.start();
		System.out.println("Evaluation complete, MAP can be found in map.txt, and R-Precision values for each query in rprecision.txt");
		
		System.out.println("Please enter a query: ");
		while (true) {
			s = sc.nextLine();
			while (!s.equals("ZZEND")) {
				search.processQuery(s);
				search.calculateCosSim();
				search.getSimilarity();
				System.out.println("Top 100 (if there were more than 100) results can be found above.");
				System.out.println("Please enter a query: ");
				s = sc.nextLine();
				
			}
			sc.close();
			System.out.println("Terminating program");
			System.exit(0);
		}
	}

}
