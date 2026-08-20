/*
 * Copyright (c) 2026, Taylor Burks
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.tailoreddata.characterstats;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import lombok.Getter;

/**
 * Every individual data point the plugin can show, in the order the game lists
 * them. Each item knows which group it belongs to, its sidebar icon, which
 * config toggle governs it, and how to render itself from an
 * {@link EquipmentBonuses} snapshot. Both the sidebar panel and the overlay
 * render from this one list.
 */
@Getter
enum InfoItem
{
	ATTACK_STAB(InfoItemGroup.ATTACK, "Stab", "stab.png",
		CharacterStatsConfig::showAttackStab,
		(b, c) -> signed(b.getStabAttack())),
	ATTACK_SLASH(InfoItemGroup.ATTACK, "Slash", "slash.png",
		CharacterStatsConfig::showAttackSlash,
		(b, c) -> signed(b.getSlashAttack())),
	ATTACK_CRUSH(InfoItemGroup.ATTACK, "Crush", "crush.png",
		CharacterStatsConfig::showAttackCrush,
		(b, c) -> signed(b.getCrushAttack())),
	ATTACK_MAGIC(InfoItemGroup.ATTACK, "Magic", "magic.png",
		CharacterStatsConfig::showAttackMagic,
		(b, c) -> signed(b.getMagicAttack())),
	ATTACK_RANGE(InfoItemGroup.ATTACK, "Range", "range.png",
		CharacterStatsConfig::showAttackRange,
		(b, c) -> signed(b.getRangeAttack())),

	DEFENCE_STAB(InfoItemGroup.DEFENCE, "Stab", "def_stab.png",
		CharacterStatsConfig::showDefenceStab,
		(b, c) -> signed(b.getStabDefence())),
	DEFENCE_SLASH(InfoItemGroup.DEFENCE, "Slash", "def_slash.png",
		CharacterStatsConfig::showDefenceSlash,
		(b, c) -> signed(b.getSlashDefence())),
	DEFENCE_CRUSH(InfoItemGroup.DEFENCE, "Crush", "def_crush.png",
		CharacterStatsConfig::showDefenceCrush,
		(b, c) -> signed(b.getCrushDefence())),
	DEFENCE_MAGIC(InfoItemGroup.DEFENCE, "Magic", "def_magic.png",
		CharacterStatsConfig::showDefenceMagic,
		(b, c) -> signed(b.getMagicDefence())),
	DEFENCE_RANGE(InfoItemGroup.DEFENCE, "Range", "def_range.png",
		CharacterStatsConfig::showDefenceRange,
		(b, c) -> signed(b.getRangeDefence())),

	MELEE_STRENGTH(InfoItemGroup.OTHER, "Melee STR", "strength.png",
		CharacterStatsConfig::showMeleeStrength,
		(b, c) -> signed(b.getMeleeStrength())),
	RANGED_STRENGTH(InfoItemGroup.OTHER, "Ranged STR", "range.png",
		CharacterStatsConfig::showRangedStrength,
		(b, c) -> signed(b.getRangedStrength())),
	MAGIC_DAMAGE(InfoItemGroup.OTHER, "Magic DMG", "magic.png",
		CharacterStatsConfig::showMagicDamage,
		(b, c) -> String.format(Locale.US, "%+.1f%%", b.getMagicDamage())),
	PRAYER(InfoItemGroup.OTHER, "Prayer", "prayer.png",
		CharacterStatsConfig::showPrayer,
		(b, c) -> signed(b.getPrayer())),

	UNDEAD(InfoItemGroup.TARGET_SPECIFIC, "Undead", "undead.png",
		CharacterStatsConfig::showUndead,
		(b, c) -> orUnknown(b.getUndeadBonus())),
	SLAYER(InfoItemGroup.TARGET_SPECIFIC, "Slayer", "slayer.png",
		CharacterStatsConfig::showSlayer,
		(b, c) -> orUnknown(b.getSlayerBonus())),

	SPEED_BASE(InfoItemGroup.WEAPON_SPEED, "Base", "speed_base.png",
		CharacterStatsConfig::showWeaponSpeedBase,
		(b, c) -> speed(b.getWeaponSpeedBase(), c.speedFormat())),
	SPEED_ACTUAL(InfoItemGroup.WEAPON_SPEED, "Actual", "speed_actual.png",
		CharacterStatsConfig::showWeaponSpeedActual,
		(b, c) -> speed(b.getWeaponSpeedActual(), c.speedFormat()));

	/** Shown in place of a value the plugin cannot currently determine. */
	static final String UNKNOWN = "?";

	private static final double SECONDS_PER_TICK = 0.6d;

	private final InfoItemGroup group;
	private final String label;
	/** File name of this item's 16x16 sidebar icon, under {@code icons/} on the classpath. */
	private final String iconFile;
	private final Predicate<CharacterStatsConfig> enabled;
	private final BiFunction<EquipmentBonuses, CharacterStatsConfig, String> formatter;

	InfoItem(
		InfoItemGroup group,
		String label,
		String iconFile,
		Predicate<CharacterStatsConfig> enabled,
		BiFunction<EquipmentBonuses, CharacterStatsConfig, String> formatter)
	{
		this.group = group;
		this.label = label;
		this.iconFile = iconFile;
		this.enabled = enabled;
		this.formatter = formatter;
	}

	boolean isEnabled(CharacterStatsConfig config)
	{
		return group.isEnabled(config) && enabled.test(config);
	}

	String format(EquipmentBonuses bonuses, CharacterStatsConfig config)
	{
		return formatter.apply(bonuses, config);
	}

	private static String signed(int value)
	{
		return value < 0 ? Integer.toString(value) : "+" + value;
	}

	private static String orUnknown(String value)
	{
		return value == null ? UNKNOWN : value;
	}

	private static String speed(int ticks, SpeedFormat format)
	{
		if (ticks <= 0)
		{
			return UNKNOWN;
		}

		return format == SpeedFormat.TICKS
			? ticks + (ticks == 1 ? " tick" : " ticks")
			: String.format(Locale.US, "%.1fs", ticks * SECONDS_PER_TICK);
	}
}
