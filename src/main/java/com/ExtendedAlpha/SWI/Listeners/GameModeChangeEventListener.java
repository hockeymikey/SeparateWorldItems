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

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class GameModeChangeEventListener implements Listener {

    private SeparateWorldItems plugin;

    public GameModeChangeEventListener(SeparateWorldItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        GameMode oldGameMode = player.getGameMode();
        GameMode newGameMode = event.getNewGameMode();

        plugin.getSerializer().writePlayerDataToFile(player,
                PlayerSeparator.separatePlayer(player, plugin),
                plugin.getWorldManager().getGroupFromWorld(player.getWorld().getName()),
                oldGameMode.toString());

        plugin.getSerializer().getPlayerDataFromFile(player,
                plugin.getWorldManager().getGroupFromWorld(player.getWorld().getName()),
                newGameMode.toString());
    }
}
