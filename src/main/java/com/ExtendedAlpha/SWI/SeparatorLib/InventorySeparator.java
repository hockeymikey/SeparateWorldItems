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

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class InventorySeparator {
	
	protected InventorySeparator() {
	}

	public static JSONArray serializeInventory(Inventory inv) {
		JSONArray inventory = new JSONArray();
		for(int i = 0; i < inv.getSize(); i++) {
			JSONObject values = SingleItemSeparator.serializeItemInInventory(inv.getItem(i), i);
			if(values != null)
				inventory.put(values);
		}
		return inventory;
	}

	public static JSONObject serializePlayerInventory(PlayerInventory inv) {
		try {
			JSONObject root = new JSONObject();
			JSONArray inventory = serializeInventory(inv);
			JSONArray armor = serializeInventory(inv.getArmorContents());
			root.put("inventory", inventory);
			root.put("armor", armor);
			return root;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String serializePlayerInventoryAsString(PlayerInventory inv) {
		return serializePlayerInventoryAsString(inv, false);
	}

	public static String serializePlayerInventoryAsString(PlayerInventory inv, boolean pretty) {
		return serializePlayerInventoryAsString(inv, pretty, 5);
	}

	public static String serializePlayerInventoryAsString(PlayerInventory inv, boolean pretty, int indentFactor) {
		try {
			if(pretty) {
				return serializePlayerInventory(inv).toString(indentFactor);
			} else {
				return serializePlayerInventory(inv).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String serializeInventoryAsString(Inventory inventory) {
		return serializeInventoryAsString(inventory, false);
	}

	public static String serializeInventoryAsString(Inventory inventory, boolean pretty) {
		return serializeInventoryAsString(inventory, pretty, 5);
	}

	public static String serializeInventoryAsString(Inventory inventory, boolean pretty, int indentFactor) {
		try {
			if(pretty) {
				return serializeInventory(inventory).toString(indentFactor);
			} else {
				return serializeInventory(inventory).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String serializeInventoryAsString(ItemStack[] contents) {
		return serializeInventoryAsString(contents, false);
	}

	public static String serializeInventoryAsString(ItemStack[] contents, boolean pretty) {
		return serializeInventoryAsString(contents, pretty, 5);
	}

	public static String serializeInventoryAsString(ItemStack[] contents, boolean pretty, int indentFactor) {
		try {
			if(pretty) {
				return serializeInventory(contents).toString(indentFactor);
			} else {
				return serializeInventory(contents).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JSONArray serializeInventory(ItemStack[] contents) {
		JSONArray inventory = new JSONArray();
		for(int i = 0; i < contents.length; i++) {
			JSONObject values = SingleItemSeparator.serializeItemInInventory(contents[i], i);
			if(values != null)
				inventory.put(values);
		}
		return inventory;
	}

	public static ItemStack[] getInventory(String json, int size) {
		try {
			return getInventory(new JSONArray(json), size);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ItemStack[] getInventory(JSONArray inv, int size) {
		try {
			ItemStack[] contents = new ItemStack[size];
			for(int i = 0; i < inv.length(); i++) {
				JSONObject item = inv.getJSONObject(i);
				int index = item.getInt("index");
				if(index > size)
					throw new IllegalArgumentException("index found is greator than expected size (" + index + ">" + size + ")");
				if(index > contents.length || index < 0)
					throw new IllegalArgumentException("Item " + i + " - Slot " + index + " does not exist in this inventory");
				ItemStack stuff = SingleItemSeparator.getItem(item);
				contents[index] = stuff;
			}
			return contents;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ItemStack[] getInventory(File jsonFile, int size) {
		String source = "";
		try {
			Scanner x = new Scanner(jsonFile);
			while (x.hasNextLine()) {
				source += x.nextLine() + "\n";
			}
			x.close();
			return getInventory(source, size);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setInventory(InventoryHolder holder, String inv) {
		setInventory(holder.getInventory(), inv);
	}

	public static void setInventory(InventoryHolder holder, JSONArray inv) {
		setInventory(holder.getInventory(), inv);
	}

	public static void setInventory(Inventory inventory, String inv) {
		try {
			setInventory(inventory, new JSONArray(inv));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void setInventory(Inventory inventory, JSONArray inv) {
		ItemStack[] items = getInventory(inv, inventory.getSize());
		inventory.clear();
		for(int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if(item == null)
				continue;
			inventory.setItem(i, item);
		}
	}

	public static void setPlayerInventory(Player player, String inv) {
		try {
			setPlayerInventory(player, new JSONObject(inv));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void setPlayerInventory(Player player, JSONObject inv) {
		try {
			PlayerInventory inventory = player.getInventory();
			ItemStack[] armor = getInventory(inv.getJSONArray("armor"), 4);
			inventory.clear();
			inventory.setArmorContents(armor);
			setInventory(player, inv.getJSONArray("inventory"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
