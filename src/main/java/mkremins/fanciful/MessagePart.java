package mkremins.fanciful;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;

final class MessagePart {

	ChatColor color = ChatColor.WHITE;
	ArrayList<ChatColor> styles = new ArrayList<ChatColor>();
	String clickActionName = null, clickActionData = null,
		   hoverActionName = null, hoverActionData = null;
	String text = null;

	MessagePart(final String text) {
		this.text = text;
	}
	
	MessagePart() {}
	
	boolean hasText() {
		return text != null;
	}

	JsonWriter writeJson(JsonWriter json) {
		try {
			json.beginObject().name("text").value(text);
			json.name("color").value(color.name().toLowerCase());
			for (final ChatColor style : styles) {
				if (style == ChatColor.MAGIC) {
					json.name("obfuscated").value(true);
				} else {
					json.name(style.name().toLowerCase()).value(true);
				}
			}
			if (clickActionName != null && clickActionData != null) {
				json.name("clickEvent")
					.beginObject()
					.name("action").value(clickActionName)
					.name("value").value(clickActionData)
					.endObject();
			}
			if (hoverActionName != null && hoverActionData != null) {
				json.name("hoverEvent")
					.beginObject()
					.name("action").value(hoverActionName)
					.name("value").value(hoverActionData)
					.endObject();
			}
			return json.endObject();
		} catch(Exception e){
			e.printStackTrace();
			return json;
		}
	}

}
