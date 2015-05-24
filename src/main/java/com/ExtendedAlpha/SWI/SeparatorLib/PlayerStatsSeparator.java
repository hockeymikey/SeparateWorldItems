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
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

public class PlayerStatsSeparator {
	
	protected PlayerStatsSeparator() {
	}

	public static JSONObject separatePlayerStats(Player player, SeparateWorldItems plugin) {
		try {
			JSONObject root = new JSONObject();
			if(shouldSerialize("can-fly", plugin))
				root.put("can-fly", player.getAllowFlight());
			if(shouldSerialize("display-name", plugin))
				root.put("display-name", player.getDisplayName());
			if(shouldSerialize("exhaustion", plugin))
				root.put("exhaustion", player.getExhaustion());
			if(shouldSerialize("exp", plugin))
				root.put("exp", player.getExp());
			if(shouldSerialize("flying", plugin))
				root.put("flying", player.isFlying());
			if(shouldSerialize("food", plugin))
				root.put("food", player.getFoodLevel());
			if(shouldSerialize("gamemode", plugin))
				root.put("gamemode", player.getGameMode().toString());
			if(shouldSerialize("health", plugin))
				root.put("health", player.getHealthScale());
			if(shouldSerialize("level", plugin))
				root.put("level", player.getLevel());
			if(shouldSerialize("potion-effects", plugin))
				root.put("potion-effects", PotionEffectSeparator.separateEffects(player.getActivePotionEffects()));
			if(shouldSerialize("saturation", plugin))
				root.put("saturation", player.getSaturation());
			return root;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String separatePlayerStatsAsString(Player player, SeparateWorldItems plugin) {
		return separatePlayerStatsAsString(player, false, plugin);
	}

	public static String separatePlayerStatsAsString(Player player, boolean pretty, SeparateWorldItems plugin) {
		return separatePlayerStatsAsString(player, pretty, 5, plugin);
	}

	public static String separatePlayerStatsAsString(Player player, boolean pretty, int indentFactor, SeparateWorldItems plugin) {
		try {
			if(pretty) {
				return separatePlayerStats(player, plugin).toString(indentFactor);
			} else {
				return separatePlayerStats(player, plugin).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void applyPlayerStats(Player player, String stats) {
		try {
			applyPlayerStats(player, new JSONObject(stats));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void applyPlayerStats(Player player, JSONObject stats) {
		try {
			if(stats.has("can-fly"))
				player.setAllowFlight(stats.getBoolean("can-fly"));
			if(stats.has("display-name"))
				player.setDisplayName(stats.getString("display-name"));
			if(stats.has("exhaustion"))
				player.setExhaustion((float) stats.getDouble("exhaustion"));
			if(stats.has("exp"))
				player.setExp((float) stats.getDouble("exp"));
			if(stats.has("flying"))
				player.setFlying(stats.getBoolean("flying"));
			if(stats.has("food"))
				player.setFoodLevel(stats.getInt("food"));
			if(stats.has("health"))
				player.setHealth(stats.getDouble("health"));
			if(stats.has("gamemode")) {
                if (stats.get("gamemode") instanceof String) {
                    player.setGameMode(GameMode.valueOf(stats.getString("gamemode")));
                } else {
                    int gm = stats.getInt("gamemode");
                    switch(gm) {
                        case 0:
                            player.setGameMode(GameMode.CREATIVE);
                            break;
                        case 1:
                            player.setGameMode(GameMode.SURVIVAL);
                            break;
                        case 2:
                            player.setGameMode(GameMode.ADVENTURE);
                            break;
                        case 3:
                            player.setGameMode(GameMode.SPECTATOR);
                            break;
                    }
                }
            }
			if(stats.has("level"))
				player.setLevel(stats.getInt("level"));
			if(stats.has("potion-effects"))
				PotionEffectSeparator.setPotionEffects(stats.getString("potion-effects"), player);
			if(stats.has("saturation"))
				player.setSaturation((float) stats.getDouble("saturation"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static boolean shouldSerialize(String key, SeparateWorldItems plugin) {
		if (key.equalsIgnoreCase("gamemode") && plugin.getConfigManager().getConfig("config").getBoolean("separate-gamemode-inventories"))
			return false;
		return plugin.getConfigManager().getShouldSerialize("player-stats." + key);
	}
	
}
