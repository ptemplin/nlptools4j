package app;

import nlu.langmodel.cfg.CYKParser;
import nlu.langmodel.cfg.ParseNode;
import nlu.semantics.SemanticAnalyzer;
import tokenize.Classifier;
import tokenize.Lexicon;
import tokenize.Token;

import java.util.List;
import java.util.Scanner;

public class  Al {
	public static void main(String[] args) {
		initialize();
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		List<Token> tokens = Classifier.classifySentence(input);
		for (Token t : tokens) {
			System.out.print("[" + t.toString() + "], ");
		}
		System.out.println();
		
		ParseNode parseTree = CYKParser.parse(tokens);
		if (parseTree != null) {
			SemanticAnalyzer.analyze(parseTree);
			System.out.println(SemanticAnalyzer.facts.toString());
		}
	}
	
	public static void initialize() {
		Classifier.lexicon = Lexicon.getLexicon();
		CYKParser.initializeRules();
	}
}
