package tokenize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Lexicon {
	
	// the root file path
	private static final String fileRoot = "C:/Projects/nlptools4j/res/lexical/";

	// the singleton
	private static Lexicon lexicon;
	
	// parts of speech
	public HashMap<String, Tag> regNouns = new HashMap<>();
	public HashMap<String,Tag> pronouns = new HashMap<>();
	public HashMap<String,Tag> properNouns = new HashMap<>();
	public HashMap<String,Tag> verbs = new HashMap<>();
	public HashMap<String,Tag> gerundVerbs = new HashMap<>();
	public HashMap<String,Tag> determiners = new HashMap<>();
	public HashMap<String,Tag> prepositions = new HashMap<>();
	public HashMap<String,Tag> auxiliaries = new HashMap<>();
	public HashMap<String,Tag> whWords = new HashMap<>();
	public HashMap<String,Tag> adjectives = new HashMap<>();
	public HashMap<String,Tag> adverbs = new HashMap<>();
	public HashMap<String,Tag> conjunctions = new HashMap<>();
	public HashMap<String,Tag> interjections = new HashMap<>();
	public HashMap<String,Tag> endPunctuation = new HashMap<>();
	
	// mapping of the files to the categories
	public HashMap<String,HashMap<String,Tag>> filenameToCat = new HashMap<>();

	private Lexicon(){
		initialize();
	}
	
	public static Lexicon getLexicon() {
		if (lexicon == null) {
			lexicon = new Lexicon();
		}
		return lexicon;
	}
	
	private void initialize() {
		// map the categories to the file they reside in
		filenameToCat.put("noun.txt",regNouns);
		filenameToCat.put("pronoun.txt",pronouns);
		filenameToCat.put("propernoun.txt",properNouns);
		filenameToCat.put("verb.txt",verbs);
		filenameToCat.put("VBG.txt",gerundVerbs);
		filenameToCat.put("determiner.txt",determiners);
		filenameToCat.put("preposition.txt",prepositions);
		filenameToCat.put("auxiliary.txt",auxiliaries);
		filenameToCat.put("whword.txt",whWords);
		filenameToCat.put("adjective.txt",adjectives);
		filenameToCat.put("adverb.txt",adverbs);
		filenameToCat.put("conjunction.txt",conjunctions);
		filenameToCat.put("interjection.txt",interjections);
		filenameToCat.put("endpunctuation.txt",endPunctuation);
		
		// initialize lexicon for all parts of speech
		for (Map.Entry<String,HashMap<String,Tag>> entry : filenameToCat.entrySet()) {
			HashMap<String,Tag> category = entry.getValue();
			File file = new File(fileRoot + entry.getKey());
			Scanner scanner = null;
			try {
				scanner = new Scanner(new BufferedReader(new FileReader(file)));
				// get the category entries
				while(scanner.hasNextLine()) {
					String line = scanner.nextLine();
					// skip the line if it's empty
					if (line.isEmpty()) {
						continue;
					}
					String[] parts = line.split(" ");
					String lexeme = parts[0];
					String morpho = null;
					Integer freq = null;
					// get the other parts of the entry
					for (int i = 1; i < parts.length; i++) {
						if (parts[i].isEmpty()) {
							break;
						}
						// scan the item in the entry
						char first = parts[i].charAt(0);
						if (first == '%') {
							// the item is the frequency
							freq = Integer.parseInt(parts[i].substring(1));
						} else if (first == '+') {
							// the item is the morphology
							morpho = parts[i];
						} else {
							// must be a multiword, so update the lexeme
							lexeme = lexeme + " " + parts[i];
						}
					}
					// create a new tag and add it to the category
					if (freq == null) {
						freq = 0;
					}
					Tag tag = new Tag(getPartOfSpeechFromMap(category),morpho,freq);
					category.put(lexeme, tag);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			if (scanner != null) {
				scanner.close();
			}
		}
	}
	
//	private void doNouns() {
//		File file = new File(fileRoot + "noun.txt");
//		Scanner scanner = null;
//		try {
//			scanner = new Scanner(new BufferedReader(new FileReader(file)));
//			String line;
//			// get the regular nouns
//			// skip the first line
//			scanner.nextLine();
//			while(true) {
//				line = scanner.nextLine();
//				if (line.isEmpty()) {
//					break;
//				}
//				String[] parts = line.split(" ");
//				regNouns.add(parts[0]);
//			}
//			// get the irregular singular nouns
//			// skip the first line
//			scanner.nextLine();
//			while(true) {
//				line = scanner.nextLine();
//				if (line.isEmpty()) {
//					break;
//				}
//				regNouns.add(line);
//			}
//			// get the irregular plural nouns
//			// skip the first line
//			scanner.nextLine();
//			while(true) {
//				line = scanner.nextLine();
//				if (line.isEmpty()) {
//					break;
//				}
//				String[] parts = line.split(":");
//				regNouns.add(parts[0]);
//			}
//			// get the irregular singular nouns
//			// skip the first line
//			scanner.nextLine();
//			while(true) {
//				line = scanner.nextLine();
//				if (line.isEmpty()) {
//					break;
//				}
//				determiners.add(line);
//			}
//			// get the pronouns
//			// skip the first line
//			scanner.nextLine();
//			while(scanner.hasNextLine()) {
//				line = scanner.nextLine();
//				pronouns.add(line);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void doVerbs() {
//		File file = new File(fileRoot + "verbs.txt");
//		Scanner scanner = null;
//		try {
//			scanner = new Scanner(new BufferedReader(new FileReader(file)));
//			String line;
//			// get the verbs
//			while(scanner.hasNextLine()) {
//				line = scanner.nextLine();
//				String[] parts = line.split(" ");
//				verbs.add(parts[0]);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void doAdjectives() {
//		File file = new File(fileRoot + "adjectives.txt");
//		Scanner scanner = null;
//		try {
//			scanner = new Scanner(new BufferedReader(new FileReader(file)));
//			String line;
//			// get the verbs
//			while(scanner.hasNextLine()) {
//				line = scanner.nextLine();
//				String[] parts = line.split(" ");
//				adjectives.add(parts[0]);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void doAdverbs() {
//		File file = new File(fileRoot + "adverbs.txt");
//		Scanner scanner = null;
//		try {
//			scanner = new Scanner(new BufferedReader(new FileReader(file)));
//			String line;
//			// get the verbs
//			while(scanner.hasNextLine()) {
//				line = scanner.nextLine();
//				adverbs.add(line);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void doDeterminers() {
//		determiners.add("a");
//		determiners.add("an");
//		determiners.add("the");
//	}
//	
//	private void doPrepositions() {
//		prepositions.add("on");
//		prepositions.add("up");
//		prepositions.add("down");
//		prepositions.add("of");
//		prepositions.add("to");
//		prepositions.add("from");
//		prepositions.add("over");
//		prepositions.add("with");
//		prepositions.add("for");
//		prepositions.add("when");
//		prepositions.add("at");
//	}
//	
//	private void doAuxillaries() {
//		auxillaries.add("can");
//		auxillaries.add("do");
//		auxillaries.add("did");
//		auxillaries.add("does");
//		auxillaries.add("want");
//		auxillaries.add("wants");
//		auxillaries.add("would");
//		auxillaries.add("should");
//	}
//	
//	private void doWHWords() {
//		whWords.add("who");
//		whWords.add("what");
//		whWords.add("which");
//		whWords.add("why");
//		whWords.add("when");
//		whWords.add("where");
//		whWords.add("how");
//	}
//	
//	private void doConjunctions() {
//		conjunctions.add("and");
//		conjunctions.add("or");
//		conjunctions.add("but");
//		conjunctions.add("because");
//		conjunctions.add("if");
//		conjunctions.add("then");
//	}
//	
//	private void doPunctuation() {
//		punctuation.add(".");
//		punctuation.add("!");
//		punctuation.add("?");
//		punctuation.add("'");
//	}
	
	private PartOfSpeech getPartOfSpeechFromMap(HashMap<String,Tag> map) {
		if (map == regNouns) {
			return PartOfSpeech.NOUN;
		} else if (map == pronouns) {
			return PartOfSpeech.PRONOUN;
		} else if (map == properNouns) {
			return PartOfSpeech.PROPER_NOUN;
		} else if (map == verbs) {
			return PartOfSpeech.VERB;
		} else if (map == gerundVerbs) {
			return PartOfSpeech.GERUNDV;
		} else if (map == determiners) {
			return PartOfSpeech.DETERMINER;
		} else if (map == prepositions) {
			return PartOfSpeech.PREPOSITION;
		} else if (map == auxiliaries) {
			return PartOfSpeech.AUXILLARY;
		} else if (map == whWords) {
			return PartOfSpeech.WH_WORD;
		} else if (map == adjectives) {
			return PartOfSpeech.ADJECTIVE;
		} else if (map == adverbs) {
			return PartOfSpeech.ADVERB;
		} else if (map == conjunctions) {
			return PartOfSpeech.CONJUNCTION;
		} else if (map == interjections) {
			return PartOfSpeech.INTERJECTION;
		} else if (map == endPunctuation) {
			return PartOfSpeech.PUNCTUATION;
		} else {
			return null;
		}
	}
	
}
