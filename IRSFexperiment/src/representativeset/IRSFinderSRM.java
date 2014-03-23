package representativeset;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Rule;

/**
 * Esegue gli IRSFinders con set di regole a diversa espressività e ne raccoglie i risultati
 *
 */
public class IRSFinderSRM {
	
	private Map<Integer, IRSFinder> IRSFinders;
	private IRSFinder fullIRSFinder;
	private int numPages;
	private Map<Integer, Integer> finder2representative;
	
	public IRSFinderSRM(List<Rule> allRules, List<Rule> rules4, List<Rule> rules3, List<Rule> rules2, List<Rule> rules1, List<Rule> rules0) {
	
		IRSFinders = new HashMap<Integer, IRSFinder>();
		IRSFinders.put(new Integer(0), new IRSFinder(shrinkRules(allRules, rules0)));
		IRSFinders.put(new Integer(1), new IRSFinder(shrinkRules(allRules, rules1)));
		IRSFinders.put(new Integer(2), new IRSFinder(shrinkRules(allRules, rules2)));
		IRSFinders.put(new Integer(3), new IRSFinder(shrinkRules(allRules, rules3)));
		IRSFinders.put(new Integer(4), new IRSFinder(shrinkRules(allRules, rules4)));
		fullIRSFinder = new IRSFinder(allRules);
		
		numPages = 0;
		
		finder2representative = new HashMap<Integer, Integer>();
		for (int i = 0; i <= 5; i++) finder2representative.put(i, 0);
				
	}
	
	//elimino le regole che estraggono solo nulli
	private List<Rule> shrinkRules(List<Rule> allRules, List<Rule> rulesX) {
		List<Rule> shrinkedRules = new LinkedList<Rule>();
		for (Rule r : rulesX) if (allRules.contains(r)) shrinkedRules.add(r);
//		System.out.println("Regole non nulle: "+shrinkedRules.size());
//		System.out.println(shrinkedRules);
				
		return shrinkedRules;
	}
	
	
	//ricavo i rulesSets meno espressivi da quello più espressivo
	private Set<Set<Rule>> shrinkRulesSets(Set<Set<Rule>> pageRS, List<Rule> rulesX) {
		Set<Set<Rule>> shrinkedPageRS = new HashSet<Set<Rule>>();
		for (Set<Rule> oldRulesSet : pageRS) {
			Set<Rule> newRulesSet = new HashSet<Rule>();
			for (Rule r : oldRulesSet) {
				if (rulesX.contains(r)) newRulesSet.add(r);
			}
			if (newRulesSet.size() != 0) shrinkedPageRS.add(newRulesSet);
		}
		
		return shrinkedPageRS;
	}
	
	public void find(String pageID, Set<Set<Rule>> pageRS) {
		
		numPages++;
		fullIRSFinder.find(pageID, pageRS);
		for (IRSFinder finder : IRSFinders.values()) finder.find(pageID, shrinkRulesSets(pageRS, finder.getRules()));		
     	        
  }
	
	public String getSRMstring() {
		return  this.numPages+";"+
				this.fullIRSFinder.getRepresentativePages().size()+";"+
				this.IRSFinders.get(4).getRepresentativePages().size()+";"+
				this.IRSFinders.get(3).getRepresentativePages().size()+";"+
				this.IRSFinders.get(2).getRepresentativePages().size()+";"+
				this.IRSFinders.get(1).getRepresentativePages().size()+";"+
				this.IRSFinders.get(0).getRepresentativePages().size()+";"+
				this.fullIRSFinder.estimateBetterCoverage()+";"+
				this.IRSFinders.get(4).estimateBetterCoverage()+";"+
				this.IRSFinders.get(3).estimateBetterCoverage()+";"+
				this.IRSFinders.get(2).estimateBetterCoverage()+";"+
				this.IRSFinders.get(1).estimateBetterCoverage()+";"+
				this.IRSFinders.get(0).estimateBetterCoverage()+";"+
				this.fullIRSFinder.getNumClasses()+";"+
				this.IRSFinders.get(4).getNumClasses()+";"+
				this.IRSFinders.get(3).getNumClasses()+";"+
				this.IRSFinders.get(2).getNumClasses()+";"+
				this.IRSFinders.get(1).getNumClasses()+";"+
				this.IRSFinders.get(0).getNumClasses()+";"+
				this.fullIRSFinder.getCurrentRulesSetsSize()+";"+
				this.IRSFinders.get(4).getCurrentRulesSetsSize()+";"+
				this.IRSFinders.get(3).getCurrentRulesSetsSize()+";"+
				this.IRSFinders.get(2).getCurrentRulesSetsSize()+";"+
				this.IRSFinders.get(1).getCurrentRulesSetsSize()+";"+
				this.IRSFinders.get(0).getCurrentRulesSetsSize()+";"+
				this.minExpressivenessVector();
	}
	
	public String getSRMLabel() {
		return  "Page;"+
				"Representative 5;"+
				"Representative 4;"+
				"Representative 3;"+
				"Representative 2;"+
				"Representative 1;"+
				"Representative 0;"+
				"C 5;"+
				"C 4;"+
				"C 3;"+
				"C 2;"+
				"C 1;"+
				"C 0;"+
				"Classes 5;"+
				"Classes 4;"+
				"Classes 3;"+
				"Classes 2;"+
				"Classes 1;"+
				"Classes 0;"+
				"Vectors 5;"+
				"Vectors 4;"+
				"Vectors 3;"+
				"Vectors 2;"+
				"Vectors 1;"+
				"Vectors 0;"+
				"Min Expres.";
	}
	
	public String minExpressivenessVector() {
		//salvo i vecchi valori
		Map<Integer, Integer> finder2oldRepresentative = new HashMap<Integer, Integer>();
		for (int i = 0; i <= 5; i++) finder2oldRepresentative.put(i, finder2representative.get(i));
		
		//aggiorno i valori
		for (int i = 0; i < 5; i++) finder2representative.put(i, IRSFinders.get(i).getRepresentativePages().size());		
		finder2representative.put(5, fullIRSFinder.getRepresentativePages().size());
		
		//trovo l'espressività minima necessaria per individuare la pagina come rappresentativa
		for (int i = 0; i <= 5; i++) if (finder2representative.get(i) != finder2oldRepresentative.get(i)) return String.valueOf(i);
			
		return "";
	}

	public boolean isThresholdReached(int pagineSoglia, double soglia) {
		
		return fullIRSFinder.isThresholdReached(pagineSoglia, soglia);
	}

	public List<String> getRepresentativePages() {

		return fullIRSFinder.getRepresentativePages();
	}

}
