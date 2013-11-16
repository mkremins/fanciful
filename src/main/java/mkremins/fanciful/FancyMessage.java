package mkremins.fanciful;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class FancyMessage {
	
	private final List<MessagePart> messageParts;
	
	public FancyMessage(final String firstPartText) {
		messageParts = new ArrayList<MessagePart>();
		messageParts.add(new MessagePart(firstPartText));
	}
	
	public FancyMessage color(final ChatColor color) {
		latest().color = color;
		return this;
	}
	
	public FancyMessage style(final ChatStyle... styles) {
		latest().styles = styles;
		return this;
	}
	
	public FancyMessage file(final String path) {
		onClick("open_file", path);
		return this;
	}
	
	public FancyMessage link(final String url) {
		onClick("open_url", url);
		return this;
	}
	
	public FancyMessage suggest(final String command) {
		onClick("suggest_command", command);
		return this;
	}
	
	public FancyMessage command(final String command) {
		onClick("run_command", command);
		return this;
	}
	
	public FancyMessage achievementTooltip(final String name) {
		onHover("show_achievement", "achievement." + name);
		return this;
	}
	
	public FancyMessage itemTooltip(final String itemJSON) {
		onHover("show_item", itemJSON);
		return this;
	}
	
	public FancyMessage tooltip(final String text) {
		onHover("show_text", text);
		return this;
	}
	
	public FancyMessage then(final Object obj) {
		messageParts.add(new MessagePart(obj.toString()));
		return this;
	}
	
	public String toJSONString() {
		if (messageParts.size() == 1) {
			return latest().toJSONString();
		} else {
			final StringBuilder JSON = new StringBuilder();
			JSON.append("{text:'',extra:[");
			for (int i = 0; i < messageParts.size(); i++) {
				JSON.append(messageParts.get(i).toJSONString());
				if (i < messageParts.size() - 1) {
					JSON.append(",");
				}
			}
			JSON.append("]}");
			return JSON.toString();
		}
	}
	
	private MessagePart latest() {
		return messageParts.get(messageParts.size() - 1);
	}
	
	private void onClick(final String name, final String data) {
		final MessagePart latest = latest();
		latest.clickActionName = name;
		latest.clickActionData = data;
	}
	
	private void onHover(final String name, final String data) {
		final MessagePart latest = latest();
		latest.hoverActionName = name;
		latest.hoverActionData = data;
	}
	
}
