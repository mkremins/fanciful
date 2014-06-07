package mkremins.fanciful;

import java.io.IOException;
import java.io.StringWriter;

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
	public void writeJson(JsonWriter writer) throws IOException{
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
	public static TextualComponent fromString(String textValue){
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
	public static TextualComponent fromLocalizedString(String translateKey){
		return new ArbitraryTextTypeComponent("translate", translateKey);
	}
	
	private static final void throwUnsupportedSnapshot(){
		throw new UnsupportedOperationException("This feature is only supported in snapshot releases.");
	}
	
	/**
	 * Create a textual component representing a scoreboard value.
	 * The client will see their own score for the specified objective as the text represented by this component.
	 * <p>
	 * <b>This method is currently guaranteed to throw an {@code UnsupportedOperationException} as it is only supported on snapshot clients.</b>
	 * </p>
	 * @param scoreboardObjective The name of the objective for which to display the score.
	 * @return The text component representing the specified scoreboard score (for the viewing player), or {@code null} if an error occurs during JSON serialization.
	 */
	public static TextualComponent fromObjectiveScore(String scoreboardObjective){
		return fromObjectiveScore("*", scoreboardObjective);
	}
	
	/**
	 * Create a textual component representing a scoreboard value.
	 * The client will see the score of the specified player for the specified objective as the text represented by this component.
	 * <p>
	 * <b>This method is currently guaranteed to throw an {@code UnsupportedOperationException} as it is only supported on snapshot clients.</b>
	 * </p>
	 * @param playerName The name of the player whos score will be shown. If this string represents the single-character sequence "*", the viewing player's score will be displayed.
	 * Standard minecraft selectors (@a, @p, etc) are <em>not</em> supported.
	 * @param scoreboardObjective The name of the objective for which to display the score.
	 * @return The text component representing the specified scoreboard score for the specified player, or {@code null} if an error occurs during JSON serialization.
	 */
	public static TextualComponent fromObjectiveScore(String playerName, String scoreboardObjective){
		throwUnsupportedSnapshot(); // Remove this line when the feature is released to non-snapshot versions, in addition to updating ALL THE OVERLOADS documentation accordingly
		
		StringWriter string = new StringWriter();
		JsonWriter json = new JsonWriter(string);
		// Write the JSON string, required for this component, out
		try {
			json.beginObject().name("name").value(playerName)
			.name("objective").value(scoreboardObjective).endObject().close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new ArbitraryTextTypeComponent("score", string.toString());
	}
	
	/**
	 * Create a textual component representing a player name, retrievable by using a standard minecraft selector.
	 * The client will see the players or entities captured by the specified selector as the text represented by this component.
	 * <p>
	 * <b>This method is currently guaranteed to throw an {@code UnsupportedOperationException} as it is only supported on snapshot clients.</b>
	 * </p>
	 * @param selector The minecraft player or entity selector which will capture the entities whose string representations will be displayed in the place of this text component.
	 * @return The text component representing the name of the entities captured by the selector.
	 */
	public static TextualComponent fromEntitySelector(String selector){
		throwUnsupportedSnapshot(); // Remove this line when the feature is released to non-snapshot versions, in addition to updating ALL THE OVERLOADS documentation accordingly
		
		return new ArbitraryTextTypeComponent("selector", selector);
	}
}
