/**
 * WorldGen plugin - (c) 2013 by Michael Huttinger (TheHUTMan)
 * LPGL v3.0 License
 */
package local.thehutman.worldgen;

import java.util.logging.Logger;

/**
 * Utility class for various common routines
 * 
 * @author Huttinger
 * 
 */
public class Utility {

	/**
	 * Global static property holding an instance to the console logger for our plugin
	 */
	public static Logger log;

	/**
	 * Helper function to find the base package string that contains a class.
	 * Used to help abstract references to version-specific minecraft/bukkit
	 * classes.
	 * 
	 * @param className
	 *            Class to search for in packages
	 * 
	 * @return Name of package that contains the class
	 */
	public static String FindPackage(String className) {

		final Package[] packages = Package.getPackages();

		for (final Package p : packages) {
			final String pack = p.getName();
			final String tentative = pack + "." + className;
			try {
				Class.forName(tentative);
			} catch (final ClassNotFoundException e) {
				continue;
			}
			return pack;
		}

		return null;

	}

}
