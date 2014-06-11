package mkremins.fanciful;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;

/**
 * Internal class: Represents a component of a JSON-serializable {@link FancyMessage}.
 */
final class MessagePart implements JsonRepresentedObject, Cloneable {

	ChatColor color = ChatColor.WHITE;
	ArrayList<ChatColor> styles = new ArrayList<ChatColor>();
	String clickActionName = null, clickActionData = null,
		   hoverActionName = null;
	JsonRepresentedObject hoverActionData = null;
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
	
	@SuppressWarnings("unchecked")
	public MessagePart clone() throws CloneNotSupportedException{
		MessagePart obj = (MessagePart)super.clone();
		obj.styles = (ArrayList<ChatColor>)styles.clone();
		if(hoverActionData instanceof JsonString){
			obj.hoverActionData = new JsonString(((JsonString)hoverActionData).getValue());
		}else if(hoverActionData instanceof FancyMessage){
			obj.hoverActionData = ((FancyMessage)hoverActionData).clone();
		}
		return obj;
		
	}

	public void writeJson(JsonWriter json) {
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
					.name("value");
					hoverActionData.writeJson(json);
					json.endObject();
			}
			json.endObject();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
