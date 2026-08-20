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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

/**
 * Sidebar view of the same {@link EquipmentBonuses} snapshot the overlay draws.
 * Every method here runs on the Swing event dispatch thread; the plugin hands
 * snapshots over with {@code SwingUtilities.invokeLater}.
 */
class CharacterStatsPanel extends PluginPanel
{
	private static final String LOGGED_OUT_MESSAGE = "Log in to see your equipment bonuses.";
	private static final String NOTHING_SELECTED_MESSAGE =
		"Every value is switched off. Turn some back on in the plugin settings.";
	private static final Color VALUE_COLOR = Color.WHITE;

	private final CharacterStatsConfig config;
	private final Map<InfoItem, Icon> icons = new EnumMap<>(InfoItem.class);
	private final Map<InfoItem, JLabel> valueLabels = new EnumMap<>(InfoItem.class);
	private final JLabel message = new JLabel();

	/** Rows currently laid out, so a plain value change does not rebuild the panel. */
	private List<InfoItem> laidOutItems = null;
	private boolean laidOutHasData;

	/** The snapshot being displayed, or null when there is nothing to show. */
	@Nullable
	private EquipmentBonuses bonuses;

	@Inject
	CharacterStatsPanel(CharacterStatsConfig config)
	{
		this.config = config;

		message.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		message.setBorder(new EmptyBorder(4, 2, 4, 2));

		loadIcons();
		refresh();
	}

	/**
	 * Publishes a new snapshot, or null when the values are not available (logged
	 * out, or the plugin has just started).
	 */
	void setBonuses(@Nullable EquipmentBonuses bonuses)
	{
		this.bonuses = bonuses;
		refresh();
	}

	/**
	 * Re-reads the config and the current snapshot, rebuilding the rows only when
	 * the set of visible rows has actually changed.
	 */
	void refresh()
	{
		final boolean hasData = bonuses != null;
		final List<InfoItem> visible = hasData ? visibleItems() : Collections.emptyList();

		if (laidOutItems == null || hasData != laidOutHasData || !visible.equals(laidOutItems))
		{
			laidOutItems = visible;
			laidOutHasData = hasData;
			rebuildLayout(visible, hasData);
		}

		updateValues();
	}

	private void loadIcons()
	{
		final Map<String, Icon> byFileName = new HashMap<>();
		for (InfoItem item : InfoItem.values())
		{
			final Icon icon = byFileName.computeIfAbsent(item.getIconFile(), CharacterStatsPanel::loadIcon);
			if (icon != null)
			{
				icons.put(item, icon);
			}
		}
	}

	@Nullable
	private static Icon loadIcon(String fileName)
	{
		final BufferedImage image = ImageUtil.loadImageResource(CharacterStatsPanel.class, "icons/" + fileName);
		return image == null ? null : new ImageIcon(image);
	}

	/** The rows to draw, in group order, honouring both config and unknown values. */
	private List<InfoItem> visibleItems()
	{
		final List<InfoItem> visible = new ArrayList<>();
		for (InfoItem item : InfoItem.values())
		{
			if (item.isEnabled(config) && !isHiddenUnknown(item))
			{
				visible.add(item);
			}
		}
		return visible;
	}

	private boolean isHiddenUnknown(InfoItem item)
	{
		return item.getGroup() == InfoItemGroup.TARGET_SPECIFIC
			&& config.hideUnknownTargetSpecific()
			&& bonuses != null
			&& InfoItem.UNKNOWN.equals(item.format(bonuses, config));
	}

	private void rebuildLayout(List<InfoItem> visible, boolean hasData)
	{
		removeAll();
		valueLabels.clear();

		if (!hasData || visible.isEmpty())
		{
			message.setText("<html><body style='width:170px'>"
				+ (hasData ? NOTHING_SELECTED_MESSAGE : LOGGED_OUT_MESSAGE)
				+ "</body></html>");
			add(message);
		}
		else
		{
			InfoItemGroup group = null;
			for (InfoItem item : visible)
			{
				if (item.getGroup() != group)
				{
					group = item.getGroup();
					if (config.showGroupHeaders())
					{
						add(groupHeader(group));
					}
				}
				add(row(item));
			}
		}

		revalidate();
		repaint();
	}

	private void updateValues()
	{
		if (bonuses == null)
		{
			return;
		}

		for (Map.Entry<InfoItem, JLabel> entry : valueLabels.entrySet())
		{
			entry.getValue().setText(entry.getKey().format(bonuses, config));
		}
	}

	private JLabel groupHeader(InfoItemGroup group)
	{
		final JLabel header = new JLabel(group.getTitle());
		header.setForeground(ColorScheme.BRAND_ORANGE);
		header.setFont(header.getFont().deriveFont(Font.BOLD));
		header.setBorder(new EmptyBorder(6, 2, 0, 2));
		return header;
	}

	private JPanel row(InfoItem item)
	{
		final JPanel row = new JPanel(new BorderLayout());
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setBorder(new EmptyBorder(4, 6, 4, 6));

		final JLabel name = new JLabel(item.getLabel());
		name.setForeground(ColorScheme.TEXT_COLOR);
		if (config.showInfoItemIcons())
		{
			name.setIcon(icons.get(item));
			name.setIconTextGap(6);
		}

		final JLabel value = new JLabel();
		value.setForeground(VALUE_COLOR);
		value.setHorizontalAlignment(SwingConstants.RIGHT);
		valueLabels.put(item, value);

		row.add(name, BorderLayout.WEST);
		row.add(value, BorderLayout.EAST);
		return row;
	}
}
