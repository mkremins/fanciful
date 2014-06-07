package mkremins.fanciful;

import java.io.IOException;

import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;

/**
 * Represents a JSON string value.
 * Writes by this object will not write name values nor begin/end objects in the JSON stream.
 * All writes merely write the represented string value.
 */
final class JsonString implements JsonRepresentedObject {

	private String _value;
	
	public JsonString(String value){
		_value = value;
	}
	
	public void writeJson(JsonWriter writer) throws IOException {
		writer.value(_value);
	}

}
