package syntax;

import java.util.List;
import java.util.Scanner;

public class Al {
	public static void main(String[] args) {
		initialize();
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		List<Token> tokens = Classifier.classifySentence(input);
		for (Token t : tokens) {
			System.out.print("[" + t.toString() + "], ");
		}
		System.out.println();
		
		ParseNode parseTree = EnglishParser.parse(tokens);
		if (parseTree != null) {
			SemanticAnalyzer.analyze(parseTree);
			System.out.println(SemanticAnalyzer.facts.toString());
		}
	}
	
	public static void initialize() {
		Classifier.lexicon = Lexicon.getLexicon();
		EnglishParser.initializeRules();
	}
}
