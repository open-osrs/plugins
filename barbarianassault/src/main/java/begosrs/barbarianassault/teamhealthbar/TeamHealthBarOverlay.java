/*
 * Copyright (c) 2019, whartd <github.com/whartd>
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
package begosrs.barbarianassault.teamhealthbar;

import begosrs.barbarianassault.BaMinigameConfig;
import begosrs.barbarianassault.BaMinigamePlugin;
import begosrs.barbarianassault.Role;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import begosrs.barbarianassault.api.widgets.BaWidgetInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.MenuOpcode;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;

public class TeamHealthBarOverlay extends Overlay
{
	@Getter
	@AllArgsConstructor
	private enum HealerTeam
	{
		TEAMMATE1(BaWidgetInfo.BA_HEAL_TEAMMATE1, 28, 2, 115),
		TEAMMATE2(BaWidgetInfo.BA_HEAL_TEAMMATE2, 26, 2, 115),
		TEAMMATE3(BaWidgetInfo.BA_HEAL_TEAMMATE3, 26, 2, 115),
		TEAMMATE4(BaWidgetInfo.BA_HEAL_TEAMMATE4, 25, 2, 115);

		private final BaWidgetInfo teammate;
		private final int offsetX;
		private final int offsetY;
		private final int width;
	}

	private final Client client;
	private final BaMinigamePlugin plugin;
	private final BaMinigameConfig config;

	@Inject
	private TeamHealthBarOverlay(Client client, BaMinigamePlugin plugin, BaMinigameConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.UNDER_WIDGETS);
		getMenuEntries().add(new OverlayMenuEntry(MenuOpcode.RUNELITE_OVERLAY_CONFIG, OverlayManager.OPTION_CONFIGURE, "Ba minigame overlay"));
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final Role role = plugin.getRole();

		if (config.showTeammateHealthBars() && role == Role.HEALER)
		{
			for (HealerTeam teammate : HealerTeam.values())
			{
				Widget widget = client.getWidget(teammate.getTeammate().getGroupId(), teammate.getTeammate().getChildId());
				if (widget == null)
				{
					continue;
				}

				String[] teammateHealth = widget.getText().split(" / ");
				int curHealth = Integer.parseInt(teammateHealth[0]);
				int maxHealth = Integer.parseInt(teammateHealth[1]);

				int width = teammate.getWidth();
				double hpRatio = (double) curHealth / maxHealth;
				int filledWidth = getBarWidth(hpRatio, width);
				Color barColor = getBarColor(hpRatio);

				int offsetX = teammate.getOffsetX();
				int offsetY = teammate.getOffsetY();
				int x = widget.getCanvasLocation().getX() - offsetX;
				int y = widget.getCanvasLocation().getY() - offsetY;

				graphics.setColor(barColor);
				graphics.fillRect(x, y, filledWidth, 20);
			}
		}

		return null;
	}

	private int getBarWidth(double ratio, int size)
	{
		if (ratio >= 1)
		{
			return size;
		}

		return (int) Math.round(ratio * size);
	}

	private Color getBarColor(double ratio)
	{
		final int transparency = config.teammateHealthBarTransparency();

		if (ratio <= 0.33)
		{
			return new Color(225, 35, 0, 255 - transparency);
		}

		if (ratio <= 0.66)
		{
			return new Color(146, 146, 0, 255 - transparency);
		}

		return new Color(10, 146, 5, 255 - transparency);
	}
}
