package nlu.semantics;

import nlu.langmodel.cfg.ParseNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs trivial semantic analysis on parse trees.
 */
public class SemanticAnalyzer {

	public static List<String> facts = new ArrayList<>();
	
	public static void analyze(ParseNode root) {
		if (root.rule.equals("S NP VP")) {
			// something is being declared
			getSubject(root.children.get(0));
			getVerbAndObjects(root.children.get(1));
		} else if (root.rule.contains("S Aux")) {
			int x = (int) (Math.random()*5);
			if (x == 0) {
				facts.add("Unfortunately not");
			} else if (x == 1) {
				facts.add("No");
			} else if (x == 2) {
				facts.add("Yes!");
			} else {
				facts.add("Yes");
			}
		}
	}
	
	public static void getSubject(ParseNode root) {
		String tok0 = root.getTokens().get(0);
		if (tok0.equals("Noun") ||tok0.equals("Pronoun")) {
			facts.add("Subject: " + root.getTokens().get(1));
		} else {
			for (ParseNode node : root.children) {
				getSubject(node);
			}
		}
	}
	
	public static void getVerbAndObjects(ParseNode root) {
		String tok0 = root.getTokens().get(0);
		if (tok0.equals("Verb")) {
			facts.add("Verb: " + root.getTokens().get(1));
		} else if (tok0.equals("Noun") || tok0.equals("Pronoun")) {
			facts.add("Object: " + root.getTokens().get(1));
		} else {
			for (ParseNode node : root.children) {
				getVerbAndObjects(node);
			}
		}
	}
}
