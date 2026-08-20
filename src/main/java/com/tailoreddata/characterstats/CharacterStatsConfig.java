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

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;
import net.runelite.client.ui.overlay.components.ComponentConstants;

@ConfigGroup(CharacterStatsConfig.GROUP)
public interface CharacterStatsConfig extends Config
{
	String GROUP = "characterstatsoverlay";

	@ConfigSection(
		name = "Display",
		description = "Where the stats are shown and how they are formatted",
		position = 0
	)
	String displaySection = "display";

	@ConfigSection(
		name = "Overlay appearance",
		description = "Transparency and colour of the on-screen overlay",
		position = 1
	)
	String appearanceSection = "appearance";

	@ConfigSection(
		name = "Attack bonus",
		description = "Which attack bonuses to display",
		position = 2
	)
	String attackSection = "attack";

	@ConfigSection(
		name = "Defence bonus",
		description = "Which defence bonuses to display",
		position = 3
	)
	String defenceSection = "defence";

	@ConfigSection(
		name = "Other bonuses",
		description = "Which of the other bonuses to display",
		position = 4
	)
	String otherSection = "other";

	@ConfigSection(
		name = "Target-specific",
		description = "Which target-specific bonuses to display",
		position = 5
	)
	String targetSection = "target";

	@ConfigSection(
		name = "Weapon speed",
		description = "Which weapon speed values to display",
		position = 6
	)
	String weaponSpeedSection = "weaponSpeed";

	// ------------------------------------------------------------------
	// Display
	// ------------------------------------------------------------------

	@ConfigItem(
		keyName = "showSidebar",
		name = "Show sidebar panel",
		description = "Show the plugin's icon in the sidebar, which opens the stats panel",
		position = 0,
		section = displaySection
	)
	default boolean showSidebar()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showOverlay",
		name = "Show overlay",
		description = "Draw the stats on screen as a movable overlay as well",
		position = 1,
		section = displaySection
	)
	default boolean showOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showInfoItemIcons",
		name = "Show icons in sidebar",
		description = "Show an icon beside each value in the sidebar panel",
		position = 2,
		section = displaySection
	)
	default boolean showInfoItemIcons()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showGroupHeaders",
		name = "Show group headers",
		description = "Show the heading above each group, e.g. \"Attack bonus\"",
		position = 3,
		section = displaySection
	)
	default boolean showGroupHeaders()
	{
		return true;
	}

	@ConfigItem(
		keyName = "speedFormat",
		name = "Weapon speed units",
		description = "Show weapon speed in seconds, as the game does, or in game ticks",
		position = 4,
		section = displaySection
	)
	default SpeedFormat speedFormat()
	{
		return SpeedFormat.SECONDS;
	}

	// ------------------------------------------------------------------
	// Overlay appearance
	// ------------------------------------------------------------------

	@Range(max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(
		keyName = "opacity",
		name = "Transparency",
		description = "How opaque the overlay is drawn. 100% is fully opaque, 0% is invisible.",
		position = 0,
		section = appearanceSection
	)
	default int opacity()
	{
		return 100;
	}

	@Alpha
	@ConfigItem(
		keyName = "backgroundColor",
		name = "Background colour",
		description = "Background colour of the overlay panel. Its own alpha stacks with the transparency slider.",
		position = 1,
		section = appearanceSection
	)
	default Color backgroundColor()
	{
		return ComponentConstants.STANDARD_BACKGROUND_COLOR;
	}

	@ConfigItem(
		keyName = "showTitle",
		name = "Show overlay title",
		description = "Show a title line at the top of the overlay",
		position = 2,
		section = appearanceSection
	)
	default boolean showTitle()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "headerColor",
		name = "Overlay header colour",
		description = "Colour of the overlay's title and group headings",
		position = 3,
		section = appearanceSection
	)
	default Color headerColor()
	{
		return Color.ORANGE;
	}

	// ------------------------------------------------------------------
	// Attack bonus
	// ------------------------------------------------------------------

	@ConfigItem(
		keyName = "showAttackBonus",
		name = "Show attack bonus",
		description = "Show the Attack bonus group",
		position = 0,
		section = attackSection
	)
	default boolean showAttackBonus()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAttackStab",
		name = "Stab",
		description = "Show the stab attack bonus",
		position = 1,
		section = attackSection
	)
	default boolean showAttackStab()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAttackSlash",
		name = "Slash",
		description = "Show the slash attack bonus",
		position = 2,
		section = attackSection
	)
	default boolean showAttackSlash()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAttackCrush",
		name = "Crush",
		description = "Show the crush attack bonus",
		position = 3,
		section = attackSection
	)
	default boolean showAttackCrush()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAttackMagic",
		name = "Magic",
		description = "Show the magic attack bonus",
		position = 4,
		section = attackSection
	)
	default boolean showAttackMagic()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAttackRange",
		name = "Range",
		description = "Show the ranged attack bonus",
		position = 5,
		section = attackSection
	)
	default boolean showAttackRange()
	{
		return true;
	}

	// ------------------------------------------------------------------
	// Defence bonus
	// ------------------------------------------------------------------

	@ConfigItem(
		keyName = "showDefenceBonus",
		name = "Show defence bonus",
		description = "Show the Defence bonus group",
		position = 0,
		section = defenceSection
	)
	default boolean showDefenceBonus()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDefenceStab",
		name = "Stab",
		description = "Show the stab defence bonus",
		position = 1,
		section = defenceSection
	)
	default boolean showDefenceStab()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDefenceSlash",
		name = "Slash",
		description = "Show the slash defence bonus",
		position = 2,
		section = defenceSection
	)
	default boolean showDefenceSlash()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDefenceCrush",
		name = "Crush",
		description = "Show the crush defence bonus",
		position = 3,
		section = defenceSection
	)
	default boolean showDefenceCrush()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDefenceMagic",
		name = "Magic",
		description = "Show the magic defence bonus",
		position = 4,
		section = defenceSection
	)
	default boolean showDefenceMagic()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDefenceRange",
		name = "Range",
		description = "Show the ranged defence bonus",
		position = 5,
		section = defenceSection
	)
	default boolean showDefenceRange()
	{
		return true;
	}

	// ------------------------------------------------------------------
	// Other bonuses
	// ------------------------------------------------------------------

	@ConfigItem(
		keyName = "showOtherBonuses",
		name = "Show other bonuses",
		description = "Show the Other bonuses group",
		position = 0,
		section = otherSection
	)
	default boolean showOtherBonuses()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMeleeStrength",
		name = "Melee strength",
		description = "Show the melee strength bonus",
		position = 1,
		section = otherSection
	)
	default boolean showMeleeStrength()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showRangedStrength",
		name = "Ranged strength",
		description = "Show the ranged strength bonus",
		position = 2,
		section = otherSection
	)
	default boolean showRangedStrength()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMagicDamage",
		name = "Magic damage",
		description = "Show the magic damage bonus",
		position = 3,
		section = otherSection
	)
	default boolean showMagicDamage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showPrayer",
		name = "Prayer",
		description = "Show the prayer bonus",
		position = 4,
		section = otherSection
	)
	default boolean showPrayer()
	{
		return true;
	}

	// ------------------------------------------------------------------
	// Target-specific
	// ------------------------------------------------------------------

	@ConfigItem(
		keyName = "showTargetSpecific",
		name = "Show target-specific",
		description = "Show the Target-specific group. These two values are read from the game's own"
			+ " equipment screen, so they show \"?\" until you next open Worn Equipment -> View stats.",
		position = 0,
		section = targetSection
	)
	default boolean showTargetSpecific()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showUndead",
		name = "Undead",
		description = "Show the undead damage multiplier",
		position = 1,
		section = targetSection
	)
	default boolean showUndead()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSlayer",
		name = "Slayer",
		description = "Show the slayer damage multiplier",
		position = 2,
		section = targetSection
	)
	default boolean showSlayer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hideUnknownTargetSpecific",
		name = "Hide until known",
		description = "Hide the target-specific values instead of showing \"?\" while they are unknown",
		position = 3,
		section = targetSection
	)
	default boolean hideUnknownTargetSpecific()
	{
		return false;
	}

	// ------------------------------------------------------------------
	// Weapon speed
	// ------------------------------------------------------------------

	@ConfigItem(
		keyName = "showWeaponSpeed",
		name = "Show weapon speed",
		description = "Show the Weapon speed group",
		position = 0,
		section = weaponSpeedSection
	)
	default boolean showWeaponSpeed()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showWeaponSpeedBase",
		name = "Base",
		description = "Show the weapon's base attack speed",
		position = 1,
		section = weaponSpeedSection
	)
	default boolean showWeaponSpeedBase()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showWeaponSpeedActual",
		name = "Actual",
		description = "Show the attack speed after the current attack style is applied,"
			+ " which is one tick faster on Rapid",
		position = 2,
		section = weaponSpeedSection
	)
	default boolean showWeaponSpeedActual()
	{
		return true;
	}
}
