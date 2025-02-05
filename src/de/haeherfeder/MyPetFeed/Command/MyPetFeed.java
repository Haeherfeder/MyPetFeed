package de.haeherfeder.MyPetFeed.Command;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.Configuration;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.entity.MyPetType;
import de.Keyle.MyPet.api.event.MyPetFeedEvent;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.api.util.ConfigItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class MyPetFeed implements CommandExecutor {
    private final FileConfiguration config;

    public MyPetFeed(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player p)) {
            commandSender.sendMessage("only possible as Player");
            return false;
        }
        MyPetPlayer mp = MyPetApi.getPlayerManager().getMyPetPlayer(p);
        if(mp == null){
            p.sendMessage("Couldn't get Mypet Player");
            return false;
        }
        MyPet pet = mp.getMyPet();
        if(pet == null){
            p.sendMessage("you dont have a pet.");
            return false;
        }
//        System.out.println(MyPetApi.getMyPetInfo().getFood(pet.getPetType()));
        if(config.getBoolean("only_enderdragon", true) && !pet.getPetType().equals(MyPetType.EnderDragon)){
            p.sendMessage("Your pet is not an Enderdragon. please feed it yourself");
            return true;
        }
        if (!config.getBoolean("remove_items", false)) {
            pet.setSaturation(100);
            return true;
        }
        double sat = pet.getSaturation();
        double sat_per_feed = Configuration.HungerSystem.HUNGER_SYSTEM_SATURATION_PER_FEED;
        if(sat_per_feed == 0){
            p.sendMessage("cant feed, as the feed amount is 0, and use items is activated.");
            return true;
        }
        int needed_to_feed = (int) Math.round((100 - sat)/sat_per_feed);
        if(needed_to_feed == 0){
            p.sendMessage("feeding should not be needed needed");
            return true;
        }
        for(ConfigItem i: MyPetApi.getMyPetInfo().getFood(pet.getPetType())){
            if(p.getInventory().contains(i.getItem().getType())){
                int amount = getItemAmount(i.getItem().getType(), p);
                if (amount < needed_to_feed){
                    MyPetFeedEvent feedEvent = new MyPetFeedEvent(pet, new ItemStack(i.getItem().getType(),amount), amount * sat_per_feed, MyPetFeedEvent.Result.Eat);
                    Bukkit.getPluginManager().callEvent(feedEvent);
                    if (!feedEvent.isCancelled()) {
                        p.getInventory().removeItem(new ItemStack(i.getItem().getType(), amount));
                        pet.setSaturation(pet.getSaturation() + feedEvent.getSaturation());
                        needed_to_feed -= amount;
                        p.sendMessage("pet partly feed.");
                    }
                } else {
                    MyPetFeedEvent feedEvent = new MyPetFeedEvent(pet, new ItemStack(i.getItem().getType(),needed_to_feed), needed_to_feed * sat_per_feed, MyPetFeedEvent.Result.Eat);
                    Bukkit.getPluginManager().callEvent(feedEvent);
                    if (!feedEvent.isCancelled()) {
                        p.getInventory().removeItem(new ItemStack(i.getItem().getType(), needed_to_feed));
                        pet.setSaturation(pet.getSaturation() + feedEvent.getSaturation());
                        p.sendMessage("pet feed.");
                        return true;
                    }
                }
            }
        }
        return true;
    }

    private int getItemAmount(Material type, Player player) {
        int amount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == type) {
                amount += item.getAmount();
            }
        }
        return amount;
    }
}
