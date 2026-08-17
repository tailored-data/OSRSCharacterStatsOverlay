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

import java.util.function.Predicate;
import lombok.Getter;

/**
 * The headed sections of the in-game "Equip Your Character" screen. Every
 * {@link InfoItem} belongs to exactly one group, and a group can be switched
 * off wholesale from the plugin config.
 */
@Getter
enum InfoItemGroup
{
	ATTACK("Attack bonus", CharacterStatsOverlayConfig::showAttackBonus),
	DEFENCE("Defence bonus", CharacterStatsOverlayConfig::showDefenceBonus),
	OTHER("Other bonuses", CharacterStatsOverlayConfig::showOtherBonuses),
	TARGET_SPECIFIC("Target-specific", CharacterStatsOverlayConfig::showTargetSpecific),
	WEAPON_SPEED("Weapon speed", CharacterStatsOverlayConfig::showWeaponSpeed);

	private final String title;
	private final Predicate<CharacterStatsOverlayConfig> enabled;

	InfoItemGroup(String title, Predicate<CharacterStatsOverlayConfig> enabled)
	{
		this.title = title;
		this.enabled = enabled;
	}

	boolean isEnabled(CharacterStatsOverlayConfig config)
	{
		return enabled.test(config);
	}
}
