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

import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.game.ItemEquipmentStats;

/**
 * A snapshot of every value the "Equip Your Character" screen shows. A fresh
 * instance is built by {@link CharacterStatsOverlayPlugin} whenever the
 * equipment or the attack style changes, then published for the overlay to
 * read; instances are never mutated after publication.
 */
@Getter
class EquipmentBonuses
{
	/** Attack speed of an empty weapon slot, in game ticks. */
	static final int UNARMED_ATTACK_SPEED = 4;

	private int stabAttack;
	private int slashAttack;
	private int crushAttack;
	private int magicAttack;
	private int rangeAttack;

	private int stabDefence;
	private int slashDefence;
	private int crushDefence;
	private int magicDefence;
	private int rangeDefence;

	private int meleeStrength;
	private int rangedStrength;
	private float magicDamage;
	private int prayer;

	/** Weapon attack speed in game ticks, ignoring the current attack style. */
	@Setter
	private int weaponSpeedBase = UNARMED_ATTACK_SPEED;

	/** Weapon attack speed in game ticks after the current attack style is applied. */
	@Setter
	private int weaponSpeedActual = UNARMED_ATTACK_SPEED;

	/**
	 * Undead damage multiplier, exactly as the game rendered it (e.g. {@code "0%"}).
	 * Null when no up-to-date value has been read from the equipment screen.
	 */
	@Setter
	@Nullable
	private String undeadBonus;

	/**
	 * Slayer damage multiplier, exactly as the game rendered it (e.g. {@code "15% (all styles)"}).
	 * Null when no up-to-date value has been read from the equipment screen.
	 */
	@Setter
	@Nullable
	private String slayerBonus;

	/**
	 * Adds one equipped item's stats to the running totals. This mirrors what the
	 * game does: every worn item contributes, with no special casing for two-handed
	 * weapons or ammunition.
	 */
	void accumulate(ItemEquipmentStats stats)
	{
		stabAttack += stats.getAstab();
		slashAttack += stats.getAslash();
		crushAttack += stats.getAcrush();
		magicAttack += stats.getAmagic();
		rangeAttack += stats.getArange();

		stabDefence += stats.getDstab();
		slashDefence += stats.getDslash();
		crushDefence += stats.getDcrush();
		magicDefence += stats.getDmagic();
		rangeDefence += stats.getDrange();

		meleeStrength += stats.getStr();
		rangedStrength += stats.getRstr();
		magicDamage += stats.getMdmg();
		prayer += stats.getPrayer();
	}
}
