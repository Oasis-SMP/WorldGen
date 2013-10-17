/**
 * WorldGen plugin - (c) 2013 by Michael Huttinger (TheHUTMan)
 * LPGL v3.0 License
 */
package local.thehutman.worldgen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Type4 generation of structures at the player's current location.
 * 
 * A constructor: c()
 * 
 * A method: a(World,Random,int,int,int)
 * 
 * Example: WorldGenDesertWell
 * 
 * @author Huttinger
 * 
 */
class GenType4 {

	/**
	 * Type4 generation of structures at the player's current location.
	 * 
	 * A constructor: c()
	 * 
	 * A method: a(World,Random,int,int,int)
	 * 
	 * @param player
	 *            Player object
	 * @param className
	 *            Name of class for structure to build. Must have a simple
	 *            constructor with no parameters and the generate method (a)
	 *            takes world, random, and position arguments.
	 * @param permName
	 *            Name of permission required (i.e. worldgen.command.simple)
	 * @param dispName
	 *            Name of structure as displayed to the user/logger
	 */
	public static void generate(Player player, String namePerm, String nameDisplay, String nameClass) {

		if (!(player.hasPermission(namePerm))) {

			player.sendMessage(ChatColor.RED + "You do not have permissions for generating that!");
			return;
		}

		Utility.log.info("Generating " + nameDisplay + "...");

		try {

			// Get Crafting packages
			WorldInterface i = new WorldInterface(player, nameClass);
			if(i.oCraftWorldHandle == null)
			{
				player.sendMessage(ChatColor.RED + "Failed to generate " + nameDisplay + ". Please check server log.");
				return;
			}

			// Get current block position and the surface level block at that
			// point
			Block block = player.getLocation().getBlock();
			int x = block.getX();
			int z = block.getZ();
			int y = player.getWorld().getHighestBlockYAt(x, z);
			
			// Nether world heights don't work right
			if(player.getWorld().getEnvironment().getId() == -1)
				y = block.getY();

			// Get the witch hut gen object via our current block's chunk
			Constructor<?> cGen = i.clObjGenerator.getConstructor();
			cGen.setAccessible(true);
			Object oGen = cGen.newInstance();

			// Execute the generation start method
			@SuppressWarnings("rawtypes")
			Class[] parameterTypes = new Class[5];
			parameterTypes[0] = i.clObjWorld;
			parameterTypes[1] = i.oRandom.getClass();
			parameterTypes[2] = int.class;
			parameterTypes[3] = int.class;
			parameterTypes[4] = int.class;
			Method a = i.clObjGenerator.getDeclaredMethod("a", parameterTypes);
			Boolean r = (Boolean) a.invoke(oGen, i.oCraftWorldHandle, i.oRandom, x, y, z);

			// Check results
			if (!r)
				player.sendMessage(ChatColor.RED + "Unable to generate " + nameDisplay
						+ " at this location.  Be sure you are on proper materials for this structure.");
			else {
				Utility.log.info("Generated " + nameDisplay + " at: (" + x + "," + z + ")");
				player.sendMessage("Generated a new " + nameDisplay + "!");
			}

		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "Failed to generate " + nameDisplay + ". Please check server log.");
		}
	}

}
