package representativeset;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Rule;

/**
 * Incremental Representative Set Finder: 
 * trova il set di pagine rappresentative interrompendo l'analisi delle pagine quando la stima del coverage raggiunge una soglia
 */
public class IRSFinder {
	
	protected List<Rule> rules;
	private Map<Rule, Integer> rule2numNull;
	protected Map<Set<Set<Rule>>, Integer> rulesSets2numPage;
	protected List<String> representativePages;
	protected int numPages;
	protected List<List<Rule>> currentRulesSets;
	private boolean useTerminationCondition;
	protected Map<Set<Set<Rule>>, Double> rulesSets2probability;
	protected List<Double> cValues;
		
	public IRSFinder(List<Rule> rules, boolean useTerminationCondition, Map<Set<Set<Rule>>, Double> rulesSets2probability) {
		this.rules = rules;
		this.rule2numNull = new HashMap<Rule, Integer>();
		for (Rule r : this.rules) {
			this.rule2numNull.put(r, new Integer(0));
		}
		this.rulesSets2numPage = new HashMap<Set<Set<Rule>>, Integer>();
		this.representativePages = new LinkedList<String>();
		this.numPages = 0;
		this.currentRulesSets = new LinkedList<List<Rule>>();
		this.currentRulesSets.add(rules);
		
		this.useTerminationCondition = useTerminationCondition;
		this.rulesSets2probability = rulesSets2probability;
		
		this.cValues = new LinkedList<Double>();
		
	}
	
	public IRSFinder(List<Rule> rules) {
		this(rules, true, null);
	}
	
	public IRSFinder(List<Rule> rules, Map<Set<Set<Rule>>, Double> rulesSets2probability) {
		this(rules, true, rulesSets2probability);
	}
	
	public IRSFinder(List<Rule> rules, boolean useTerminationCondition) {
		this(rules, useTerminationCondition, null);
	}
	
	public void find(String pageID, Set<Set<Rule>> pageRS) {
		

//        if (!(this.flagD[0] && this.flagD[1] && this.flagD[2])) {
    		
//    		Map<Rule, String> rule2value = new HashMap<Rule, String>();
//        	for (Rule r : this.rules) {
//				String value = r.applyOn(p).getTextContent();
//				rule2value.put(r, value);
//				
////				//se la regola non ha estratto un valore lo inserisco in rule2null
////				if (value.equals("")) {
////					int numNull = this.rule2numNull.get(r);
////					numNull++;
////					this.rule2numNull.put(r, new Integer(numNull));
////				}
//			}
//			
//			Set<Set<Rule>> pageRS = getRulesSets(rule2value);
        	
       	
        	//creo un rule2value fittizio per il calcolo delle pagine rappresentative
        	Map<Rule, String> rule2value = new HashMap<Rule, String>();
        	int fakeValue = 0;
        	int numRules = 0;
        	for (Set<Rule> rulesSet : pageRS) {
				for (Rule r : rulesSet) {
					rule2value.put(r, Integer.toString(fakeValue));
					numRules++;
				}
				fakeValue++;
			}
        	
			if (this.rulesSets2numPage.containsKey(pageRS)) {
				int numPage = this.rulesSets2numPage.get(pageRS);
				numPage++;
				this.rulesSets2numPage.put(pageRS, new Integer(numPage));
			} else {
				this.rulesSets2numPage.put(pageRS, new Integer(1));
				if (this.isRepresentative(rule2value)) this.representativePages.add(pageID);
			}			
	        this.numPages++;
	        
	        
//	        if (rulesSets2probability != null) {
//	        	if (this.numPages == 1) {
//	        		allValuesWriter.println(this.getLabels());
////	        		resultsWriter.println(this.getLabels());
//	        	}
////	        	if (this.numPages % 100 == 0) resutsWriter.println(this.getCSVstring());
//        		allValuesWriter.println(this.getCSVstring());
//        	}
	        
	      //aggiorno la lista delle stime di C
			if (this.cValues.size() < 100) {
	        	this.cValues.add(this.estimateBetterCoverage());
	        } else {
	        	this.cValues.remove(0);
	        	this.cValues.add(this.estimateBetterCoverage());
	        }
	        
//	        if (this.useTerminationCondition && this.terminationCondition()) {
//	        	allValuesWriter.println("\n ********** Termination Condition ********** \n");
//	        	break;
//	        }
        
//    	}
    	
//    	allValuesWriter.close();			
       	        
    }
	
//	private String getXiString() {
//		String XiString = null;
//		
//		for (int Xi : this.rulesSets2numPage.values()) {
//			XiString = XiString+" "+Xi;
//		}
//		
//		return XiString;
//	}
	
	public boolean isThresholdReached(int numLastPages, double threashold) {
		boolean thresholdReached = false;		
		double cSum = 0;
		if(this.cValues.size() >= numLastPages) {
			for (int i = (this.cValues.size()-1); i >= (this.cValues.size()-numLastPages); i--) {
				cSum += this.cValues.get(i);
			}
			if (cSum/numLastPages >= threashold/100) {
				thresholdReached = true;
			}
		}
		
		return thresholdReached;
	}
	
	protected String getCSVstring() {
		return  this.numPages+";"+
				this.getRepresentativePages().size()+";"+
				//this.estimateCoverage()+";"+
				this.estimateBetterCoverage()+";"+
				this.computeCoverage()+";"+
				//this.estimateGamma2()+";"+
				this.estimateGamma2BC()+";"+
				//this.estimateBetterGamma2()+";"+
				this.estimateBetterGamma2BC()+";"+
				this.computeGamma2()+";"+
				this.getNumClasses()+";"+
				//this.estimateNumClasses1()+";"+
				this.estimateNumClasses1BC()+";"+
				//this.estimateNumClasses2()+";"+
				this.estimateNumClasses2BC()+";"+
				//this.estimateNumClasses3()+";"+
				this.estimateNumClasses3BC()+";"+
				this.rulesSets2probability.size();
	}
	
	protected String getLabels() {
		return  "Page;"+
				"Representative;"+
				//"C;"+
				"Better C;"+
				"Actual C;"+
				//"Gamma^2;"+
				"Gamma^2 BC;"+
				//"Better gamma^2;"+
				"Better gamma^2 BC;"+
				"Actual gamma^2;"+
				"Classes;"+
				//"N1;"+
				"N1 BC;"+
				//"N2;"+
				"N2 BC;"+
				//"N3;"+
				"N3 BC;"+
				"Actual N";
	}

	public List<String> getRepresentativePages() {
		return this.representativePages;
	}
	
	public double estimateCoverage() {
		int f1 = classCount(1);	
		double coverage = 1 - (((double)f1)/this.numPages);
		
		return coverage;
	}
	
	public double estimateBetterCoverage() {
		int f1 = classCount(1);
		int f2 = classCount(2);
		double coverage = 1 - ((((double)f1) / this.numPages) *
				(((double)((this.numPages-1) * f1)) / (((this.numPages-1) * f1) + (2 * f2))));
		
		return coverage;
	}
		
	public double computeCoverage() {
		double coverage = 0;
		for (Set<Set<Rule>> rulesSets : this.rulesSets2numPage.keySet()) {
			coverage += this.rulesSets2probability.get(rulesSets);
		}		
		
		return coverage;
	}
	
	//stima numero classi senza correzione coefficente variazione
	public double estimateNumClasses1BC() {
		return (this.rulesSets2numPage.size() / this.estimateBetterCoverage());
	}
	
	//stima numero classi con correzione coefficente variazione
	public double estimateNumClasses2BC() {
		return ( this.estimateNumClasses1() + 
				(((this.numPages * (1 - this.estimateBetterCoverage())) / this.estimateBetterCoverage()) * this.estimateGamma2BC()) );
	}
	
	//stima numero classi con migliore correzione coefficente variazione
	public double estimateNumClasses3BC() {
		return ( this.estimateNumClasses1() + 
				(((this.numPages * (1 - this.estimateBetterCoverage())) / this.estimateBetterCoverage()) * this.estimateBetterGamma2BC()) );
	}
	
	public double estimateGamma2BC() {
		double gamma2 = ( (this.estimateNumClasses1BC() * this.sumFi()) / ((this.numPages * (this.numPages - 1)) - 1) );
		
		if (gamma2 > 0) {
			return gamma2;
		} else {
			return 0;
		}
	}
	
	public double estimateBetterGamma2BC() {
		double gamma2 = this.estimateGamma2() * (
						(1 + (this.numPages * (1 - this.estimateBetterCoverage()) * this.sumFi())) / 
						( this.numPages * (this.numPages - 1) * this.estimateBetterCoverage() ) 
						);
		
		if (gamma2 > 0) {
			return gamma2;
		} else {
			return 0;
		}
	}
	
	//stima numero classi senza correzione coefficente variazione
	public double estimateNumClasses1() {
		return (this.rulesSets2numPage.size() / this.estimateCoverage());
	}
	
	//stima numero classi con correzione coefficente variazione
	public double estimateNumClasses2() {
		return ( this.estimateNumClasses1() + 
				(((this.numPages * (1 - this.estimateCoverage())) / this.estimateCoverage()) * this.estimateGamma2()) );
	}
	
	//stima numero classi con migliore correzione coefficente variazione
	public double estimateNumClasses3() {
		return ( this.estimateNumClasses1() + 
				(((this.numPages * (1 - this.estimateCoverage())) / this.estimateCoverage()) * this.estimateBetterGamma2()) );
	}
	
	public double estimateGamma2() {
		double gamma2 = ( (this.estimateNumClasses1() * this.sumFi()) / ((this.numPages * (this.numPages - 1)) - 1) );
		
		if (gamma2 > 0) {
			return gamma2;
		} else {
			return 0;
		}
	}
	
	public double estimateBetterGamma2() {
		double gamma2 = this.estimateGamma2() * (
						(1 + (this.numPages * (1 - this.estimateCoverage()) * this.sumFi())) / 
						( this.numPages * (this.numPages - 1) * this.estimateCoverage() ) 
						);
		
		if (gamma2 > 0) {
			return gamma2;
		} else {
			return 0;
		}
	}
	
	private int classCount(int i) {
		int fi = 0;
		for (int numPage : this.rulesSets2numPage.values()) {
			if (numPage == i) fi++;
		}
		
		return fi;
	}
	
	private long sumFi() {
		long sommatoria = 0;
		for (int i = 1; i <= this.numPages; i++) {
			sommatoria += i * (i - 1) * this.classCount(i);
		}
		
		return sommatoria;
	}
	
	public Map<Set<Set<Rule>>, Double> getClassesProbabilities() {
		int numTotSamples = 0;
		for (int numPages : this.rulesSets2numPage.values()) {
			numTotSamples += numPages;
		}
		//calcolo le probabilita'  delle classi
		Map<Set<Set<Rule>>, Double> rulesSets2probability = new HashMap<Set<Set<Rule>>, Double>();
		for (Set<Set<Rule>> rulesSets : this.rulesSets2numPage.keySet()) {
			double numPage = (double)this.rulesSets2numPage.get(rulesSets);
			double probability = numPage/(double)numTotSamples;
			rulesSets2probability.put(rulesSets, probability);
		}		
					
		return rulesSets2probability;				
	}
	
	public double computeGamma2() {
		double gamma2 = this.computeCoefficentOfVariation() * this.computeCoefficentOfVariation();
		
		return gamma2;
	}
	
	public double computeCoefficentOfVariation() {
		double CV = Double.NaN;
		if (rulesSets2probability != null) {
			double sum = 0;
			double media = (1 / (double)this.rulesSets2probability.size());
			for (double probability : this.rulesSets2probability.values()) {
				sum += ((probability - media) * (probability - media));
			}
			CV = Math.sqrt(sum/this.rulesSets2probability.size())/media;
		}
		
		return CV;
	}

	public boolean terminationCondition() {
		boolean result = false;
		//TODO
//		//termino se f1 e' vuoto e ho trovato piu' di una classe
//		if (!this.rulesSets2numPage.values().contains(new Integer(1)) && this.getNumClasses() > 1) result = true;
		
		return result;
	}
	
	public int getNumClasses() {
		return this.rulesSets2numPage.size();
	}
	
	public List<List<Rule>> getCurrentRulesSets() {
		return this.currentRulesSets;
	}

	public Map<Set<Set<Rule>>, Integer> getRulesSets2numPage() {
		return this.rulesSets2numPage;
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
	
	protected boolean isRepresentative(Map<Rule, String> rule2value) {
		boolean notSameValues = false;
		List<List<Rule>> newRulesSets = new LinkedList<List<Rule>>();
		for (List<Rule> rules : this.currentRulesSets) {
			List<String> values = new ArrayList<String>();

			for (Rule regola : rules) {
				values.add(rule2value.get(regola));
			}

			if (!sameValues(values)) {
				notSameValues = true;
				newRulesSets.addAll(groupForExtractedValues(values, rules));
			} else {
				// if they are same
				newRulesSets.add(rules);
			}
		}
		this.currentRulesSets = newRulesSets;
		
		return notSameValues;
	}

	private boolean sameValues(List<String> values) {
		boolean same = true;
		String firstValue = values.get(0);
		for (String otherValues : values) {
			if (!firstValue.equals(otherValues)) {
				same = false;
				break;
			}
		}
		
		return same;
	}    
    
    private List<List<Rule>> groupForExtractedValues(List<String> values, List<Rule> rules) {
		Map<String, List<Rule>> val2rules = new HashMap<String, List<Rule>>();
		List<List<Rule>> result = new LinkedList<List<Rule>>();
		int i = 0;
		for (Rule r : rules) {
			String val = values.get(i);
			if (val2rules.containsKey(val)) {
				val2rules.get(val).add(r);
			} else {
				List<Rule> newSet = new LinkedList<Rule>();
				newSet.add(r);
				val2rules.put(val, newSet);
			}
			i++;
		}
		for (List<Rule> ruleGroup : val2rules.values()) {
			result.add(ruleGroup);
		}
		
		return result;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public int getCurrentRulesSetsSize() {
		return this.currentRulesSets.size();
	}
	
}
