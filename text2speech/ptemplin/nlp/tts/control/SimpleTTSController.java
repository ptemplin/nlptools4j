package ptemplin.nlp.tts.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import ptemplin.nlp.tts.audio.SimpleSpeechPlayer;
import ptemplin.nlp.tts.audio.SpeechPlayer;
import ptemplin.nlp.tts.data.CMUPronunciationDictionary;
import ptemplin.nlp.tts.data.Phoneme;
import ptemplin.nlp.tts.data.PhonemeBoundaries;
import ptemplin.nlp.tts.data.PronunciationDictionary;
import ptemplin.nlp.tts.parse.SimpleInputParser;

/**
 * Plays speech tokens individually.
 */
public class SimpleTTSController implements TTSController {
	
	private final SpeechPlayer speechPlayer;
	private final PronunciationDictionary pronunciations;

	public SimpleTTSController() {
		pronunciations = new CMUPronunciationDictionary();
		speechPlayer = new SimpleSpeechPlayer();
	}
	
	@Override
	public void playSpeech(String rawText) {
		
		// 1. Tokenize the raw text
		List<String> words = SimpleInputParser.getTokensFromText(rawText);

		// For each word of input:
		for (String word : words) {
			
			// 2. Retrieve the pronunciation from the dictionary
			List<Phoneme> phonemes = pronunciations.getPronunciation(word);
			
			// 3. Play the audio for the phonemes
			speechPlayer.playWord(phonemes);
			
		}
	}
	
	@Override
	public void close() {
		// do nothing for now
	}
	
}
