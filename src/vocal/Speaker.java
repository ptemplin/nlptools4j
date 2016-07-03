package vocal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javafx.embed.swing.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Speaker {
	
	public static final String recordingDir = "file:/C:/Users/Me/Documents/SoundRecordings/";
	public static HashMap<String,Media> phonemeToRecording = new HashMap<>();
	public static String[] phonemes = {
		"SHORT_PAUSE","LONG_PAUSE",
		"AA","AE","AH","AO","AW","AY",
		"B","CH","D","DH","EH","ER",
		"EY","F","G","HH","IH","IY",
		"JH","K","L","M","N","NG","OW",
		"OY","P","R","S","SH","T","TH",
		"UH","UW","V","W","Y","Z","ZH"};
	public static HashMap<String,List<String>> wordToPhones = new HashMap<>();
	
//	public static void main(String[] args) throws Exception{
//		initialize();
//		System.out.println("What would you like me to say?");
//		Scanner scanner = new Scanner(System.in);
//		String input = "";
//		while (!input.equals("x")) {
//			input = scanner.nextLine();
//			String[] tokens = input.split(" ");
//			for (String s : tokens) {
//				sayWord(s);
//			}
//		}
//	}
	
	public static void sayWord(String text) throws Exception{
		if (phonemeToRecording.isEmpty()) {
			initialize();
		}
		// word entries are in all caps, so do toUpper
		List<String> phones = null;
		if (wordToPhones.containsKey(text.toUpperCase())) {
			phones = wordToPhones.get(text.toUpperCase());
		} else {
			return;
		}
		for (String p : phones) {
			Media recording = phonemeToRecording.get(p);
			MediaPlayer player = new MediaPlayer(recording);
			//int duration = (int) recording.getDuration().toMillis();
			player.play();
			Thread.sleep(200);
		}
		Thread.sleep(300);
		return;
	}
	
	private static void initialize() throws IOException {
		// get phone recordings
		JFXPanel panel = new JFXPanel();
		for (String phoneme : phonemes) {
			Media media = new Media(recordingDir + phoneme + "_recording.m4a");
			phonemeToRecording.put(phoneme,media);
		}
		// initialize pronunciation dictionary
		File dict = new File("C:\\Projects\\NLPExperiments\\CMUPronunciationDict.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dict)));
		while (true) {
			String line = reader.readLine();
			// break at end of file
			if (line == null) {
				break;
			} else if (line.isEmpty()) {
				continue;
			}
			// create a new entry in the dictionary
			String[] parts = line.split(" ");
			String lexeme = parts[0];
			List<String> phones = new ArrayList<>();
			for (int i = 1; i < parts.length; i++) {
				if (!parts[i].equals("")) {
					// contains extra numeral for pitch
					if (parts[i].length() == 3) {
						phones.add(parts[i].substring(0,parts[i].length()-1));
					} else {
						phones.add(parts[i]);
					}
				}
			}
			wordToPhones.put(lexeme, phones);
		}
	}
	
}
