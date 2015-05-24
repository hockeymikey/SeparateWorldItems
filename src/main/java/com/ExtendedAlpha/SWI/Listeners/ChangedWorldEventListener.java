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

package com.ExtendedAlpha.SWI.Listeners;

import com.ExtendedAlpha.SWI.SeparateWorldItems;
import com.ExtendedAlpha.SWI.SeparatorLib.PlayerSeparator;
import com.ExtendedAlpha.SWI.Data.WorldManager;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class ChangedWorldEventListener implements Listener {

    private WorldManager manager;
    private SeparateWorldItems plugin;

    public ChangedWorldEventListener(SeparateWorldItems plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String worldFrom = event.getFrom().getName();
        String worldTo = player.getWorld().getName();
        String groupFrom = manager.getGroupFromWorld(worldFrom);

        if (plugin.getConfigManager().getConfig("config").getBoolean("separate-gamemode-inventories")) {
            plugin.getSerializer().writePlayerDataToFile(player,
                    PlayerSeparator.separatePlayer(player, plugin),
                    groupFrom,
                    player.getGameMode().toString());
        } else {
            plugin.getSerializer().writePlayerDataToFile(player,
                    PlayerSeparator.separatePlayer(player, plugin),
                    groupFrom,
                    GameMode.SURVIVAL.toString());
        }

        if (!shouldKeepInventory(worldFrom, worldTo)) {
            if (plugin.getConfigManager().getConfig("config").getBoolean("separate-gamemode-inventories")) {
                if (plugin.getConfigManager().getConfig("config").getBoolean("manage-gamemodes")) {
                    plugin.getSerializer().getPlayerDataFromFile(player, manager.getGroupFromWorld(worldTo),
                            manager.getGameMode(manager.getGroupFromWorld(worldTo)).toString());
                } else {
                    plugin.getSerializer().getPlayerDataFromFile(player, manager.getGroupFromWorld(worldTo), player.getGameMode().toString());
                }
            } else {
                plugin.getSerializer().getPlayerDataFromFile(player, manager.getGroupFromWorld(worldTo), GameMode.SURVIVAL.toString());
            }
        }
    }

    private boolean shouldKeepInventory(String worldFrom, String worldTo) {
        try {
            return manager.getGroup(manager.getGroupFromWorld(worldFrom)).contains(worldTo);
        } catch (NullPointerException ex) {
            return false;
        }
    }
}
