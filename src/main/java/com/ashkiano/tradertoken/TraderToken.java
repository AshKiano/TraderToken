package com.ashkiano.tradertoken;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

import java.util.Arrays;

public class TraderToken extends JavaPlugin implements Listener {

    private final String TOKEN_LORE = "Special token to summon a wandering trader";

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Metrics metrics = new Metrics(this, 19515);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("givetoken")) {
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
        }
        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.EMERALD) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore() && meta.getLore().contains(TOKEN_LORE)) {
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
            meta.setDisplayName("Trader Token");
            meta.setLore(Arrays.asList(TOKEN_LORE));
            token.setItemMeta(meta);
            player.getInventory().addItem(token);
        }
    }
}