package mkremins.fanciful;

import java.io.IOException;

import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;

import com.google.common.base.Preconditions;

/**
 * Represents a textual component of a message part.
 * This can be used to not only represent string literals in a JSON message,
 * but also to represent localized strings and other text values.
 */
public abstract class TextualComponent {

	/**
	 * Get the JSON key used to represent text components of this type.
	 */
	public abstract String getKey();
	
	/**
	 * Get the string value of this text component instance which will be included in JSON.
	 */
	public abstract String getValue();
	
	/**
	 * Writes the text data represented by this textual component to the specified JSON writer object.
	 * A new object within the writer is not started.
	 * @param writer The object to which to write the JSON data.
	 * @throws IOException If an error occurs while writing to the stream.
	 */
	public void writeToJSON(JsonWriter writer) throws IOException{
		writer.name(getKey()).value(getValue());
	}
	
	/**
	 * Internal class used to represent all types of text components.
	 * Exception validating done is on keys and values.
	 */
	private static final class ArbitraryTextTypeComponent extends TextualComponent{

		public ArbitraryTextTypeComponent(String key, String value){
			setKey(key);
			setValue(value);
		}
		
		@Override
		public String getKey() {
			return _key;
		}

		public void setKey(String key) {
			Preconditions.checkArgument(key != null && !key.isEmpty(), "The key must be specified.");
			_key = key;
		}
		
		@Override
		public String getValue() {
			return _value;
		}

		public void setValue(String value) {
			Preconditions.checkArgument(value != null && !value.isEmpty(), "The value must be specified.");
			_value = value;
		}

		private String _key;
		private String _value;
		
	}
	
	/**
	 * Create a textual component representing a string literal.
	 * This is the default type of textual component when a single string literal is given to a method.
	 * @param textValue The text which will be represented.
	 * @return The text component representing the specified literal text.
	 */
	public static TextualComponent createStringLiteral(String textValue){
		return new ArbitraryTextTypeComponent("text", textValue);
	}
	

	/**
	 * Create a textual component representing a localized string.
	 * The client will see this text component as their localized version of the specified string <em>key</em>, which can be overridden by a resource pack.
	 * <p>
	 * If the specified translation key is not present on the client resource pack, the translation key will be displayed as a string literal to the client.
	 * </p>
	 * @param translateKey The string key which maps to localized text.
	 * @return The text component representing the specified localized text.
	 */
	public static TextualComponent createTranslateString(String translateKey){
		return new ArbitraryTextTypeComponent("translate", translateKey);
	}
}
