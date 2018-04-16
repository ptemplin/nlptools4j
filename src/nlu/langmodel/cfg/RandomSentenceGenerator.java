package nlu.langmodel.cfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import tokenize.Lexicon;
import tokenize.Tag;

public class RandomSentenceGenerator {

    private static final int RANDOM_FREQUENCY_FACTOR = 10;

    private static final String CNF_PRODUCTIONS_FILE = CFGParser.CFG_RES_DIR + "CNFProductions";

    // the production rules of the grammar
    private Map<String,List<String>> rules = new HashMap<>();
    // the lexicon to lookup terminal words
    private Lexicon lexicon;

    public RandomSentenceGenerator() {
        initializeRules();
        lexicon = Lexicon.getLexicon();
    }

    public static void main(String[] args) {
        System.out.println(new RandomSentenceGenerator().generateRandomSentence());
    }

    public String generateRandomSentence() {
        StringBuilder sentenceBuilder = new StringBuilder();
        generateRandomSentenceHelper(sentenceBuilder, "S");
        return sentenceBuilder.toString();
    }

    private void generateRandomSentenceHelper(StringBuilder builder, String currentSymbol) {
        List<String> productions = rules.get(currentSymbol);
        String productionToUse = productions.get((int) Math.floor(Math.random() * productions.size()));
        System.out.println(currentSymbol + " " + productionToUse);
        String[] symbols = productionToUse.split(" ");
        if (symbols.length == 1) {
            builder.append(lookupWordByPartOfSpeech(symbols[0]) + " ");
        } else {
            generateRandomSentenceHelper(builder, symbols[0]);
            generateRandomSentenceHelper(builder, symbols[1]);
        }
    }

    private String lookupWordByPartOfSpeech(String pos) {
        final Map<String, Tag> wordToFrequency = lexicon.getCategoryFromPartOfSpeech(pos);
        while (true) {
            int elementNum = (int) Math.floor(Math.random() * wordToFrequency.size());
            int i = 0;
            for (Entry<String, Tag> entry : wordToFrequency.entrySet()) {
                // we've reached the ith element
                if (i == elementNum) {
                    // favour more common words
                    double frequency = entry.getValue().numOccurences;
                    if (Math.random() < (frequency / frequency + RANDOM_FREQUENCY_FACTOR)) {
                        return entry.getKey();
                    } else {
                        break;
                    }
                }
                i++;
            }
        }
    }

    private void initializeRules() {
        File file = new File(CNF_PRODUCTIONS_FILE);
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
                String LHS, RHS;
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
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

}
