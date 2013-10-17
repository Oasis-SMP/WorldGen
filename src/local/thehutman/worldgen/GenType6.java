/**
 * WorldGen plugin - (c) 2013 by Michael Huttinger (TheHUTMan)
 * LPGL v3.0 License
 */
package local.thehutman.worldgen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Type6 generation of a Minecraft structure. This is used specifically to
 * generate custom village structures at the user's current coordinates.
 * 
 * @author Huttinger
 * 
 */
class GenType6 {

	/**
	 * Type6 generation of a Minecraft structure. This is used specifically to
	 * generate custom village structures at the user's current coordinates.
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
	 * @param nameFID
	 *            Name of the field ID used to flag rendering of the village
	 *            (some use a, some b)
	 * @param Offset
	 *            Vertical start offset from current land position.
	 */
	public static void generate(Player player, int radius, String namePerm, String nameDisplay, String nameClass, String nameFID, int Offset) {

		if (!(player.hasPermission(namePerm))) {

			player.sendMessage(ChatColor.RED + "You do not have permissions for generating that!");
			return;
		}

		Utility.log.info("Generating " + nameDisplay + "...");

		try {

			// Get Crafting packages
			WorldInterface i = new WorldInterface(player, "WorldGenVillageStart");
			if (i.oCraftWorldHandle == null) {
				player.sendMessage(ChatColor.RED + "Failed to generate " + nameDisplay + ". Please check server log.");
				return;
			}

			// Get current block position
			Block block = player.getLocation().getBlock();
			Block cblock = block.getRelative(-radius, 0, -radius);
			int x = cblock.getChunk().getX();
			int z = cblock.getChunk().getZ();

			// Get the generation start object via our current block's chunk
			Constructor<?> cGen = i.clObjGenerator.getConstructor(i.clObjWorld, Random.class, int.class, int.class, int.class);
			cGen.setAccessible(true);
			Object oGen = cGen.newInstance(i.oCraftWorldHandle, i.oRandom, x, z, 0);

			// Move to middle of chunk
			x = block.getX() - radius;
			z = block.getZ() + radius;

			// Check our area for best Y coordinate
			int y = 512;
			for (int xx = block.getX() - radius; xx <= block.getX(); ++xx) {
				for (int zz = block.getZ(); zz <= block.getZ() + radius; ++zz) {
					int yy = player.getWorld().getHighestBlockYAt(xx, zz);
					y = Math.min(y, yy - Offset);
				}
			}

			// Prep the bounding box
			Constructor<?> cBox = i.clObjStrucBox.getConstructor(int.class, int.class, int.class, int.class, int.class, int.class);
			Object oBox = cBox.newInstance(x - radius, y, z - radius, x + radius, y + 50, z + radius);

			// Get start position
			Method mStartPiece = i.clObjGenerator.getMethod("b");
			@SuppressWarnings("rawtypes")
			LinkedList lPieces = (LinkedList) mStartPiece.invoke(oGen);

			// Now, create an instance of our custom class
			Class<?> clStartPiece = Class.forName(i.pckMinecraft + ".WorldGenVillageStartPiece");
			Class<?> clBuild = Class.forName(i.pckMinecraft + "." + nameClass);
			Constructor<?> cBuild = clBuild.getConstructor(clStartPiece, int.class, i.oRandom.getClass(), i.clObjStrucBox, int.class);
			Object oBuild = cBuild.newInstance(lPieces.getFirst(), 1, i.oRandom, oBox, 1);

			// Init the custom attribute to force generation on location.
			Field fA = clBuild.getDeclaredField(nameFID);
			fA.setAccessible(true);
			fA.set(oBuild, 1);

			// Execute the generation start method
			@SuppressWarnings("rawtypes")
			Class[] parameterTypes = new Class[3];
			parameterTypes[0] = i.clObjWorld;
			parameterTypes[1] = i.oRandom.getClass();
			parameterTypes[2] = i.clObjStrucBox;
			Method a = clBuild.getMethod("a", parameterTypes);
			Boolean r = (Boolean) a.invoke(oBuild, i.oCraftWorldHandle, i.oRandom, oBox);

			if (!r)
				player.sendMessage(ChatColor.RED + "Unable to generate a " + nameDisplay + " at this location.");
			else {
				Utility.log.info("Generated " + nameDisplay + " at: (" + x + "," + y + "," + z + ")");
				player.sendMessage("Generated a new " + nameDisplay + "!");
			}

		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "Failed to generate " + nameDisplay + ". Please check server log.");
		}

	}

}
