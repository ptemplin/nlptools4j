package scripting;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MASCExtractor {
	
	public static final String rootDir = "C:\\Users\\Me\\Downloads\\MASC-3.0.0\\data";
	public static final String writeDstn = "C:\\Projects\\NLPExperiments\\";
	
	public static int numWords = 0;
	public static HashMap<Token,Integer> freqs = new HashMap<>();
	
	public static String[] categories = {
		"CC",
		"CD","DT","EX",
		"FW","IN","JJ",
		"JJR","JJS","LS",
		"MD","NN","NNS",
		"NNP","NNPS","PDT",
		"POS","PP","PRP","PRP$",
		"RB","RBR","RBS",
		"RP","SYM","TO","UH",
		"VB","VBD","VBG",
		"VBN","VBP","VBZ",
		"WDT","WP","WP$",
		"WRB"};
	
	public static void main(String[] args) throws IOException {
		scanDirectory(new File(rootDir));
		printCategoryFrequences();
		saveDataToFile();
	}
	
	public static void saveDataToFile() throws IOException {
		// remap all of the tokens to categories
		HashMap<String,List<Token>> cats = new HashMap<>();
		Arrays.sort(categories);
		for (Map.Entry<Token,Integer> freq : freqs.entrySet()) {
			Token key = freq.getKey();
			if (cats.containsKey(key.tag)) {
				cats.get(key.tag).add(key);
			} else {
				if (Arrays.binarySearch(categories, key.tag) >= 0) {
					cats.put(key.tag, new ArrayList<Token>());
				}
			}
		}
		System.out.println("Number of categories: " + cats.size());
		
		// remap all of the categories to terminals in the CFG (parts of speech)
		HashMap<String,List<Token>> partsOfSpeech = new HashMap<>();
		partsOfSpeech.put("noun",new ArrayList<>());
		//partsOfSpeech.put("pronoun",new ArrayList<>());
		partsOfSpeech.put("propernoun",new ArrayList<>());
		//partsOfSpeech.put("determiner",new ArrayList<>());
		partsOfSpeech.put("verb",new ArrayList<>());
		partsOfSpeech.put("adjective",new ArrayList<>());
		partsOfSpeech.put("adverb",new ArrayList<>());
		//partsOfSpeech.put("preposition",new ArrayList<>());
		//partsOfSpeech.put("wh_word",new ArrayList<>());
		//partsOfSpeech.put("auxillary",new ArrayList<>());
		//partsOfSpeech.put("interjection",new ArrayList<>());
		//partsOfSpeech.put("conjunction",new ArrayList<>());
		
		for (Map.Entry<String,List<Token>> cat : cats.entrySet()) {
			String key = cat.getKey();
			// determine the part of speech from the key
			String partOfSpeech = null;
			switch(key) {
			case "NN":
			case "NNS":
				partOfSpeech = "noun";
				break;
			case "NNP":
			case "NNPS":
				partOfSpeech = "propernoun";
				break;
			case "VB":
			case "VBD":
			case "VBN":
			case "VBP":
			case "VBZ":
				partOfSpeech = "verb";
				break;
			case "RB":
			case "RBR":
			case "RBS":
				partOfSpeech = "adverb";
				break;
			case "JJ":
			case "JJR":
			case "JJS":
				partOfSpeech = "adjective";
				break;
			}
			// if the category was productive
			if (partOfSpeech != null) {
				// add the tokens from the tag to the part of speech
				List<Token> catToks = cats.get(key);
				List<Token> posToks = partsOfSpeech.get(partOfSpeech);
				posToks.addAll(catToks);
			}			
		}
		
		// write each part of speech to a category of the same name
		for (Map.Entry<String,List<Token>> pos : partsOfSpeech.entrySet()) {
			System.out.println("Writing " + pos.getKey() + "...");
			PrintWriter writer = new PrintWriter(writeDstn + pos.getKey() + ".txt");
			for (Token tok : pos.getValue()) {
				writer.println(tok.lexeme + " %" + freqs.get(tok) + " +" + tok.tag);
			}
			writer.close();
		}
		
		// write each Penn tag to a file of the same name
		for (Map.Entry<String,List<Token>> category : cats.entrySet()) {
			PrintWriter writer = new PrintWriter(writeDstn + category.getKey() + ".txt");
			for (Token tok : category.getValue()) {
				writer.println(tok.lexeme + " %" + freqs.get(tok) + " +" + tok.tag);
			}
			writer.close();
		}
	}
	
	/*
	 * Recursively scans a directory for all -penn.xml files and
	 * builds the lexicon
	 */
	private static void scanDirectory(File dir) throws IOException {
		if (dir.isDirectory()) {
			// for every file in the directory
			for (File file : dir.listFiles()) {
				// if it is a directory, search it
				if (file.isDirectory()) {
					scanDirectory(file);
				} else if (file.getName().contains("-penn.xml")){
					// get the tags from the file
					System.out.println("Scanning " + file.getAbsolutePath() + "...");
					List<Token> tags = getTags(file);
					// update the number of total words
					numWords += tags.size();
					// hash the words in the frequency map
					for (Token tok : tags) {
						if (freqs.containsKey(tok)) {
							freqs.put(tok,freqs.get(tok)+1);
						} else {
							freqs.put(tok,1);
						}
					}
					System.out.println("Total words: " + numWords);
					System.out.println("Unique words: " + freqs.size());
				}
			}
		}
	}
	
	private static List<Token> getTags(File textFile) throws IOException {
		List<Token> tokens = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile)));
		while(true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			// scans the line and extracts info
			if (line.contains("targets=")) {
				int dataNum = 0;
				String tag, lexeme;
				tag = lexeme = null;
				while (dataNum < 2) {
					line = reader.readLine();
					// the tag
					if (line.contains("=\"msd")) {
						String[] parts = line.split("\"");
						tag = parts[3];
						dataNum++;
					} else if (line.contains("=\"string")) {
						String[] parts = line.split("\"");
						lexeme = parts[3];
						dataNum++;
					}
				}
				if (tag == null || lexeme == null) {
					continue;
				}
				if (!(tag.equals("NNP") || tag.equals("NNPS"))) {
					lexeme = lexeme.toLowerCase();
				}
				Token token = new Token(lexeme,tag);
				tokens.add(token);
			}
		}
		return tokens;
	}
	
	private static void printCategoryFrequences() {
		HashMap<String,Integer> catFreqs = new HashMap<>();
		for (Map.Entry<Token,Integer> freq : freqs.entrySet()) {
			Token key = freq.getKey();
			//System.out.println(key.lexeme + " " + key.tag + " " + freq.getValue());
			if (catFreqs.containsKey(key.tag)) {
				catFreqs.put(key.tag, catFreqs.get(key.tag)+1);
			} else {
				catFreqs.put(key.tag, 1);
			}
		}
		System.out.println("**** Categories:");
		for (Map.Entry<String,Integer> freq : catFreqs.entrySet()) {
			System.out.println(freq.getKey() + " " + freq.getValue());
		}
	}
	
	private static class Token {
		public String lexeme;
		public String tag;
		public Token(String lexeme, String tag) {
			this.lexeme = lexeme;
			this.tag = tag;
		}
		@Override
		public int hashCode() {
			return lexeme.hashCode() + tag.hashCode();
		}
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Token)) {
				return false;
			}
			Token t = (Token) o;
			if (this.lexeme.equals(t.lexeme) && this.tag.equals(t.tag)) {
				return true;
			}
			return false;
		}
	}

}
