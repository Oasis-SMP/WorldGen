/**
 * WorldGen plugin - (c) 2013 by Michael Huttinger (TheHUTMan)
 * LPGL v3.0 License
 */
package local.thehutman.worldgen.v1_8_R1;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;

import local.thehutman.worldgen.Utility;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Type1 generation of a Minecraft structure. This has requirements of:
 * 
 * A constructor: c(Random,int,int)
 * 
 * A method: a(World,Random,StructureBoundingBox)
 * 
 * Example: WorldGenJungleTemple
 * 
 * @author Huttinger
 */
class GenType1 {

	/**
	 * Type1 generation of a Minecraft structure. This has requirements of:
	 * 
	 * A constructor: c(Random,int,int)
	 * 
	 * A method: a(World,Random,StructureBoundingBox)
	 * 
	 * @param player
	 *            Player object
	 * @param radius
	 *            Radius to limit creation to
	 * @param namePerm
	 *            Name of permission required (i.e. worldgen.command.simple)
	 * @param nameDisplay
	 *            Name of structure being built as displayed to the user/logger
	 * @param nameClass
	 *            Name of class for structure to build.
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
			
			// Get current block position
			Block block = player.getLocation().getBlock();
			int x = block.getX();
			int z = block.getZ();

			// Get the witch hut gen object via our current block's chunk
			Constructor<?> cGen = i.clObjGenerator.getConstructor(Random.class, int.class, int.class);
			cGen.setAccessible(true);
			Object oGen = cGen.newInstance(i.oRandom, x, z);

			// Prep the bounding box
			Constructor<?> cBox = i.clObjStrucBox.getConstructor(int.class, int.class, int.class, int.class);
			Object oBox = cBox.newInstance(x - radius, z - radius, x + radius, z + radius);

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
