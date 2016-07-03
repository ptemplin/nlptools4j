package main;
import java.util.List;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.io.*;

public class Parser {
	
	public static enum WORD_TYPE {
		NOUN("N"),
		VERB("V"),
		ADJECTIVE("AJ"),
		ADVERB("AD"),
		PREPOSITION("PR"),
		CONJUCTION("CJ"),
		INTERJECTION("IJ"),
		UNKNOWN("U");
		
		private final String letter;
		
		private WORD_TYPE(String letter) {
			this.letter = letter;
		}
		
		public String toString() {
			return letter;
		}
		
	}
	
	public static final String FILE_PATH = "C:\\Projects\\NLPExperiments\\";
	public static final String NOUN_FILE = "nouns.txt";
	public static final String ADJECTIVE_FILE = "adjectives.txt";
	public static final String VERB_FILE = "verbs.txt";
	
	public HashSet<String> nouns;
	public HashSet<String> adjectives;
	public HashSet<String> verbs;
	
	public Parser() throws IOException {
		initializeVocab();
	}
	
	private void initializeVocab() throws IOException {
		// initialize the sets
		nouns = new HashSet<>();
		adjectives = new HashSet<>();
		verbs = new HashSet<>();
		
		// do nouns
		File nounFile = new File(FILE_PATH+NOUN_FILE);
		FileReader reader = new FileReader(nounFile);
		Scanner scan = new Scanner(reader);
		while (scan.hasNextLine()) {
			nouns.add(scan.nextLine());
		}
		if (reader != null) {
			reader.close();
		}
		
		// adjectives
		File adjectiveFile = new File(FILE_PATH+ADJECTIVE_FILE);
		reader = new FileReader(adjectiveFile);
		scan = new Scanner(reader);
		while(scan.hasNextLine()) {
			adjectives.add(scan.nextLine());
		}
		if (reader != null) {
			reader.close();
		}
		
		// verbs
		File verbFile = new File(FILE_PATH+VERB_FILE);
		reader = new FileReader(verbFile);
		scan = new Scanner(reader);
		while(scan.hasNextLine()) {
			verbs.add(scan.nextLine());
		}
		if (reader != null) {
			reader.close();
		}
	}

	public void processInput(String input) {
		String[] words = input.split(" ");
		List<Token> tokens = new ArrayList<>();
		HashSet<String> unknownTokens = new HashSet<>();
		for (String s : words) {
			tokens.add(getToken(s, unknownTokens));
		}
		
		// if is a statement, print classes
		if (!input.contains("?")) {
			handleStatement(tokens, unknownTokens);
		} else {
			// else, respond to the question asked
			handleQuestion(tokens);
		}
		
		
	}

	private void handleStatement(List<Token> tokens, HashSet<String> unknowns) {
		// print the classification, mainly for debugging
		printClassification(tokens);

		// handle any unknown words encountered
		Scanner scanner = new Scanner(System.in);
		for (String s : unknowns) {
			WORD_TYPE type = null;
			System.out.println("What kind of word is: " + s);

			String answer = scanner.nextLine();
			switch(answer) {
			case "noun":
			case "NOUN":
			case "N":
				type = WORD_TYPE.NOUN;
				break;
			case "verb":
			case "VERB":
			case "V":
				type = WORD_TYPE.VERB;
				break;
			case "ADJECTIVE":
			case "adjective":
			case "AJ":
				type = WORD_TYPE.ADJECTIVE;
				break;
			}

			// update the vocabulary
			updateVocabulary(s, type);
		}
		System.out.println("Vocabulary updated\n");
		
		// add the new knowledge to the graph
		
		
	}
	
	public void handleQuestion(List<Token> tokens) {
		
	}
	
	private void updateVocabulary(String word, WORD_TYPE type) {
		File file = null;
		if (type == null) {
			return;
		}
		switch(type) {
		case NOUN:
			file = new File(FILE_PATH+NOUN_FILE);
			break;
		case VERB:
			file = new File(FILE_PATH+VERB_FILE);
			break;
		case ADJECTIVE:
			file = new File(FILE_PATH+ADJECTIVE_FILE);
			break;
		}
		
		// if the type doesn't match, return
		if (file == null) {
			return;
		}
		
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		    out.println('\n' + word);
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
			e.printStackTrace();
		}
		
		if (out != null) {
			out.close();
		}
				
	}
	
	private Token getToken(String input, HashSet<String> unknowns) {
		Token token = new Token();
		token.value = input;
		if (nouns.contains(input)) {
			token.type = WORD_TYPE.NOUN;
		} else if (adjectives.contains(input)) {
			token.type = WORD_TYPE.ADJECTIVE;
		} else if (verbs.contains(input)) {
			token.type = WORD_TYPE.VERB;
		} else {
			unknowns.add(input);
			token.type = WORD_TYPE.UNKNOWN;
		}
		return token;
	}
	
	private void printClassification(List<Token> tokens) {
		for (Token token : tokens) {
			System.out.print(token.value + " ");
		}
		System.out.println();
		int i = 0;
		StringBuilder builder = new StringBuilder();
		for (Token token : tokens) {
			builder.append(token.type.toString());
			for (int j = token.type.toString().length(); j < token.value.length() + 1; j++) {
				builder.append(' ');
			}
			++i;
		}
		System.out.println(builder.toString() + '\n');
	}
	
}
