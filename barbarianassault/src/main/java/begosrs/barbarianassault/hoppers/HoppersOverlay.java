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
package begosrs.barbarianassault.hoppers;

import begosrs.barbarianassault.BaMinigameConfig;
import begosrs.barbarianassault.BaMinigamePlugin;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.QuantityFormatter;

@Slf4j
@Singleton
public class HoppersOverlay extends Overlay
{
	// The 15 pixel gap between each drawn egg.
	private static final int STRING_GAP = 15;
	private static final int Z_OFFSET = 100;
	private static final int MAXIMUM_DISTANCE = 20;

	private final Client client;
	private final BaMinigamePlugin plugin;
	private final BaMinigameConfig config;

	private final TextComponent textComponent;
	private final Map<WorldPoint, Integer> offsetMap;

	@Inject
	private HoppersOverlay(final Client client, final BaMinigamePlugin plugin, final BaMinigameConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.plugin = plugin;
		this.config = config;

		this.textComponent = new TextComponent();
		this.offsetMap = new HashMap<>();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showEggsOnHopper() || plugin.getInGameBit() != 1)
		{
			return null;
		}

		final Player player = client.getLocalPlayer();
		if (player == null)
		{
			return null;
		}

		offsetMap.clear();

		for (GameObject hopper : plugin.getHoppers())
		{
			WorldPoint hopperPoint = hopper.getWorldLocation();
			if (hopperPoint.distanceTo(player.getWorldLocation()) >= MAXIMUM_DISTANCE)
			{
				continue;
			}

			final LocalPoint localPoint = LocalPoint.fromWorld(client, hopperPoint);
			if (localPoint == null)
			{
				continue;
			}

			for (Map.Entry<CollectorEgg, Integer> eggCount : plugin.getCannonEggs().entrySet())
			{
				final CollectorEgg collectorEgg = eggCount.getKey();
				final int count = eggCount.getValue();

				String message = String.format("%s %s", QuantityFormatter.quantityToStackSize(count), collectorEgg.getName());

				Point textPoint = Perspective.getCanvasTextLocation(client, graphics, localPoint, message, Z_OFFSET);
				if (textPoint == null)
				{
					continue;
				}

				final int offset = offsetMap.compute(hopper.getWorldLocation(), (k, v) -> v != null ? v + 1 : 0);

				final int textX = textPoint.getX();
				final int textY = textPoint.getY() - (STRING_GAP * offset);

				textComponent.setText(message);
				textComponent.setColor(collectorEgg.getColor());
				textComponent.setPosition(new java.awt.Point(textX, textY));
				textComponent.render(graphics);
			}
		}

		return null;
	}
}
