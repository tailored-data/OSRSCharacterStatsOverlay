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

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import net.runelite.client.ui.overlay.OverlayLayer;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class CharacterStatsOverlay extends OverlayPanel
{
	/** Name shown in the overlay's right-click menu. */
	private static final String OVERLAY_NAME = "Character stats overlay";
	/** Title line drawn at the top of the panel. */
	private static final String TITLE = "Character stats";
	private static final int DEFAULT_WIDTH = 150;

	private final Client client;
	private final CharacterStatsOverlayPlugin plugin;
	private final CharacterStatsOverlayConfig config;

	@Inject
	CharacterStatsOverlay(Client client, CharacterStatsOverlayPlugin plugin, CharacterStatsOverlayConfig config)
	{
		super(plugin);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPreferredSize(new Dimension(DEFAULT_WIDTH, 0));
		addMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, OVERLAY_NAME);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return null;
		}

		final float opacity = config.opacity() / 100f;
		if (opacity <= 0f)
		{
			return null;
		}

		final List<LayoutableRenderableEntity> children = panelComponent.getChildren();
		buildContents(children);

		if (children.isEmpty())
		{
			return null;
		}

		panelComponent.setBackgroundColor(config.backgroundColor());

		final Composite originalComposite = graphics.getComposite();
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(opacity, 1f)));
		try
		{
			return super.render(graphics);
		}
		finally
		{
			graphics.setComposite(originalComposite);
		}
	}

	private void buildContents(List<LayoutableRenderableEntity> children)
	{
		final EquipmentBonuses bonuses = plugin.getBonuses();

		for (InfoItemGroup group : InfoItemGroup.values())
		{
			if (!group.isEnabled(config))
			{
				continue;
			}

			final List<LineComponent> lines = new ArrayList<>();
			for (InfoItem item : InfoItem.values())
			{
				if (item.getGroup() != group || !item.isEnabled(config))
				{
					continue;
				}

				final String value = item.format(bonuses, config);
				if (InfoItem.UNKNOWN.equals(value)
					&& group == InfoItemGroup.TARGET_SPECIFIC
					&& config.hideUnknownTargetSpecific())
				{
					continue;
				}

				lines.add(LineComponent.builder()
					.left(item.getLabel())
					.right(value)
					.build());
			}

			if (lines.isEmpty())
			{
				continue;
			}

			// The title goes above the first group that actually has something to show.
			if (children.isEmpty() && config.showTitle())
			{
				children.add(TitleComponent.builder()
					.text(TITLE)
					.color(config.headerColor())
					.build());
			}

			if (config.showGroupHeaders())
			{
				children.add(TitleComponent.builder()
					.text(group.getTitle())
					.color(config.headerColor())
					.build());
			}

			children.addAll(lines);
		}
	}
}
