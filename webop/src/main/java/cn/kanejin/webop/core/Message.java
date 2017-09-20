package cn.kanejin.webop.core;

public class Message {
	public enum Level {
		SUCCESS,
		INFO,
		ERROR
	}
	
	private Level level;
	private String text;
	
	public Message(Level level, String text) {
		this.level = level;
		this.text = text;
	}

	/**
	 * @return the level
	 */
	public Level getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(Level level) {
		this.level = level;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
}
