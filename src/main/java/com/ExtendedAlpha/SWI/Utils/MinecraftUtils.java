package com.ExtendedAlpha.SWI.Utils;

import org.bukkit.Bukkit;

public class MinecraftUtils {
    public static String getMinecraftVersion() {
        // Get the raw version
        final String rawVersion = Bukkit.getVersion();

        // Get the start of the raw string
        int start = rawVersion.indexOf("MC:");
        if(start == -1)
            return rawVersion;

        // Exclude the 'MC:'
        start += 4;

        // Get the end of the string
        int end = rawVersion.indexOf(')', start);

        // Get and return the Minecraft version number
        return rawVersion.substring(start, end);
    }
}
