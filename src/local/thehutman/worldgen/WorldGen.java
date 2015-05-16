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
	
	public CommandExecutor wgen;

	@Override
	public void onEnable() {

		String packageName = this.getServer().getClass().getPackage().getName();
		String version = packageName.substring(packageName.lastIndexOf('.') + 1);
		if(version.contains("v1_7")){
			version.equals("v1_7_R3");
		}
		
		try {
            final Class<?> clazz = Class.forName("local.thehutman.worldgen." + version + ".WorldGenCommand");
            // Check if we have a NMSHandler class at that location.
            if (CommandExecutor.class.isAssignableFrom(clazz)) { // Make sure it actually implements NMS
                this.wgen = (CommandExecutor) clazz.getConstructor(WorldGen.class).newInstance(this); // Set our handler
            }
        } catch (final Exception e) {
            e.printStackTrace();
            this.getLogger().severe("Could not find support for this CraftBukkit version.");
            this.getLogger().info("Check for updates at http://dev.bukkit.org/bukkit-plugins/worldgen/");
            this.setEnabled(false);
            return;
        }
        this.getLogger().info("Loading support for " + version);
		
		// Init commands
		getCommand("worldgen").setExecutor(wgen);

		// Save our logger for utility work later
		Utility.log = getLogger();
		Utility.log.info("Plugin has been enabled!");
	}

	

}
