/*
 * Copyright (c) 2018, Cameron <https://github.com/noremac201>
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
package begosrs.barbarianassault.timer;

import begosrs.barbarianassault.BaMinigameConfig;
import begosrs.barbarianassault.BaMinigamePlugin;
import begosrs.barbarianassault.Role;
import begosrs.barbarianassault.Wave;
import begosrs.barbarianassault.api.widgets.BaWidgetInfo;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuOpcode;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ImageUtil;

@Slf4j
@Singleton
public class TimerOverlay extends Overlay
{
	private final Client client;
	private final BaMinigamePlugin plugin;
	private final BaMinigameConfig config;

	private final BufferedImage clockImage;

	@Inject
	private TimerOverlay(Client client, BaMinigamePlugin plugin, BaMinigameConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		getMenuEntries().add(new OverlayMenuEntry(MenuOpcode.RUNELITE_OVERLAY_CONFIG, OverlayManager.OPTION_CONFIGURE, "Ba minigame overlay"));
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		clockImage = ImageUtil.getResourceStreamFromClass(getClass(), "/clock.png");
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final Wave wave = plugin.getWave();
		if (wave == null)
		{
			return null;
		}

		final Role role = wave.getRole();
		if (role == null)
		{
			return null;
		}

		final BaWidgetInfo roleTextInfo = role.getRoleText();
		final Widget roleText = client.getWidget(roleTextInfo.getGroupId(), roleTextInfo.getChildId());
		if (roleText == null)
		{
			return null;
		}
		final BaWidgetInfo roleSpriteInfo = role.getRoleSprite();
		final Widget roleSprite = client.getWidget(roleSpriteInfo.getGroupId(), roleSpriteInfo.getChildId());
		if (roleSprite == null)
		{
			return null;
		}

		String text = roleText.getText();
		// replace to remove old count
		text = text.replaceAll("\\(.*\\) ", "");

		StringBuilder stringBuilder = new StringBuilder();

		if (config.showEggCountOverlay() && role == Role.COLLECTOR)
		{
			stringBuilder.append("(").append(wave.getCollectedEggsCount()).append(") ");
		}
		else if (config.showHpCountOverlay() && role == Role.HEALER)
		{
			stringBuilder.append("(").append(wave.getHpHealed()).append(") ");
		}

		if (config.showTimer())
		{
			stringBuilder.append(String.format("00:%02d", wave.getTimeUntilCallChange()));
			Rectangle spriteBounds = roleSprite.getBounds();
			roleSprite.setHidden(true);
			graphics.drawImage(clockImage, spriteBounds.x, spriteBounds.y, null);
		}
		else
		{
			stringBuilder.append(role.getName());
			roleSprite.setSpriteId(role.getSpriteId());
			roleSprite.setHidden(false);
		}

		roleText.setText(stringBuilder.toString());

		return null;
	}
}
