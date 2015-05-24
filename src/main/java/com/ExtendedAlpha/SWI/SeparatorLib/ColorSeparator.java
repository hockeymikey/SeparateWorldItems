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

import org.bukkit.Color;
import org.json.JSONException;
import org.json.JSONObject;

public class ColorSeparator {
	
	protected ColorSeparator() {
	}

	public static JSONObject separateColor(Color color) {
		try {
			
			JSONObject root = new JSONObject();
			root.put("red", color.getRed());
			root.put("green", color.getGreen());
			root.put("blue", color.getBlue());
			return root;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Color getColor(String color) {
		try {
			return getColor(new JSONObject(color));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Color getColor(JSONObject color) {
		try {
			int r = 0, g = 0, b = 0;
			if(color.has("red"))
				r = color.getInt("red");
			if(color.has("green"))
				g = color.getInt("green");
			if(color.has("blue"))
				b = color.getInt("blue");
			return Color.fromRGB(r, g, b);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String separateColorAsString(Color color) {
		return separateColorAsString(color, false);
	}

	public static String separateColorAsString(Color color, boolean pretty) {
		return separateColorAsString(color, pretty, 5);
	}

	public static String separateColorAsString(Color color, boolean pretty, int indentFactor) {
		try {
			if(pretty) {
				return separateColor(color).toString(indentFactor);
			} else {
				return separateColor(color).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
