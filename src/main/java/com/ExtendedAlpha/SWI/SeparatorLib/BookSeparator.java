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

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class BookSeparator {
	
	protected BookSeparator() {
	}

	public static BookMeta getBookMeta(String json) {
		try {
			return getBookMeta(new JSONObject(json));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BookMeta getBookMeta(JSONObject json) {
		try {
			ItemStack dummyItems = new ItemStack(Material.WRITTEN_BOOK, 1);
			BookMeta meta = (BookMeta) dummyItems.getItemMeta();
			String title = null, author = null;
			JSONArray pages = null;
			if(json.has("title"))
				title = json.getString("title");
			if(json.has("author"))
				author = json.getString("author");
			if(json.has("pages"))
				pages = json.getJSONArray("pages");
			if(title != null)
				meta.setTitle(title);
			if(author != null)
				meta.setAuthor(author);
			if(pages != null) {
				String[] allPages = new String[pages.length()];
				for(int i = 0; i < pages.length(); i++) {
					String page = pages.getString(i);
					if(page.isEmpty() || page == null)
						page = "";
					allPages[i] = page;
				}
				meta.setPages(allPages);
			}
			return meta;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JSONObject separateBookMeta(BookMeta meta) {
		try {
			JSONObject root = new JSONObject();
			if(meta.hasTitle())
				root.put("title", meta.getTitle());
			if(meta.hasAuthor())
				root.put("author", meta.getAuthor());
			if(meta.hasPages()) {
				String[] pages = meta.getPages().toArray(new String[] {});
				root.put("pages", pages);
			}
			return root;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String separateBookMetaAsString(BookMeta meta) {
		return separateBookMetaAsString(meta, false);
	}

	public static String separateBookMetaAsString(BookMeta meta, boolean pretty) {
		return separateBookMetaAsString(meta, pretty, 5);
	}

	public static String separateBookMetaAsString(BookMeta meta, boolean pretty, int indentFactor) {
		try {
			if(pretty) {
				return separateBookMeta(meta).toString(indentFactor);
			} else {
				return separateBookMeta(meta).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static EnchantmentStorageMeta getEnchantedBookMeta(String json) {
		try {
			return getEnchantedBookMeta(new JSONObject(json));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static EnchantmentStorageMeta getEnchantedBookMeta(JSONObject json) {
		try {
			ItemStack dummyItems = new ItemStack(Material.ENCHANTED_BOOK, 1);
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) dummyItems.getItemMeta();
			if(json.has("enchantments")) {
				Map<Enchantment, Integer> enchants = EnchantmentSeparator.getEnchantments(json.getString("enchantments"));
				for(Enchantment e : enchants.keySet()) {
					meta.addStoredEnchant(e, enchants.get(e), true);
				}
			}
			return meta;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JSONObject separateEnchantedBookMeta(EnchantmentStorageMeta meta) {
		try {
			JSONObject root = new JSONObject();
			String enchants = EnchantmentSeparator.separateEnchantments(meta.getStoredEnchants());
			root.put("enchantments", enchants);
			return root;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String separateEnchantedBookMetaAsString(EnchantmentStorageMeta meta) {
		return separateEnchantedBookMetaAsString(meta, false);
	}

	public static String separateEnchantedBookMetaAsString(EnchantmentStorageMeta meta, boolean pretty) {
		return separateEnchantedBookMetaAsString(meta, pretty, 5);
	}

	public static String separateEnchantedBookMetaAsString(EnchantmentStorageMeta meta, boolean pretty, int indentFactor) {
		try {
			if(pretty) {
				return separateEnchantedBookMeta(meta).toString(indentFactor);
			} else {
				return separateEnchantedBookMeta(meta).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
