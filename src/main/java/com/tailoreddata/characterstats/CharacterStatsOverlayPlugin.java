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

import com.google.inject.Provides;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ParamID;
import net.runelite.api.StructComposition;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "Character Stats Overlay",
	description = "Shows your equipment bonuses from the Equip Your Character screen as a movable overlay",
	tags = {"equipment", "gear", "bonus", "bonuses", "stats", "attack", "defence", "strength", "prayer"}
)
public class CharacterStatsOverlayPlugin extends Plugin
{
	/**
	 * The style name the game uses internally for the ranged "Rapid" style, which
	 * attacks one tick faster than the weapon's base speed.
	 */
	private static final String RAPID_STYLE_NAME = "Ranging";

	private static final Item[] NO_ITEMS = new Item[0];
	private static final int NO_SYNC = 0;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private CharacterStatsOverlay overlay;

	/** The most recently computed snapshot. Written and read only on the client thread. */
	@Getter
	private EquipmentBonuses bonuses = new EquipmentBonuses();

	/** Hash of the equipment that was worn when the target-specific values were last read. */
	private int syncedEquipmentHash = NO_SYNC;

	@Nullable
	private String syncedUndead;

	@Nullable
	private String syncedSlayer;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		clientThread.invokeLater(this::rebuild);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		reset();
	}

	@Provides
	CharacterStatsOverlayConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CharacterStatsOverlayConfig.class);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		final GameState state = event.getGameState();
		if (state == GameState.LOGIN_SCREEN || state == GameState.HOPPING)
		{
			reset();
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.WORN)
		{
			rebuild();
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		// The attack style decides whether the actual attack speed differs from the base speed.
		if (event.getVarpId() == VarPlayerID.COM_MODE
			|| event.getVarbitId() == VarbitID.COMBAT_WEAPON_CATEGORY)
		{
			rebuild();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		syncTargetSpecific();
	}

	private void reset()
	{
		bonuses = new EquipmentBonuses();
		syncedEquipmentHash = NO_SYNC;
		syncedUndead = null;
		syncedSlayer = null;
	}

	/**
	 * Recomputes every bonus from the currently worn equipment. Cheap enough to run
	 * on equipment or attack style changes: it touches at most fourteen items.
	 */
	private void rebuild()
	{
		final Item[] items = wornItems();
		final EquipmentBonuses next = new EquipmentBonuses();

		for (Item item : items)
		{
			final ItemStats stats = equipmentStats(item);
			if (stats != null)
			{
				next.accumulate(stats.getEquipment());
			}
		}

		final int baseSpeed = weaponSpeed(items);
		next.setWeaponSpeedBase(baseSpeed);
		next.setWeaponSpeedActual(isRapidStyle() ? Math.max(1, baseSpeed - 1) : baseSpeed);

		if (syncedEquipmentHash != NO_SYNC && syncedEquipmentHash == equipmentHash(items))
		{
			next.setUndeadBonus(syncedUndead);
			next.setSlayerBonus(syncedSlayer);
		}

		bonuses = next;
	}

	/**
	 * Reads the undead and slayer multipliers off the game's own equipment stats
	 * screen while it is open. The game derives these from rules the client API does
	 * not expose, so mirroring its text is the only way to report them accurately.
	 * They are dropped as soon as the equipment changes, rather than shown stale.
	 */
	private void syncTargetSpecific()
	{
		final Widget undeadWidget = client.getWidget(InterfaceID.Equipment.TYPEMULTIPLIER);
		final Widget slayerWidget = client.getWidget(InterfaceID.Equipment.SLAYERMULTIPLIER);
		if (undeadWidget == null || slayerWidget == null || undeadWidget.isHidden())
		{
			return;
		}

		final String undead = multiplierValue(undeadWidget.getText());
		final String slayer = multiplierValue(slayerWidget.getText());
		if (undead == null || slayer == null)
		{
			return;
		}

		final int hash = equipmentHash(wornItems());
		if (hash == syncedEquipmentHash && undead.equals(syncedUndead) && slayer.equals(syncedSlayer))
		{
			return;
		}

		syncedEquipmentHash = hash;
		syncedUndead = undead;
		syncedSlayer = slayer;
		rebuild();
	}

	private Item[] wornItems()
	{
		final ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
		return equipment == null ? NO_ITEMS : equipment.getItems();
	}

	private int weaponSpeed(Item[] items)
	{
		final int slot = EquipmentInventorySlot.WEAPON.getSlotIdx();
		final ItemStats stats = slot < items.length ? equipmentStats(items[slot]) : null;
		if (stats == null)
		{
			return EquipmentBonuses.UNARMED_ATTACK_SPEED;
		}

		final int speed = stats.getEquipment().getAspeed();
		return speed > 0 ? speed : EquipmentBonuses.UNARMED_ATTACK_SPEED;
	}

	/**
	 * @return the item's stats, or null if the slot is empty or the item has no
	 * equipment stats
	 */
	@Nullable
	private ItemStats equipmentStats(@Nullable Item item)
	{
		if (item == null || item.getId() <= 0)
		{
			return null;
		}

		final ItemStats stats = itemManager.getItemStats(item.getId());
		return stats == null || stats.getEquipment() == null ? null : stats;
	}

	/**
	 * @return true if the current attack style is the ranged "Rapid" style
	 */
	private boolean isRapidStyle()
	{
		// Mirrors how the game resolves the style list for the equipped weapon category.
		final EnumComposition weaponStyles = client.getEnum(EnumID.WEAPON_STYLES);
		if (weaponStyles == null)
		{
			return false;
		}

		final int styleEnumId = weaponStyles.getIntValue(client.getVarbitValue(VarbitID.COMBAT_WEAPON_CATEGORY));
		if (styleEnumId == -1)
		{
			return false;
		}

		final EnumComposition styles = client.getEnum(styleEnumId);
		if (styles == null)
		{
			return false;
		}

		final int[] styleStructIds = styles.getIntVals();
		final int styleIndex = client.getVarpValue(VarPlayerID.COM_MODE);
		if (styleIndex < 0 || styleIndex >= styleStructIds.length)
		{
			return false;
		}

		final StructComposition style = client.getStructComposition(styleStructIds[styleIndex]);
		return style != null && RAPID_STYLE_NAME.equalsIgnoreCase(style.getStringValue(ParamID.ATTACK_STYLE_NAME));
	}

	/**
	 * Strips the game's label off a multiplier line, turning "Undead: 0%" into "0%".
	 *
	 * @return the value, or null if the widget has no text yet
	 */
	@Nullable
	private static String multiplierValue(@Nullable String widgetText)
	{
		if (widgetText == null)
		{
			return null;
		}

		final String text = Text.removeTags(widgetText).trim();
		final int separator = text.indexOf(':');
		final String value = separator == -1 ? text : text.substring(separator + 1).trim();
		return value.isEmpty() ? null : value;
	}

	private static int equipmentHash(Item[] items)
	{
		int hash = 1;
		for (Item item : items)
		{
			hash = 31 * hash + (item == null ? 0 : item.getId());
		}
		return hash;
	}
}
