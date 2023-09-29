package com.ashkiano.tradertoken;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TraderToken extends JavaPlugin implements Listener {

    private String commandPermission;
    private String itemName;
    private List<String> itemLore;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        commandPermission = config.getString("commandPermission", "tradertoken.givetoken");
        itemName = config.getString("itemName", "Trader Token");
        itemLore = config.getStringList("itemLore");

        Bukkit.getPluginManager().registerEvents(this, this);
        Metrics metrics = new Metrics(this, 19515);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("givetoken")) {
            if (sender.hasPermission(commandPermission)) {
                if (args.length == 1) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        giveToken(target);
                        sender.sendMessage("Token has been given to " + target.getName());
                    } else {
                        sender.sendMessage("Player is not online.");
                    }
                    return true;
                }
            } else {
                sender.sendMessage("You don't have permission to use this command.");
            }
        }
        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.EMERALD) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore() && meta.getLore().equals(itemLore)) {
                event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.WANDERING_TRADER);
                if(item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    event.getPlayer().getInventory().remove(item);
                }
                event.getPlayer().sendMessage("You have summoned a wandering trader!");
            }
        }
    }

    private void giveToken(Player player) {
        ItemStack token = new ItemStack(Material.EMERALD, 1);
        ItemMeta meta = token.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(itemName);
            meta.setLore(itemLore);
            token.setItemMeta(meta);
            player.getInventory().addItem(token);
        }
    }
}
