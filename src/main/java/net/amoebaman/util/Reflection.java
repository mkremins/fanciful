package net.amoebaman.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

public class Reflection {

	private static String _versionString;
	
	/**
	 * Gets the version string from the package name of the CraftBukkit server implementation.
	 * This is needed to bypass the JAR package name changing on each update.
	 * @return The version string of the OBC and NMS packages, <em>including the trailing dot</em>.
	 */
	public static String getVersion() {
		if(_versionString == null){
			if(Bukkit.getServer() == null){
				// The server hasn't started, static initializer call?
				return null;
			}
			String name = Bukkit.getServer().getClass().getPackage().getName();
			_versionString = name.substring(name.lastIndexOf('.') + 1) + ".";
		}
		return _versionString;
	}

	/**
	 * Stores loaded classes from the {@code net.minecraft.server} package.
	 */
	private static final Map<String, Class<?>> _loadedNMSClasses = new HashMap<String, Class<?>>();
	/**
	 * Stores loaded classes from the {@code org.bukkit.craftbukkit} package (and subpackages).
	 */
	private static final Map<String, Class<?>> _loadedOBCClasses = new HashMap<String, Class<?>>();
	
	/**
	 * Gets a {@link Class} object representing a type contained within the {@code net.minecraft.server} versioned package.
	 * The class instances returned by this method are cached, such that no lookup will be done twice (unless multiple threads are accessing this method simultaneously).
	 * @param className The name of the class, excluding the package, within NMS.
	 * @return The class instance representing the specified NMS class, or {@code null} if it could not be loaded.
	 */
	public static Class<?> getNMSClass(String className) {
		if(_loadedNMSClasses.containsKey(className)){
			return _loadedNMSClasses.get(className);
		}
		
		String fullName = "net.minecraft.server." + getVersion() + className;
		Class<?> clazz = null;
		try {
			clazz = Class.forName(fullName);
		} catch (Exception e) {
			e.printStackTrace();
			_loadedNMSClasses.put(className, null);
			return null;
		}
		_loadedNMSClasses.put(className, clazz);
		return clazz;
	}

	/**
	 * Gets a {@link Class} object representing a type contained within the {@code org.bukkit.craftbukkit} versioned package.
	 * The class instances returned by this method are cached, such that no lookup will be done twice (unless multiple threads are accessing this method simultaneously).
	 * @param className The name of the class, excluding the package, within OBC. This name may contain a subpackage name, such as {@code inventory.CraftItemStack}.
	 * @return The class instance representing the specified OBC class, or {@code null} if it could not be loaded.
	 */
	public static Class<?> getOBCClass(String className) {
		if(_loadedOBCClasses.containsKey(className)){
			return _loadedOBCClasses.get(className);
		}
		
		String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
		Class<?> clazz = null;
		try {
			clazz = Class.forName(fullName);
		} catch (Exception e) {
			e.printStackTrace();
			_loadedOBCClasses.put(className, null);
			return null;
		}
		_loadedOBCClasses.put(className, clazz);
		return clazz;
	}

	public static Object getHandle(Object obj) {
		try {
			return getMethod(obj.getClass(), "getHandle").invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Field getField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Method getMethod(Class<?> clazz, String name,
			Class<?>... args) {
		for (Method m : clazz.getMethods())
			if (m.getName().equals(name) && (args.length == 0 || ClassListEqual(args, m.getParameterTypes()))) {
				m.setAccessible(true);
				return m;
			}
		return null;
	}

	public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
		boolean equal = true;
		if (l1.length != l2.length)
			return false;
		for (int i = 0; i < l1.length; i++)
			if (l1[i] != l2[i]) {
				equal = false;
				break;
			}
		return equal;
	}

}
