package ptemplin.nlp.tts.data;

public class PhonemeBoundaries {

    private final float start;
    private final float end;
    public PhonemeBoundaries(float start, float end) {
    	this.start = start;
    	this.end = end;
    }
    
    public float getStart() { return start; }
    public float getEnd() {  return end; }
	
}
