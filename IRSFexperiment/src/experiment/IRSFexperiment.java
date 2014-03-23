package experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.RulesGenerator;

import representativeset.IRSFinderSRM;
import rules.xpath.XPathRule;
import utils.PageProvider;

import model.Page;
import model.Rule;

/**
 * Simula l'IncrementalRepresentativeSetFinder con tutti i livelli di espressività a partire dai .ser generati da classToSer
 *
 */
public class IRSFexperiment {
	
	public static void main(String[] args) {

		IRSFexperiment exp = new IRSFexperiment();
		
		try {
			exp.run("alf", "actors", "www.imdb.com", "Name", 98.5, 100, 10);
			exp.run("alf", "albums", "www.allmusic.com", "Artist", 97, 100, 10);
//			exp.run("alf", "bands", "www.allmusic.com", "active", 98, 100, 10);
//			exp.run("alf", "basketplayers", "espn.go.com", "name", 95.5, 100, 10);
//			exp.run("alf", "books", "www.leggereditore.it", "autore", 97, 100, 10);
//			exp.run("alf", "footballplayers", "espn.go.com", "name", 97, 100, 10);
//			exp.run("alf", "games", "www.allgame.com", "genre", 88.5, 100, 10);
//			exp.run("alf", "hockeyplayers", "espn.go.com", "name", 93.5, 100, 10);
//			exp.run("alf", "movies", "www.allmovie.com", "title", 84.5, 100, 10);
//			exp.run("alf", "movies", "www.imdb.com", "Title", 89.5, 100, 10);
//			exp.run("alf", "soccerplayers", "espnfc.com", "name", 99, 100, 10);
//			exp.run("alf", "soccerteams", "espnfc.com", "player", 96.5, 100, 10);
//			exp.run("alf", "stamps", "colnect.com", "name", 79, 100, 10);
//			exp.run("alf", "stocks", "www.nasdaq.com", "Name", 99.99, 100, 10);
//			exp.run("alf", "tennisplayers", "espn.go.com", "name", 94, 100, 10);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	public void run(String dataset, String domain, String site, String attribute, double soglia, int pagineSoglia, int iterazioni) throws IOException {
		
		PageProvider pp = new PageProvider(dataset, domain, site);
		Map<String, Set<Set<Rule>>> ID2RulesSets = pp.getID2RulesSets(attribute);
//		for (String ID : ID2RulesSets.keySet()) System.out.println(ID);
		System.out.println("Size: "+ID2RulesSets.size());
		Page firstPage = pp.getFirstPage();
		List<String> results = new LinkedList<String>();
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("SC-"+dataset+domain+site+attribute+".csv", true)));
		
		for(int i=0;i<iterazioni;i++) {
			
//			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("SC-"+dataset+domain+site+attribute+i+".csv", true)));
			Set<Page> representatives = getRepresentatives(pp, ID2RulesSets, attribute, writer, soglia, pagineSoglia);
			representatives.add(firstPage);
			System.out.println("rappresentative: "+representatives.size());
//			Map<String, String> page2values = getPage2Values(representatives, new XPathRule(pp.getGoldenRules().get(attribute)));
//			System.out.println("estratti "+page2values.size()+" valori con la golden rule");
//			AlfCoreFacade alfcorefacade = AlfCoreFactory.getSimpleFacade();
//			AlfCoreSimulation acs = new AlfCoreSimulation(page2values, new ExperimentKeyFile(domain, site, attribute), 1, alfcorefacade, firstPage, 1);
//			acs.simulate(new LinkedList<Page>(representatives));
//			results.add(acs.getResult());
//			System.out.println("Result: "+acs.getResult());
//			writer.close();
		}
		
		writer.close();
//		System.out.println();
//
//		Set<Page> representatives = pp.getRepresentatives(attribute);
//		representatives.add(firstPage);
//		System.out.println("rappresentative: "+representatives.size());
//		Map<String, String> page2values = getPage2Values(representatives, new XPathRule(pp.getGoldenRules().get(attribute)));
//		System.out.println("estratti "+page2values.size()+" valori con la golden rule");
//		AlfCoreFacade alfcorefacade = AlfCoreFactory.getSimpleFacade();
//		AlfCoreSimulation acs = new AlfCoreSimulation(page2values, new ExperimentKeyFile(domain, site, attribute), 1, alfcorefacade, firstPage, 1);
//		acs.simulate(new LinkedList<Page>(representatives));
//		String fullSamplingResult = acs.getResult();
//		
//		System.out.println();
//		for (String result : results) {
//			System.out.println("Result: "+result);
//		}
//		System.out.println();
//		System.out.println("Result: "+fullSamplingResult);
	}
	
	public Set<Page> getRepresentatives(PageProvider pp, Map<String, Set<Set<Rule>>> ID2RulesSets, String attribute, PrintWriter writer, double soglia, int pagineSoglia) {
		List<Rule> rules = pp.getRules(attribute);
		RulesGenerator RG = new RulesGenerator(pp.getFirstPage(), new XPathRule(pp.getGoldenRules().get(attribute)));
		IRSFinderSRM finder = new IRSFinderSRM(rules, RG.getRules(4).getAllRules(), RG.getRules(3).getAllRules(), RG.getRules(2).getAllRules(), RG.getRules(1).getAllRules(), RG.getRules(0).getAllRules());
		writer.println(finder.getSRMLabel());
		String p = pp.getRandPageID();
		int numPagine = 0;
		while (!finder.isThresholdReached(pagineSoglia, soglia) && numPagine < pp.getTotNumPages()) {
			Set<Set<Rule>> pageRS = ID2RulesSets.get(p);
			if (pageRS != null) {
				finder.find(p, pageRS);
				writer.println(finder.getSRMstring());
				numPagine++;
			} else {
				System.out.println("RulesSets not found for page: "+p);
			}
			p = pp.getRandPageID();
			
		}
		System.out.println("Pagine analizzate: "+numPagine);
		
		Set<Page> representatives = new HashSet<Page>();
		for (String ID : finder.getRepresentativePages()) {
			representatives.add(pp.getSinglePage(ID, null));
		}
		
		
		return representatives;
	}
	
	private Map<String, String> getPage2Values(Collection<Page> pages, Rule rule) {
		Map<String, String> page2values = new HashMap<String, String>();
		for (Page page : pages) {
			page2values.put(page.getTitle(), rule.applyOn(page).getTextContent());
		}
		
		return page2values;
	}

}
