package net.jforum.services;

import net.jforum.formatters.Formatter;
import net.jforum.formatters.PostFormatters;
import net.jforum.formatters.PostOptions;

public class MessageFormatService {

	private PostFormatters formatters;

	//----------Getter & Setter----------
	public PostFormatters getFormatters() {
		return formatters;
	}

	public void setFormatters(PostFormatters formatters) {
		this.formatters = formatters;
	}
	
	//---------- Business Logic----------
	
	/**
	 * format the text
	 */
	public String format(String text,PostOptions options){
		for (Formatter formatter : formatters) {
			text = formatter.format(text, options);
		}
		return text;
	}
	
}
