package tokenize;

import app.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Lexicon {
	
	// the root file path
	private static final String fileRoot = Constants.RES_DIR + "/lexical/";

	// the singleton
	private static Lexicon lexicon;
	
	// parts of speech
	private HashMap<String, Tag> regNouns = new HashMap<>();
	private HashMap<String,Tag> pronouns = new HashMap<>();
	private HashMap<String,Tag> properNouns = new HashMap<>();
	private HashMap<String,Tag> verbs = new HashMap<>();
	private HashMap<String,Tag> gerundVerbs = new HashMap<>();
	private HashMap<String,Tag> determiners = new HashMap<>();
	private HashMap<String,Tag> prepositions = new HashMap<>();
	private HashMap<String,Tag> auxiliaries = new HashMap<>();
	private HashMap<String,Tag> whWords = new HashMap<>();
	private HashMap<String,Tag> adjectives = new HashMap<>();
	private HashMap<String,Tag> adverbs = new HashMap<>();
	private HashMap<String,Tag> conjunctions = new HashMap<>();
	private HashMap<String,Tag> interjections = new HashMap<>();
	private HashMap<String,Tag> endPunctuation = new HashMap<>();
	
	// mapping of the files to the categories
	private HashMap<String,HashMap<String,Tag>> filenameToCat = new HashMap<>();

	private Lexicon(){
		initialize();
	}
	
	public static Lexicon getLexicon() {
		if (lexicon == null) {
			lexicon = new Lexicon();
		}
		return lexicon;
	}

	public Set<Map.Entry<String, HashMap<String, Tag>>> getCategoryMaps() {
		return filenameToCat.entrySet();
	}

	public boolean isEndPunctuation(String s) {
		return endPunctuation.containsKey(s);
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
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}
		}
	}
	
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
