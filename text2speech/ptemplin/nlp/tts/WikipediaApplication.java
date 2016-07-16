package ptemplin.nlp.tts;

import java.util.Scanner;

import ptemplin.nlp.tts.control.SimpleTTSController;
import ptemplin.nlp.tts.control.TTSController;
import ptemplin.nlp.tts.parse.HTMLExtractor;

public class WikipediaApplication {
	
	private static final String INITIAL_INPUT_PROMPT = "What would you like to hear about?";
	private static final String FURTHER_INPUT_PROMPT = "Anything else?";
	private static final String EXIT_INPUT = "no";
	private static final String EXIT_MESSAGE = "Exiting...";

	public static void main(String[] args) {
		String input = "";
		
		try (TTSController ttsPlayer = new SimpleTTSController();
				Scanner scanner = new Scanner(System.in))
		{
			int count = 0;
			while (!input.equalsIgnoreCase(EXIT_INPUT)) {
				if (count == 0) {
					System.out.println(INITIAL_INPUT_PROMPT);
				} else {
					System.out.println(FURTHER_INPUT_PROMPT);
				}
				input = scanner.nextLine();
				ttsPlayer.playSpeech(getWikiPageToRead(input));
				++count;
			}
			
			System.out.println(EXIT_MESSAGE);
		}
	}
	
	private static String getWikiPageToRead(String pageTitle) {
		return HTMLExtractor.getWikiText("/wiki/" + pageTitle);
	}
	
}
