package findClasses;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Page;
import model.Rule;

/**
 * calcola i rulesSets delle pagine
 *
 */
public class ClassFinder {
	
	private List<Rule> rules;
	private HashMap<Set<Set<Rule>>, List<String>> rulesSets2IDs;
	
	public ClassFinder(List<Rule> rules) {
		this.rules = rules;
		this.rulesSets2IDs = new HashMap<Set<Set<Rule>>, List<String>>();
	}
	
	//restituisce una mappa dei rulesSets di tutte le pagine analizzate
	//rulesSets2IDs invece di ID2rulesSets perchè occupa meno memoria
	public Map<Set<Set<Rule>>, List<String>> getRulesSets2IDs() {
		return rulesSets2IDs;
	}

	public void find(Page p, String ID) {
		
		Map<Rule, String> rule2value = new HashMap<Rule, String>();
    	for (Rule r : rules) {
			String value = r.applyOn(p).getTextContent();
			rule2value.put(r, value);
		}
		
		Set<Set<Rule>> pageRS = getRulesSets(rule2value);
		if (rulesSets2IDs.containsKey(pageRS)) {
			List<String> IDs = rulesSets2IDs.get(pageRS);
			IDs.add(ID);
//			System.out.println("old rulesSets - ID: "+ID);
		} else {
			List<String> IDs = new LinkedList<String>();
			IDs.add(ID);
			rulesSets2IDs.put(pageRS, IDs);
//			System.out.println("new rulesSets - ID: "+ID);
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
