package nlu.langmodel.cfg;

import tokenize.Token;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a parse node in a CFG parse tree.
 */
public class ParseNode {

	// the children of this node in the parse tree
	public List<ParseNode> children = new ArrayList<>();
	// the tokens making up the rule of this node
	public String rule;
	// the token for leaf nodes
	public Token token;
	
	// only use for terminals
	public String getTerminalWord() {
		if (rule != null) {
			String[] tokens = rule.split(" ");
			return tokens[1];
		}
		return "";
	}
	
	public List<String> getTokens() {
		return Arrays.asList(rule.split(" "));
	}
	
}
