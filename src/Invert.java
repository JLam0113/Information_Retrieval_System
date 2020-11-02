/* Jessye Lam
 * 500702091
 * CPS 842 - Assignment 2
 * 10/20/2020
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

/*
 * Inverter object used to create an inverted index.
 * Change the file in file to change to a different text document.
 * For stopwords, all numbers, and specials characters except for hyphens and apostrophes are removed.
 * Dictionary and Posting terms are all lowercase.
 */
public class Invert {
	public ArrayList<String> doc = new ArrayList<String>();
	public HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	public HashMap<String, ArrayList<String[]>> posting = new HashMap<String, ArrayList<String[]>>();
	public File file = new File("cacm.all");
	public boolean stopword = false;
	public boolean stemB = false;
	public int docNum = 0;
	public HashMap<Integer, String> author = new HashMap<Integer, String>();

	public void start() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("dictionary.txt", "UTF-8");
		PrintWriter postWriter = new PrintWriter("postings.txt", "UTF-8");

		readFile();
		if (stopword)
			removeStopWords();
		if (stemB)
			stemDoc();

		createDictionary();
		sort(writer);

		createPostings();
		printPost(postWriter);

		writer.close();
		postWriter.close();
	}

	// Reads the file and creates an ArrayList with the desired fields.
	public void readFile() throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		String line = "", line2 = "";
		boolean nextLine = true;

		while (sc.hasNextLine()) {
			if (nextLine)
				line = sc.nextLine();
			nextLine = true;
			if (line.substring(0, 2).equals(".I")) {
				doc.add(line);
				docNum++;
			} else if (line.substring(0, 2).equals(".T")) {
				doc.add(line);
				line = sc.nextLine();
				while (!line.substring(0, 1).equals(".")) {
					line2 = line2.concat(line);
					line2 = line2.concat(" ");
					line = sc.nextLine();
					nextLine = false;
				}
				doc.add(line2);
				line2 = "";
			} else if (line.substring(0, 2).equals(".W")) {
				doc.add(line);
				line = sc.nextLine();
				while (!line.substring(0, 1).equals(".")) {
					line2 = line2.concat(line);
					line2 = line2.concat(" ");
					line = sc.nextLine();
					nextLine = false;
				}
				doc.add(line2);
				line2 = "";
			} else if (line.substring(0, 2).equals(".A")) {
				line = sc.nextLine();
				while (!line.substring(0, 1).equals(".")) {
					line2 = line2.concat(line);
					line = sc.nextLine();
					if (!line.substring(0, 1).equals("."))
						line2 = line2.concat(", ");
					nextLine = false;
				}
				author.put(docNum, line2);
				line2 = "";
			}
		}
		sc.close();
		for (int i = 0; i < doc.size(); i++) {
			if (doc.get(i).trim().isEmpty())
				doc.remove(i);
			if (!doc.get(i).trim().isEmpty())
				if (!doc.get(i).substring(0, 1).equals("."))
					doc.set(i, doc.get(i).toLowerCase());
		}
	}

	// Removes special characters
	public void clean() {
		for (int i = 0; i < doc.size(); i++) {
			if (!doc.get(i).trim().isEmpty())
				if (!doc.get(i).substring(0, 1).equals(".")) {
					doc.set(i, doc.get(i).replaceAll("[0123456789]", ""));
					doc.set(i, doc.get(i).replaceAll("[+.()<>=?;/{}\"+_`!@#$|%&*^:,]", ""));
					doc.set(i, doc.get(i).replaceAll("\\[[^\\[]*\\]", ""));
					doc.set(i, doc.get(i).replaceAll("--", " "));
					doc.set(i, doc.get(i).replaceAll(" -", " "));
					doc.set(i, doc.get(i).replaceAll("- ", " "));
				}
		}
	}

	// Creates a HashMap with the term as the key and the value as the number of
	// occurences.
	public void createDictionary() {

		for (int i = 0; i < doc.size(); i++) {
			if (doc.get(i).equals(".T")) {
				String[] s = doc.get(i + 1).split("\\s+");
				for (int j = 0; j < s.length; j++) {
					if (!s[j].equals("")) {
						if (dictionary.get(s[j]) == null)
							dictionary.put(s[j], 1);
						else
							dictionary.put(s[j], dictionary.get(s[j]) + 1);
					}
				}
			} else if (doc.get(i).equals(".W")) {
				String[] s = doc.get(i + 1).split("\\s+");
				for (int j = 0; j < s.length; j++) {
					if (!s[j].equals("")) {
						if (dictionary.get(s[j]) == null)
							dictionary.put(s[j], 1);
						else
							dictionary.put(s[j], dictionary.get(s[j]) + 1);
					}
				}
			}
		}
	}

	// Sorts and prints the file to dictionary.txt
	public void sort(PrintWriter fileDictionary) {
		TreeMap<String, Integer> tm = new TreeMap<String, Integer>(dictionary);
		Iterator<String> it = tm.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			fileDictionary.print(s + ": " + dictionary.get(s));
			fileDictionary.println();
		}
		System.out.println("Dictionary Complete");
	}

	// Turn stopwords removal on or off
	public void setStopWord(boolean b) {
		stopword = b;
	}

	// Turn stemming on or off
	public void setStem(boolean b) {
		stemB = b;
	}

	// Removes the stopwords in stopwords.txt
	public void removeStopWords() {
		StringBuilder sb = new StringBuilder();
		CharSequence charAdd = " ";
		clean();

		for (int i = 0; i < doc.size(); i++) {
			if (doc.get(i).equals(".T")) {
				ArrayList<String> s = new ArrayList<>(Arrays.asList(doc.get(i + 1).split("\\s+")));
				for (int j = 0; j < s.size(); j++) {
					if (s.get(j).matches(
							"i|a|about|an|and|are|as|at|be|by|for|from|how|in|is|it|of|on|or|that|the|this|to|was|what|when|where|who|will|with|the")
							|| s.get(j).length() <= 1) {
						s.remove(j);
						j--;
					}
				}
				for (int k = 0; k < s.size(); k++) {
					sb.append(s.get(k));
					if (k < s.size() - 1)
						sb.append(charAdd);
				}
				doc.set(i + 1, sb.toString());
				sb.setLength(0);
			} else if (doc.get(i).equals(".W")) {
				ArrayList<String> s = new ArrayList<>(Arrays.asList(doc.get(i + 1).split("\\s+")));
				for (int j = 0; j < s.size(); j++) {
					if (s.get(j).matches(
							"i|a|about|an|and|are|as|at|be|by|for|from|how|in|is|it|of|on|or|that|the|this|to|was|what|when|where|who|will|with|the")
							|| s.get(j).length() <= 1) {
						s.remove(j);
						j--;
					}
				}
				for (int k = 0; k < s.size(); k++) {
					sb.append(s.get(k));
					if (k < s.size() - 1)
						sb.append(charAdd);
				}
				doc.set(i + 1, sb.toString());
				sb.setLength(0);
			}
		}
	}

	// Uses Porter's Stemming Algorithm to stem the document
	public void stemDoc() {
		Stemmer stem = new Stemmer();
		String word = null;
		char[] wordArr = null;

		for (int i = 0; i < doc.size(); i++) {
			if (!doc.get(i).trim().isEmpty())
				if (!doc.get(i).substring(0, 1).equals(".")) {
					ArrayList<String> s = new ArrayList<>(Arrays.asList(doc.get(i).split("\\s+")));
					for (int j = 0; j < s.size(); j++) {
						word = s.get(j);
						wordArr = word.toCharArray();
						for (int k = 0; k < wordArr.length; k++) {
							stem.add(wordArr[k]);
						}
						stem.stem();
						s.set(j, stem.toString());
					}
					doc.set(i, s.toString().replace("[", "").replace("]", "").replace(",", ""));
				}
		}
	}

	// Creates a HashMap for posting
	public void createPostings() {
		Set<String> set = dictionary.keySet();
		int count = 0;
		int count2 = 0;
		int count3 = 0;
		boolean check = false;

		for (String key : set) {
			ArrayList<String[]> temp = new ArrayList<>();
			for (int j = 0; j < doc.size(); j++) {
				if (!doc.get(j).trim().isEmpty())
					if (doc.get(j).substring(0, 2).equals(".I"))
						count++;
				if (doc.get(j).contains(key)) {
					String[] s = doc.get(j).split("\\s+");
					for (int k = 0; k < s.length; k++) {
						if (s[k].equals(key)) {
							String[] s2 = new String[3];
							s2[0] = Integer.toString(count);
							s2[1] = Integer.toString(count2);
							temp.add(s2);
							count3++;
						}
						count2++;
					}
					check = true;
				}
				if (check) {
					for (int i = temp.size() - count3; i < temp.size(); i++) {
						String[] s3 = temp.get(i);
						s3[2] = Integer.toString(count3);
						temp.set(i, s3);
					}
				}
				count3 = 0;
				count2 = 0;
				check = false;
				if (dictionary.get(key) < temp.size())
					break;
			}
			posting.put(key, temp);
			count = 0;
		}
		postVerify();
	}

	// Verifies correct number of frequency in document
	// Necessary if word occurs in title and then in abstract
	public void postVerify() {
		Set<String> set = posting.keySet();

		for (String key : set) {
			int count = 0;
			String temp = posting.get(key).get(0)[0];
			for (int i = 0; i < posting.get(key).size(); i++) {
				if (posting.get(key).get(i)[0].equals(temp)) {
					count++;
				} else if (!posting.get(key).get(i)[0].equals(temp)) {
					// System.out.println(count + " vs " + posting.get(key).get(i)[2]);
					// System.out.println(temp);
					if (Integer.parseInt(posting.get(key).get(i - 1)[2]) != count) {
						for (int j = i - count; j < i; j++) {
							// System.out.println(key + " : " + posting.get(key).get(j)[0] +
							// posting.get(key).get(j)[2]);
							posting.get(key).get(j)[2] = Integer.toString(count);
						}
					}
					count = 1;
				}
				if (i == (posting.get(key).size() - 1)) {
					if (Integer.parseInt(posting.get(key).get(i)[2]) != count) {
						for (int j = (i - count + 1); j < i; j++) {
							// System.out.println(key + " : " + posting.get(key).get(j)[0] +
							// posting.get(key).get(j)[2]);
							posting.get(key).get(j)[2] = Integer.toString(count);
						}
					}
				}
				temp = posting.get(key).get(i)[0];
				// System.out.println(temp);
			}
		}
	}

	// Prints the HashMap posting to posting.txt
	public void printPost(PrintWriter filePostings) {
		Set<String> set = posting.keySet();

		for (String key : set) {
			filePostings.print(key + ": ");
			for (int i = 0; i < posting.get(key).size(); i++) {
				String[] b = posting.get(key).get(i);
				filePostings.print("[");
				for (int j = 0; j < posting.get(key).get(i).length; j++) {
					if (j == posting.get(key).get(i).length - 1)
						filePostings.print(b[j]);
					else
						filePostings.print(b[j] + ", ");
				}
				filePostings.print("]");
			}
			filePostings.println();
		}
		System.out.println("Posting Complete");
	}

	// Returns the dictionary HashMap
	public HashMap<String, Integer> getDictionary() {
		return dictionary;
	}

	// Returns the posting HashMap
	public HashMap<String, ArrayList<String[]>> getPosting() {
		return posting;
	}

	// Returns the document with the desired fields
	public ArrayList<String> getDoc() {
		return doc;
	}

	// Returns total number of documents (N)
	public int getNumDocs() {
		return docNum;
	}

	// Returns the amount of documents the term appears in
	public int docFreq(String s) {
		int count = 0;
		for (int i = 0; i < doc.size(); i++) {
			if (doc.get(i).substring(0, 2).equals(".I")) {
				if (doc.get(i + 1).substring(0, 2).equals(".T") & !doc.get(i + 2).substring(0, 1).equals(".")
						& exactMatch(doc.get(i + 2), s)) {
					count++;
				} else if (doc.get(i + 3).substring(0, 2).equals(".W") & !doc.get(i + 4).substring(0, 1).equals(".")
						& exactMatch(doc.get(i + 4), s)) {
					count++;
				}
			}
		}
		return count;
	}
	
	// Returns the amount of times the term appears in a document
	public int termFreq(int id, String term) {
		ArrayList<String[]> temp = new ArrayList<String[]>();
		if (posting.containsKey(term)) {
			temp = posting.get(term);
			for (int i = 0; i < temp.size(); i++) {
				if (Integer.parseInt(temp.get(i)[0]) == id) {
					return Integer.parseInt(temp.get(i)[2]);
				}
			}
		}
		return 0;
	}

	// Returns true if two strings are equal
	public boolean exactMatch(String source, String term) {
		for (String word : source.split("\\s+")) {
			if (word.equals(term))
				return true;
		}
		return false;
	}

	// Returns the title of the document ID
	public String getTitle(int id) {
		for (int i = 0; i < doc.size(); i++) {
			if (doc.get(i).contains(".I " + id) & doc.get(i + 1).substring(0, 2).equals(".T")
					& !doc.get(i + 2).substring(0, 1).equals("."))
				return doc.get(i + 2);
		}
		return "";
	}

	// Returns the author of the document ID
	public String getAuthor(int id) {
		if (id >= 0 & id <= docNum)
			return author.get(id);
		return "";
	}
}
