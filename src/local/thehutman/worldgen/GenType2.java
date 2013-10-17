/**
 * WorldGen plugin - (c) 2013 by Michael Huttinger (TheHUTMan)
 * LPGL v3.0 License
 */
package local.thehutman.worldgen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Type2 generation of a Minecraft structure. This has requirements of:
 * 
 * A constructor: c(World,Random,int,int,int) and inherits from StructureStart.
 * 
 * A method: a(World,Random,StructureBoundingBox)
 * 
 * Example: WorldGenVillageStart
 * 
 * @author Huttinger
 * 
 */
class GenType2 {

	/**
	 * Type2 generation of a Minecraft structure. This has requirements of:
	 * 
	 * A constructor: c(World,Random,int,int,int) and inherits from
	 * StructureStart.
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
			if (i.oCraftWorldHandle == null) {
				player.sendMessage(ChatColor.RED + "Failed to generate " + nameDisplay + ". Please check server log.");
				return;
			}

			// Get current block position
			Block block = player.getLocation().getBlock();
			int x = block.getChunk().getX();
			int z = block.getChunk().getZ();

			// Get the generation start object via our current block's chunk
			Constructor<?> cGen = i.clObjGenerator.getConstructor(i.clObjWorld, Random.class, int.class, int.class, int.class);
			cGen.setAccessible(true);
			Object oGen = cGen.newInstance(i.oCraftWorldHandle, i.oRandom, x, z, 0);

			// Move to middle of chunk
			x = (x << 4) + 8;
			z = (z << 4) + 8;

			// Prep the bounding box
			Constructor<?> cBox = i.clObjStrucBox.getConstructor(int.class, int.class, int.class, int.class);
			Object oBox = cBox.newInstance(x - radius, z - radius, x + radius, z + radius);

			// Execute the generation start method
			@SuppressWarnings("rawtypes")
			Class[] parameterTypes = new Class[3];
			parameterTypes[0] = i.clObjWorld;
			parameterTypes[1] = i.oRandom.getClass();
			parameterTypes[2] = i.clObjStrucBox;
			Method a = i.clObjStruc.getDeclaredMethod("a", parameterTypes);
			a.invoke(oGen, i.oCraftWorldHandle, i.oRandom, oBox);

			// All done!
			Utility.log.info("Generated " + nameDisplay + " at: (" + x + "," + z + ")");
			player.sendMessage("Generated a new " + nameDisplay + "!");

		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "Failed to generate " + nameDisplay + ". Please check server log.");
		}

	}

}
