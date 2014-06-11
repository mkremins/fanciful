package mkremins.fanciful;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;

/**
 * Internal class: Represents a component of a JSON-serializable {@link FancyMessage}.
 */
final class MessagePart implements ConfigurationSerializable {

	ChatColor color = ChatColor.WHITE;
	ArrayList<ChatColor> styles = new ArrayList<ChatColor>();
	String clickActionName = null, clickActionData = null,
		   hoverActionName = null, hoverActionData = null;
	TextualComponent text = null;
	
	MessagePart(final TextualComponent text){
		this.text = text;
	}
	
	MessagePart() {
		this.text = null;
	}
	
	boolean hasText() {
		return text != null;
	}

	JsonWriter writeJson(JsonWriter json) {
		try {
			json.beginObject();
			text.writeJson(json);
			json.name("color").value(color.name().toLowerCase());
			for (final ChatColor style : styles) {
				String styleName;
				switch (style) {
				case MAGIC:
					styleName = "obfuscated"; break;
				case UNDERLINE:
					styleName = "underlined"; break;
				default:
					styleName = style.name().toLowerCase(); break;
				}
				json.name(styleName).value(true);
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

	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("text", text);
		map.put("styles", styles);
		map.put("color", color.getChar());
		map.put("hoverActionName", hoverActionName);
		map.put("hoverActionData", hoverActionData);
		map.put("clickActionName", clickActionName);
		map.put("clickActionData", clickActionData);
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static MessagePart deserialize(Map<String, Object> serialized){
		MessagePart part = new MessagePart((TextualComponent)serialized.get("text"));
		part.styles = (ArrayList<ChatColor>)serialized.get("styles");
		part.color = ChatColor.getByChar(serialized.get("color").toString());
		part.hoverActionName = serialized.get("hoverActionName").toString();
		part.hoverActionData = serialized.get("hoverActionData").toString();
		part.clickActionName = serialized.get("clickActionName").toString();
		part.clickActionData = serialized.get("clickActionData").toString();
		return part;
	}
	
	static{
		ConfigurationSerialization.registerClass(MessagePart.class);
	}

}
