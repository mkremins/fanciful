package mkremins.fanciful;

import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.amoebaman.util.Reflection;

import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FancyMessage {
	
	private final List<MessagePart> messageParts;
	private String jsonString;
	private boolean dirty;
	
	private static Constructor<?> nmsPacketPlayOutChatConstructor;
	
	public FancyMessage(final String firstPartText) {
		this(TextualComponent.createStringLiteral(firstPartText));
	}
	
	public FancyMessage(final TextualComponent firstPartText) {
		messageParts = new ArrayList<MessagePart>();
		messageParts.add(new MessagePart(firstPartText));
		jsonString = null;
		dirty = false;
		
		if(nmsPacketPlayOutChatConstructor == null){
			try {
				nmsPacketPlayOutChatConstructor = Reflection.getNMSClass("PacketPlayOutChat").getDeclaredConstructor(Reflection.getNMSClass("IChatBaseComponent"));
				nmsPacketPlayOutChatConstructor.setAccessible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public FancyMessage() {
		this((TextualComponent)null);
	}
	
	public FancyMessage text(String text) {
		MessagePart latest = latest();
		if (latest.hasText()) {
			throw new IllegalStateException("text for this message part is already set");
		}
		latest.text = TextualComponent.createStringLiteral(text);
		dirty = true;
		return this;
	}
	
	public FancyMessage text(TextualComponent text) {
		MessagePart latest = latest();
		if (latest.hasText()) {
			throw new IllegalStateException("text for this message part is already set");
		}
		latest.text = text;
		dirty = true;
		return this;
	}
	
	public FancyMessage color(final ChatColor color) {
		if (!color.isColor()) {
			throw new IllegalArgumentException(color.name() + " is not a color");
		}
		latest().color = color;
		dirty = true;
		return this;
	}
	
	public FancyMessage style(ChatColor... styles) {
		for (final ChatColor style : styles) {
			if (!style.isFormat()) {
				throw new IllegalArgumentException(style.name() + " is not a style");
			}
		}
		latest().styles.addAll(Arrays.asList(styles));
		dirty = true;
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
	
	public FancyMessage achievementTooltip(final Achievement which) {
		try {
			Object achievement = Reflection.getMethod(Reflection.getOBCClass("CraftStatistic"), "getNMSAchievement", Achievement.class).invoke(null, which);
			return achievementTooltip((String) Reflection.getField(Reflection.getNMSClass("Achievement"), "name").get(achievement));
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	public FancyMessage statisticTooltip(final Statistic which) {
		Type type = which.getType();
		if (type != Type.UNTYPED) {
			throw new IllegalArgumentException("That statistic requires an additional " + type + " parameter!");
		}
		try {
			Object statistic = Reflection.getMethod(Reflection.getOBCClass("CraftStatistic"), "getNMSStatistic", Statistic.class).invoke(null, which);
			return achievementTooltip((String) Reflection.getField(Reflection.getNMSClass("Statistic"), "name").get(statistic));
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	public FancyMessage statisticTooltip(final Statistic which, Material item) {
		Type type = which.getType();
		if (type == Type.UNTYPED) {
			throw new IllegalArgumentException("That statistic needs no additional parameter!");
		}
		if ((type == Type.BLOCK && item.isBlock()) || type == Type.ENTITY) {
			throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
		}
		try {
			Object statistic = Reflection.getMethod(Reflection.getOBCClass("CraftStatistic"), "getMaterialStatistic", Statistic.class, Material.class).invoke(null, which, item);
			return achievementTooltip((String) Reflection.getField(Reflection.getNMSClass("Statistic"), "name").get(statistic));
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	public FancyMessage statisticTooltip(final Statistic which, EntityType entity) {
		Type type = which.getType();
		if (type == Type.UNTYPED) {
			throw new IllegalArgumentException("That statistic needs no additional parameter!");
		}
		if (type != Type.ENTITY) {
			throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
		}
		try {
			Object statistic = Reflection.getMethod(Reflection.getOBCClass("CraftStatistic"), "getEntityStatistic", Statistic.class, EntityType.class).invoke(null, which, entity);
			return achievementTooltip((String) Reflection.getField(Reflection.getNMSClass("Statistic"), "name").get(statistic));
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	public FancyMessage itemTooltip(final String itemJSON) {
		onHover("show_item", itemJSON);
		return this;
	}
	
	public FancyMessage itemTooltip(final ItemStack itemStack) {
		try {
			Object nmsItem = Reflection.getMethod(Reflection.getOBCClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class).invoke(null, itemStack);
			return itemTooltip(Reflection.getMethod(Reflection.getNMSClass("ItemStack"), "save", Reflection.getNMSClass("NBTTagCompound")).invoke(nmsItem, Reflection.getNMSClass("NBTTagCompound").newInstance()).toString());
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	public FancyMessage tooltip(final String text) {
		return tooltip(text.split("\\n"));
	}
	
	public FancyMessage tooltip(final List<String> lines) {
		return tooltip((String[])lines.toArray());
	}
	
	public FancyMessage tooltip(final String... lines) {
		if (lines.length == 1) {
			onHover("show_text", lines[0]);
		} else {
			itemTooltip(makeMultilineTooltip(lines));
		}
		return this;
	}
	
	public FancyMessage then(final String text) {
		if (!latest().hasText()) {
			throw new IllegalStateException("previous message part has no text");
		}
		messageParts.add(new MessagePart(TextualComponent.createStringLiteral(text)));
		dirty = true;
		return this;
	}
	
	public FancyMessage then(final TextualComponent text) {
		if (!latest().hasText()) {
			throw new IllegalStateException("previous message part has no text");
		}
		messageParts.add(new MessagePart(text));
		dirty = true;
		return this;
	}
	
	public FancyMessage then() {
		if (!latest().hasText()) {
			throw new IllegalStateException("previous message part has no text");
		}
		messageParts.add(new MessagePart());
		dirty = true;
		return this;
	}
	
	public String toJSONString() {
		if (!dirty && jsonString != null) {
			return jsonString;
		}
		StringWriter string = new StringWriter();
		JsonWriter json = new JsonWriter(string);
		try {
			if (messageParts.size() == 1) {
				latest().writeJson(json);
			} else {
				json.beginObject().name("text").value("").name("extra").beginArray();
				for (final MessagePart part : messageParts) {
					part.writeJson(json);
				}
				json.endArray().endObject();
				json.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("invalid message");
		}
		jsonString = string.toString();
		dirty = false;
		return jsonString;
	}
	
	public void send(Player player){
		try {
			Object handle = Reflection.getHandle(player);
			Object connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);
			Reflection.getMethod(connection.getClass(), "sendPacket", Reflection.getNMSClass("Packet")).invoke(connection, createChatPacket(toJSONString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// The ChatSerializer's instance of Gson
	private net.minecraft.util.com.google.gson.Gson nmsChatSerializerGsonInstance;
	
	private Object createChatPacket(String json) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException{
		if(nmsChatSerializerGsonInstance == null){
			// Find the field and its value, completely bypassing obfuscation
			for(Field declaredField : Reflection.getNMSClass("ChatSerializer").getDeclaredFields()){
				if(Modifier.isFinal(declaredField.getModifiers()) && Modifier.isStatic(declaredField.getModifiers()) && declaredField.getType() == net.minecraft.util.com.google.gson.Gson.class){
					// We've found our field
					declaredField.setAccessible(true);
					nmsChatSerializerGsonInstance = (net.minecraft.util.com.google.gson.Gson)declaredField.get(null);
					break;
				}
			}
		}
		
		// Since the method is so simple, and all the obfuscated methods have the same name, it's easier to reimplement 'IChatBaseComponent a(String)' than to reflectively call it
		// Of course, the implementation may change, but fuzzy matches might break with signature changes
		Object serializedChatComponent = nmsChatSerializerGsonInstance.fromJson(json, Reflection.getNMSClass("IChatBaseComponent"));
		
		return nmsPacketPlayOutChatConstructor.newInstance(serializedChatComponent);
	}

	public void send(CommandSender sender) {
		if (sender instanceof Player) {
			send((Player) sender);
		} else {
			sender.sendMessage(toOldMessageFormat());
		}
	}

	public void send(final Iterable<? extends CommandSender> senders) {
		for (final CommandSender sender : senders) {
			send(sender);
		}
	}
	
	public String toOldMessageFormat() {
		StringBuilder result = new StringBuilder();
		for (MessagePart part : messageParts) {
			result.append(part.color).append(part.text);
		}
		return result.toString();
	}
	
	private MessagePart latest() {
		return messageParts.get(messageParts.size() - 1);
	}
	
	private String makeMultilineTooltip(final String[] lines) {
		StringWriter string = new StringWriter();
		JsonWriter json = new JsonWriter(string);
		try {
			json.beginObject().name("id").value(1);
			json.name("tag").beginObject().name("display").beginObject();
			json.name("Name").value("\\u00A7f" + lines[0].replace("\"", "\\\""));
			json.name("Lore").beginArray();
			for (int i = 1; i < lines.length; i++) {
				final String line = lines[i];
				json.value(line.isEmpty() ? " " : line.replace("\"", "\\\""));
			}
			json.endArray().endObject().endObject().endObject();
			json.close();
		} catch (Exception e) {
			throw new RuntimeException("invalid tooltip");
		}
		return string.toString();
	}
	
	private void onClick(final String name, final String data) {
		final MessagePart latest = latest();
		latest.clickActionName = name;
		latest.clickActionData = data;
		dirty = true;
	}
	
	private void onHover(final String name, final String data) {
		final MessagePart latest = latest();
		latest.hoverActionName = name;
		latest.hoverActionData = data;
		dirty = true;
	}
	
}
