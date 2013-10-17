package net.minecraft.server.v1_5_R2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Iterator;

/**
 * This is a customized version of the WorldGenVillage plugin for Bukkit that
 * provides the ability to generate villages in different biomes than the
 * standard plains and desert.
 * 
 * THIS IS A SERVER MOD.  You must place the .class file (contained in the jar) INTO
 * the craftbukkit.jar file for this to work.
 * 
 * You can then use the WorldGen /worldgen village command to make villages anywhere
 * you want.
 * 
 * Nether generation doesn't work so well, since it seems to want to place the village
 * on the top-o-the-world.
 * 
 * @author Huttinger
 *
 */
@SuppressWarnings("rawtypes")
public class WorldGenVillage extends StructureGenerator {

	public static final List e = Arrays.asList(new BiomeBase[] { 
			BiomeBase.PLAINS
			,BiomeBase.DESERT
			// These are added biomes we support making villages in
			,BiomeBase.BEACH
			,BiomeBase.DESERT_HILLS
			,BiomeBase.EXTREME_HILLS
			,BiomeBase.FOREST
			,BiomeBase.FOREST_HILLS
			,BiomeBase.FROZEN_OCEAN
			,BiomeBase.FROZEN_RIVER
			,BiomeBase.ICE_MOUNTAINS
			,BiomeBase.ICE_PLAINS
			,BiomeBase.JUNGLE
			,BiomeBase.JUNGLE_HILLS
			,BiomeBase.MUSHROOM_ISLAND
			,BiomeBase.MUSHROOM_SHORE
			,BiomeBase.RIVER
			,BiomeBase.SMALL_MOUNTAINS
			,BiomeBase.SWAMPLAND
			,BiomeBase.TAIGA
			,BiomeBase.TAIGA_HILLS
			// Non-normal biomes
			,BiomeBase.SKY	// The-End
			// Non-working or buggy
			//,BiomeBase.OCEAN	// Just look wierd
			//,BiomeBase.HELL	// Nether doesn't seem to work well since it puts everything over-the-top.
			 });

	private int f;
	private int g;
	private int h;

	public WorldGenVillage() {
		this.f = 0;
		this.g = 32;
		this.h = 8;
	}

	public WorldGenVillage(Map map) {
		this();
		Iterator iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();

			if (((String) entry.getKey()).equals("size")) {
				this.f = MathHelper.a((String) entry.getValue(), this.f, 0);
			} else if (((String) entry.getKey()).equals("distance")) {
				this.g = MathHelper.a((String) entry.getValue(), this.g, this.h + 1);
			}
		}
	}

	protected boolean a(int i, int j) {
		int k = i;
		int l = j;

		if (i < 0) {
			i -= this.g - 1;
		}

		if (j < 0) {
			j -= this.g - 1;
		}

		int i1 = i / this.g;
		int j1 = j / this.g;
		Random random = this.c.H(i1, j1, 10387312);

		i1 *= this.g;
		j1 *= this.g;
		i1 += random.nextInt(this.g - this.h);
		j1 += random.nextInt(this.g - this.h);
		if (k == i1 && l == j1) {
			boolean flag = this.c.getWorldChunkManager().a(k * 16 + 8, l * 16 + 8, 0, e);

			if (flag) {
				return true;
			}
		}

		return false;
	}

	protected StructureStart b(int i, int j) {
		return new WorldGenVillageStart(this.c, this.b, i, j, this.f);
	}
}