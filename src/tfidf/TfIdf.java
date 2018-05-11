package tfidf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Computes the Tf-Idf for a collection of documents. Takes a directory as input and builds the mappings from all text
 * files in the directory.
 */
public class TfIdf {

    public Map<String, Integer> totalCounts = new HashMap<>();
    public Map<String, Map<String, Integer>> documentToTermFrequency = new HashMap<>();

    public float[][] compressedValues;
    public String[] documents;
    public Map<String, Integer> termsToIds;

    public double get(String document, String term) {
        if (documentToTermFrequency.containsKey(document)) {
            double termFreq = documentToTermFrequency.get(document).getOrDefault(term, 0);
            if (termFreq != 0) {
                return termFreq / totalCounts.get(term);
            }
        }
        return 0;
    }

    public String[] retrieve3BestDocs(String term) {
        int first = 0, second = 0, third = 0;
        float firstVal = 0, secondVal = 0, thirdVal = 0;
        int termId = termsToIds.get(term);
        for (int i = 0; i < compressedValues.length; i++) {
            float val = compressedValues[i][termId];
            if (val > firstVal) {
                thirdVal = secondVal;
                secondVal = firstVal;
                firstVal = val;
                third = second;
                second = first;
                first = i;
            } else if (val > secondVal) {
                thirdVal = secondVal;
                secondVal = val;
                third = second;
                second = i;
            } else if (val > thirdVal) {
                thirdVal = val;
                third = i;
            }
        }
        System.out.println(firstVal + "," + secondVal + "," + thirdVal);
        return new String[] {documents[first], documents[second], documents[third]};
    }

    public void compute(File directory) throws IOException {
        for (File document : directory.listFiles()) {
            processFile(document);
        }
        compress();
    }

    private void processFile(File document) throws IOException {
        if (!documentToTermFrequency.containsKey(document.getName())) {
            documentToTermFrequency.put(document.getName(), new HashMap<>());
        }

        Map<String, Integer> termFreqInDoc = documentToTermFrequency.get(document.getName());

        Scanner scanner = new Scanner(new FileInputStream(document));
        while (scanner.hasNext()) {
            for (String token : getTerms(scanner.next())) {
                if (!token.isEmpty()) {
                    inc(totalCounts, token);
                    inc(termFreqInDoc, token);
                }
            }
        }
    }

    private void compress() {
        compressedValues = new float[documentToTermFrequency.size()][totalCounts.size()];
        documents = new String[documentToTermFrequency.size()];
        termsToIds = new HashMap<>(totalCounts.size());
        int nextTermId = 0;
        int docId = 0;
        for (Map.Entry<String, Map<String, Integer>> documentFreqs : documentToTermFrequency.entrySet()) {
            documents[docId] = documentFreqs.getKey();
            for (Map.Entry<String, Integer> termFreqs : documentFreqs.getValue().entrySet()) {
                int termId;
                if (!termsToIds.containsKey(termFreqs.getKey())) {
                    termId = nextTermId;
                    nextTermId++;
                    termsToIds.put(termFreqs.getKey(), termId);
                } else {
                    termId = termsToIds.get(termFreqs.getKey());
                }
                float freqInDoc = termFreqs.getValue();
                compressedValues[docId][termId] = freqInDoc == 0 ? 0 : freqInDoc / totalCounts.get(termFreqs.getKey());
            }
            docId++;
        }
    }

    private String[] getTerms(String token) {
        return token.split("[.,!?/\"()~<>+]");
    }

    private void inc(Map<String, Integer> counts, String term) {
        if (!counts.containsKey(term)) {
            counts.put(term, 1);
        } else {
            counts.put(term, counts.get(term) + 1);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        //System.out.println("What directory do you want to use?");
        String directory = "C:/Projects/nlptools4j/res/tfidf";

        TfIdf map = new TfIdf();
        map.compute(new File(directory));

        while (true) {
            String term = scanner.next();
            String[] results = map.retrieve3BestDocs(term);
            System.out.println("1: " + results[0]);
            System.out.println("2: " + results[1]);
            System.out.println("3: " + results[2]);
        }
    }

}
