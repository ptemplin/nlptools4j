package morpho;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static app.Constants.RES_DIR;

/*
 * Morphological parser for nouns of the English language. It takes as input a sentence of English
 * words and outputs the morphological classification, consisting of the stem and appropriate
 * affixes, for each word in the sentence (one per line). 
 */
public class MorphoParser {

	private static final String NOUNS_FILE = RES_DIR + "/lexical/nouns.txt";
	
	// end punctuation symbols in English
	public static char[] punctuation = {'.','!','?'};
	// noun classes
	public static Set<String> regNouns = new HashSet<>();
	public static Set<String> nonRegSgNouns = new HashSet<>();
	public static Map<String,String> nonRegPlNouns = new HashMap<>();
	
	public static void main(String[] args) {
		// initialize the lexicon
		initializeLexicon();
		
		Scanner scan = new Scanner(System.in);
		String inputLine = scan.nextLine();
		// strip punctuation if it's there, for now
		if (isPunctuation(inputLine.charAt(inputLine.length()-1))) {
			inputLine = inputLine.substring(0, inputLine.length()-1);
		}
		// words is all tokens in line, without punctuation
		String[] words = inputLine.split(" ");
		for (String s : words) {
			System.out.println(getClass(s));
		}
	}
	
	public static String getClass(String surface) {
		StringBuilder lexical = new StringBuilder();
		String stem = null;
		boolean accepted = false;
		for (int i = 1; i <= surface.length(); i++) {
			stem = surface.substring(0,i);
			if (isStem(stem)) {
				String affixes = getAffixes(surface,i);
				if (affixes != null) {
					accepted = true;
					lexical.append(stem);
					lexical.append(' ');
					lexical.append(affixes);
				}
			}
		}
		
		// if not an accepted noun, return NaN
		if (!accepted) {
			return "NaN";
		}
		return lexical.toString();
	}
	
	private static String getAffixes(String surface, int start) {
		int length = surface.length();
		if (start == length) {
			return "+N +SG";
		} else if (start + 1 == length && surface.charAt(start) == 's') {
			return "+N +PL";
		} else if (start + 2 == length && surface.charAt(start) == 'e' && surface.charAt(start+1) == 's') {
			char prev = surface.charAt(length-1);
			switch(prev) {
			case 's':
			case 'z':
			case 'x':
				return "+N +PL";
			default:
				return null;
			}
		} else {
			return null;
		}
	}
	
	/*
	 * True if symbol is punctuation, false otherwise
	 */
	private static boolean isPunctuation(char c) {
		boolean found = false;
		for (char ch : punctuation) {
			if (ch == c) {
				found = true;
			}
		}
		return found;
	}
	
	private static boolean isStem(String stem) {
		if (regNouns.contains(stem)) {
			return true;
		}
		return false;
	}
	
	private static void initializeLexicon() {
		File file = new File(NOUNS_FILE);
		Scanner scanner = null;
		try {
			scanner = new Scanner(new BufferedReader(new FileReader(file)));
			String line;
			// get the regular nouns
			// skip the first line
			scanner.nextLine();
			while(true) {
				line = scanner.nextLine();
				if (line.isEmpty()) {
					break;
				}
				regNouns.add(line);
			}
			// get the irregular singular nouns
			// skip the first line
			scanner.nextLine();
			while(true) {
				line = scanner.nextLine();
				if (line.isEmpty()) {
					break;
				}
				nonRegSgNouns.add(line);
			}
			// get the irregular plural nouns
			// skip the first line
			scanner.nextLine();
			while(scanner.hasNextLine()) {
				line = scanner.nextLine();
				String[] parts = line.split(":");
				nonRegPlNouns.put(parts[0],parts[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
