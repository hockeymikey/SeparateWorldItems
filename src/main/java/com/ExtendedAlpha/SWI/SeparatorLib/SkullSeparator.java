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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.JSONException;
import org.json.JSONObject;

public class SkullSeparator {
	
	protected SkullSeparator() {
	}

	public static JSONObject separateSkull(SkullMeta meta) {
		try {
			JSONObject root = new JSONObject();
			if(meta.hasOwner())
				root.put("owner", meta.getOwner());
			return root;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String separateSkullAsString(SkullMeta meta) {
		return separateSkullAsString(meta, false);
	}

	public static String separateSkullAsString(SkullMeta meta, boolean pretty) {
		return separateSkullAsString(meta, pretty, 5);
	}

	public static String separateSkullAsString(SkullMeta meta, boolean pretty, int indentFactor) {
		try {
			if(pretty) {
				return separateSkull(meta).toString(indentFactor);
			} else {
				return separateSkull(meta).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static SkullMeta getSkullMeta(String meta) {
		try {
			return getSkullMeta(new JSONObject(meta));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static SkullMeta getSkullMeta(JSONObject meta) {
		try {
			ItemStack dummyItems = new ItemStack(Material.SKULL_ITEM);
			SkullMeta dummyMeta = (SkullMeta) dummyItems.getItemMeta();
			if(meta.has("owner"))
				dummyMeta.setOwner(meta.getString("owner"));
			return dummyMeta;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
