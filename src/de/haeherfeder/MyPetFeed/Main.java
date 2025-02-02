package de.haeherfeder.MyPetFeed;

import de.haeherfeder.MyPetFeed.Command.MyPetFeed;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable(){
        Bukkit.getPluginCommand("feed").setExecutor(new MyPetFeed());
    }
}
