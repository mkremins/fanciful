package net.amoebaman.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
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

	private static final Map<Class<?>, Map<String, Field>> _loadedFields = new HashMap<Class<?>, Map<String, Field>>();
	
	public static Field getField(Class<?> clazz, String name) {
		Map<String, Field> loaded;
		if(!_loadedFields.containsKey(clazz)){
			loaded = new HashMap<String, Field>();
			_loadedFields.put(clazz, loaded);
		}else{
			loaded = _loadedFields.get(clazz);
		}
		if(loaded.containsKey(name)){
			// If the field is loaded (or cached as not existing), return the relevant value, which might be null
			return loaded.get(name);
		}
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			loaded.put(name, field);
			return field;
		} catch (Exception e) {
			// Error loading
			e.printStackTrace();
			// Cache field as not existing
			loaded.put(name, null);
			return null;
		}
	}

	/**
	 * Contains loaded methods in a cache.
	 * The map maps [types to maps of [method names to maps of [parameter types to method instances]]].
	 */
	private static final Map<Class<?>, Map<String, Map<ArrayWrapper<Class<?>>, Method>>> _loadedMethods = new HashMap<Class<?>, Map<String, Map<ArrayWrapper<Class<?>>, Method>>>();
	
	public static Method getMethod(Class<?> clazz, String name,
			Class<?>... args) {
		if(!_loadedMethods.containsKey(clazz)){
			_loadedMethods.put(clazz, new HashMap<String, Map<ArrayWrapper<Class<?>>, Method>>());
		}
		
		Map<String, Map<ArrayWrapper<Class<?>>, Method>> loadedMethodNames = _loadedMethods.get(clazz);
		if(!loadedMethodNames.containsKey(name)){
			loadedMethodNames.put(name, new HashMap<ArrayWrapper<Class<?>>, Method>());
		}
		
		Map<ArrayWrapper<Class<?>>, Method> loadedSignatures = loadedMethodNames.get(name);
		ArrayWrapper<Class<?>> wrappedArg = new ArrayWrapper<Class<?>>(args);
		if(loadedSignatures.containsKey(wrappedArg)){
			return loadedSignatures.get(wrappedArg);
		}
		
		for (Method m : clazz.getMethods())
			if (m.getName().equals(name) && Arrays.equals(args, m.getParameterTypes())) {
				m.setAccessible(true);
				loadedSignatures.put(wrappedArg, m);
				return m;
			}
		loadedSignatures.put(wrappedArg, null);
		return null;
	}

}
