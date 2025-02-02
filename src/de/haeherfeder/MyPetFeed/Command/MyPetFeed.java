package de.haeherfeder.MyPetFeed.Command;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.entity.MyPetType;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyPetFeed implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(command instanceof Player)){
            commandSender.sendMessage("only possible as Player");
            return false;
        }
        Player p = (Player) commandSender;
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
        System.out.println(MyPetApi.getMyPetInfo().getFood(pet.getPetType()));
        if(!pet.getPetType().equals(MyPetType.EnderDragon)){
            p.sendMessage("Your pet is not an Enderdragon. please feed it yourself");
            return true;
        }
        System.out.println(pet.getSaturation());
        pet.setSaturation(100);
        return true;
    }
}
