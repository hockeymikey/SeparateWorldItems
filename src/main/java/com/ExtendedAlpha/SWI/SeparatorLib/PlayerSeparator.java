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

package com.ExtendedAlpha.SWI.SeparatorLib;

import com.ExtendedAlpha.SWI.SeparateWorldItems;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

public class PlayerSeparator {
	
	protected PlayerSeparator() {
	}

	public static JSONObject separatePlayer(Player player, SeparateWorldItems plugin) {
		try {
			JSONObject root = new JSONObject();
			if(plugin.getConfigManager().getShouldSerialize("player.ender-chest"))
				root.put("ender-chest", InventorySeparator.separateInventory(player.getEnderChest()));
			if(plugin.getConfigManager().getShouldSerialize("player.inventory"))
				root.put("inventory", InventorySeparator.separatePlayerInventory(player.getInventory()));
			if(plugin.getConfigManager().getShouldSerialize("player.stats"))
				root.put("stats", PlayerStatsSeparator.separatePlayerStats(player, plugin));
			return root;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String separatePlayerAsString(Player player, SeparateWorldItems plugin) {
		return separatePlayerAsString(player, false, plugin);
	}

	public static String separatePlayerAsString(Player player, boolean pretty, SeparateWorldItems plugin) {
		return separatePlayerAsString(player, pretty, 5, plugin);
	}

	public static String separatePlayerAsString(Player player, boolean pretty, int indentFactor, SeparateWorldItems plugin) {
		try {
			if(pretty) {
				return separatePlayer(player, plugin).toString(indentFactor);
			} else {
				return separatePlayer(player, plugin).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setPlayer(String meta, Player player) {
		try {
			setPlayer(new JSONObject(meta), player);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void setPlayer(JSONObject meta, Player player) {
		try {
			if(meta.has("ender-chest"))
				InventorySeparator.setInventory(player.getEnderChest(), meta.getJSONArray("ender-chest"));
			if(meta.has("inventory"))
				InventorySeparator.setPlayerInventory(player, meta.getJSONObject("inventory"));
			if(meta.has("stats"))
				PlayerStatsSeparator.applyPlayerStats(player, meta.getJSONObject("stats"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
