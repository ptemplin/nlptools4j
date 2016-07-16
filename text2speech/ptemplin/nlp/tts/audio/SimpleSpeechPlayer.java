package ptemplin.nlp.tts.audio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import ptemplin.nlp.tts.data.Phoneme;
import ptemplin.nlp.tts.data.PhonemeBoundaries;

public class SimpleSpeechPlayer implements SpeechPlayer {
	
	private final Map<Phoneme, byte[]> phonemeToRecording = new HashMap<>();
	private final Map<Phoneme, PhonemeBoundaries> phonemeToBoundaries = new HashMap<>();
	private AudioFormat audioFormat = null;
	private DataLine.Info dataLineInfo = null;
	
	private static final String PHONEME_BOUNDARIES_FILEPATH = "C:\\Projects\\NLPExperiments\\PhonemeBoundaries_temp";
	private static final String PHONEME_RECORDING_FILEPATH_PREFIX = "C:\\Users\\Me\\Documents\\Sound recordings\\";
	private static final String PHONEME_RECORDING_FILEPATH_SUFFIX = "_recording.wav";
	
	private static final int INPUT_STREAM_BUFFER_SIZE = 128000;
	private static final int WORD_PAUSE = 100;

	/**
	 * Initializes the necessary mappings for the speech player to execute.
	 */
	public SimpleSpeechPlayer() {
		initializePhonemeBoundaries();
		initializePhonemeRecordings();
	}
	
	/**
	 * Plays the word as the string of merged phonemes.
	 */
	public void playWord(List<Phoneme> phonemes) {
		
		SourceDataLine sourceLine = startAudioOutput();
		if (sourceLine == null) {
			System.out.println("Couldn't start audio output, exiting");
			return;
		}
		
		byte[] speechData = buildSpeechPlaybackData(phonemes);

		// write to the line and close it
		sourceLine.write(speechData, 0, speechData.length);
		sourceLine.drain();
		sourceLine.close();
		try {
			Thread.sleep(WORD_PAUSE);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Initialize the phoneme boundaries mapping from file.
	 */
	private void initializePhonemeBoundaries() {
		File boundaries = new File(PHONEME_BOUNDARIES_FILEPATH);
    	Scanner scanner;
    	try {
    		scanner = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream(boundaries))));
    	} catch (IOException ex) {
    		System.out.println("Error reading phoneme boundaries file");
    		ex.printStackTrace();
    		return;
    	}
    	while (scanner.hasNext()) {
    		Phoneme phoneme = Phoneme.valueOf(scanner.next());
    		if (scanner.hasNextFloat()) {
    			float start = scanner.nextFloat();
    			float end = scanner.nextFloat();
    			PhonemeBoundaries bound = new PhonemeBoundaries(start,end);
    			phonemeToBoundaries.put(phoneme, bound);
    		} else {
    			// defaults
    			phonemeToBoundaries.put(phoneme, new PhonemeBoundaries(0f,0.9f));
    		}
    	}
    	scanner.close();
	}
	
	/**
	 * Initialize the phoneme to recording mapping from file.
	 */
	private void initializePhonemeRecordings() {
		// get phone recordings
		for (Phoneme phoneme : Phoneme.values()) {
			// get the stream
			String fileName = PHONEME_RECORDING_FILEPATH_PREFIX + phoneme.toString() + PHONEME_RECORDING_FILEPATH_SUFFIX;
			File soundFile = new File(fileName);
			AudioInputStream audioStream;
			try {
				audioStream = AudioSystem.getAudioInputStream(soundFile);
			} catch (UnsupportedAudioFileException ex) {
				System.out.println("Couldn't open audio stream for " + phoneme.toString() + " recording");
				ex.printStackTrace();
				continue;
			} catch (IOException ex) {
				System.out.println("Couldn't open audio stream for " + phoneme.toString() + " recording");
				ex.printStackTrace();
				continue;
			}
			// init the audio format if necessary
			if (audioFormat == null) {
				audioFormat = audioStream.getFormat();
				dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
			}
			// read the contents of the stream buffer by buffer
			int nBytesRead = 0;
			byte[] abData = new byte[INPUT_STREAM_BUFFER_SIZE];
			List<Byte> fullSample = new ArrayList<>();
			while (nBytesRead != -1) {
				try {
					nBytesRead = audioStream.read(abData, 0, abData.length);
				} catch (IOException ex) {
					System.out.println("Error reading bytes from audio file for " + phoneme.toString());
					ex.printStackTrace();
					break;
				}
				if (nBytesRead >= 0) {
					for (int i = 0; i < nBytesRead; i++) {
						fullSample.add(abData[i]);
					}
				}
			}
			// calculate the offset and length to play
			PhonemeBoundaries bound = phonemeToBoundaries.get(phoneme);
			int size = fullSample.size();
			int offset = (int) ((bound.getStart()*size)/4)*4;
			int length = (int) ((bound.getEnd()*size)/4)*4 - offset;
			// copy the list values to an array for playback
			byte[] fullSampleArr = new byte[length];
			for (int i = 0; i < fullSampleArr.length; i++) {
				fullSampleArr[i] = fullSample.get(i+offset);
			}

			// put it in the table
			phonemeToRecording.put(phoneme,fullSampleArr);
		}
	}
	
	/**
	 * Starts a configured audio output stream.
	 *
	 * @return an audio output stream
	 */
	public SourceDataLine startAudioOutput() {
		SourceDataLine sourceLine;
		try {
			sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceLine.open(audioFormat);
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
			return null;
		}
		sourceLine.start();
		return sourceLine;
	}
	
	/**
	 * Builds the speech playback data using the given phonemes and the configured
	 * phoneme recordings. Performs a byte-level merge between phonemes for consistency.
	 *
	 * @param phonemes the phonemes to construct the playback from
	 * @return the raw audio to by played
	 */
	private byte[] buildSpeechPlaybackData(List<Phoneme> phonemes) {
		List<Byte> data = new ArrayList<>();
		int MERGE_LENGTH = 4000;
		for (int i = 0; i < phonemes.size(); i++) {
			// lookup the recording in the dictionary
			byte[] recording = phonemeToRecording.get(phonemes.get(i));
			// if the data array is empty, simply add the recording
			// also don't merge if consonant follows a vowel
			if (data.isEmpty() || phonemes.get(i-1).isVowel()) {
				for (byte b : recording) {
					data.add(b);
				}
			} else {
				int mergeStart = data.size() - MERGE_LENGTH;
				if (mergeStart < 0) {
					mergeStart = 0;
					MERGE_LENGTH = data.size();
				}
				// merge the next recording with the previous one
				for (int j = 0; j < MERGE_LENGTH; j+=4) {
					int dataIndex = j + mergeStart;
					// get the previous left and right values from data
					short prevL = (short) ((data.get(dataIndex) & 0x00FF) + (data.get(dataIndex+1) & 0xFF00));
					short prevR = (short) ((data.get(dataIndex+2) & 0x00FF) + (data.get(dataIndex+3) & 0xFF00));
					// get the next left and right values from the recording
					short nextL = (short) ((recording[j] & 0x00FF) + (recording[j+1] & 0xFF00));
					short nextR = (short) ((recording[j+2] & 0x00FF) + (recording[j+3] & 0xFF00));
					// combine the previous and next values
					short newL = (short) ((prevL + nextL)/2);
					short newR = (short) ((prevR + nextR)/2);
					// write back to the data list
					data.set(dataIndex, (byte)(newL&0xFF));
					data.set(dataIndex+1, (byte)(newL>>8));
					data.set(dataIndex+2, (byte)(newR&0xFF));
					data.set(dataIndex+3, (byte)(newR>>8));
				}
				for (int j = MERGE_LENGTH; j < recording.length; j++) {
					data.add(recording[j]);
				}
			}
		}

		// flush the list into an array
		byte[] dataArr = new byte[data.size()];
		for (int i = 0; i < dataArr.length; i++) {
			dataArr[i] = data.get(i);
		}
		return dataArr;
	}
}
