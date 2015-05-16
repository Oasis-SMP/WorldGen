/**
 * WorldGen plugin - (c) 2013 by Michael Huttinger (TheHUTMan)
 * LPGL v3.0 License
 */
package local.thehutman.worldgen;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is a plugin for Bukkit that provides some manully started
 * world-generation commands for things like villages, witch's hut, and
 * strongholds.
 * 
 * Credit to xxx and yyy for some initial code ideas for generating villages,
 * which I have been extended into a much more robust solution that removes
 * dependency issues with server build classes via reflection. I then spent some
 * time working out how to generate other structures :)
 * 
 * @author Huttinger
 * 
 */
public class WorldGen extends JavaPlugin{

	@Override
	public void onEnable() {

		// Init commands
		getCommand("worldgen");

		// Save our logger for utility work later
		Utility.log = getLogger();
		Utility.log.info("Plugin has been enabled!");
	}

	

}
