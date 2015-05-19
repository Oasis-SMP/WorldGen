package local.thehutman.worldgen.v1_8_R2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Random;

import local.thehutman.worldgen.Utility;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

class WorldGenDungeon {

	public static void generate(Player player, int radius, String namePerm, String nameDisplay, String type, String nameClass) {

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
			int x = block.getChunk().getX();
			int z = block.getChunk().getZ();

			int blockx = block.getLocation().getBlockX();
			int blockz = block.getLocation().getBlockZ();
			int blocky = block.getLocation().getBlockY();

			// Get the generation start object via our current block's chunk
			Constructor<?> cGen = i.clObjGenerator.getConstructor();
			cGen.setAccessible(true);
			Object oGen = cGen.newInstance();

			Utility.log.info(i.clObjGenerator.getName());
			Field mob = i.clObjGenerator.getDeclaredField("b");
			mob.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(mob, mob.getModifiers() & ~Modifier.FINAL);

			String [] things = {type, type, type, type};
			mob.set(i.clObjGenerator, things);
			mob.setAccessible(false);
			// Move to middle of chunk
			x = (x << 4) + 8;
			z = (z << 4) + 8;

			Constructor<?> cBlockPos = i.clObjBlockPos.getConstructor(int.class,int.class,int.class);
			Object oBlock;

			//			// Prep the bounding box
			//			Constructor<?> cBox = i.clObjStrucBox.getConstructor(int.class, int.class, int.class, int.class);
			//			Object oBox = cBox.newInstance(x - radius, z - radius, x + radius, z + radius);

			boolean test = false;
			Method generate = i.clObjGenerator.getDeclaredMethod("generate", i.clObjWorld, Random.class,i.clObjBlockPos);
			int X = blockx;
			int Y = blocky;
			int Z = blockz;

			int xx = (int) (X -radius);
			int yy = (int) (Y -radius);
			int zz = (int) (Z -radius);

			int bx = xx;
			int bz = zz;


			for (int ii=0; ii<radius*2+1; ii++) {
				for (int jj=0; jj<radius*2+1; jj++) {
					for (int kk=0; kk<radius*2+1; kk++) {
						Utility.log("test");
						oBlock = cBlockPos.newInstance(xx,yy,zz);
						test = (Boolean) generate.invoke(oGen, i.oCraftWorldHandle, i.oRandom, oBlock);
						Utility.log(Boolean.toString(test));
						if(test){
							findMossy(xx,yy,zz,radius,player.getWorld());
							// All done!
							Utility.log("Generated " + nameDisplay + " at: (" + x + "," + z + ")");
							player.sendMessage("Generated a new " + nameDisplay + "!");
							return;
						}
						xx++;
					}
					zz++;
					xx = bx;
				}
				zz = bz;
				xx = bx;
				yy++;
			}

			// All done!
			Utility.log("Could not generate " + nameDisplay + " at: (" + x + "," + z + ")");
			Utility.sendMsg(player,"Could not generate " + nameDisplay + "!");

		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "Failed to generate " + nameDisplay + ". Please check server log.");
		}

	}
	
	public static void findMossy(int x, int y, int z, int radius, World world){
		
		int xx = (int) (x -radius);
		int yy = (int) (y -radius);
		int zz = (int) (z -radius);

		int bx = xx;
		int bz = zz;
		
		for (int a=0; a<radius*2+1; a++) {
			for (int b=0; b<radius*2+1; b++) {
				for (int c=0; c<radius*2+1; c++) {
					Block block = new Location(world,xx,yy,zz).getBlock();
					if(block.getType().equals(Material.MOSSY_COBBLESTONE)){
						setGrass(world,xx,yy,zz);
					}
					xx++;
				}
				zz++;
				xx = bx;
			}
			zz = bz;
			xx = bx;
			yy++;
		}
	}
	
	public static void setGrass(World world, int x, int y , int z){
		for(int xx=x-10; xx<x+10;xx++){
			for(int zz=z-10;zz<z+10;zz++){
				Block block = new Location(world,xx,y,zz).getBlock();
				if(block.getType().equals(Material.COBBLESTONE) || block.getType().equals(Material.MOSSY_COBBLESTONE)){
					block.setType(Material.GRASS);
				}
			}
		}
	}
	
	public EntityType checkType(String type){
		return EntityType.fromName(type);
	}
	
//	public boolean isHostile(EntityType eType){
//		if(eType instanceof Monster){
//			return true;
//		}
//		
//		return false;
//	}

}
