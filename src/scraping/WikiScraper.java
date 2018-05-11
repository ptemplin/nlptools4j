package scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class WikiScraper {

    private static final String STORAGE_DIR = "C:/Projects/nlptools4j/res/tfidf/";

    private void fetchDocument(String document) throws IOException {
        System.out.println(">> Retrieving HTML At: http://en.wikipedia.org/wiki/"+document+"...");
        PrintWriter writer = new PrintWriter(new File(STORAGE_DIR + document));
        try {
            Document doc = Jsoup.connect("http://en.wikipedia.org/wiki/"+document).get();
            Elements title = doc.select("H1");
            Elements tag = doc.select("p");
            System.out.println("");
            System.out.println(title.text());
            for(Element e: tag) {
                System.out.println(e.text());
                writer.println(e.text());
            }
        } catch (IOException ex) {
            System.out.println(">> Connection Error: WebPage may not exist");
        } finally {
            writer.close();
        }
    }

    public static void main(String[] args) throws IOException {
        WikiScraper scraper = new WikiScraper();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n\nDocument to fetch: ");
            scraper.fetchDocument(scanner.nextLine());
        }
    }
}