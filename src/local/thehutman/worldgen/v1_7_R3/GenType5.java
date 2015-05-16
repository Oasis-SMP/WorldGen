/**
 * WorldGen plugin - (c) 2013 by Michael Huttinger (TheHUTMan)
 * LPGL v3.0 License
 */
package local.thehutman.worldgen.v1_7_R3;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;

import local.thehutman.worldgen.Utility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Type5 generation of structures at the player's current location. This also
 * takes into account a Y component for the bounding box used to draw the
 * object.
 * 
 * A constructor: c(int,Random,StructureBoundingBox,int)
 * 
 * A method: a(World,Random,StructureBoundingBox)
 * 
 * Example: WorldGenStrongholdPortalRoom
 * 
 * @author Huttinger
 * 
 */
class GenType5 {

	/**
	 * Type5 generation of structures at the player's current location. This
	 * also takes into account a Y component for the bounding box used to draw
	 * the object.
	 * 
	 * A constructor: c(int,Random,StructureBoundingBox,int)
	 * 
	 * A method: a(World,Random,StructureBoundingBox)
	 * 
	 * @param player
	 *            Player object
	 * @param radius
	 *            Bounding box to build the object
	 * @param className
	 *            Name of class for structure to build. Must have a simple
	 *            constructor with no parameters and the generate method (a)
	 *            takes world, random, and position arguments.
	 * @param permName
	 *            Name of permission required (i.e. worldgen.command.simple)
	 * @param dispName
	 *            Name of structure as displayed to the user/logger
	 */
	public static void generate(Player player, int radius, String namePerm, String nameDisplay, String nameClass) {

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

			// Get current block
			Block block = player.getLocation().getBlock();

			// Position in the middle of the current chunk
			int x = block.getX();
			int y = block.getY();
			int z = block.getZ();

			// Prep the bounding box
			Constructor<?> cBox = i.clObjStrucBox.getConstructor(int.class, int.class, int.class, int.class, int.class, int.class);
			Object oBox = cBox.newInstance(x - radius, y, z - radius, x + radius, y + radius, z + radius);

			// Get the generation start object via our current block's chunk
			Constructor<?> cGen = i.clObjGenerator.getConstructor(int.class, Random.class, i.clObjStrucBox, int.class);
			cGen.setAccessible(true);

			Object oGen = cGen.newInstance(0, i.oRandom, oBox, 0);

			// Execute the generation start method
			@SuppressWarnings("rawtypes")
			Class[] parameterTypes = new Class[3];
			parameterTypes[0] = i.clObjWorld;
			parameterTypes[1] = i.oRandom.getClass();
			parameterTypes[2] = i.clObjStrucBox;
			Method a = i.clObjGenerator.getDeclaredMethod("a", parameterTypes);
			Boolean r = (Boolean) a.invoke(oGen, i.oCraftWorldHandle, i.oRandom, oBox);

			// Check results
			if (!r)
				player.sendMessage(ChatColor.RED + "Unable to generate a " + nameDisplay + " at this location.");
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
