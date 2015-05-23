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

package com.ExtendedAlpha.SWI.Updater;

import com.ExtendedAlpha.SWI.SeparateWorldItems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class SpigotUpdater
{
    private SeparateWorldItems plugin;
    final int resource = 2568;
    private static String latestVersion = "";
    private static boolean updateAvailable = false;

    public SpigotUpdater(SeparateWorldItems var1)
    {
        this.plugin = var1;
    }

    private String getSpigotVersion()
    {
        try
        {
            HttpURLConnection var1 = (HttpURLConnection)new URL("http://www.spigotmc.org/api/general.php").openConnection();
            var1.setDoOutput(true);
            var1.setRequestMethod("POST");
            var1.getOutputStream().write("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=2568".getBytes("UTF-8"));
            String var2 = new BufferedReader(new InputStreamReader(var1.getInputStream())).readLine();
            if (var2.length() <= 7) {
                return var2;
            }
        }
        catch (Exception var3)
        {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "----------------------------");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "         SWI Updater");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " ");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not connect to spigotmc.org");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "to check for updates! ");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " ");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "----------------------------");
        }
        return null;
    }

    private boolean checkHigher(String var1, String var2)
    {
        String var3 = toReadable(var1);
        String var4 = toReadable(var2);
        return var3.compareTo(var4) < 0;
    }

    public boolean checkUpdates()
    {
        if (getHighest() != "") {
            return true;
        }
        String var1 = getSpigotVersion();
        if ((var1 != null) && (checkHigher(this.plugin.getDescription().getVersion(), var1)))
        {
            latestVersion = var1;
            updateAvailable = true;
            return true;
        }
        return false;
    }

    public static boolean updateAvailable()
    {
        return updateAvailable;
    }

    public static String getHighest()
    {
        return latestVersion;
    }

    private String toReadable(String var1)
    {
        String[] var2 = Pattern.compile(".", 16).split(var1.replace("v", ""));
        var1 = "";
        String[] var6 = var2;
        int var5 = var2.length;
        for (int var4 = 0; var4 < var5; var4++)
        {
            String var3 = var6[var4];
            var1 = var1 + String.format("%4s", new Object[] { var3 });
        }
        return var1;
    }
}
