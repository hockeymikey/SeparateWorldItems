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

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;

public class PotionEffectSeparator {
	
	protected PotionEffectSeparator() {
	}

	public static String serializeEffects(Collection<PotionEffect> effects) {
		String serialized = "";
		for(PotionEffect e : effects) {
			serialized += e.getType().getId() + ":" + e.getDuration() + ":" + e.getAmplifier() + ";";
		}
		return serialized;
	}

	public static Collection<PotionEffect> getPotionEffects(String serializedEffects) {
		ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();
		if(serializedEffects.isEmpty())
			return effects;
		String[] effs = serializedEffects.split(";");
		for(int i = 0; i < effs.length; i++) {
			String[] effect = effs[i].split(":");
			if(effect.length < 3)
				throw new IllegalArgumentException(serializedEffects + " - PotionEffect " + i + " (" + effs[i] + "): split must at least have a length of 3");
			if(!Utils.isNum(effect[0]))
				throw new IllegalArgumentException(serializedEffects + " - PotionEffect " + i + " (" + effs[i] + "): id is not an integer");
			if(!Utils.isNum(effect[1]))
				throw new IllegalArgumentException(serializedEffects + " - PotionEffect " + i + " (" + effs[i] + "): duration is not an integer");
			if(!Utils.isNum(effect[2]))
				throw new IllegalArgumentException(serializedEffects + " - PotionEffect " + i + " (" + effs[i] + "): amplifier is not an integer");
			int id = Integer.parseInt(effect[0]);
			int duration = Integer.parseInt(effect[1]);
			int amplifier = Integer.parseInt(effect[2]);
			PotionEffectType effectType = PotionEffectType.getById(id);
			if(effectType == null)
				throw new IllegalArgumentException(serializedEffects + " - PotionEffect " + i + " (" + effs[i] + "): no PotionEffectType with id of " + id);
			PotionEffect e = new PotionEffect(effectType, duration, amplifier);
			effects.add(e);
		}
		return effects;
	}

	public static void addPotionEffects(String code, LivingEntity entity) {
		entity.addPotionEffects(getPotionEffects(code));
	}

	public static void setPotionEffects(String code, LivingEntity entity) {
		for(PotionEffect effect : entity.getActivePotionEffects()) {
			entity.removePotionEffect(effect.getType());
		}
		addPotionEffects(code, entity);
	}
	
}
