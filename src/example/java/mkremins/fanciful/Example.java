package mkremins.fanciful;

import static org.bukkit.ChatColor.*;
import mkremins.fanciful.ChatStyle;
import mkremins.fanciful.FancyMessage;

public final class Example {
	
	public static void main(String[] args) {
		System.out.println(welcome("Orbixitron"));
		System.out.println(advertisement());
		System.out.println(gui("Starbux42", 413000));
	}

	static String welcome(String playername) {
		return new FancyMessage("Hello, ")
			.color(YELLOW)
		.then(playername)
			.color(LIGHT_PURPLE)
			.style(ChatStyle.ITALIC, ChatStyle.UNDERLINED)
		.then("!")
			.color(YELLOW)
			.style(ChatStyle.ITALIC)
		.toJSONString();
	}
	
	static String advertisement() {
		return new FancyMessage("Visit ")
			.color(GREEN)
		.then("our website")
			.color(YELLOW)
			.style(ChatStyle.UNDERLINED)
			.link("http://awesome-server.net")
			.tooltip("AwesomeServer Forums")
		.then(" to win ")
			.color(GREEN)
		.then("big prizes!")
			.color(AQUA)
			.style(ChatStyle.BOLD)
			.tooltip("Terms and conditions may apply. Offer not valid in Sweden.")
		.toJSONString();
	}
	
	static String gui(String playername, int blocksEdited) {
		return new FancyMessage("Player ")
			.color(DARK_RED)
		.then(playername)
			.color(RED)
			.style(ChatStyle.ITALIC)
		.then(" changed ").color(DARK_RED)
		.then(blocksEdited).color(AQUA)
		.then(" blocks. ").color(DARK_RED)
		.then("Roll back?")
			.color(GOLD)
			.style(ChatStyle.UNDERLINED)
			.suggest("/rollenbacken " + playername)
			.tooltip("Be careful, this might undo legitimate edits!")
		.then(" ")
		.then("Ban?")
			.color(RED)
			.style(ChatStyle.UNDERLINED)
			.suggest("/banhammer " + playername)
			.tooltip("Remember: only ban if you have photographic evidence of grief.")
		.toJSONString();
	}
	
}
