/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Cas <https://github.com/casvandongen>
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
package net.runelite.client.plugins.agility;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.AgilityShortcut;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

@Singleton
class AgilityOverlay extends Overlay
{
	private static final int MAX_DISTANCE = 2350;
	private static final Color SHORTCUT_HIGH_LEVEL_COLOR = Color.ORANGE;

	private final Client client;
	private final AgilityConfig config;
	private final AgilityPlugin plugin;

	@Inject
	private AgilityOverlay(final Client client, final AgilityConfig config, final AgilityPlugin plugin)
	{
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.config = config;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
		Point mousePosition = client.getMouseCanvasPosition();
		final List<Tile> marksOfGrace = plugin.getMarksOfGrace();
		final Tile stickTile = plugin.getStickTile();
		plugin.getObstacles().forEach((object, obstacle) ->
		{
			if (Obstacles.SHORTCUT_OBSTACLE_IDS.containsKey(object.getId()) && !config.highlightShortcuts() ||
				Obstacles.TRAP_OBSTACLE_IDS.contains(object.getId()) && !config.showTrapOverlay() ||
				Obstacles.COURSE_OBSTACLE_IDS.contains(object.getId()) && !config.showCourseClickboxes())
			{
				return;
			}

			Tile tile = obstacle.getTile();

			if (tile.getPlane() == client.getPlane() && checkDistance(object.getLocalLocation(), playerLocation))
			{
				// This assumes that the obstacle is not clickable.
				if (Obstacles.TRAP_OBSTACLE_IDS.contains(object.getId()))
				{
					Polygon polygon = object.getCanvasTilePoly();
					if (polygon != null)
					{
						OverlayUtil.renderPolygon(graphics, polygon, config.getTrapColor());
					}
					return;
				}
				Shape objectClickbox = object.getClickbox();
				if (objectClickbox != null)
				{
					AgilityShortcut agilityShortcut = obstacle.getShortcut();
					Color configColor = agilityShortcut == null || agilityShortcut.getLevel() <= plugin.getAgilityLevel() ? config.getOverlayColor() : SHORTCUT_HIGH_LEVEL_COLOR;
					if (config.highlightMarks() && !marksOfGrace.isEmpty())
					{
						configColor = config.getMarkColor();
					}

					OverlayUtil.renderClickBox(graphics, mousePosition, objectClickbox, configColor);
				}
			}

		});

		if (config.highlightMarks() && !marksOfGrace.isEmpty())
		{
			for (Tile markOfGraceTile : marksOfGrace)
			{
				highlightTile(graphics, playerLocation, markOfGraceTile, config.getMarkColor());
			}
		}
		
		if (stickTile != null && config.highlightStick())
		{
			highlightTile(graphics, playerLocation, stickTile, config.stickHighlightColor());
		}

		return null;
	}

	private boolean checkDistance(LocalPoint localPoint, LocalPoint playerPoint)
	{
		if (config.removeDistanceCap())
		{
			return true;
		}
		return localPoint.distanceTo(playerPoint) < MAX_DISTANCE;
	}
	
	private void highlightTile(Graphics2D graphics, LocalPoint playerLocation, Tile tile, Color color)
	{
		if (tile.getPlane() == client.getPlane() && tile.getItemLayer() != null
			&& tile.getLocalLocation().distanceTo(playerLocation) < MAX_DISTANCE)
		{
			final Polygon poly = tile.getItemLayer().getCanvasTilePoly();

			if (poly != null)
			{
				OverlayUtil.renderPolygon(graphics, poly, color);
			}
		}
	}
}
