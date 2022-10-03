package com.kamran.coorddump.commands;

import com.kamran.coorddump.CoordDump;
import com.kamran.coorddump.discord.DiscordWebhook;
import com.kamran.coorddump.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

public class CoordDumpCommands implements CommandExecutor {

    private final String webhookURL;
    private final HashMap<World.Environment, String[]> environmentInfo;

    public CoordDumpCommands() {
        CoordDump plugin = CoordDump.getPlugin();
        webhookURL = plugin.getConfig().getString("discord-webhook");
        environmentInfo = new HashMap<>();
        environmentInfo.put(World.Environment.NORMAL, new String[] {"Overworld", "https://cdn.discordapp.com/attachments/821931312503193650/916926188847501332/grass.png"});
        environmentInfo.put(World.Environment.NETHER, new String[] {"Nether", "https://cdn.discordapp.com/attachments/821931312503193650/916924249699139655/netherrack.png"});
        environmentInfo.put(World.Environment.THE_END, new String[] {"The End", "https://cdn.discordapp.com/attachments/821931312503193650/916924575785316382/endstone.png"});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 1) { return baseCommand(sender); }

        else {
            if (args[0].equalsIgnoreCase("n") || args[0].equalsIgnoreCase("nether")) {
                return nether(sender);
            }
            if (args[0].equalsIgnoreCase("o") || args[0].equalsIgnoreCase("overworld")) {
                return overworld(sender);
            }
            if (args[0].equalsIgnoreCase("d") || args[0].equalsIgnoreCase("discord")) {
                return discord(sender, Util.trim(args));
            }
        }

        return false;
    }

    private boolean baseCommand(CommandSender sender) {
        if (sender instanceof Player player) {
            broadcastCoords(player);
            return true;
        }

        return false;
    }

    private boolean nether(CommandSender sender) {
        if (sender instanceof Player player) {

            if (player.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
                player.sendMessage("Cannot get nether coords in the end!");
                return true;
            }
            if (player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                Bukkit.broadcastMessage("<" + player.getDisplayName() + "> Nether coords:");
                broadcastCoords(player);
                return true;
            }
            if (player.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                Bukkit.broadcastMessage("<" + player.getDisplayName() + "> Nether coords:");
                int x = (int) player.getLocation().getX() / 8;
                int y = (int) player.getLocation().getY() / 8;
                int z = (int) player.getLocation().getZ() / 8;
                broadcastCoords(player, x, y, z);
                return true;
            }
        }

        return false;
    }

    private boolean overworld(CommandSender sender) {
        if (sender instanceof Player player) {

            if (player.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
                player.sendMessage("Cannot get overworld coords in the end!");
                return true;
            }
            if (player.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                Bukkit.broadcastMessage("<" + player.getDisplayName() + "> Overworld coords:");
                broadcastCoords(player);
                return true;
            }
            if (player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                Bukkit.broadcastMessage("<" + player.getDisplayName() + "> Overworld coords:");
                int x = (int) player.getLocation().getX() * 8;
                int y = (int) player.getLocation().getY() * 8;
                int z = (int) player.getLocation().getZ() * 8;
                broadcastCoords(player, x, y, z);
                return true;
            }
        }

        return false;
    }

    private boolean discord(CommandSender sender, String[] args) {
        if (webhookURL == null) {
            sender.sendMessage(ChatColor.RED + "Webhook URL is not setup in the config!");
            return true;
        }

        if (sender instanceof Player player) {
            int x = (int) player.getLocation().getX();
            int y = (int) player.getLocation().getY();
            int z = (int) player.getLocation().getZ();
            String title = environmentInfo.get(player.getWorld().getEnvironment())[0];
            String thumbnail = environmentInfo.get(player.getWorld().getEnvironment())[1];
            String playerIcon = String.format("https://crafatar.com/avatars/%s?default=MHF_Steve", player.getUniqueId());

            // If the player added a message in the command, replace title with it
            if (args.length > 0) {
                title = String.join(" ", args);
            }

            // Post location to discord webhook
            DiscordWebhook webhook = new DiscordWebhook(webhookURL);
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setColor(Color.GREEN)
                    .setTitle(title)
                    .setThumbnail(thumbnail)
                    .addField("Coordinates:", String.format("X:`%d` Y:`%d` Z:`%d`", x, y, z), true)
                    .setAuthor(player.getName(), "", playerIcon));
            try {
                webhook.execute();
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "Unable to post to discord!");
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    private void broadcastCoords(Player player) {
        Bukkit.broadcastMessage("<" + player.getDisplayName() + ">" +
                ChatColor.GRAY + " X: " + ChatColor.RED + (int) player.getLocation().getX() +
                ChatColor.GRAY + " Y: " + ChatColor.GREEN + (int) player.getLocation().getY() +
                ChatColor.GRAY + " Z: " + ChatColor.AQUA + (int) player.getLocation().getZ());
    }

    private void broadcastCoords(Player player, int x, int y, int z) {
        Bukkit.broadcastMessage("<" + player.getDisplayName() + ">" +
                ChatColor.GRAY + " X: " + ChatColor.RED + x +
                ChatColor.GRAY + " Y: " + ChatColor.GREEN + y +
                ChatColor.GRAY + " Z: " + ChatColor.AQUA + z);
    }

}
