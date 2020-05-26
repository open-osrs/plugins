/*
 * Copyright (c) 2020, chestnut1693 <chestnut1693@gmail.com>
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
package net.runelite.client.plugins.raidsthieving;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

public class BatsOverlay extends Overlay
{
	private final BatsLocatorPlugin plugin;
	private final BatsLocatorConfig config;
	private final Client client;
	private final ProgressPieComponent pie = new ProgressPieComponent();

	@Inject
	public BatsOverlay(BatsLocatorConfig config, BatsLocatorPlugin plugin, Client client)
	{
		this.config = config;
		this.plugin = plugin;
		this.client = client;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		pie.setProgress(100);
		pie.setBorderColor(Color.black);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Player player = client.getLocalPlayer();
		WorldPoint playerLocation = player == null ? null : player.getWorldLocation();

		if (playerLocation == null)
		{
			return null;
		}

		BatsLocator batsLocator = plugin.getBatsLocator();

		if (plugin.isInRaidChambers() && batsLocator.isDrawChestStates())
		{
			for (Chest chest : batsLocator.getChests().values())
			{
				if (chest.getState() == Chest.State.GRUBS)
				{
					continue;
				}

				if (playerLocation.distanceTo(chest.getLocation()) <= 25)
				{
					LocalPoint chestLocal = LocalPoint.fromWorld(client, chest.getLocation());

					if (chestLocal != null)
					{
						Point chestCanvas = Perspective.localToCanvas(client, chestLocal, client.getPlane());

						if (chestCanvas != null)
						{
							Color color;

							switch (chest.getState())
							{
								case UNVISITED:
									color = config.unvisitedColor();
									break;
								case BATS:
									color = config.batsColor();
									break;
								case POISON:
									color = config.poisonColor();
									break;
								default:
									//This will not happen since only grubs chests reach this but they are skipped earlier on.
									color = Color.white;
									break;
							}

							String chestNumber = String.valueOf(chest.getNumber());
							graphics.setFont(FontManager.getRunescapeSmallFont());
							int stringWidth = graphics.getFontMetrics().stringWidth(chestNumber);
							int stringHeight = graphics.getFontMetrics().getHeight();

							if (batsLocator.getSolutionSets().size() == 0 && (chest.getState() == Chest.State.POISON || chest.getState() == Chest.State.BATS))
							{
								chestCanvas = new Point(chestCanvas.getX() - stringWidth / 2, chestCanvas.getY() - stringHeight / 2);
								OverlayUtil.renderTextLocation(graphics, chestCanvas, chestNumber, color);
							}
							else
							{
								if (chest.getSolutionSetCount() != 0 && chest.getSolutionSetCount() == batsLocator.getHighestSolutionSetCount())
								{
									pie.setDiameter((int) Math.round(config.dotSize() + config.dotSize() / 3.0));
									graphics.setFont(FontManager.getRunescapeFont());
									stringWidth = graphics.getFontMetrics().stringWidth(chestNumber);
									stringHeight = graphics.getFontMetrics().getHeight();
								}
								else
								{
									pie.setDiameter(config.dotSize());
									color = new Color(color.getRed(), color.getGreen(), color.getBlue(), config.transparency());
								}

								pie.setFill(color);
								pie.setPosition(new Point(chestCanvas.getX(), chestCanvas.getY()));

								switch (config.displayMode())
								{
									case DOTS:
										pie.render(graphics);
										break;
									case NUMBERS:
										chestCanvas = new Point(chestCanvas.getX() - stringWidth / 2, chestCanvas.getY() + stringHeight / 2);
										OverlayUtil.renderTextLocation(graphics, chestCanvas, chestNumber, color);
										break;
								}
							}
						}
					}
				}
			}
		}

		return null;
	}
}