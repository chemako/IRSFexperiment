package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;

import rules.dom.TextElements;
import rules.xpath.XPathRulesGenerator;

import model.ExtractedValue;
import model.LabeledValue;
import model.Page;
import model.Rule;
import model.RuleSet;
import model.LabeledValue.Label;

public class RulesGenerator {
	
	private ExtractedValue found;
	private Page page;
	private LabeledValue LV;
//	private String templatePath;
		
	public RulesGenerator(Page firstPage, Rule firstRule, String templatePath) {
		
	    this.page = firstPage;	    
	    this.found = firstRule.applyOn(page);
	    double correttezza = 1;
	    this.LV = new LabeledValue(this.found.getPage(), getEnumFromString("+"), this.found.getTextContent(), this.found.getOccurrenceInPage(), correttezza);
//		this.templatePath = templatePath;
	    
	}
	
	public RulesGenerator(Page firstPage, Rule firstRule) {
		this(firstPage, firstRule, null);
	}
	
	private static Label getEnumFromString(String label) {
		if (Label.CORRECT.getValue().equals(label))
			return Label.CORRECT;
		if (Label.WRONG.getValue().equals(label))
			return Label.WRONG;
		if (Label.UNKNOWN.getValue().equals(label))
			return Label.UNKNOWN;
		throw new RuntimeException("Not valid Label");
	}
	
//	private Set<String> loadTemplate() {
//		
//		Set<String> contentNodiTemplate = null;
//		try {
//			FileInputStream fileIn = new FileInputStream(this.templatePath);
//			ObjectInputStream in = new ObjectInputStream(fileIn);
//			contentNodiTemplate = (Set<String>)in.readObject();
//			in.close();
//			fileIn.close();
//		} catch(IOException i) {
//			i.printStackTrace();
//		} catch(ClassNotFoundException c) {
//			c.printStackTrace();
//		}
//		
//		return contentNodiTemplate;
//	}
	
//	private Set<Node> getPivotNodes() {
//		
//		//carico il template
//		Set<String> contentNodiTemplate = loadTemplate();
//		
//		//estraggo dalla pagina tutti i nodi testuali
//		TextElements textElements = new TextElements(this.page);		
//		Set<Node> tuttiNodi = textElements.getAllTextNodes();		
//		
//		//prendo tutti i nodi della pagina che fanno parte del template
//		Set<Node> pivotNodes = new HashSet<Node>();
//		for (Node nodo : tuttiNodi) {
//			String content = nodo.getTextContent();
//			if (contentNodiTemplate.contains(content)) {
//				pivotNodes.add(nodo);
//			}
//		}
//		
//		return pivotNodes;
//	}
	
	public RuleSet getRules() {

		return getRules(5);
	}
	
	public RuleSet getRules(int expressiveness) {
		XPathRulesGenerator generator = new XPathRulesGenerator(expressiveness);
//		if (this.templatePath != null) {
//			Set<Node> pivotNodes = getPivotNodes();
//			
//			return generator.inferValidRuleSet(this.LV, pivotNodes);	
//		} else {
			
			return generator.inferValidRuleSet(this.LV);	
//		}
	}
	
}
