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

package com.ExtendedAlpha.SWI.Config;

import com.ExtendedAlpha.SWI.SeparateWorldItems;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigFiles {
    
    private Map<String, File> configFiles = new HashMap<>();
    private Map<File, YamlConfiguration> configs = new HashMap<>();
    
    private SeparateWorldItems plugin;

    private static ConfigFiles manager;

    private ConfigFiles(SeparateWorldItems plugin) {
        this.plugin = plugin;
    }

    public static ConfigFiles getManager(SeparateWorldItems plugin) {
        if (manager == null) {
            manager = new ConfigFiles(plugin);
        }

        return manager;
    }

    public void disable() {
        plugin.getPrinter().printToConsole("Saving configs for shutdown.", false);
        for (String config : configFiles.keySet()) {
            saveConfig(config);
        }
        
        this.configFiles.clear();
        this.configs.clear();
        manager = null;
    }

    public boolean getShouldSerialize(String path) {
        return getConfig("config").getBoolean(path);
    }
    
    public File getConfigFile(String config) {
        return configFiles.containsKey(config) ? configFiles.get(config) : null;
    }
    
    public YamlConfiguration getConfig(String config) {
        return configs.containsKey(getConfigFile(config)) ? configs.get(getConfigFile(config)) : null;
    }
    
    public File addConfigFile(String name, File file, boolean addConfig) {
        checkNotNull(name);
        checkNotNull(file);
        
        configFiles.put(name, file);
        if (addConfig) {
            reloadConfig(name);
        }
        
        return file;
    }
    
    public void reloadConfigs() {
        for (String config : configFiles.keySet()) {
            reloadConfig(config);
        }
    }
    
    public void reloadConfig(String config) {
        if (!config.equalsIgnoreCase("groups")) {
            setDefaults(config);
        } else {
            if (getConfig("config").getBoolean("reset-config")) {
                setDefaults(config);
                getConfig("config").set("reset-config", false);
                saveConfig("config");
            }
        }
        
        addConfig(getConfigFile(config), YamlConfiguration.loadConfiguration(getConfigFile(config)));
    }

    private void addConfig(File file, YamlConfiguration config) {
        checkNotNull(file);
        checkNotNull(config);

        configs.put(file, config);
    }
    
    private void addDefault(YamlConfiguration config, String path, Object value) {
        if (!(config.contains(path))) {
            config.set(path, value);
        }
    }
    
    private void checkNotNull(Object o) {
        if (o == null)
            throw new IllegalArgumentException("Parameter cannot be null!");
    }
    
    private void saveConfig(String config) {
        try {
            getConfig(config).save(getConfigFile(config));
        } catch (IOException ex) {
            plugin.getPrinter().printToConsole("Error saving " + config + ".yml': " + ex.getMessage(), true);
        }
    }
    
    private void setDefaults(String config) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(getConfigFile(config));
        
        if (config.equalsIgnoreCase("config")) {
            configuration.options().header("Do not edit reset-config unless you want to reset your config!!");
            addDefault(configuration, "reset-config", true);
            addDefault(configuration, "check-updates", true);
            addDefault(configuration, "manage-gamemodes", false);
            addDefault(configuration, "separate-gamemode-inventories", false);
            addDefault(configuration, "player.ender-chest", true);
            addDefault(configuration, "player.inventory", true);
            addDefault(configuration, "player.stats", true);
            addDefault(configuration, "player-stats.can-fly", true);
            addDefault(configuration, "player-stats.display-name", false);
            addDefault(configuration, "player-stats.exhaustion", true);
            addDefault(configuration, "player-stats.exp", true);
            addDefault(configuration, "player-stats.food", true);
            addDefault(configuration, "player-stats.flying", true);
            addDefault(configuration, "player-stats.gamemode", false);
            addDefault(configuration, "player-stats.health", true);
            addDefault(configuration, "player-stats.level", true);
            addDefault(configuration, "player-stats.potion-effects", true);
            addDefault(configuration, "player-stats.saturation", true);
        } else if (config.equalsIgnoreCase("groups")) {
            List<String> defaults = new ArrayList<>();
            defaults.add("world");
            defaults.add("world_nether");
            defaults.add("world_the_end");
            addDefault(configuration, "groups.exampleGroup.worlds", defaults);
            addDefault(configuration, "groups.exampleGroup.default-gamemode", "SURVIVAL");
        }
        
        try {
            configuration.save(getConfigFile(config));
        } catch (IOException ex) {
            plugin.getPrinter().printToConsole("Error saving " + config + ".yml': " + ex.getMessage(), true);
        }
    }
}
