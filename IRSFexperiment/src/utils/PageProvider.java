package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import model.Page;
import model.Rule;

import org.apache.commons.io.IOUtils;

import rules.xpath.XPathRule;

/**
 * legge le pagine di un sito e tutte le informazioni aggiuntive salvate insieme ad esse
 *
 */
public class PageProvider {
	
	private int lette;
	private String path;
	private Map<String, String> id2url;
	private Map<String, String> url2id;
	private int totNumPages;
	private Random random;
	private List<String> IDs;
	
	public PageProvider(String dataset, String domain, String site) {
		lette = 0;
		path = dataset+"/"+domain+"/"+site;
		File cartella = new File(path);
		id2url = new HashMap<String, String>();
		url2id = new HashMap<String, String>();
		IDs = new ArrayList<String>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(this.path+"/_id2url.txt"));
			String line = br.readLine();
			while (line != null) {
				String[] splittedLine = line.split("\t");
				id2url.put(splittedLine[0], splittedLine[1]);
				url2id.put(splittedLine[1], splittedLine[0]);
				IDs.add(splittedLine[0]);
				line = br.readLine();
			}			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File "+path+"/_id2url.txt non trovato");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		totNumPages = id2url.keySet().size();
		
		this.random = new Random();
		
		System.out.println("Indicizzate "+IDs.size()+" pagine");
		
	}

	
	/**
	 * @return
	 * up to "totNumPages" random pages or
	 * null if called more than "totNumPages"
	 */
	public Page getRandPage(){
		int rand = this.random.nextInt(totNumPages);
		rand++;
		if (lette < totNumPages) {
			lette++;
			
			return getSinglePage(Integer.toString(rand), null);			
		} else {
			
			return null;
		}
	}
	
	
	/**
	 * @return
	 * a random page ID
	 */
	public String getRandPageID() {
		int rand = this.random.nextInt(totNumPages-1);
		rand++;
		
		return IDs.get(rand);
	}
	
	public int getTotNumPages() {
		return totNumPages;
	}

	public void resetNumReadPages() {
		this.lette = 0;
	}

	public List<Page> getPages(int numPages){
		List<Page> pagelist = new LinkedList<Page>();
		numPages += lette;		
		System.out.print("Fetching pages: "+(lette+1)+" - ");
		for (int i = (lette+1); i <= numPages && i < totNumPages; i++) {
			pagelist.add(getSinglePage(Integer.toString(i), null));
			lette++;
		}		
		System.out.println(lette);
		
		return pagelist;
	}
	
	
	/**
	 * @return
	 * Next page or null if there are no more pages
	 */
	public Page getPage() {
		if (lette < totNumPages) {
			Page p = getSinglePage(IDs.get(lette), null);
			lette++;
			
			return p;			
		} else {
			
			return null;
		}
	}
	
	
	/**
	 * @param ID
	 * page's ID or null if unknown
	 * @param URL
	 * null or page's URL if ID is unknown
	 */
	public Page getSinglePage(String ID, String URL) {
		InputStream in = null;
		Page pagina = null;
		String pageID;
		if (ID != null) {
			pageID = ID;
		} else {
			pageID = this.url2id.get(URL);
		}
		try {
			in = new FileInputStream(this.path+"/pages/"+pageID+".html");
			String content = IOUtils.toString(in, "UTF-8");
			pagina = new Page(content);
			pagina.setTitle(this.id2url.get(pageID));
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
		
		return pagina;
	}
	
	
	public Page getFirstPage() {
		InputStream in = null;
		Page pagina = null;
		try {			
			BufferedReader br = new BufferedReader(new FileReader(this.path+"/firstPage.txt"));
			String firstPageID = br.readLine();
			br.close();			
			in = new FileInputStream(this.path+"/pages/"+firstPageID+".html");
			String content = IOUtils.toString(in, "UTF-8");
			pagina = new Page(content);
			pagina.setTitle(id2url.get(firstPageID));
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
		
		return pagina;
	}
	
	
	/**
	 * @return
	 * Map(Attribute, EncodedGoldenRule)
	 */
	public Map<String, String> getGoldenRules() {
		Map<String, String> attribute2goldenRule = new HashMap<String, String>();
		try {			
			BufferedReader br = new BufferedReader(new FileReader(this.path+"/goldenRules.txt"));
			String line = br.readLine();
			while(line != null) {
				String[] splittedLine = line.split("\t");
				attribute2goldenRule.put(splittedLine[0], splittedLine[1]);
				line = br.readLine();
			}			
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return attribute2goldenRule;
	}
	
	
	public Map<Rule, Integer> getRules2Null(String attribute) {
		Map<Rule, Integer> rules2null = new HashMap<Rule, Integer>();
		try {			
			BufferedReader br = new BufferedReader(new FileReader(this.path+"/rules2null-"+attribute+".txt"));
			String line = br.readLine();
			while(line != null) {
				String regola = line;
				line = br.readLine();
				
				line = br.readLine();
			}			
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return rules2null;
	}
	
	public String getIDfromURL(String URL){
		
		return url2id.get(URL);
	}
	
	
	public List<Rule> getRules(String attribute) {
		List<Rule> rules = new LinkedList<Rule>();
		try {			
			BufferedReader br = new BufferedReader(new FileReader(this.path+"/rules2null-"+attribute+".txt"));
			String line = br.readLine();
			while(line != null) {
				String regola = line;
				line = br.readLine();
				String[] splittedLine = line.split(" ");
				if (Integer.parseInt(splittedLine[0]) != Integer.parseInt(splittedLine[1]))
					rules.add(new XPathRule(regola));
				line = br.readLine();
			}			
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return rules;
	}
	
	
	public Map<String, Set<Set<Rule>>> getID2RulesSets(String attribute) {
		Map<Set<Set<String>>, List<String>> encodedRulesSet2IDs = null;
		try {
			FileInputStream fileIn = new FileInputStream(this.path+"/encodedRulesSets2IDs-"+attribute+".ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			encodedRulesSet2IDs = (Map<Set<Set<String>>, List<String>>) in.readObject();
			in.close();
			fileIn.close();
		} catch(IOException e) {
	         e.printStackTrace();
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Map<String, Set<Set<Rule>>> ID2RulesSets = new HashMap<String, Set<Set<Rule>>>();
		for (Set<Set<String>> encodedRulesSets : encodedRulesSet2IDs.keySet()) {
			Set<Set<Rule>> rulesSets = new HashSet<Set<Rule>>();
			for (Set<String> encodedRulesSet : encodedRulesSets) {
				Set<Rule> rulesSet = new HashSet<Rule>();
				for (String encodedRule : encodedRulesSet) {
					rulesSet.add(new XPathRule(encodedRule));
				}
				rulesSets.add(rulesSet);
			}
			for (String ID : encodedRulesSet2IDs.get(encodedRulesSets)) {
				ID2RulesSets.put(ID, rulesSets);
			}
		}
		
		return ID2RulesSets;
	}
	

	public Set<Page> getRepresentatives(String attribute) {
		Set<Page> pagine = new HashSet<Page>();
		try {			
			BufferedReader br = new BufferedReader(new FileReader(this.path+"/representatives-"+attribute+".txt"));
			String line = br.readLine();
			while(line != null && !line.equals("")) {
				
				pagine.add(getSinglePage(line, null));
				line = br.readLine();
			}			
			br.close();			
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return pagine;
	}
	
	public String getPath() {
		
		return this.path;
	}
	
}
