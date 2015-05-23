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

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BannerSeparator {

    protected BannerSeparator() {
    }

    public static JSONObject serializeBanner(BannerMeta banner) {
        try {
            JSONObject root = new JSONObject();
            if (banner.getBaseColor() != null)
                root.put("base-color", banner.getBaseColor().getDyeData());

            JSONArray colors = new JSONArray();
            JSONArray patternTypes = new JSONArray();
            for (Pattern pattern : banner.getPatterns()) {
                colors.put(pattern.getColor().getDyeData());
                patternTypes.put(pattern.getPattern().getIdentifier());
            }

            root.put("colors", colors);
            root.put("pattern-types", patternTypes);

            return root;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static BannerMeta getBannerMeta(JSONObject json) {
        try {
            BannerMeta dummy = (BannerMeta) new ItemStack(Material.BANNER).getItemMeta();
            if (json.has("base-color"))
                dummy.setBaseColor(DyeColor.getByDyeData(Byte.parseByte("" + json.get("base-color"))));

            JSONArray colors = json.getJSONArray("colors");
            JSONArray patternTypes = json.getJSONArray("pattern-types");
            for (int i = 0; i < colors.length(); i++) {
                dummy.addPattern(new Pattern(DyeColor.getByDyeData(Integer.valueOf((int) colors.get(i)).byteValue()),
                        PatternType.getByIdentifier(patternTypes.getString(i))));
            }

            return dummy;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
