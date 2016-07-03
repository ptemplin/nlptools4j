package syntax;

public enum PartOfSpeech {

	NOUN("Noun"),
	PRONOUN("Pronoun"),
	PROPER_NOUN("ProperNoun"),
	DETERMINER("Det"),
	VERB("Verb"),
	GERUNDV("GerundV"),
	ADJECTIVE("Adj"),
	ADVERB("Adv"),
	PREPOSITION("Preposition"),
	WH_WORD("Wh-Word"),
	AUXILLARY("Aux"),
	CONJUNCTION("Conj"),
	INTERJECTION("Inj"),
	PUNCTUATION("Punct");
	
	private String text;
	
	private PartOfSpeech(String s) {text = s;}
	
	public String toString() {
		return text;
	}
}
