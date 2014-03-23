package data2graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * Legge i csv prodotti da IRSFexperiment e media i valori di tutte le iterazioni
 *
 */
public class CSVsameValuesAverage {
 
  public static void main(String[] args) {
 
	CSVsameValuesAverage obj = new CSVsameValuesAverage();
	
	//true per mediare i valori del Coverage, false per mediare il numero di Vectors
	boolean CorVectors = false;
	
	obj.run("alf", "tennisplayers", "espn.go.com", "name", CorVectors);
	obj.run("alf", "stocks", "www.nasdaq.com", "Name", CorVectors);
	obj.run("alf", "games", "www.allgame.com", "genre", CorVectors);
	obj.run("alf", "movies", "www.imdb.com", "Title", CorVectors);
	obj.run("alf", "actors", "www.imdb.com", "Name", CorVectors);
	obj.run("alf", "albums", "www.allmusic.com", "Artist", CorVectors);
	obj.run("alf", "bands", "www.allmusic.com", "active", CorVectors);
	obj.run("alf", "basketplayers", "espn.go.com", "name", CorVectors);
	obj.run("alf", "footballplayers", "espn.go.com", "name", CorVectors);
	obj.run("alf", "hockeyplayers", "espn.go.com", "name", CorVectors);
	obj.run("alf", "movies", "www.allmovie.com", "title", CorVectors);
	obj.run("alf", "stamps", "colnect.com", "name", CorVectors);
	obj.run("alf", "books", "www.leggereditore.it", "autore", CorVectors);
	obj.run("alf", "soccerteams", "espnfc.com", "player", CorVectors);
	obj.run("alf", "soccerplayers", "espnfc.com", "name", CorVectors);
	 
  }
 
  public void run(String dataset, String domain, String site, String attribute, boolean CorVectors) {
 
    String csvFile = "SC-"+dataset+domain+site+attribute+".csv";
    String outFile;
    
    //NOTA: creare la cartella di output prima di eseguire
    if (CorVectors) {
    	outFile = "ExpSC10-Clin/SC-"+dataset+domain+site+attribute+"-10rep-Clin.csv";
    } else {
    	outFile = "ExpSC10-Vlin/SC-"+dataset+domain+site+attribute+"-10rep-Vlin.csv";
    }
	int columnC0, columnC1, columnC2, columnC3, columnC4, columnC5;
	if (CorVectors) {
		columnC0 = 12;
		columnC1 = 11;
		columnC2 = 10;
		columnC3 = 9;
		columnC4 = 8;
		columnC5 = 7;
	} else {
		columnC0 = 24;
		columnC1 = 23;
		columnC2 = 22;
		columnC3 = 21;
		columnC4 = 20;
		columnC5 = 19;
	}
	int columnPages = 0;
	
	BufferedReader br = null;
	String line = "";
	int pages = 0;
	String cvsSplitBy = ";";
	Map<Integer, Map<Integer, List<Double>>> pages2Clists = new TreeMap<Integer, Map<Integer, List<Double>>>();
	boolean notNumber = false;
 
	try {
 
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {
 
		    // use comma as separator
			String[] lineArray = line.split(cvsSplitBy);
			Map<Integer, Double> expressiveness2C = new HashMap<Integer, Double>();
			try {
				expressiveness2C.put(0, Double.parseDouble(lineArray[columnC0]));
				expressiveness2C.put(1, Double.parseDouble(lineArray[columnC1]));
				expressiveness2C.put(2, Double.parseDouble(lineArray[columnC2]));
				expressiveness2C.put(3, Double.parseDouble(lineArray[columnC3]));
				expressiveness2C.put(4, Double.parseDouble(lineArray[columnC4]));
				expressiveness2C.put(5, Double.parseDouble(lineArray[columnC5]));
				pages = Integer.parseInt(lineArray[columnPages]);
			} catch (NumberFormatException e) {
				//e.printStackTrace();
				notNumber = true;
			}
			if (!notNumber) {
				if (pages2Clists.containsKey(pages)) {
					Map<Integer, List<Double>> expressiveness2values = pages2Clists.get(pages);
					for (int i = 0; i <=5; i++) {
						List<Double> values = expressiveness2values.get(i);
						values.add(expressiveness2C.get(i));
					}
				} else {
					Map<Integer, List<Double>> expressiveness2values = new HashMap<Integer, List<Double>>();
					for (int i = 0; i <=5; i++) {
						List<Double> values = new LinkedList<Double>();
						values.add(expressiveness2C.get(i));
						expressiveness2values.put(i, values);
					}
					pages2Clists.put(pages, expressiveness2values);
				}
			}
			notNumber = false;						
		}
 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	System.out.println("File letto");
	
	try {
		PrintStream ps = new PrintStream( new FileOutputStream(outFile) );
		if (CorVectors) {
			ps.println("Pages;C 0;C 1;C 2;C 3;C 4;C 5");
		} else {
			ps.println("Pages;V 0;V 1;V 2;V 3;V 4;V 5");
		}
		for (Entry<Integer, Map<Integer, List<Double>>> entry : pages2Clists.entrySet()) {
			Map<Integer, Double> expressiveness2C = new HashMap<Integer, Double>();
			Map<Integer, List<Double>> expressiveness2values = entry.getValue();
			for (int i = 0; i <= 5; i++) {
				List<Double> values = expressiveness2values.get(i);
				Double C = 0.0;
				for (Double value : values) {
					C += value;
				}
				expressiveness2C.put(i, C/values.size());
			}
			
			//per googleDoc
			ps.println(entry.getKey()+";"+expressiveness2C.get(0)+";"+expressiveness2C.get(1)+";"+expressiveness2C.get(2)
					+";"+expressiveness2C.get(3)+";"+expressiveness2C.get(4)+";"+expressiveness2C.get(5));
			
			//per excel
//			ps.print(entry.getKey()+";");
//			ps.format(Locale.FRANCE, "%f", expressiveness2C.get(0));
//			ps.print(";");
//			ps.format(Locale.FRANCE, "%f", expressiveness2C.get(1));
//			ps.print(";");
//			ps.format(Locale.FRANCE, "%f", expressiveness2C.get(2));
//			ps.print(";");
//			ps.format(Locale.FRANCE, "%f", expressiveness2C.get(3));
//			ps.print(";");
//			ps.format(Locale.FRANCE, "%f", expressiveness2C.get(4));
//			ps.print(";");
//			ps.format(Locale.FRANCE, "%f", expressiveness2C.get(5));
//			ps.println();
		}
		ps.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	System.out.println("File scritto");
	
	
  }
 
}