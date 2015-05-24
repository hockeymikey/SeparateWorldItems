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
import com.ExtendedAlpha.SWI.SeparatorLib.InventorySeparator;
import com.ExtendedAlpha.SWI.SeparatorLib.PotionEffectSeparator;
import com.onarandombox.multiverseinventories.MultiverseInventories;
import com.onarandombox.multiverseinventories.ProfileTypes;
import com.onarandombox.multiverseinventories.api.profile.PlayerProfile;
import com.onarandombox.multiverseinventories.api.profile.WorldGroupProfile;
import com.onarandombox.multiverseinventories.api.share.Sharables;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.JSONArray;
import org.json.JSONObject;
import uk.co.tggl.pluckerpluck.multiinv.MultiInv;
import uk.co.tggl.pluckerpluck.multiinv.MultiInvAPI;
import uk.co.tggl.pluckerpluck.multiinv.api.MIAPIPlayer;
import uk.co.tggl.pluckerpluck.multiinv.inventory.MIItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Converter {

    private SeparateWorldItems plugin;

    private static Converter converter = null;

    private Converter(SeparateWorldItems plugin) {
        this.plugin = plugin;
    }

    public static Converter getInstance(SeparateWorldItems plugin) {
        if (converter == null) {
            converter = new Converter(plugin);
        }

        return converter;
    }

    public static void disable() {
        converter = null;
    }

    public void convertMultiVerseData() {
        plugin.getPrinter().printToConsole("Beginning data conversion. This may take awhile...", false);
        MultiverseInventories mvinventories = (MultiverseInventories) plugin.getServer().getPluginManager().getPlugin("Multiverse-Inventories");
        List<WorldGroupProfile> mvgroups = mvinventories.getGroupManager().getGroups();

        for (WorldGroupProfile mvgroup : mvgroups) {
            for (OfflinePlayer player1 : Bukkit.getOfflinePlayers()) {
                try {
                    PlayerProfile playerData = mvgroup.getPlayerData(ProfileTypes.SURVIVAL, player1);
                    if (playerData != null) {
                        JSONObject writable = separateMVIToNewFormat(playerData);
                        plugin.getSerializer().writePlayerDataToFile(player1, writable, mvgroup.getName(), GameMode.SURVIVAL.toString());
                    }
                } catch (Exception ex) {
                    plugin.getPrinter().printToConsole("Error importing inventory for player: " + player1.getName() +
                            " For group: " + mvgroup.getName(), true);
                    ex.printStackTrace();
                }
            }
        }

        plugin.getPrinter().printToConsole("Data conversion complete! Disabling Multiverse-Inventories...", false);
        plugin.getServer().getPluginManager().disablePlugin(mvinventories);
        plugin.getPrinter().printToConsole("Multiverse-Inventories disabled! Don't forget to remove the .jar!", false);
    }

    public void convertMultiInvData() {
        plugin.getPrinter().printToConsole("Beginning data conversion. This may take awhile...", false);
        MultiInv multiinv = (MultiInv) plugin.getServer().getPluginManager().getPlugin("MultiInv");
        MultiInvAPI mvAPI = new MultiInvAPI(multiinv);

        for (String world : mvAPI.getGroups().values()) {
            System.out.println("World: " + world);
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                System.out.println("OfflinePlayer: " + offlinePlayer.getName());
                MIAPIPlayer player = mvAPI.getPlayerInstance(offlinePlayer, world, GameMode.SURVIVAL);
                if (player != null && player.getInventory() != null && player.getInventory().getInventoryContents() != null) {
                    System.out.println("MIAPIPlayer: " + player.getPlayername());
                    try {
                        plugin.getSerializer().writePlayerDataToFile(offlinePlayer, separateMIToNewFormat(player), mvAPI.getGroups().get(world), GameMode.SURVIVAL.toString());
                    } catch (Exception ex) {
                        plugin.getPrinter().printToConsole("Error importing inventory for player '" + offlinePlayer.getName() + ": " + ex.getMessage(), true);
                        ex.printStackTrace();
                    }
                }
            }
        }

        plugin.getPrinter().printToConsole("Data conversion complete! Disabling MultiInv...", false);
        plugin.getServer().getPluginManager().disablePlugin(multiinv);
        plugin.getPrinter().printToConsole("MultiInv disabled! Don't forget to remove the .jar!", false);
    }

    private JSONObject separateMVIToNewFormat(PlayerProfile data) {
        JSONObject root = new JSONObject();

        JSONObject inv = new JSONObject();
        if (data.get(Sharables.INVENTORY) != null) {
            JSONArray inventory = InventorySeparator.separateInventory(data.get(Sharables.INVENTORY));
            inv.put("inventory", inventory);
        }
        if (data.get(Sharables.ARMOR) != null) {
            JSONArray armor = InventorySeparator.separateInventory(data.get(Sharables.ARMOR));
            inv.put("armor", armor);
        }

        JSONObject stats = new JSONObject();
        if (data.get(Sharables.EXHAUSTION) != null)
            stats.put("exhaustion", data.get(Sharables.EXHAUSTION));
        if (data.get(Sharables.EXPERIENCE) != null)
            stats.put("exp", data.get(Sharables.EXPERIENCE));
        if (data.get(Sharables.FOOD_LEVEL) != null)
            stats.put("food", data.get(Sharables.FOOD_LEVEL));
        if (data.get(Sharables.HEALTH) != null)
            stats.put("health", data.get(Sharables.HEALTH));
        if (data.get(Sharables.LEVEL) != null)
            stats.put("level", data.get(Sharables.LEVEL));
        if (data.get(Sharables.POTIONS) != null) {
            PotionEffect[] effects = data.get(Sharables.POTIONS);
            Collection<PotionEffect> potionEffects = new LinkedList<>();
            for (PotionEffect effect : effects) {
                potionEffects.add(effect);
            }
            stats.put("potion-effects", PotionEffectSeparator.separateEffects(potionEffects));
        }
        if (data.get(Sharables.SATURATION) != null)
            stats.put("saturation", data.get(Sharables.SATURATION));

        root.put("inventory", inv);
        root.put("stats", stats);

        return root;
    }

    private JSONObject separateMIToNewFormat(MIAPIPlayer player) {
        JSONObject root = new JSONObject();
        JSONObject inventory = new JSONObject();

        List<ItemStack> items = new ArrayList<>();
        for (MIItemStack item : player.getInventory().getInventoryContents()) {
            if (item == null || item.getItemStack() == null) {
                items.add(new ItemStack(Material.AIR));
            } else {
                items.add(item.getItemStack());
            }
        }
        ItemStack[] invArray = new ItemStack[items.size()];
        invArray = items.toArray(invArray);
        JSONArray inv = InventorySeparator.separateInventory(invArray);
        inventory.put("inventory", inv);

        List<ItemStack> armorList = new ArrayList<>();
        for (MIItemStack item : player.getInventory().getArmorContents()) {
            if (item == null || item.getItemStack() == null) {
                items.add(new ItemStack(Material.AIR));
            } else {
                armorList.add(item.getItemStack());
            }
        }
        ItemStack[] armorArray = new ItemStack[armorList.size()];
        armorArray = armorList.toArray(armorArray);
        JSONArray armor = InventorySeparator.separateInventory(armorArray);
        inventory.put("armor", armor);

        List<ItemStack> enderChestList = new ArrayList<>();
        for (MIItemStack item : player.getEnderchest().getInventoryContents()) {
            if (item == null || item.getItemStack() == null) {
                items.add(new ItemStack(Material.AIR));
            } else {
                enderChestList.add(item.getItemStack());
            }
        }
        ItemStack[] endArray = new ItemStack[enderChestList.size()];
        endArray = enderChestList.toArray(endArray);
        JSONArray enderChest = InventorySeparator.separateInventory(endArray);
        root.put("ender-chest", enderChest);

        JSONObject stats = new JSONObject();
        if (plugin.getConfigManager().getConfig("config").getBoolean("player-stats.exp"))
            stats.put("exp", player.getXp());
        if (plugin.getConfigManager().getConfig("config").getBoolean("player-stats.food"))
            stats.put("food", player.getFoodlevel());
        if (plugin.getConfigManager().getConfig("config").getBoolean("player-stats.gamemode"))
            stats.put("gamemode", player.getGm().toString());
        if (plugin.getConfigManager().getConfig("config").getBoolean("player-stats.health"))
            stats.put("health", player.getHealth());
        if (plugin.getConfigManager().getConfig("config").getBoolean("player-stats.level"))
            stats.put("level", player.getXpLevel());
        if (plugin.getConfigManager().getConfig("config").getBoolean("player-stats.saturation"))
            stats.put("saturation", player.getSaturation());

        root.put("inventory", inventory);
        root.put("stats", stats);

        return root;
    }
}
