package nlu.langmodel.cfg;


import app.Constants;
import tokenize.Token;

import java.util.List;

/**
 * Interface for parsers of Context-Free Grammars.
 */
public interface CFGParser {

    String CFG_RES_DIR = Constants.RES_DIR + "/cfg/";

    /**
     * Produces a parse tree from the tokens using the configured CFG productions and symbols.
     *
     * @param tokens of the sentence to parse
     * @return the root node of a parse tree for the tokens
     */
    ParseNode parse(List<Token> tokens);

}
