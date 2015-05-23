/*
 * Copyright (C) 2014-2015  TriptychPlaysMC
 *          All rights reserved
 */

package com.ExtendedAlpha.SWI.Utils;

import com.ExtendedAlpha.SWI.SeparateWorldItems;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static com.ExtendedAlpha.SWI.SeparateWorldItems.log;

public class Messages {

    private static SeparateWorldItems plugin;
    private static Messages instance;

    private Messages(SeparateWorldItems main) {
        plugin = main;
    }

    public static Messages getInstance(SeparateWorldItems plugin) {
        if (instance == null) {
            instance = new Messages(plugin);
        }

        return instance;
    }

    public static void disable() {
        instance = null;
    }

    /**
     * Method to print a message to the server console.
     *
     * @param msg  The message to send to console.
     * @param warn The level of the message.
     */
    public void printToConsole(String msg, boolean warn) {
            log("" + msg);

        }


    /**
     * Method to print a message to a specific player on the server.
     *
     * @param player The player to send the message to.
     * @param msg    The message to send.
     * @param warn   If the message should be a warning (true for red, false for green).
     */
    public void printToPlayer(Player player, String msg, boolean warn) {
        String message = "";

        if (warn) {
            message += ChatColor.RED;
        } else {
            message += ChatColor.GRAY;
        }

        message += ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "SWI" + ChatColor.DARK_GRAY + "] " + ChatColor.YELLOW + msg;

        player.sendMessage(message);
    }
}
