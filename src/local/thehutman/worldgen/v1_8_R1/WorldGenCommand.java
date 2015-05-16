package local.thehutman.worldgen.v1_8_R1;

import local.thehutman.worldgen.WorldGen;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldGenCommand implements CommandExecutor{

	private WorldGen plugin;

	public WorldGenCommand (WorldGen plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		// Get player reference
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (cmd.getName().equalsIgnoreCase("worldgen")) {

			// Check for valid args
			if (args.length > 2)
				return false;

			String type = "";
			int radius = 200;

			if (args.length >= 1)
				type = args[0].toLowerCase();
			if (args.length == 2)
				radius = Integer.parseInt(args[1]);

			// Check if this is a player, or if we are asking for help
			if (!(sender instanceof Player)) {
				if (!type.equals("") && !type.equals("help")) {
					sender.sendMessage(ChatColor.RED + "You need to be in-game to use WorldGen commands!");
					return false;
				}
				type = "help";
			}

			// Generate based on given type
			if (type.equals("village")) {
				// This ONLY works outside of the NETHER.
				if (player.getWorld().getEnvironment().getId() == -1) {
					player.sendMessage(ChatColor.RED + "Villages can not be generated in the Nether.");
					return true;
				}
				GenType2.generate(player, radius, "worldgen.command.village", "village", "WorldGenVillageStart");
			} else if (type.equals("witch") || type.equals("witchhut")) {
				GenType1.generate(player, radius, "worldgen.command.witch", "witch's hut", "WorldGenWitchHut");
			} else if (type.equals("jtemple") || type.equals("jungletemple")) {
				GenType1.generate(player, radius, "worldgen.command.jungletemple", "jungle temple", "WorldGenJungleTemple");
			} else if (type.equals("dtemple") || type.equals("deserttemple")) {
				GenType1.generate(player, radius, "worldgen.command.deserttemple", "desert temple", "WorldGenPyramidPiece");
			} else if (type.equals("well") || type.equals("desertwell")) {
				// You must be on sand for a well to generate
				GenType4.generate(player, "worldgen.command.simple", "well", "WorldGenDesertWell");
			} else if (type.equals("mushroom") || type.equals("shroom")) {
				// You must be ON grass, dirt, or mycelium for a shroom to
				// generate. Seems to be a bit picky as to location, so try
				// around the area to get one to generate.
				GenType4.generate(player, "worldgen.command.simple", "huge mushroom", "WorldGenHugeMushroom");
			} else if (type.equals("reed")) {
				// Note -- you must be ON grass, dirt, or sand next to water
				// for reeds to generate
				GenType4.generate(player, "worldgen.command.simple", "reeds", "WorldGenReed");
			} else if (type.equals("lily")) {
				// You must be over water for lilies to generate
				GenType4.generate(player, "worldgen.command.simple", "water lilies", "WorldGenWaterLily");
			} else if (type.equals("pumpkin")) {
				// Needs to be ON grass to work.
				GenType4.generate(player, "worldgen.command.simple", "pumpkins", "WorldGenPumpkin");
			} else if (type.equals("swamptree")) {
				// Generate on grass or dirt (can be in shallow water)
				GenType4.generate(player, "worldgen.command.simple", "swamp tree", "WorldGenSwampTree");
			} else if (type.equals("stronghold")) {
				GenType3.generate(player, radius, "worldgen.command.stronghold", "stronghold", "WorldGenStronghold2Start");
			} else if (type.equals("monument") || type.equals("wtemple") || type.equals("watertemple")){
				GenType3.generate(player, radius, "worldgen.command.monument","ocean monument", "WorldGenMonumentStart");
			} else if (type.equals("mineshaft")) {
				GenType3.generate(player, radius, "worldgen.command.mineshaft", "mineshaft", "WorldGenMineshaftStart");
			} else if (type.equals("shportal") || type.equals("strongholdportal")) {
				GenType5.generate(player, 10, "worldgen.command.stronghold", "stronghold portal room", "WorldGenStrongholdPortalRoom");
			} else if (type.equals("netherfortress") || type.equals("fortress")) {
				GenType3.generate(player, radius, "worldgen.command.nether", "nether fortress", "WorldGenNetherStart");
			} else if (type.equals("vblack")) {
				GenType6.generate(player, 10, "worldgen.command.village", "village blacksmith", "WorldGenVillageBlacksmith", "b", 0);
			} else if (type.equals("vbutcher")) {
				GenType6.generate(player, 10, "worldgen.command.village", "village butcher", "WorldGenVillageButcher", "a", 0);
			} else if (type.equals("vfarm")) {
				GenType6.generate(player, 10, "worldgen.command.village", "village farm", "WorldGenVillageFarm", "a", -1);
			} else if (type.equals("vfarm2")) {
				GenType6.generate(player, 10, "worldgen.command.village", "village farm (type2)", "WorldGenVillageFarm2", "a", -1);
			} else if (type.equals("vhouse")) {
				GenType6.generate(player, 10, "worldgen.command.village", "village house", "WorldGenVillageHouse", "a", 0);
			} else if (type.equals("vhouse2")) {
				GenType6.generate(player, 10, "worldgen.command.village", "village house (type2)", "WorldGenVillageHouse2", "a", 0);
			} else if (type.equals("vhut")) {
				GenType6.generate(player, 10, "worldgen.command.village", "village hut", "WorldGenVillageHut", "a", 0);
			} else if (type.equals("vlibrary") ){
				GenType6.generate(player, 10, "worldgen.command.village", "village library", "WorldGenVillageLibrary", "a", 0);
			} else if (type.equals("vlight")) {
				GenType6.generate(player, 10, "worldgen.command.village", "village light pole", "WorldGenVillageLight", "a", -1);
			} else if (type.equals("vtemple")) {
				GenType6.generate(player, 10, "worldgen.command.village", "village temple", "WorldGenVillageTemple", "a", 0);
			} else if (type.equals("help")) {

				// Get page number (next parameter)
				if (radius == 200)
					radius = 1;
				if (radius > 3)
					radius = 3;

				if (sender instanceof Player)
					sender.sendMessage(ChatColor.YELLOW + "-----" + ChatColor.WHITE + " WorldGen Commands (" + radius + "/3) " + ChatColor.YELLOW + "-----");
				else
					sender.sendMessage(ChatColor.YELLOW + "-----" + ChatColor.WHITE + " WorldGen Commands " + ChatColor.YELLOW + "-----");

				if (radius == 1 || !(sender instanceof Player)) {
					sender.sendMessage(ChatColor.YELLOW + "/worldgen village [radius]" + ChatColor.WHITE + " - Generate a village");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen stronghold [radius]" + ChatColor.WHITE + " - Generate a stronghold");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen mineshaft [radius]" + ChatColor.WHITE + " - Generate a mineshaft");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen fortress [radius]" + ChatColor.WHITE + " - Generate a nether fortress");
				}
				if (radius == 2 || !(sender instanceof Player)) {
					sender.sendMessage(ChatColor.YELLOW + "/worldgen witch" + ChatColor.WHITE + " - Generate a witch's hut");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen monument/wtemple/watertemple" + ChatColor.WHITE + " - Generate an Ocean Monument");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen jtemple" + ChatColor.WHITE + " - Generate a jungle temple");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen dtemple" + ChatColor.WHITE + " - Generate a desert temple");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen well" + ChatColor.WHITE + " - Generate a desert well");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen shroom" + ChatColor.WHITE + " - Generate a giant mushroom");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen reed" + ChatColor.WHITE + " - Generate sugar cane field (near water)");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen lily" + ChatColor.WHITE + " - Generate a lily patch (on water)");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen pumpkin" + ChatColor.WHITE + " - Generate a pumpkin patch");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen swamptree" + ChatColor.WHITE + " - Generate a swamp tree");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen shportal" + ChatColor.WHITE + " - Generate a stronghold portal room");
				}
				if (radius == 3 || !(sender instanceof Player)) {
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vblack" + ChatColor.WHITE + " - Generate a village blacksmith");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vbutcher" + ChatColor.WHITE + " - Generate a village butcher");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vfarm" + ChatColor.WHITE + " - Generate a village farm");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vfarm2" + ChatColor.WHITE + " - Generate a village farm x2");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vhouse" + ChatColor.WHITE + " - Generate a village house");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vhouse2" + ChatColor.WHITE + " - Generate a village house x2");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vhut" + ChatColor.WHITE + " - Generate a village hut");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vlibrary" + ChatColor.WHITE + " - Generate a village library");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vlight" + ChatColor.WHITE + " - Generate a village light pole");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vtemple" + ChatColor.WHITE + " - Generate a village temple");
				}
				return true;
			} else {
				// Invalid type specified. Show valid list and exit.
				sender.sendMessage(ChatColor.RED + "Invalid WorldGen command specified.");
				return false;
			}

			// Complete!
			return true;
		}

		// Bad command given .. return help
		return false;
	}

}
