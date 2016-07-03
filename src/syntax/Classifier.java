package syntax;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/*
 * The classifier class performs the first 1.5 steps in the
 * analysis of English text. It will take a string of text (a sentence)
 * and return the part-of-speech associated with each word. The additional
 * 0.5 step comes from multi word grouping, as in the case of numbers, compound
 * words, etc. (eg. 42, forty-two, lift up)
 */
public class Classifier {
	
	public static Lexicon lexicon;
	
	public static List<Token> classifySentence(String sentence) {
		// if the lexicon isn't initialized, do that first
		if (lexicon == null) {
			lexicon = Lexicon.getLexicon();
		}		
		return tokenize(sentence);
	}
	
	private static List<Token> tokenize(String sentence) {
		List<Token> tokens = new ArrayList<>();
		// split the sentence into words by space delimiting
		String[] words = sentence.split(" ");
		// handle starting capital and end punctuation
		preprocessSentence(words);
		// for each word group, build a token
		for (String word : words) {
			tokens.add(buildToken(word));
		}
		return tokens;
	}
	
	private static Token buildToken(String s) {
		Token tok = new Token(s);
		for (Map.Entry<String, HashMap<String,Tag>> entry : lexicon.filenameToCat.entrySet()) {
			HashMap<String,Tag> category = entry.getValue();
			if (category.containsKey(s)) {
				tok.addTag(category.get(s));
			}
		}
		
		// backup in the case of not found
		if (tok.getTags().isEmpty()) {
			if (s.contains("'s")) {
				tok.addTag(new Tag(PartOfSpeech.DETERMINER,"DT",1));
			}
		}
		// if still empty,
		if (tok.getTags().isEmpty()) {
			System.out.println("Unknown word: " + tok.getLexeme());
			System.out.println("Couldn't find a class, assigning noun");
			tok.addTag(new Tag(PartOfSpeech.NOUN,"NN",1));
		}
		return tok;
	}
	
	private static void preprocessSentence(String[] words) {
		if (words != null && words.length > 0) {
			// first word capitalization heuristic
			if (words[0].equals("I")) {
				// leave as is
			} else {
				// bring to lowercase
				words[0] = words[0].toLowerCase();
			}
			// strip end punctuation
			String lastWord = words[words.length-1];
			if (lexicon.endPunctuation.containsKey(lastWord.substring(lastWord.length()-1))) {
				words[words.length-1] = lastWord.substring(0, lastWord.length()-1);
			}
			
			// remove any commas
			for (int i = 0; i < words.length; i++) {
				if (words[i].contains(",")) {
					// at the start
					if (words[i].charAt(0) == ',') {
						words[i] = words[i].substring(1);
					} else if (words[i].charAt(words[i].length()-1) == ',') {
						words[i] = words[i].substring(0,words[i].length()-1);
					} else {
						// TODO
					}
				}
			}
		}
	}
	
}
