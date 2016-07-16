package ptemplin.nlp.tts.audio;

import java.util.List;

import ptemplin.nlp.tts.data.Phoneme;

public interface SpeechPlayer {

	void playWord(List<Phoneme> phonemes);
	
}
