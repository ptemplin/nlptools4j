package tokenize;

import java.util.List;
import java.util.ArrayList;

public class Token {
	
	private List<Tag> tags = new ArrayList<>();
	private String lexeme;
	
	public Token(String lexeme) {
		this.lexeme = lexeme;
	}
	
	public void addTag(Tag tag) {
		tags.add(tag);
	}
	
	public Tag getSingleTag() {
		Tag max = tags.get(0);
		for (Tag tag : tags) {
			if (tag.numOccurences > max.numOccurences) {
				max = tag;
			}
		}
		return max;
	}
	
	public List<Tag> getTags() {
		return tags;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Tag tag : tags) {
			if (!first) {
				builder.append(", ");
			} else {
				first = false;
			}
			builder.append(tag.partOfSpeech.toString() + " " + tag.numOccurences);
		}
		return builder.toString();
	}

}
