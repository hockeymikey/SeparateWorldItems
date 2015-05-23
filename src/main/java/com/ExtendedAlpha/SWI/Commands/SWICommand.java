/*
 * This file is part of SeparateWorldItems, licensed under the MIT License (MIT).
 *
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ExtendedAlpha.SWI.Commands;

import com.ExtendedAlpha.SWI.SeparateWorldItems;
import com.ExtendedAlpha.SWI.SeparatorLib.PlayerSeparator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class SWICommand implements CommandExecutor {

    private enum Commands {CONVERT, HELP, RELOAD, SETWORLDDEFAULT}

    private SeparateWorldItems plugin;

    private final String NO_PERMISSION = "You do not have permission to do that.";
    private final String PERMISSION_NODE = "separateworlditems.";

    public SWICommand(SeparateWorldItems plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean isPlayer = false;
        Player player = null;
        if (sender instanceof Player) {
            isPlayer = true;
            player = (Player) sender;
        }

        Commands command;
        try {
            command = Commands.valueOf(args[0].toUpperCase());
        } catch (
                ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
            if (isPlayer) {
                plugin.getPrinter().printToPlayer((Player) sender, "Not a valid command. Please type /swi help for help.", true);
            } else {
                displayConsoleHelp();
            }

            return true;
        }

        switch (command) {
            case CONVERT:
                if (isPlayer) {
                    if (player.hasPermission(PERMISSION_NODE + "convert")) {
                        if (args.length == 2) {
                            switch (args[1].toUpperCase()) {
                                case "MULTIVERSE":
                                    plugin.getPrinter().printToPlayer(player, "Converting from Multiverse-Inventories! All messages are sent to console!", false);
                                    mvConvert();
                                    break;
                                case "MULTIINV":
                                    plugin.getPrinter().printToPlayer(player, "Converting from MultiInv! All messages are sent to console!", false);
                                    miConvert();
                                    break;
                                default:
                                    plugin.getPrinter().printToPlayer(player, "Valid arguments are: MULTIVERSE | MULTIINV", true);
                                    break;
                            }
                        } else {
                            plugin.getPrinter().printToPlayer(player, "You must specify the plugin to convert from: MULTIVERSE | MULTIINV", true);
                        }
                    } else {
                        plugin.getPrinter().printToPlayer(player, NO_PERMISSION, true);
                    }
                } else {
                    if (args.length == 2) {
                        switch (args[1].toUpperCase()) {
                            case "MULTIVERSE":
                                plugin.getPrinter().printToConsole("Converting from Multiverse-Inventories!", false);
                                mvConvert();
                                break;
                            case "MULTIINV":
                                plugin.getPrinter().printToConsole("Converting from MultiInv!", false);
                                miConvert();
                                break;
                            default:
                                plugin.getPrinter().printToConsole("Valid arguments are: MULTIVERSE | MULTIINV", true);
                                break;
                        }
                    } else {
                        plugin.getPrinter().printToConsole("You must specify the plugin to convert from: MULTIVERSE | MULTIINV", true);
                    }
                }

                return true;

            case HELP:
                if (isPlayer) {
                    if (player.hasPermission(PERMISSION_NODE + "help")) {
                    displayPlayerHelp(player);
                } else {
                        plugin.getPrinter().printToPlayer(player, NO_PERMISSION, true);
                    }
                }


                return true;

            case RELOAD:
                if (isPlayer) {
                    if (player.hasPermission(PERMISSION_NODE + "reload")) {
                        reload(player);
                    } else {
                        plugin.getPrinter().printToPlayer(player, NO_PERMISSION, true);
                    }
                } else {
                    reload();
                }

                return true;

            case SETWORLDDEFAULT:
                if (isPlayer) {
                    if (player.hasPermission(PERMISSION_NODE + "setdefault")) {
                        String group = "";

                        if (args.length == 2) {
                            group = args[1].equalsIgnoreCase("default") ? "settings" : args[1];
                            setWorldDefault(player, group);
                        } else {
                            try {
                                group = plugin.getWorldManager().getGroupFromWorld(player.getWorld().getName());
                                setWorldDefault(player, group);
                            } catch (IllegalArgumentException ex) {
                                plugin.getPrinter().printToPlayer(player, "You are not standing in a valid world!", true);
                            }
                        }
                    } else {
                        plugin.getPrinter().printToPlayer(player, NO_PERMISSION, true);
                    }
                } else {
                    plugin.getPrinter().printToConsole("This command can only be run from ingame.", true);
                }

                return true;
        }

        return false;
    }

    private void mvConvert() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.getDataConverter().convertMultiVerseData();
            }
        });
    }

    private void miConvert() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.getDataConverter().convertMultiInvData();
            }
        });
    }

    private void displayConsoleHelp() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "-------------------------------------------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "" + "Available Commands:");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "/swi convert " + ChatColor.AQUA + "- Convert Multiverse-Inventories or Multiinv data into SWI");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "/swi help " + ChatColor.AQUA + "- Displays this help");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "/swi reload " + ChatColor.AQUA + "- Reload Config and world files");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "-------------------------------------------------------");
    }

    private void displayPlayerHelp(Player player) {
        String version = plugin.getDescription().getVersion();

        player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +"-----------------------------------------------------");
        player.sendMessage(ChatColor.DARK_GRAY +"  [" + ChatColor.AQUA + ChatColor.ITALIC + ChatColor.BOLD + "SeparateWorldItems Help:" + ChatColor.DARK_GRAY + "]");
        player.sendMessage("");
        player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + " Version: " + ChatColor.GREEN + version);
        player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + " Authors: " + ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "TriptcyhPlaysMC, CaptainDenial" + ChatColor.DARK_GRAY + "]");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + " /swi convert" + ChatColor.RED + "" + ChatColor.ITALIC + " - Convert data from Multiverse-Inventories or Multiinv into swi.");
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + " /swi help" + ChatColor.RED + "" + ChatColor.ITALIC + " - Displays this help page.");
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + " /swi reload" + ChatColor.RED + "" + ChatColor.ITALIC + " - Reloads all configuration files.");
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + " /swi setworlddefault [group]" + ChatColor.RED + "" + ChatColor.ITALIC + " - Set the default inventory of the world you are standing in.");
        player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
    }

    private void reload() {
        reloadConfigFiles();

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Plugin configuration successfully reloaded!");
    }

    private void reload(Player player) {
        reloadConfigFiles();

        plugin.getPrinter().printToPlayer(player, "Plugin configuration successfully reloaded!", false);
    }

    private void reloadConfigFiles() {
        plugin.getConfigManager().reloadConfigs();
        plugin.getWorldManager().loadGroups();
    }

    private void setWorldDefault(Player player, String group) {
        File file = new File(plugin.getDefaultFilesDirectory() + File.separator + group + ".json");
        if (!file.exists()) {
            plugin.getPrinter().printToPlayer(player, "Default file for this group not found!", true);
            return;
        }

        File tmp = new File(plugin.getDataFolder() + File.separator + "data" + File.separator + player.getUniqueId() + File.separator + "tmp.json");
        try {
            tmp.mkdirs();
            tmp.createNewFile();
        } catch (IOException ex) {
            plugin.getPrinter().printToPlayer(player, "Could not create temporary file! Aborting!", true);
            return;
        }
        plugin.getSerializer().writeData(tmp, PlayerSeparator.serializePlayerAsString(player, plugin));

        player.setFoodLevel(20);
        player.setHealth(20);
        player.setSaturation(20);
        player.setTotalExperience(0);

        plugin.getSerializer().writeData(file, PlayerSeparator.serializePlayerAsString(player, plugin));

        plugin.getSerializer().getPlayerDataFromFile(player, "tmp", GameMode.SURVIVAL.toString());
        tmp.delete();
        plugin.getPrinter().printToPlayer(player, "Settings for '" + group + "' set!", false);
    }
}
