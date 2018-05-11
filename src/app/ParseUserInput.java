package app;

import nlu.langmodel.cfg.CFGParser;
import nlu.langmodel.cfg.CYKParser;
import nlu.langmodel.cfg.ParseNode;
import nlu.langmodel.cfg.ParseTreePrinter;
import nlu.semantics.SemanticAnalyzer;
import tokenize.Tokenizer;
import tokenize.Lexicon;
import tokenize.Token;

import java.io.File;
import java.util.List;
import java.util.Scanner;

/**
 * A simple command line app that parses sentences from stdin.
 */
public class ParseUserInput {

	public static void main(String[] args) {
		acceptInput();
	}

    /**
     * Gets a line of input from stdin, tokenizes the sentence, parses using a CYKParser and
     * performs trivial semantic analysis if the parse is successful. Entering an empty line or
     * 'q' will stop execution.
     */
	private static void acceptInput() {
		Tokenizer tokenizer = new Tokenizer();
		CFGParser parser = new CYKParser();
		Scanner scanner = new Scanner(System.in);
		while (true) {
			// get a line of input
			String input = scanner.nextLine();
			if (input.isEmpty() || input.equals("q")) {
				break;
			}
			// tokenize the input and print
			List<Token> tokens = tokenizer.tokenize(input);
			printTokens(tokens);

			// parse the tokens and perform simple semantic analysis if successful
			ParseNode parseTree = parser.parse(tokens);
			if (parseTree != null) {
				ParseTreePrinter.printTreeWithTabs(parseTree);
				SemanticAnalyzer.analyze(parseTree);
				System.out.println(SemanticAnalyzer.facts.toString());
			}
		}
	}

	private static void printTokens(List<Token> tokens) {
		for (Token t : tokens) {
			System.out.print("[" + t.toString() + "], ");
		}
		System.out.println();
	}
}
