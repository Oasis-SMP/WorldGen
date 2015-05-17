package local.thehutman.worldgen.v1_8_R2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;

import local.thehutman.worldgen.Utility;
import local.thehutman.worldgen.v1_8_R2.WorldInterface;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

class WorldGenMineshaft {
	
	public static void generate(Player player, int radius, String namePerm) {

		if (!(player.hasPermission(namePerm))) {

			player.sendMessage(ChatColor.RED + "You do not have permissions for generating that!");
			return;
		}

		Utility.log.info("Generating mineshaft...");

		try {

			// Get Crafting packages
			WorldInterface i = new WorldInterface(player, "WorldGenMineshaftStart");
			if(i.oCraftWorldHandle == null)
			{
				player.sendMessage(ChatColor.RED + "Failed to generate mineshaft. Please check server log.");
				return;
			}

			// Get current block position
			Block block = player.getLocation().getBlock();
			int x = block.getChunk().getX();
			int z = block.getChunk().getZ();

			// Get the generation start object via our current block's chunk
			Constructor<?> cGen = i.clObjGenerator.getConstructor(i.clObjWorld, Random.class, int.class, int.class);
			cGen.setAccessible(true);
			Object oGen = cGen.newInstance(i.oCraftWorldHandle, i.oRandom, x, z);

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
			Utility.log.info("Generated mineshaft at: (" + x + "," + z + ")");
			player.sendMessage("Generated a new mineshaft!");

		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "Failed to generate mineshaft. Please check server log.");
		}

	}

}