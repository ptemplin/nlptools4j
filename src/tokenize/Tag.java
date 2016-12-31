package tokenize;

public class Tag {

	// the terminal category
	public PartOfSpeech partOfSpeech;
	// the Penn Treebank morpho category
	public String morphology;
	// the number of occurrences in MASC
	public int numOccurences;
	
	public Tag(PartOfSpeech pos, String morpho, int numOcc) {
		partOfSpeech = pos;
		morphology = morpho;
		numOccurences = numOcc;
	}
	
}
