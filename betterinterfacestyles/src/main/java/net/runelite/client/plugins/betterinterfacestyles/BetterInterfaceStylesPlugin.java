/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 * Copyright (c) 2018, Raqes <j.raqes@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.betterinterfacestyles;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.HealthBar;
import net.runelite.api.SpriteID;
import net.runelite.api.SpritePixels;
import net.runelite.api.events.BeforeMenuRender;
import net.runelite.api.events.BeforeRender;
import net.runelite.client.events.ConfigChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.PostHealthBar;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

@Extension
@Slf4j
@PluginDescriptor(
	name = "Better Interface Styles",
	description = "Change the interface style to the 2005/2010 interface (with larger HP Bars)",
	tags = {"2005", "2010", "skin", "theme", "ui"},
	conflicts = "Interface Styles",
	enabledByDefault = false
)
public class BetterInterfaceStylesPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private net.runelite.client.plugins.betterinterfacestyles.BetterInterfaceStylesConfig config;

	@Inject
	private SpriteManager spriteManager;

	private SpritePixels[] defaultCrossSprites;

	@Provides
	BetterInterfaceStylesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BetterInterfaceStylesConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invoke(this::updateAllOverrides);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientThread.invoke(() ->
		{
			restoreWidgetDimensions();
			removeGameframe();
			restoreHealthBars();
			restoreCrossSprites();
		});
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged config)
	{
		if (config.getGroup().equals("betterinterfaceStyles"))
		{
			clientThread.invoke(this::updateAllOverrides);
		}
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if ("forceStackStones".equals(event.getEventName()) && config.alwaysStack())
		{
			int[] intStack = client.getIntStack();
			int intStackSize = client.getIntStackSize();
			intStack[intStackSize - 1] = 1;
		}
	}

	@Subscribe
	public void onBeforeRender(BeforeRender event)
	{
		adjustWidgetDimensions();
	}

	@Subscribe
	public void onPostHealthBar(PostHealthBar postHealthBar)
	{
		if (!config.hdHealthBars())
		{
			return;
		}

		HealthBar healthBar = postHealthBar.getHealthBar();
		BetterHealthbarOverride override = BetterHealthbarOverride.get(healthBar.getHealthBarFrontSpriteId());

		// Check if this is the health bar we are replacing
		if (override != null)
		{
			// Increase padding to show some more green at very low hp percentages
			healthBar.setPadding(override.getPadding());
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() != GameState.LOGIN_SCREEN)
		{
			return;
		}

		/*
		 * The cross sprites aren't loaded yet when the initial config change event is received.
		 * So run the overriding for cross sprites when we reach the login screen,
		 * at which point the cross sprites will have been loaded.
		 */
		overrideCrossSprites();
	}

	private void updateAllOverrides()
	{
		removeGameframe();
		overrideSprites();
		overrideWidgetSprites();
		restoreWidgetDimensions();
		adjustWidgetDimensions();
		overrideHealthBars();
		overrideCrossSprites();
	}

	@Subscribe
	public void onBeforeMenuRender(BeforeMenuRender event)
	{
		if (config.hdMenu())
		{
			client.draw2010Menu(config.menuAlpha());
			event.consume();
		}
		else if (config.menuAlpha() != 255)
		{
			client.drawOriginalMenu(config.menuAlpha());
			event.consume();
		}
	}

	private void overrideSprites()
	{
		final net.runelite.client.plugins.betterinterfacestyles.BetterSkin configuredSkin = config.skin();
		for (BetterSpriteOverride spriteOverride : BetterSpriteOverride.values())
		{
			for (BetterSkin skin : spriteOverride.getBetterSkin())
			{
				if (skin == configuredSkin)
				{
					final String configSkin = skin.getExtendBetterSkin() != null
						? skin.getExtendBetterSkin().toString()
						: skin.toString();
					String file = configSkin + "/" + spriteOverride.getSpriteID() + ".png";
					SpritePixels spritePixels = getFileSpritePixels(file);

					if (spriteOverride.getSpriteID() == SpriteID.COMPASS_TEXTURE)
					{
						client.setCompass(spritePixels);
					}
					else
					{
						client.getSpriteOverrides().put(spriteOverride.getSpriteID(), spritePixels);
					}
				}
			}
		}
	}

	private void restoreSprites()
	{
		client.getWidgetSpriteCache().reset();

		for (BetterSpriteOverride spriteOverride : BetterSpriteOverride.values())
		{
			client.getSpriteOverrides().remove(spriteOverride.getSpriteID());
		}
	}

	private void overrideWidgetSprites()
	{
		final net.runelite.client.plugins.betterinterfacestyles.BetterSkin configuredSkin = config.skin();
		for (BetterWidgetOverride widgetOverride : BetterWidgetOverride.values())
		{
			if (widgetOverride.getBetterSkin() == configuredSkin
				|| widgetOverride.getBetterSkin() == configuredSkin.getExtendBetterSkin())
			{
				final String configSkin = configuredSkin.getExtendBetterSkin() != null
					? configuredSkin.getExtendBetterSkin().toString()
					: configuredSkin.toString();
				String file = configSkin + "/widget/" + widgetOverride.getName() + ".png";
				SpritePixels spritePixels = getFileSpritePixels(file);

				if (spritePixels != null)
				{
					for (WidgetInfo widgetInfo : widgetOverride.getWidgetInfo())
					{
						client.getWidgetSpriteOverrides().put(widgetInfo.getPackedId(), spritePixels);
					}
				}
			}
		}
	}

	private void restoreWidgetSprites()
	{
		for (BetterWidgetOverride widgetOverride : BetterWidgetOverride.values())
		{
			for (WidgetInfo widgetInfo : widgetOverride.getWidgetInfo())
			{
				client.getWidgetSpriteOverrides().remove(widgetInfo.getPackedId());
			}
		}
	}

	private SpritePixels getFileSpritePixels(String file)
	{
		try
		{
			log.debug("Loading: {}", file);
			BufferedImage image = ImageUtil.loadImageResource(this.getClass(), file);
			return ImageUtil.getImageSpritePixels(image, client);
		}
		catch (RuntimeException ex)
		{
			log.debug("Unable to load image: ", ex);
		}

		return null;
	}

	private void adjustWidgetDimensions()
	{
		for (BetterWidgetOffset widgetOffset : BetterWidgetOffset.values())
		{
			if (widgetOffset.getBetterSkin() != config.skin())
			{
				continue;
			}

			Widget widget = client.getWidget(widgetOffset.getWidgetInfo());

			if (widget != null)
			{
				if (widgetOffset.getOffsetX() != null)
				{
					widget.setRelativeX(widgetOffset.getOffsetX());
				}

				if (widgetOffset.getOffsetY() != null)
				{
					widget.setRelativeY(widgetOffset.getOffsetY());
				}

				if (widgetOffset.getWidth() != null)
				{
					widget.setWidth(widgetOffset.getWidth());
				}

				if (widgetOffset.getHeight() != null)
				{
					widget.setHeight(widgetOffset.getHeight());
				}
			}
		}
	}

	private void overrideHealthBars()
	{
		if (config.hdHealthBars())
		{
			spriteManager.addSpriteOverrides(BetterHealthbarOverride.values());
			// Reset health bar caches to apply the override
			clientThread.invokeLater(client::resetHealthBarCaches);
		}
		else
		{
			restoreHealthBars();
		}
	}

	private void restoreHealthBars()
	{
		spriteManager.removeSpriteOverrides(BetterHealthbarOverride.values());
		clientThread.invokeLater(client::resetHealthBarCaches);
	}

	private void overrideCrossSprites()
	{
		if (config.rsCrossSprites())
		{
			// If we've already replaced them,
			// we don't need to replace them again
			if (defaultCrossSprites != null)
			{
				return;
			}

			SpritePixels[] crossSprites = client.getCrossSprites();

			if (crossSprites == null)
			{
				return;
			}

			defaultCrossSprites = new SpritePixels[crossSprites.length];
			System.arraycopy(crossSprites, 0, defaultCrossSprites, 0, defaultCrossSprites.length);

			for (int i = 0; i < crossSprites.length; i++)
			{
				SpritePixels newSprite = getFileSpritePixels("rs3/cross_sprites/" + i + ".png");

				if (newSprite == null)
				{
					continue;
				}

				crossSprites[i] = newSprite;
			}
		}
		else
		{
			restoreCrossSprites();
		}
	}

	private void restoreCrossSprites()
	{
		if (defaultCrossSprites == null)
		{
			return;
		}

		SpritePixels[] crossSprites = client.getCrossSprites();

		if (crossSprites != null && defaultCrossSprites.length == crossSprites.length)
		{
			System.arraycopy(defaultCrossSprites, 0, crossSprites, 0, defaultCrossSprites.length);
		}

		defaultCrossSprites = null;
	}

	private void restoreWidgetDimensions()
	{
		for (BetterWidgetOffset widgetOffset : BetterWidgetOffset.values())
		{
			Widget widget = client.getWidget(widgetOffset.getWidgetInfo());

			if (widget != null)
			{
				widget.revalidate();
			}
		}
	}

	private void removeGameframe()
	{
		restoreSprites();
		restoreWidgetSprites();

		BufferedImage compassImage = spriteManager.getSprite(SpriteID.COMPASS_TEXTURE, 0);

		if (compassImage != null)
		{
			SpritePixels compass = ImageUtil.getImageSpritePixels(compassImage, client);
			client.setCompass(compass);
		}
	}
}