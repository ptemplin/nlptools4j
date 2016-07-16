package ptemplin.nlp.tts;

import java.util.Scanner;

import ptemplin.nlp.tts.control.SimpleTTSController;
import ptemplin.nlp.tts.control.TTSController;

/**
 * Reads in a single line of user input from stdin and plays the corresponding TTS,
 * until the user inputs the 'EXIT_TEXT'.
 */
public class CLIApplication {
	
	private static final String USER_PROMPT = "What would you like me to say?";
	private static final String EXIT_MESSAGE = "Exiting...";
	private static final String EXIT_TEXT = "exit";

	public static void main(String[] args) {
		String input = "";
		try (TTSController ttsPlayer = new SimpleTTSController();
				Scanner scanner = new Scanner(System.in))
		{
			while (!input.equalsIgnoreCase(EXIT_TEXT)) {
				System.out.println(USER_PROMPT);
				input = scanner.nextLine();
				ttsPlayer.playSpeech(input);
			}
			System.out.println(EXIT_MESSAGE);
		}
	}
}
