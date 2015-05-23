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

import org.json.JSONObject;
import com.ExtendedAlpha.SWI.SeparateWorldItems;
import com.ExtendedAlpha.SWI.SeparatorLib.PlayerSeparator;
import com.ExtendedAlpha.SWI.SeparatorLib.Separator;
import com.ExtendedAlpha.SWI.Utils.Messages;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class DataSeparator {

    private SeparateWorldItems plugin;

    private final String FILE_PATH;

    private static DataSeparator instance = null;

    private DataSeparator(SeparateWorldItems plugin) {
        this.plugin = plugin;
        FILE_PATH = plugin.getDataFolder() + File.separator + "data" + File.separator;
    }

    public static DataSeparator getInstance(SeparateWorldItems plugin) {
        if (instance == null) {
            instance = new DataSeparator(plugin);
        }

        return instance;
    }

    public static void disable() {
        instance = null;
    }

    public void writePlayerDataToFile(OfflinePlayer player, JSONObject data, String group, String gamemode) {
        File file;
        if (gamemode.equalsIgnoreCase("SURVIVAL")) {
            file = new File(FILE_PATH + player.getUniqueId().toString(), group + ".json");
        } else if (gamemode.equalsIgnoreCase("ADVENTURE")) {
            file = new File(FILE_PATH + player.getUniqueId().toString(), group + "_adventure.json");
        } else {
            file = new File(FILE_PATH + player.getUniqueId().toString(), group + "_creative.json");
        }

        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }

            if (!file.exists()) {
                file.createNewFile();
            }
            writeData(file, Separator.toString(data));
        } catch (IOException ex) {
            Messages.getInstance(plugin).printToConsole("Error creating file '" + FILE_PATH +
                    player.getUniqueId().toString() + File.separator + group + ".json': " + ex.getMessage(), true);
            ex.printStackTrace();
        }
    }

    public void writeData(final File file, final String data) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                FileWriter writer = null;
                try {
                    writer = new FileWriter(file);
                    writer.write(data);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void getPlayerDataFromFile(Player player, String group, String gamemode) {
        File file;
        if (gamemode.equalsIgnoreCase("SURVIVAL")) {
            file = new File(FILE_PATH + player.getUniqueId().toString(), group + ".json");
        } else if (gamemode.equalsIgnoreCase("ADVENTURE")) {
            file = new File(FILE_PATH + player.getUniqueId().toString(), group + "_adventure.json");
        } else {
            file = new File(FILE_PATH + player.getUniqueId().toString(), group + "_creative.json");
        }

        try {
            JSONObject data = Separator.getObjectFromFile(file);
            PlayerSeparator.setPlayer(data, player);
        } catch (FileNotFoundException ex) {
            try {
                file.createNewFile();
                JSONObject defaultGroupData = Separator.getObjectFromFile(
                        new File(FILE_PATH + "settings" + File.separator + group + ".json"));
                PlayerSeparator.setPlayer(defaultGroupData, player);
            } catch (FileNotFoundException ex2) {
                try {
                    JSONObject defaultData = Separator.getObjectFromFile(
                            new File(FILE_PATH + "settings" + File.separator + "settings.json"));
                    PlayerSeparator.setPlayer(defaultData, player);
                } catch (FileNotFoundException ex3) {
                    plugin.getPrinter().printToPlayer(player, "Something went horribly wrong when loading your inventory! " +
                            "Please notify a server administrator!", true);
                    plugin.getPrinter().printToConsole("Unable to find inventory data for player '" + player.getName() +
                            "' for group '" + group + "': " + ex3.getMessage(), true);
                }
            } catch (IOException exIO) {
                Messages.getInstance(plugin).printToConsole("Error creating file '" + FILE_PATH +
                        player.getUniqueId().toString() + File.separator + group + ".json': " + ex.getMessage(), true);
            }
        }
    }
}
