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

package com.ExtendedAlpha.SWI;

import com.ExtendedAlpha.SWI.Commands.SWICommand;
import com.ExtendedAlpha.SWI.Config.ConfigFiles;
import com.ExtendedAlpha.SWI.Data.Converter;
import com.ExtendedAlpha.SWI.Data.DataSeparator;
import com.ExtendedAlpha.SWI.Data.WorldManager;
import com.ExtendedAlpha.SWI.Listeners.ChangedWorldEventListener;
import com.ExtendedAlpha.SWI.Listeners.GameModeChangeEventListener;
import com.ExtendedAlpha.SWI.Listeners.QuitEventListener;
import com.ExtendedAlpha.SWI.Metrics.Metrics;
import com.ExtendedAlpha.SWI.Updater.SpigotUpdater;
import com.ExtendedAlpha.SWI.Utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SeparateWorldItems extends JavaPlugin {

    private SpigotUpdater updater;

    @Override
    public void onEnable() {
        if (!(new File(getDataFolder() + File.separator + "data" + File.separator + "settings").exists())) {
            new File(getDataFolder() + File.separator + "data" + File.separator + "settings").mkdirs();
        }

        if (!(new File(getDataFolder() + File.separator + "settings.json").exists())) {
            saveResource("settings.json", false);
            File dFile = new File(getDataFolder() + File.separator + "settings.json");
            dFile.renameTo(new File(getDefaultFilesDirectory() + File.separator + "settings.json"));
        }

        getConfigManager().addConfigFile("config", new File(getDataFolder() + File.separator + "config.yml"), true);
        getConfigManager().addConfigFile("groups", new File(getDataFolder() + File.separator + "groups.yml"), true);

        getWorldManager().loadGroups();


        log(ChatColor.WHITE + "Initializing commands...");
        getCommand("swi").setExecutor(new SWICommand(this));
        getServer().getPluginManager().registerEvents(new ChangedWorldEventListener(this), this);
        log(ChatColor.WHITE + "Initializing ChangeWorldEventListener...");
        getServer().getPluginManager().registerEvents(new QuitEventListener(this), this);
        log(ChatColor.WHITE + "Initializing QuitEventListener...");

        if (getConfigManager().getConfig("config").getBoolean("separate-gamemode-inventories"))
            log(ChatColor.WHITE + "Initializing GameModeChangeEventListener...");
        getServer().getPluginManager().registerEvents(new GameModeChangeEventListener(this), this);
        {

        }
        if (getConfig().getBoolean("check-updates")) {

            log(ChatColor.WHITE + "Initializing updater...");
            this.updater = new SpigotUpdater(this);
            getUpdater().checkUpdates();
            if (SpigotUpdater.updateAvailable()) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "---------------------------------");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "            SWI Updater");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " ");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "An update for SWI has been found!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Separate World Items " + SpigotUpdater.getHighest());
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You are running " + getDescription().getVersion());
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " ");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Download at:");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Spigotmc: http://goo.gl/gX761N");
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "---------------------------------");
            }
        }

        log(ChatColor.WHITE + "Initializing Metrics..");
        log(ChatColor.GREEN + "Enabled!");
        setupMetrics();
    }

    @Override
    public void onDisable() {
        log(ChatColor.RED + "Disabled...");
        Messages.disable();
        DataSeparator.disable();
        Converter.disable();
        getConfigManager().disable();
        WorldManager.disable();
        getServer().getScheduler().cancelTasks(this);
    }

    private void setupMetrics() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            log("Couldn't submit metrics stats: " + e.getMessage());
        }
    }

    public ConfigFiles getConfigManager() {
        return ConfigFiles.getManager(this);
    }

    public Converter getDataConverter() {
        return Converter.getInstance(this);
    }

    public DataSeparator getSerializer() {
        return DataSeparator.getInstance(this);
    }

    public File getDefaultFilesDirectory() {
        return new File(getDataFolder() + File.separator + "data" + File.separator + "settings");
    }

    public Messages getPrinter() {
        return Messages.getInstance(this);
    }

    public SpigotUpdater getUpdater()
    {
        return this.updater;
    }

    public WorldManager getWorldManager() {
        return WorldManager.getInstance(this);
    }

    public static void log(final Object message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "SWI" + ChatColor.DARK_GRAY + "] " + ChatColor.YELLOW + message.toString());
    }

    public static void errorlog(final Object message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "SWI" + ChatColor.DARK_GRAY + "] " + message.toString());
    }

}
