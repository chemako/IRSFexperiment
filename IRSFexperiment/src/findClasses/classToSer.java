package findClasses;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Page;
import model.Rule;
import utils.PageProvider;

/**
 * salva in un file .ser i rulesSets di tutte le pagine di un sito
 *
 */
public class classToSer {
	
	public static void main(String[] args) {
		classToSer c2s = new classToSer();
		c2s.run("alf", "tennisplayers", "espn.go.com");
//		c2s.run("alf", "actors", "www.imdb.com");
//		c2s.run("alf", "albums", "www.allmusic.com");
//		c2s.run("alf", "bands", "www.allmusic.com");
//		c2s.run("alf", "basketplayers", "espn.go.com");
//		c2s.run("alf", "footballplayers", "espn.go.com");
//		c2s.run("alf", "games", "www.allgame.com");
//		c2s.run("alf", "hockeyplayers", "espn.go.com");
//		c2s.run("alf", "movies", "www.imdb.com");
//		c2s.run("alf", "movies", "www.allmovie.com");		
//		c2s.run("alf", "stamps", "colnect.com");
//		c2s.run("alf", "stocks", "www.nasdaq.com");
//		c2s.run("alf", "books", "www.leggereditore.it");
//		c2s.run("alf", "soccerteams", "espnfc.com");
//		c2s.run("alf", "soccerplayers", "espnfc.com");
		
	}
	
	public void run(String dataset, String domain, String site) {
		
		PageProvider pp = new PageProvider(dataset, domain, site);
		Map<String, ClassFinder> attribute2classFinder = new HashMap<String, ClassFinder>();
		Set<String> attributes = pp.getGoldenRules().keySet();
		for (String attribute : attributes) {
			attribute2classFinder.put(attribute, new ClassFinder(pp.getRules(attribute)));
		}			
			
		Page p = pp.getPage();
		String ID;
		int i = 0;
		while (p != null) {
			ID = pp.getIDfromURL(p.getTitle());
			for (String attribute : attributes) {
				attribute2classFinder.get(attribute).find(p, ID);
			}
			p = pp.getPage();
			i++;
			System.out.print(ID+" ");
			if (i%1000 == 0) System.out.println();
		}
		
		for (String attribute : attributes) {
			Map<Set<Set<Rule>>, List<String>> rulesSets2IDs = attribute2classFinder.get(attribute).getRulesSets2IDs();
			Map<Set<Set<String>>, List<String>> encodedRulesSet2IDs = new HashMap<Set<Set<String>>, List<String>>();
			for (Set<Set<Rule>> rulesSets : rulesSets2IDs.keySet()) {
				Set<Set<String>> encodedRulesSets = new HashSet<Set<String>>();
				for (Set<Rule> rulesSet : rulesSets) {
					Set<String> encodedRulesSet = new HashSet<String>();
					for (Rule r : rulesSet) {
						encodedRulesSet.add(r.encode());
					}
					encodedRulesSets.add(encodedRulesSet);
				}
				encodedRulesSet2IDs.put(encodedRulesSets, rulesSets2IDs.get(rulesSets));
			}
			
			try {
				FileOutputStream fileOut = new FileOutputStream(dataset+"/"+domain+"/"+site+"/encodedRulesSets2IDs-"+attribute+".ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(encodedRulesSet2IDs);
				out.close();
				fileOut.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	//costruisce i rulesSets a partire dai valori estratti dalle regole
	protected Set<Set<Rule>> getRulesSets(Map<Rule, String> rule2value) {
		Set<Set<Rule>> rulesSets = new HashSet<Set<Rule>>();
		Map<String, Set<Rule>> value2ruleSet = new HashMap<String, Set<Rule>>();
		for (Rule r : rule2value.keySet()) {
			String value = rule2value.get(r);
			if (value2ruleSet.containsKey(value)) {
				Set<Rule> ruleSet = value2ruleSet.get(value);
				ruleSet.add(r);
			} else {
				Set<Rule> ruleSet = new HashSet<Rule>();
				ruleSet.add(r);
				value2ruleSet.put(value, ruleSet);
			}
		}
		rulesSets.addAll(value2ruleSet.values());
		
		return rulesSets;
	}
	
}
