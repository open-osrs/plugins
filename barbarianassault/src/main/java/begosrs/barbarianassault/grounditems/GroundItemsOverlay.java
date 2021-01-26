/*
 * Copyright (c) 2020, BegOsrs <https://github.com/begosrs>
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
package begosrs.barbarianassault.grounditems;

import begosrs.barbarianassault.BaMinigameConfig;
import begosrs.barbarianassault.BaMinigamePlugin;
import begosrs.barbarianassault.Role;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.QuantityFormatter;

@Slf4j
@Singleton
public class GroundItemsOverlay extends OverlayPanel
{
	private static final int MAX_DISTANCE = 2500;
	// We must offset the text on the z-axis such that
	// it doesn't obscure the ground items below it.
	private static final int OFFSET_Z = 20;
	// The game won't send anything higher than this value to the plugin -
	// so we replace any item quantity higher with "Lots" instead.
	private static final int MAX_QUANTITY = 65535;
	// The 15 pixel gap between each drawn ground item.
	private static final int STRING_GAP = 15;

	private final StringBuilder itemStringBuilder = new StringBuilder();
	private final TextComponent textComponent = new TextComponent();
	private final Map<WorldPoint, Integer> offsetMap = new HashMap<>();

	private final Client client;

	private final BaMinigamePlugin plugin;
	private final BaMinigameConfig config;

	@Inject
	public GroundItemsOverlay(final Client client, final BaMinigamePlugin plugin, final BaMinigameConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showGroundItemHighlights())
		{
			return null;
		}

		final Role role = plugin.getRole();
		if (role == null)
		{
			return null;
		}

		switch (role)
		{
			case COLLECTOR:
			{
				renderEggs(graphics);
				break;
			}
			case DEFENDER:
			{
				if (config.highlightGroundBait())
				{
					renderBait(graphics);
				}
				if (config.highlightGroundLogsHammer())
				{
					renderLogsHammer(graphics);
				}
				break;
			}
		}

		return null;
	}

	private void renderEggs(Graphics2D graphics)
	{
		final Collection<GroundItem> eggsList = plugin.getGroundEggs().values();

		final String calledEgg = plugin.getLastListen();

		final GroundEggsMode groundEggsMode = config.highlightGroundEggsMode();

		final Predicate<GroundItem> filter = item ->
			groundEggsMode == GroundEggsMode.ALL
				|| groundEggsMode == GroundEggsMode.CALLED &&
				(item.getId() == ItemID.YELLOW_EGG || calledEgg != null && calledEgg.startsWith(item.getName()));

		renderGroundItems(graphics, eggsList, filter);
	}

	private void renderBait(Graphics2D graphics)
	{
		final Collection<GroundItem> bait = plugin.getGroundBait().values();

		renderGroundItems(graphics, bait);
	}

	private void renderLogsHammer(Graphics2D graphics)
	{
		final Collection<GroundItem> logsHammer = plugin.getGroundLogsHammer().values();

		renderGroundItems(graphics, logsHammer);
	}

	private void renderGroundItems(Graphics2D graphics, Collection<GroundItem> itemsList)
	{
		renderGroundItems(graphics, itemsList, null);
	}

	private void renderGroundItems(Graphics2D graphics, Collection<GroundItem> itemsList, Predicate<GroundItem> filter)
	{
		final Player player = client.getLocalPlayer();

		if (player == null)
		{
			return;
		}

		offsetMap.clear();
		final LocalPoint localLocation = player.getLocalLocation();

		for (GroundItem item : itemsList)
		{

			if (filter != null && !filter.test(item))
			{
				log.debug("item {} failed the filter test", item.getName());
				continue;
			}

			final LocalPoint groundPoint = LocalPoint.fromWorld(client, item.getLocation());

			if (groundPoint == null || localLocation.distanceTo(groundPoint) > MAX_DISTANCE)
			{
				continue;
			}

			final Color color = plugin.getColorForGroundItemId(item.getId());

			if (config.highlightGroundTiles())
			{
				final Polygon poly = Perspective.getCanvasTilePoly(client, groundPoint);

				if (poly != null)
				{
					OverlayUtil.renderPolygon(graphics, poly, color);
				}
			}

			itemStringBuilder.append(item.getName());

			if (item.getQuantity() > 1)
			{
				if (item.getQuantity() >= MAX_QUANTITY)
				{
					itemStringBuilder.append(" (Lots!)");
				}
				else
				{
					itemStringBuilder.append(" (")
						.append(QuantityFormatter.quantityToStackSize(item.getQuantity()))
						.append(")");
				}
			}

			final String itemString = itemStringBuilder.toString();
			itemStringBuilder.setLength(0);

			final Point textPoint = Perspective.getCanvasTextLocation(client,
				graphics,
				groundPoint,
				itemString,
				item.getHeight() + OFFSET_Z);

			if (textPoint == null)
			{
				continue;
			}

			final int offset = offsetMap.compute(item.getLocation(), (k, v) -> v != null ? v + 1 : 0);

			final int textX = textPoint.getX();
			final int textY = textPoint.getY() - (STRING_GAP * offset);

			textComponent.setText(itemString);
			textComponent.setColor(color);
			textComponent.setPosition(new java.awt.Point(textX, textY));
			textComponent.render(graphics);
		}
	}

}