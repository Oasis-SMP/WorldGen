package local.thehutman.worldgen.v1_8_R2;

import java.lang.reflect.Method;
import local.thehutman.worldgen.Utility;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

class WorldGenCave {

	public static void generate(Player player, int radius, String namePerm) {
		if (!(player.hasPermission(namePerm))) {

			player.sendMessage(ChatColor.RED + "You do not have permissions for generating that!");
			return;
		}

		Utility.log.info("Generating village...");
		
		try {

			// Get Crafting packages
			WorldInterface i = new WorldInterface(player, "WorldGenCaves");
			if (i.oCraftWorldHandle == null) {
				player.sendMessage(ChatColor.RED + "Failed to generate village. Please check server log.");
				return;
			}

			// Get current block position
			Block block = player.getLocation().getBlock();
			int x = block.getChunk().getX();
			int z = block.getChunk().getZ();
			
			int xx = block.getLocation().getBlockX();
			int yy = block.getLocation().getBlockY();
			int zz = block.getLocation().getBlockZ();

			double meDouble = radius;
			
			// Get the generation start object via our current block's chunk
			Object caves = i.clObjGenerator.getConstructor().newInstance();
			
			Object chunksnapshot = i.clObjChunkSnapshot.getConstructor().newInstance();
			Method m = i.clObjChunkSnapshot.getDeclaredMethod("a", int.class,int.class,int.class);
			Object test = m.invoke(chunksnapshot, xx,yy,zz);
			
			Method c = i.clObjGenerator.getDeclaredMethod("a", int.class,int.class,int.class,i.clObjChunkSnapshot,double.class,double.class,double.class);
			c.invoke(caves, xx,yy,zz,test,meDouble,meDouble,meDouble);

			// All done!
			Utility.log.info("Generated village at: (" + x + "," + z + ")");
			player.sendMessage("Generated a new village!");

		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "Failed to generate village. Please check server log.");
		}
		
	}

}
