/* Jessye Lam
 * 500702091
 * CPS 842 - Assignment 2
 * 10/20/2020
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.lang.Math;

/*
 * Search object used to calculate cosine similarity for queries
 */
public class Search {
	Invert invert = new Invert();
	ArrayList<String> query = new ArrayList<String>();
	boolean stemming = true;
	boolean stopword = true;
	HashMap<Integer, Double> similarity = new HashMap<Integer, Double>();

	public Search() {
	}

	// Set stemming to on or off
	public void setStemming(boolean b) {
		stemming = b;
	}

	// Set stopword to on or off
	public void setStopWord(boolean b) {
		stopword = b;
	}

	// Set inverted index
	public void setInverter(Invert inverter) {
		invert = inverter;
	}

	// Process the query applying stopwords/stemming if necessary and store into arraylist
	public void processQuery(String s) {
		String s2 = s.toLowerCase();
		ArrayList<String> temp = new ArrayList<>(Arrays.asList(s2.split("\\s+")));
		Stemmer stem = new Stemmer();
		String word = null;
		char[] wordArr = null;

		if (stopword) {
			for (int i = 0; i < temp.size(); i++) {
				temp.set(i, temp.get(i).replaceAll("[0123456789]", ""));
				temp.set(i, temp.get(i).replaceAll("[+.()<>=?;/{}\"+_`!@#$|%&*^:,]", ""));
				temp.set(i, temp.get(i).replaceAll("\\[[^\\[]*\\]", ""));
				temp.set(i, temp.get(i).replaceAll("--", " "));
				temp.set(i, temp.get(i).replaceAll(" -", " "));
				temp.set(i, temp.get(i).replaceAll("- ", " "));
				if (temp.get(i).matches(
						"i|a|about|an|and|are|as|at|be|by|for|from|how|in|is|it|of|on|or|that|the|this|to|was|what|when|where|who|will|with|the")) {
					temp.remove(i);
					i--;
				}
			}
		}
		if (stemming) {
			for (int j = 0; j < temp.size(); j++) {
				word = temp.get(j);
				wordArr = word.toCharArray();
				for (int k = 0; k < wordArr.length; k++) {
					stem.add(wordArr[k]);
				}
				stem.stem();
				temp.set(j, stem.toString());
			}
		}
		query = temp;
		Collections.sort(query);
	}

	// Returns the frequency of the term inside the query
	public int getFreq(String s) {
		int count = 0;
		for (int i = 0; i < query.size(); i++) {
			if (s.equals(query.get(i)))
				count++;
		}
		return count;
	}

	// Calculates the cosine similarity and stores into a hashmap with the
	// Document ID as key and score as the value
	public void calculateCosSim() {
		int numDocs = invert.getNumDocs();
		ArrayList<Double> qVector = new ArrayList<Double>();
		ArrayList<Double> idf = new ArrayList<Double>();
		ArrayList<String> temp = new ArrayList<String>();
		String tempS = "";
		double f = 0;
		double tf = 0;
		int count = 0;
		double freq = 0;

		// Calculate query vector
		for (int i = 0; i < query.size(); i++) {
			if (!tempS.equals(query.get(i))) {
				temp.add(query.get(i));
				f = (double) Collections.frequency(query, query.get(i));
				freq = invert.docFreq(query.get(i));
				if (freq == 0) {
					qVector.add(0.0);
					idf.add(0.0);
					count++;
					tempS = query.get(i);
					continue;
				}
				idf.add(Math.log10(numDocs / freq));
				tf = 1 + Math.log10(f);
				qVector.add(tf * idf.get(count));
				count++;
			}
			tempS = query.get(i);
		}
		// Go through every document
		// Calculate doc vector and cosine similarity
		// Store into similarity
		for (int i = 1; i <= numDocs; i++) {
			ArrayList<Double> dVector = new ArrayList<Double>();
			for (int j = 0; j < temp.size(); j++) {
				// System.out.println(temp.get(j));
				f = invert.termFreq(i, temp.get(j));
				if (f == 0)
					tf = 0;
				else
					tf = 1 + Math.log10(f);
				dVector.add(tf * idf.get(j));
				// System.out.println(temp.get(j) + ": " + " f: " + f + " w: " + tf*idf.get(j));
			}
			double docLength = 0;
			double qLength = 0;
			double total = 0;
			for (int k = 0; k < dVector.size(); k++) {
				docLength += dVector.get(k) * dVector.get(k);
				qLength += qVector.get(k) * qVector.get(k);
				total += dVector.get(k) * qVector.get(k);
			}
			docLength = Math.sqrt(docLength);
			qLength = Math.sqrt(qLength);
			double cos = total / (qLength * docLength);
			if (cos > 0.0)
				similarity.put(i, cos);

		}
	}

	// Clears the hashmap so new queries can be done
	public void clear() {
		similarity.clear();
	}

	// Prints out the top 100 (or less) results
	public void getSimilarity() {
		Map<Integer, Double> sortedSim = sortByValueDesc(similarity);
		int count = 1;
		int count2 = 1;
		int count3 = 0;
		double score = 0;
		
		for (Map.Entry<Integer, Double> entry : sortedSim.entrySet()) {
			if (count3 < 100) {
				if (score == entry.getValue()) {
					System.out.println("Rank: " + count + ", Doc ID: " + entry.getKey() + ", Score: " + entry.getValue()
							+ ", Title: " + invert.getTitle(entry.getKey()) + ", Author: "
							+ invert.getAuthor(entry.getKey()));
				} else {
					count = count2;
					System.out.println("Rank: " + count2 + ", Doc ID: " + entry.getKey() + ", Score: "
							+ entry.getValue() + ", Title: " + invert.getTitle(entry.getKey()) + ", Author: "
							+ invert.getAuthor(entry.getKey()));
				}
				count2++;
				score = entry.getValue();
			}
			count3++;
		}
		clear();
	}

	// Sort in descending order by score
	public Map<Integer, Double> sortByValueDesc(Map<Integer, Double> map) {
		List<Map.Entry<Integer, Double>> list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		Map<Integer, Double> result = new LinkedHashMap<>();
		for (Map.Entry<Integer, Double> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
