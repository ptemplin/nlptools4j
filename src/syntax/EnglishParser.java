package syntax;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 * The EnglishParser class represents a parser for the structures
 * of the English language. It takes as input a sentence
 * of English words and outputs the parse tree,
 * consisting of the appropriate production rules for the sentence
 */
public class EnglishParser {
	
	// the production rules of the grammar
	public static Map<String,List<String>> rules = new HashMap<>();
	// the terminals of the grammar
	public static Set<String> terminals = new HashSet<>();
	// the nonterminals in the grammar
	public static List<String> nonTerminals = new ArrayList<>();

	public static ParseNode parse(List<Token> tokens) {
		// if the rules haven't been intialized, do that first
		if (rules.isEmpty()) {
			// intialize the production rules of the grammar
			initializeRules();
		}
		
		Token[] tokArr = new Token[tokens.size()];
		for (int i = 0; i < tokens.size(); i++) {
			tokArr[i] = tokens.get(i);
		}
		ParseNode root = parseCYK(tokArr);
		if (root == null) {
			System.out.println("Unsuccessful parse");
			return null;
		}
		List<String> derivation = new ArrayList<>();
		buildDerivation(root,derivation,null);
		for (String s : derivation) {
			System.out.println(s);
		}
		return root;
	}
	
	// input is word classes, words is lexeme
	public static ParseNode parseCYK(Token[] tokens) {
		int inputLen = tokens.length;
		int numNonTerminals = nonTerminals.size();
		ParseNode[][][] parseMatrix = new ParseNode[inputLen][inputLen][numNonTerminals];
		
		// build the base nodes for terminals
		for (int i = 0; i < inputLen; ++i) {
			Token curToken = tokens[i];
			String curInput = curToken.getSingleTag().partOfSpeech.toString();
			for (Map.Entry<String,List<String>> rule : rules.entrySet()) {
				// if the non-terminal on the RHS can produce the current word,
				// add a ParseNode to the matrix
				if (rule.getValue().contains(curInput)) {
					int nonTerIndex = getIndexOfNonTerminal(rule.getKey());
					ParseNode leaf = new ParseNode();
					leaf.rule = rule.getKey() + " " + curInput;
					// create the "surface leaf" as a child, which maps
					// the word class to the word
					ParseNode leafChild = new ParseNode();
					leafChild.rule = curInput + " " + curToken.getLexeme();
					leafChild.token = curToken;
					leaf.children.add(leafChild);
					parseMatrix[0][i][nonTerIndex] = leaf;
				}
			}
		}
		
		// fill in the rest of the matrix using production rules
		for (int i = 2; i <= inputLen; ++i) {
			for (int j = 1; j <=inputLen-i+1;++j) {
				for (int k = 1; k <= i-1; ++k) {
					for (Map.Entry<String, List<String>> lhs : rules.entrySet()) {
						for (String rhs : lhs.getValue()) {
							// only process rules that go to 2 non terminals A -> B C
							String[] toks = rhs.split(" ");
							if (toks.length == 2) {
								int indexB = getIndexOfNonTerminal(toks[0]);
								int indexC = getIndexOfNonTerminal(toks[1]);
								ParseNode nodeB = parseMatrix[k-1][j-1][indexB];
								ParseNode nodeC = parseMatrix[i-k-1][j+k-1][indexC];
								if (nodeB != null && nodeC != null) {
									int indexA = getIndexOfNonTerminal(lhs.getKey());
									// build the node and add it to the matrix
									ParseNode internal = new ParseNode();
									internal.rule = lhs.getKey() + " " + rhs;
									internal.children.add(nodeB);
									internal.children.add(nodeC);
									parseMatrix[i-1][j-1][indexA] = internal;
								}
							}
						}
					}
				}
			}
		}
		
		//printMatrix(parseMatrix,inputLen,inputLen,numNonTerminals);
		// finally, check to see the the start symbol S can be derived
		ParseNode root = parseMatrix[inputLen-1][0][0];
		return root;
	}
	
	private static int getIndexOfNonTerminal(String s) {
		for (int i = 0; i < nonTerminals.size(); ++i) {
			if (nonTerminals.get(i).equals(s)) {
				return i;
			}
		}
		return 0;
	}
	
	private static void printMatrix(ParseNode[][][] matrix, int i, int j, int k) {
		for (int a = 0; a < i; a++) {
			for (int b = 0; b < j; b++) {
				int count = 0;
				System.out.print("[");
				for (int c = 0; c < k; ++c) {
					if (matrix[a][b][c] != null) {
						System.out.print(nonTerminals.get(c) + " ");
						count++;
					}
				}
				if (count == 0) {
					System.out.print("* ");
				}
				System.out.print("] ");
			}
			System.out.println();
		}
	}
	
	private static void buildDerivation(ParseNode current, List<String> derivation, List<String> sentence) {
		// base case of terminal
		if (current.children.isEmpty()) {
			//derivation.add(current.rule);
			if (current.rule == null) {
				System.out.println("ERR: null rule");
				return;
			}
			String[] tokens = current.rule.split(" ");
			if (sentence != null) {
				if (tokens.length == 2) {
					sentence.add(tokens[1]);
				}
			} else {
				derivation.add(current.rule);
			}
		} else {
			// do a preorder traversal of the children
			derivation.add(current.rule);
			if (current.rule.equals("NP Det Nominal")) {
				//applyAdjustArticleRule(current);
			}
			for (ParseNode node : current.children) {
				buildDerivation(node,derivation,sentence);
			}
		}
	}
	
	public static void initializeRules() {
		File file = new File("C:\\Projects\\NLPExperiments\\CNFProductions_v2");
		Scanner scanner = null;
		try {
			scanner = new Scanner(new BufferedReader(new FileReader(file)));
			String line;
			// get the verbs
			while(scanner.hasNextLine()) {
				line = scanner.nextLine();
				// ignore comment lines
				if (line.isEmpty() || line.charAt(0) == '/') {
					continue;
				}
				int space = line.indexOf(' ');
				String LHS = null;
				String RHS = null;
				if (space == -1) {
					LHS = line;
					RHS = "";
				} else {
					LHS = line.substring(0,space);
					RHS = line.substring(space+1);
				}
				if (rules.containsKey(LHS)) {
					rules.get(LHS).add(RHS);
				} else {
					List<String> RHSs = new ArrayList<>();
					RHSs.add(RHS);
					rules.put(LHS,RHSs);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner.close();
		file = new File("C:\\Projects\\NLPExperiments\\terminals");
		scanner = null;
		try {
			scanner = new Scanner(new BufferedReader(new FileReader(file)));
			String line;
			// get the verbs
			while(scanner.hasNextLine()) {
				line = scanner.nextLine();
				terminals.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner.close();
		file = new File("C:\\Projects\\NLPExperiments\\nonterminals");
		scanner = null;
		try {
			scanner = new Scanner(new BufferedReader(new FileReader(file)));
			String line;
			// get the nonterminals
			while(scanner.hasNextLine()) {
				line = scanner.nextLine();
				nonTerminals.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
