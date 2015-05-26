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

import com.ExtendedAlpha.SWI.Utils.MinecraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ExtendedAlpha.SWI.SeparateWorldItems.errorlog;

public class SingleItemSeparator {

	protected SingleItemSeparator() {
	}

	public static JSONObject separateItemInInventory(ItemStack items, int index) {
		return separateItems(items, true, index);
	}

	public static JSONObject separateItem(ItemStack items) {
		return separateItems(items, false, 0);
	}

	private static JSONObject separateItems(ItemStack items, boolean useIndex, int index) {
		try {
			JSONObject values = new JSONObject();
			if(items == null)
				return null;
			int id = items.getTypeId();
			int amount = items.getAmount();
			int data = items.getDurability();
			boolean hasMeta = items.hasItemMeta();
			String name = null, enchants = null;
            String[] flags = null;
			String[] lore = null;
			int repairPenalty = 0;
			Material mat = items.getType();
			JSONObject bannerMeta = null, bookMeta = null, armorMeta = null, skullMeta = null, fwMeta = null;

			if(MinecraftUtils.getMinecraftVersion().startsWith("1.8")) {
                if (mat == Material.BANNER) {
                    bannerMeta = BannerSeparator.separateBanner((BannerMeta) items.getItemMeta());
                    bannerMeta = BannerSeparator.separateBanner((BannerMeta) items.getItemMeta());
                } else if (mat == Material.BOOK_AND_QUILL || mat == Material.WRITTEN_BOOK) {
                    bookMeta = BookSeparator.separateBookMeta((BookMeta) items.getItemMeta());
                } else if (mat == Material.ENCHANTED_BOOK) {
                    bookMeta = BookSeparator.separateEnchantedBookMeta((EnchantmentStorageMeta) items.getItemMeta());
                } else if (Utils.isLeatherArmor(mat)) {
                    armorMeta = LeatherArmorSeparator.separateArmor((LeatherArmorMeta) items.getItemMeta());
                } else if (mat == Material.SKULL_ITEM) {
                    skullMeta = SkullSeparator.separateSkull((SkullMeta) items.getItemMeta());
                } else if (mat == Material.FIREWORK) {
                    fwMeta = FireworkSeparator.separateFireworkMeta((FireworkMeta) items.getItemMeta());
                }

			if(hasMeta) {
				ItemMeta meta = items.getItemMeta();
				if(meta.hasDisplayName())
					name = meta.getDisplayName();
				if(meta.hasLore()) {
					lore = meta.getLore().toArray(new String[]{});
				}
				if(meta.hasEnchants())
					enchants = EnchantmentSeparator.separateEnchantments(meta.getEnchants());
				if(meta instanceof Repairable){
					Repairable rep = (Repairable) meta;
					if(rep.hasRepairCost()){
						repairPenalty = rep.getRepairCost();
					}
				}

                if (meta.getItemFlags() != null && !meta.getItemFlags().isEmpty()) {
                    List<String> flagsList = new ArrayList<>();
                    for (ItemFlag flag : meta.getItemFlags()) {
                        flagsList.add(flag.toString());
                    }
                    flags = flagsList.toArray(new String[flagsList.size()]);
                }

			}

            } else {
                if (MinecraftUtils.getMinecraftVersion().startsWith("1.7"))
                    if (mat == Material.BOOK_AND_QUILL || mat == Material.WRITTEN_BOOK) {
                        bookMeta = BookSeparator.separateBookMeta((BookMeta) items.getItemMeta());
                    } else if (mat == Material.ENCHANTED_BOOK) {
                        bookMeta = BookSeparator.separateEnchantedBookMeta((EnchantmentStorageMeta) items.getItemMeta());
                    } else if (Utils.isLeatherArmor(mat)) {
                        armorMeta = LeatherArmorSeparator.separateArmor((LeatherArmorMeta) items.getItemMeta());
                    } else if (mat == Material.SKULL_ITEM) {
                        skullMeta = SkullSeparator.separateSkull((SkullMeta) items.getItemMeta());
                    } else if (mat == Material.FIREWORK) {
                        fwMeta = FireworkSeparator.separateFireworkMeta((FireworkMeta) items.getItemMeta());
                    }
            }

            if(hasMeta) {
				ItemMeta meta = items.getItemMeta();
				if (meta.hasDisplayName())
					name = meta.getDisplayName();
				if (meta.hasLore()) {
					lore = meta.getLore().toArray(new String[]{});
				}
				if (meta.hasEnchants())
					enchants = EnchantmentSeparator.separateEnchantments(meta.getEnchants());
				if (meta instanceof Repairable) {
					Repairable rep = (Repairable) meta;
					if (rep.hasRepairCost()) {
						repairPenalty = rep.getRepairCost();
					}
				}
			}

			values.put("id", id);
			values.put("amount", amount);
			values.put("data", data);
			if(useIndex)
				values.put("index", index);
			if(name != null)
				values.put("name", name);
			if(enchants != null)
				values.put("enchantments", enchants);
            if (flags != null)
                values.put("flags", flags);
			if(lore != null)
				values.put("lore", lore);
			if(repairPenalty != 0)
				values.put("repairPenalty", repairPenalty);
			if (bannerMeta != null && bannerMeta.length() > 0)
				values.put("banner-meta", bannerMeta);
			if(bookMeta != null && bookMeta.length() > 0)
				values.put("book-meta", bookMeta);
			if(armorMeta != null && armorMeta.length() > 0)
				values.put("armor-meta", armorMeta);
			if(skullMeta != null && skullMeta.length() > 0)
				values.put("skull-meta", skullMeta);
			if(fwMeta != null && fwMeta.length() > 0)
				values.put("firework-meta", fwMeta);
			return values;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ItemStack getItem(String item) {
		return getItem(item, 0);
	}

	public static ItemStack getItem(String item, int index) {
		try {
			return getItem(new JSONObject(item), index);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ItemStack getItem(JSONObject item) {
		return getItem(item, 0);
	}

public static ItemStack getItem(JSONObject item, int index) {
		try {
            int id = item.getInt("id");
            int amount = item.getInt("amount");
            int data = item.getInt("data");
            String name = null;
            Map<Enchantment, Integer> enchants = null;
            ArrayList<String> flags = null;
            ArrayList<String> lore = null;
            int repairPenalty = 0;
            if (item.has("name"))
                name = item.getString("name");
            if (item.has("enchantments"))
                enchants = EnchantmentSeparator.getEnchantments(item.getString("enchantments"));
            if (item.has("flags")) {
                JSONArray f = item.getJSONArray("flags");
                flags = new ArrayList<>();
                for (int i = 0; i < f.length(); i++) {
                    flags.add(f.getString(i));
                }
            }
            if (item.has("lore")) {
                JSONArray l = item.getJSONArray("lore");
                lore = new ArrayList<String>();
                for (int j = 0; j < l.length(); j++) {
                    lore.add(l.getString(j));
                }
            }
            if (item.has("repairPenalty"))
                repairPenalty = item.getInt("repairPenalty");


			if (Material.getMaterial(id) == null)
				errorlog(ChatColor.WHITE + "Item " + index + " - No Material found with id of " + id);
            Material mat = Material.getMaterial(id);
            ItemStack stuff = new ItemStack(mat, amount, (short) data);
            if (MinecraftUtils.getMinecraftVersion().startsWith("1.8"))
                if (mat == Material.BANNER) {
                    BannerMeta meta = BannerSeparator.getBannerMeta(item.getJSONObject("banner-meta"));
                    stuff.setItemMeta(meta);
                } else if ((mat == Material.BOOK_AND_QUILL || mat == Material.WRITTEN_BOOK) && item.has("book-meta")) {
                    BookMeta meta = BookSeparator.getBookMeta(item.getJSONObject("book-meta"));
                    stuff.setItemMeta(meta);
                } else if (mat == Material.ENCHANTED_BOOK && item.has("book-meta")) {
                    EnchantmentStorageMeta meta = BookSeparator.getEnchantedBookMeta(item.getJSONObject("book-meta"));
                    stuff.setItemMeta(meta);
                } else if (Utils.isLeatherArmor(mat) && item.has("armor-meta")) {
                    LeatherArmorMeta meta = LeatherArmorSeparator.getLeatherArmorMeta(item.getJSONObject("armor-meta"));
                    stuff.setItemMeta(meta);
                } else if (mat == Material.SKULL_ITEM && item.has("skull-meta")) {
                    SkullMeta meta = SkullSeparator.getSkullMeta(item.getJSONObject("skull-meta"));
                    stuff.setItemMeta(meta);
                } else if (mat == Material.FIREWORK && item.has("firework-meta")) {
                    FireworkMeta meta = FireworkSeparator.getFireworkMeta(item.getJSONObject("firework-meta"));
                    stuff.setItemMeta(meta);
                }
            if(MinecraftUtils.getMinecraftVersion().startsWith("1.7"))
                if((mat == Material.BOOK_AND_QUILL || mat == Material.WRITTEN_BOOK) && item.has("book-meta")) {
                    BookMeta meta = BookSeparator.getBookMeta(item.getJSONObject("book-meta"));
                    stuff.setItemMeta(meta);
                } else if(mat == Material.ENCHANTED_BOOK && item.has("book-meta")) {
                    EnchantmentStorageMeta meta = BookSeparator.getEnchantedBookMeta(item.getJSONObject("book-meta"));
                    stuff.setItemMeta(meta);
                } else if(Utils.isLeatherArmor(mat) && item.has("armor-meta")) {
                    LeatherArmorMeta meta = LeatherArmorSeparator.getLeatherArmorMeta(item.getJSONObject("armor-meta"));
                    stuff.setItemMeta(meta);
                } else if(mat == Material.SKULL_ITEM && item.has("skull-meta")) {
                    SkullMeta meta = SkullSeparator.getSkullMeta(item.getJSONObject("skull-meta"));
                    stuff.setItemMeta(meta);
                    } else if(mat == Material.FIREWORK && item.has("firework-meta")) {
                    FireworkMeta meta = FireworkSeparator.getFireworkMeta(item.getJSONObject("firework-meta"));
                    stuff.setItemMeta(meta);
                }

			ItemMeta meta = stuff.getItemMeta();
			if(name != null)
				meta.setDisplayName(name);
            if (flags != null) {
                for (String flag : flags) {
                    meta.addItemFlags(ItemFlag.valueOf(flag));
                }
            }
			if(lore != null)
				meta.setLore(lore);
			stuff.setItemMeta(meta);
			if(repairPenalty != 0){
				Repairable rep = (Repairable) meta;
				rep.setRepairCost(repairPenalty);
				stuff.setItemMeta((ItemMeta) rep);
			}

			if(enchants != null)
				stuff.addUnsafeEnchantments(enchants);
			return stuff;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String separateItemInInventoryAsString(ItemStack items, int index) {
		return separateItemInInventoryAsString(items, index, false);
	}

	public static String separateItemInInventoryAsString(ItemStack items, int index, boolean pretty) {
		return separateItemInInventoryAsString(items, index, pretty, 5);
	}

	public static String separateItemInInventoryAsString(ItemStack items, int index, boolean pretty, int indentFactor) {
		try {
			if(pretty) {
				return separateItemInInventory(items, index).toString(indentFactor);
			} else {
				return separateItemInInventory(items, index).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String separateItemAsString(ItemStack items) {
		return separateItemAsString(items, false);
	}

	public static String separateItemAsString(ItemStack items, boolean pretty) {
		return separateItemAsString(items, pretty, 5);
	}

	public static String separateItemAsString(ItemStack items, boolean pretty, int indentFactor) {
		try {
			if(pretty) {
				return separateItem(items).toString(indentFactor);
			} else {
				return separateItem(items).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}