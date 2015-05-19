/**
 * WorldGen plugin - (c) 2013 by Michael Huttinger (TheHUTMan)
 * LPGL v3.0 License
 */
package local.thehutman.worldgen.v1_8_R2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

import local.thehutman.worldgen.Utility;

import org.bukkit.WorldType;
import org.bukkit.entity.Player;

/**
 * Class used to get baseline class and object references into the Bukkit system via
 * reflection that are used in various generator constructs.
 * 
 * @author Huttinger
 *
 */
public class WorldInterface {

	// Package identifiers
	public String pckCraft;
	public String pckMinecraft;
	
	// Object references
	public Object oCraftWorldHandle;
	public Random oRandom;
	
	// Class references
	Class<?> clObjGenerator;
	Class<?> clObjWorld;
	Class<?> clObjWorldServer;
	Class<?> clObjWorldProvider;
	Class<?> clObjStrucBox;
	Class<?> clObjStruc;
	Class<?> clObjStart;
	Class<?> clObjBlockPos;
	Class<?> clObjChunkSnapshot;

	public WorldInterface(Player player, String nameClass) {
		
		// Initialize our member variables to work with our nameClass
		// constructor type
		try {

			pckCraft = Utility.FindPackage("CraftWorld");
			Class<?> clObjCraftWorld = Class.forName(pckCraft + ".CraftWorld");

			// Get the full class path minecraft class objects
			pckMinecraft = Utility.FindPackage(nameClass);
			clObjGenerator = Class.forName(pckMinecraft + "." + nameClass);
			clObjWorld = Class.forName(pckMinecraft + ".World");
			clObjWorldServer = Class.forName(pckMinecraft + ".WorldServer");
			clObjWorldProvider = Class.forName(pckMinecraft + ".WorldProvider");
			clObjStrucBox = Class.forName(pckMinecraft + ".StructureBoundingBox");
			clObjStruc = Class.forName(pckMinecraft + ".StructureStart");
			clObjBlockPos = Class.forName(pckMinecraft + ".BlockPosition");
			clObjChunkSnapshot = Class.forName(pckMinecraft + ".ChunkSnapshot");
			for(Class<?> myclass:clObjGenerator.getDeclaredClasses()){
				clObjStart = myclass;
				break;
			}

			// Generate object references we will need later
			
			Object oCraftWorld = player.getWorld();
			Method mGetHandle = clObjCraftWorld.getMethod("getHandle");
			oCraftWorldHandle = mGetHandle.invoke(oCraftWorld);

			Field fWorldProvider = clObjWorldServer.getField("worldProvider");
			Object oWorldProvider = fWorldProvider.get(oCraftWorldHandle);

			Method mGetChunkProvider = clObjWorldProvider.getMethod("getChunkProvider");
			Object oChunkProvider = mGetChunkProvider.invoke(oWorldProvider);

			Field randField = getChunkProvider(player);
			oRandom = (Random) randField.get(oChunkProvider);
			
		} catch (Exception e) {
			oCraftWorldHandle = null;
			oRandom = null;
			Utility.log.info(nameClass);
			Utility.log.info(pckCraft);
			Utility.log.info(pckMinecraft);
			e.printStackTrace();
		}
	}

	/**
	 * Get the chunk provider object for the current world.
	 * 
	 * @param player
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchFieldException
	 */
	private Field getChunkProvider(Player player) throws ClassNotFoundException, NoSuchFieldException {

		int envType = player.getWorld().getEnvironment().getId();
		Class<?> clObjChunkGen;
		Field randField;
		if (envType == -1) {
			clObjChunkGen = Class.forName(pckMinecraft + ".ChunkProviderHell");
			randField = clObjChunkGen.getDeclaredField("j");
		} else if (envType == 1) {
			clObjChunkGen = Class.forName(pckMinecraft + ".ChunkProviderTheEnd");
			randField = clObjChunkGen.getDeclaredField("h");
		} else {
			// Normal world has FLAT type generator
			if (player.getWorld().getWorldType() == WorldType.FLAT) {
				clObjChunkGen = Class.forName(pckMinecraft + ".ChunkProviderFlat");
				randField = clObjChunkGen.getDeclaredField("b");
			} else {
				clObjChunkGen = Class.forName(pckMinecraft + ".ChunkProviderGenerate");
				randField = clObjChunkGen.getDeclaredField("h");
			}
		}

		randField.setAccessible(true);
		return randField;
	}

}
