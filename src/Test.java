

/* Jessye Lam
 * 500702091
 * CPS 842 - Assignment 1
 * 09/29/2020
 */
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

//import Invert;
//import Stemmer;

/*
 * Main Class used to test the program
 * Program will allow you enable/disable stopwords and stemming.
 * It will then create the inverted index and ask for a term, please use lowercase when entering the term.
 * Type ZZEND to stop.
 */
public class Test {
	static Invert inverter = new Invert();
	static HashMap<String, Integer> dic = inverter.getDictionary();
	static HashMap<String, ArrayList<String[]>> post = inverter.getPosting();
	static ArrayList<String> document = inverter.getDoc();
	static Set<String> set = post.keySet();
	static Stemmer stem = new Stemmer();

	/*public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		boolean b2 = false;
		long time = 0;
		int count = 0;
		boolean stemming = false;

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
		} else if (s.equals("no")) {
			System.out.println("Stopwords disabled");
			inverter.setStopWord(false);
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
			stemming = true;
		} else if (s.equals("no")) {
			System.out.println("Stemming disabled");
			stemming = false;
			inverter.setStem(false);
		}

		System.out.println("Creating Inverted Index");
		inverter.start();
		System.out.println("Please enter a term: ");

		while (true) {
			s = sc.nextLine();
			if (stemming)
				s = stemTerm(s);
			while (!s.equals("ZZEND")) {
				long start = System.currentTimeMillis();
				for (String key : set) {
					if (s.equals(key)) {
						System.out.println(s + " has been found.");
						System.out.println("Total Frequency: " + dic.get(key));
						getInfo(s);
						b2 = true;
						long end = System.currentTimeMillis();
						System.out.println("Time taken to retrieve results: " + (end - start) + "ms");
						time += (end - start);
						count++;
						break;
					}
				}
				if (!b2)
					System.out.println("Term not found, please try again.");
				else
					System.out.println("Please enter a term: ");
				b2 = false;
				s = sc.nextLine();
				if (stemming)
					s = stemTerm(s);
			}
			sc.close();
			System.out.println("Average time taken for all the terms is " + (time / count) + "ms");
			System.out.println("Terminating program");
			System.exit(0);
		}
	}

	// Displays term information
	public static void getInfo(String s) {
		ArrayList<String[]> temp = post.get(s);
		String id = "";
		for (int i = 0; i < temp.size(); i++) {
			if (!id.equals(temp.get(i)[0])) {
				int freq = Integer.parseInt(temp.get(i)[2]);
				ArrayList<String> pos = new ArrayList<String>(freq);
				id = temp.get(i)[0];
				for (int j = i; j < (i + freq); j++) {
					pos.add(temp.get(j)[1]);
				}
				System.out.println("Document ID: " + id);
				System.out.println("Title: " + getTitle(id));
				System.out.println("Term Frequency: " + freq);
				System.out.print("Position: ");
				for (int k = 0; k < pos.size(); k++) {
					if (k == pos.size() - 1)
						System.out.println(pos.get(k));
					else
						System.out.print(pos.get(k) + ", ");
				}
				System.out.print("Summary: ");
				ArrayList<String> summary = getSummary(s, id, pos.get(0));
				for (int j = 0; j < summary.size(); j++) {
					System.out.print(summary.get(j) + " ");
				}
				System.out.println();
			}
		}

	}

	// Returns the title of the document ID passed through
	static String getTitle(String id) {
		for (int i = 0; i < document.size(); i++) {
			if (document.get(i).contains(".I " + id) & document.get(i + 1).substring(0, 2).equals(".T")
					& !document.get(i + 2).substring(0, 1).equals("."))
				return document.get(i + 2);
		}
		return "";
	}

	static boolean exactMatch(String source, String term) {
		for (String word : source.split("\\s+")) {
			if (word.equals(term))
				return true;
		}
		return false;
	}

	// Returns an ArrayList of Strings containing the words near the term
	static ArrayList<String> getSummary(String word, String id, String pos) {
		int index = Integer.parseInt(pos);
		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < document.size(); i++) {
			if (document.get(i).contains(".I " + id)) {
				if (document.get(i + 1).substring(0, 2).equals(".T") & !document.get(i + 2).substring(0, 1).equals(".")
						& exactMatch(document.get(i + 2), word)) {
					// document.get(i + 2).contains(word)) {
					String[] s = document.get(i + 2).split("\\s+");
					int right = s.length - index - 1;
					if (index >= 4 & right >= 5) {
						for (int j = 4; j > 0; j--) {
							temp.add(s[index - j]);
						}
						temp.add(s[index]);
						for (int k = index + 1; k <= (index + 5); k++) {
							temp.add(s[k]);
						}
					} else if (index < 4) {
						int count = 0;
						for (int j = 0; j < s.length; j++) {
							temp.add(s[j]);
							count++;
							if (count == 9)
								break;
						}
					} else if (right < 5) {
						int left = 9 - right;
						int space = index - left;
						if (space < 0)
							space = 0;
						for (int j = space; j < index; j++) {
							temp.add(s[j]);
						}
						for (int k = index; k < s.length; k++) {
							temp.add(s[k]);
						}
					}
					return temp;
				}
				if (document.get(i + 3).substring(0, 2).equals(".W") & !document.get(i + 4).substring(0, 1).equals(".")
						& exactMatch(document.get(i + 4), word)) {
					// document.get(i + 4).contains(word)) {
					String[] s = document.get(i + 4).split("\\s+");
					int right = s.length - index - 1;
					if (index >= 4 & right >= 5) {
						for (int j = 4; j > 0; j--) {
							temp.add(s[index - j]);
						}
						temp.add(s[index]);
						for (int k = index + 1; k <= (index + 5); k++) {
							temp.add(s[k]);
						}
					} else if (index < 4) {
						int count = 0;
						for (int j = 0; j < s.length; j++) {
							temp.add(s[j]);
							count++;
							if (count == 9)
								break;
						}
					} else if (right < 5) {
						int left = 9 - right;
						int space = index - left;
						if (space < 0)
							space = 0;
						for (int j = space; j < index; j++) {
							temp.add(s[j]);
						}
						for (int k = index; k < s.length; k++) {
							temp.add(s[k]);
						}
					}
					return temp;
				}
			}
		}
		return temp;
	}

	public static String stemTerm(String s) {
		Stemmer stem = new Stemmer();
		String word = s;
		char[] wordArr = null;

		wordArr = word.toCharArray();
		for (int k = 0; k < wordArr.length; k++) {
			stem.add(wordArr[k]);
		}
		stem.stem();
		return stem.toString();
	}*/
}
