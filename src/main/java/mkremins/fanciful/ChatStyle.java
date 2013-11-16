package mkremins.fanciful;

public enum ChatStyle {
	
	BOLD, ITALIC, OBFUSCATED, STRIKETHROUGH, UNDERLINED;
	
	public String toString() {
		return super.toString().toLowerCase();
	}
	
}
