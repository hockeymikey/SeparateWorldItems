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

package com.ExtendedAlpha.SWI.Data;

import com.ExtendedAlpha.SWI.SeparateWorldItems;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.*;

public class WorldManager {

    private Map<String, List<String>> groups = new HashMap<>();
    private Map<String, GameMode> gameModes = new HashMap<>();
    private SeparateWorldItems plugin;

    private static WorldManager manager = null;

    private WorldManager(SeparateWorldItems plugin) {
        this.plugin = plugin;
    }

    public static WorldManager getInstance(SeparateWorldItems plugin) {
        if (manager == null) {
            manager = new WorldManager(plugin);
        }

        return manager;
    }

    public static void disable() {
        manager = null;
    }

    public GameMode getGameMode(String group) {
        return gameModes.containsKey(group) ? gameModes.get(group) : null;
    }

    public List<String> getGroup(String group) {
        return groups.containsKey(group) ? groups.get(group) : null;
    }

    public Set<String> getGroups() {
        return groups.keySet();
    }

    public String getGroupFromWorld(String world) {
        for (String group : groups.keySet()) {
            if (groups.get(group).contains(world)) {
                return group;
            }
        }

        throw new IllegalArgumentException("World '" + world + "' not found! Could not get group!");
    }

    public void addGroup(String group, List<String> worlds, GameMode defGameMode) {
        groups.put(group, worlds);
        if (defGameMode != null)
            gameModes.put(group, defGameMode);
    }

    public void removeGroup(String group) {
        groups.remove(group);
        gameModes.remove(group);
    }

    public boolean isInGroup(String group, String world) {
        if (groups.containsKey(group)) {
            return groups.get(group).contains(world);
        } else {
            throw new IllegalArgumentException("Group '" + group + "' does not exist!");
        }
    }

    public void loadGroups() {
        groups.clear();
        gameModes.clear();

        YamlConfiguration config = plugin.getConfigManager().getConfig("groups");
        for (String key : config.getConfigurationSection("groups").getKeys(false)) {
            List<String> worlds;
            GameMode gameMode;
            if (config.contains("groups." + key + ".worlds")) {
                worlds = config.getStringList("groups." + key + ".worlds");
                if (plugin.getConfigManager().getConfig("config").getBoolean("manage-gamemodes")) {
                    gameMode = GameMode.valueOf(config.getString("groups." + key + ".default-gamemode").toUpperCase());
                } else {
                    gameMode = null;
                }
            } else {
                worlds = config.getStringList("groups." + key);

                config.set("groups." + key, null);
                config.set("groups." + key + ".worlds", worlds);

                if (plugin.getConfigManager().getConfig("config").getBoolean("manage-gamemodes")) {
                    gameMode = GameMode.SURVIVAL;
                    config.set("groups." + key + ".default-gamemode", "SURVIVAL");
                } else {
                    gameMode = null;
                }
            }
            addGroup(key, worlds, gameMode);
        }

        for (String group : groups.keySet()) {
            File fileTo = new File(plugin.getDefaultFilesDirectory() + File.separator + group + ".json");
            if (!fileTo.exists()) {
                File fileFrom = new File(plugin.getDefaultFilesDirectory() + File.separator + "settings.json");
                copyFile(fileFrom, fileTo);
            }
        }
    }

    private void copyFile(File from, File to) {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(from);
            out = new FileOutputStream(to);

            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff)) > 0) {
                out.write(buff, 0, len);
            }
        } catch (IOException ex) {
            plugin.getPrinter().printToConsole("An error occurred copying file '" + from.getName() + "' to '" + to.getName() + "': " + ex.getMessage(), true);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
