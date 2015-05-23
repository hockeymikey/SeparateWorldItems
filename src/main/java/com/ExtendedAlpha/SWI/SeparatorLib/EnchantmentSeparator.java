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

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentSeparator {
	
	protected EnchantmentSeparator() {
	}

	public static String serializeEnchantments(Map<Enchantment, Integer> enchantments) {
		String serialized = "";
		for(Enchantment e : enchantments.keySet()) {
			serialized += e.getId() + ":" + enchantments.get(e) + ";";
		}
		return serialized;
	}

	public static Map<Enchantment, Integer> getEnchantments(String serializedEnchants) {
		HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
		if(serializedEnchants.isEmpty())
			return enchantments;
		String[] enchants = serializedEnchants.split(";");
		for(int i = 0; i < enchants.length; i++) {
			String[] ench = enchants[i].split(":");
			if(ench.length < 2)
				throw new IllegalArgumentException(serializedEnchants + " - Enchantment " + i + " (" + enchants[i] + "): split must at least have a length of 2");
			if(!Utils.isNum(ench[0]))
				throw new IllegalArgumentException(serializedEnchants + " - Enchantment " + i + " (" + enchants[i] + "): id is not an integer");
			if(!Utils.isNum(ench[1]))
				throw new IllegalArgumentException(serializedEnchants + " - Enchantment " + i + " (" + enchants[i] + "): level is not an integer");
			int id = Integer.parseInt(ench[0]);
			int level = Integer.parseInt(ench[1]);
			Enchantment e = Enchantment.getById(id);
			if(e == null)
				throw new IllegalArgumentException(serializedEnchants + " - Enchantment " + i + " (" + enchants[i] + "): no Enchantment with id of " + id);
			enchantments.put(e, level);
		}
		return enchantments;
	}

	public static Map<Enchantment, Integer> getEnchantsFromOldFormat(String oldFormat) {
		HashMap<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
		if(oldFormat.length() == 0) {
			return enchants;
		}
		String nums = Long.parseLong(oldFormat, 32) + "";
		System.out.println(nums);
		for(int i = 0; i < nums.length(); i += 3) {
			int enchantId = Integer.parseInt(nums.substring(i, i + 2));
			int enchantLevel = Integer.parseInt(nums.charAt(i + 2) + "");
			Enchantment ench = Enchantment.getById(enchantId);
			enchants.put(ench, enchantLevel);
		}
		return enchants;
	}

	public static String convert(String oldFormat) {
		Map<Enchantment, Integer> enchants = getEnchantsFromOldFormat(oldFormat);
		return serializeEnchantments(enchants);
	}

	public static Map<Enchantment, Integer> convertAndGetEnchantments(String oldFormat) {
		String newFormat = convert(oldFormat);
		return getEnchantments(newFormat);
	}

	public static void addEnchantments(String code, ItemStack items) {
		items.addUnsafeEnchantments(getEnchantments(code));
	}
	
}
