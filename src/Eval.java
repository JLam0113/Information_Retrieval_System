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
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/*
 * Eval object used to do evaluation onto query.text and qrels.text
 */
public class Eval {
	Search search = new Search();
	File file = new File("query.text");
	File file2 = new File("qrels.text");
	ArrayList<String> query = new ArrayList<String>();
	ArrayList<Double> ap = new ArrayList<Double>();
	ArrayList<Double> rp = new ArrayList<Double>();

	
	public Eval(Search search) {
		this.search = search;
	}

	//Starts evaluation process
	public void start() throws FileNotFoundException, UnsupportedEncodingException {
		readQuery();
		search();
		printValues();
	}

	// Reads the query file adding each query to the arraylist
	public void readQuery() throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		String line = "", line2 = "";

		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (!line.trim().isEmpty())
				if (line.substring(0, 2).equals(".W")) {
					line = sc.nextLine();
					line2 = line2.concat(line);
					boolean nextLine = false;
					while (!line.substring(0, 1).equals(".")) {
						if (nextLine)
							line2 = line2.concat(line);
						line2 = line2.concat(" ");
						line = sc.nextLine();
						nextLine = true;
					}
					query.add(line2);
					line2 = "";
				}
		}
		sc.close();
	}

	// For all queries inside the file, calculate the cosine similarity
	public void search() throws FileNotFoundException {
		for (int i = 0; i < query.size(); i++) {
			search.processQuery(query.get(i));
			search.calculateCosSim();
			Map<Integer, Double> sortedSim = search.sortByValueDesc(search.similarity);
			search.clear();
			calculatePrecision(sortedSim, i + 1);
		}
	}

	// Calculate the R-Precision for each query as well as the average precision
	private void calculatePrecision(Map<Integer, Double> sortedSim, int id) throws FileNotFoundException {
		Scanner sc = new Scanner(file2);
		String line = "";
		ArrayList<Integer> docID = new ArrayList<Integer>();
		int count = 0;
		double total = 0;
		int count2 = 0;
		int count3 = 0;

		while (sc.hasNextLine()) {
			line = sc.nextLine();
			String[] s = line.split("\\s+");
			if (Integer.parseInt(s[0]) == id) {
				docID.add(Integer.parseInt(s[1]));
			}
		}
		sc.close();

		Set<Integer> set = sortedSim.keySet();
		for (int key : set) {
			count++;
			if (docID.contains(key)) {
				count2++;
				if (count <= docID.size())
					count3++;
				total += count2 / count;
			}
		}
		rp.add((double) count3 / docID.size());
		double average = total/docID.size();
		if(Double.isNaN(average))
			average = 0.0;
		ap.add(average);

	}

	// Returns the MAP value
	public double getMAP() {
		double total = 0;
		for (int i = 0; i < ap.size(); i++) {
			//System.out.println(ap.get(i));
			total += ap.get(i);
		}

		return total / 64;
	}

	// Prints the MAP value into map.txt and R-Precision values into rprecision.txt
	public void printValues() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("map.txt", "UTF-8");
		PrintWriter rWriter = new PrintWriter("rprecision.txt", "UTF-8");

		writer.print("The MAP is: " + getMAP());
		writer.close();
		double total = 0;
		for (int i = 0; i < rp.size(); i++) {
			if (rp.get(i).isNaN()) {
				rp.set(i, 0.0);
			}
			int j = i + 1;
			rWriter.println("The R-Precision for Query " + j + " is: " + rp.get(i));
			total += rp.get(i);
		}
		rWriter.println("The average R-Precision is: " + total/rp.size());
		rWriter.close();
	}

}
