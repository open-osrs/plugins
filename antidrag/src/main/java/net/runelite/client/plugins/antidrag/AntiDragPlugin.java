/*
 * Copyright (c) 2018, DennisDeV <https://github.com/DevDennis>
 * Copyright (c) 2019, ganom <https://github.com/ganom>
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
package net.runelite.client.plugins.antidrag;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Anti Drag",
	enabledByDefault = false,
	description = "Prevent dragging an item for a specified delay",
	tags = {"antidrag", "delay", "inventory", "items"},
	type = PluginType.UTILITY
)
public class AntiDragPlugin extends Plugin
{
	private static final int DEFAULT_DELAY = 5;

	@Inject
	private Client client;

	@Inject
	private ClientUI clientUI;

	@Inject
	private AntiDragConfig config;

	@Inject
	private AntiDragOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private KeyManager keyManager;

	private boolean toggleDrag;

	private final HotkeyListener toggleListener = new HotkeyListener(() -> config.key())
	{
		@Override
		public void hotkeyPressed()
		{
			toggleDrag = !toggleDrag;
			if (toggleDrag)
			{
				if (config.overlay())
				{
					overlayManager.add(overlay);
				}
				if (config.changeCursor())
				{
					clientUI.setCursor(config.selectedCursor().getCursorImage(), config.selectedCursor().toString());
				}

				setDragDelay();
			}
			else
			{
				overlayManager.remove(overlay);
				clientUI.resetCursor();
				resetDragDelay();
			}
		}
	};

	private final HotkeyListener holdListener = new HotkeyListener(() -> config.key())
	{
		@Override
		public void hotkeyPressed()
		{
			if (config.overlay())
			{
				overlayManager.add(overlay);
			}
			if (config.changeCursor())
			{
				clientUI.setCursor(config.selectedCursor().getCursorImage(), config.selectedCursor().toString());
			}

			setDragDelay();
		}

		@Override
		public void hotkeyReleased()
		{
			overlayManager.remove(overlay);
			clientUI.resetCursor();
			resetDragDelay();
		}
	};

	@Provides
	AntiDragConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AntiDragConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlay.setColor(config.color());
		updateKeyListeners();

		if (config.alwaysOn())
		{
			setDragDelay();
		}
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(holdListener);
		keyManager.unregisterKeyListener(toggleListener);
		toggleDrag = false;
		overlayManager.remove(overlay);
		clientUI.resetCursor();
		resetDragDelay();
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("antiDrag"))
		{
			switch (event.getKey())
			{
				case "toggleKeyBind":
				case "holdKeyBind":
					updateKeyListeners();
					break;
				case "alwaysOn":
					client.setInventoryDragDelay(config.alwaysOn() ? config.dragDelay() : DEFAULT_DELAY);
					setBankDragDelay(config.alwaysOn() ? config.dragDelay() : DEFAULT_DELAY);
					break;
				case "dragDelay":
					if (config.alwaysOn())
					{
						setDragDelay();
					}
					break;
				case ("changeCursor"):
					clientUI.resetCursor();
					break;
				case ("color"):
					overlay.setColor(config.color());
					break;
			}
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			keyManager.unregisterKeyListener(toggleListener);
			keyManager.unregisterKeyListener(holdListener);
		}
		else if (event.getGameState() == GameState.LOGGING_IN)
		{
			updateKeyListeners();
		}
	}

	@Subscribe
	private void onFocusChanged(FocusChanged focusChanged)
	{
		if (!focusChanged.isFocused() && config.reqFocus() && !config.alwaysOn())
		{
			resetDragDelay();
			overlayManager.remove(overlay);
		}
	}

	private void updateKeyListeners()
	{
		if (config.holdKeyBind())
		{
			keyManager.registerKeyListener(holdListener);
		}
		else
		{
			keyManager.unregisterKeyListener(holdListener);
		}

		if (config.toggleKeyBind())
		{
			keyManager.registerKeyListener(toggleListener);
		}
		else
		{
			keyManager.unregisterKeyListener(toggleListener);
		}
	}

	private void setBankDragDelay(int delay)
	{
		final Widget bankItemContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
		if (bankItemContainer != null)
		{
			Widget[] items = bankItemContainer.getDynamicChildren();
			for (Widget item : items)
			{
				item.setDragDeadTime(delay);
			}
		}
	}
	
	private void setDragDelay()
	{
		client.setInventoryDragDelay(config.dragDelay());
		setBankDragDelay(config.dragDelay());
	}

	private void resetDragDelay()
	{
		client.setInventoryDragDelay(DEFAULT_DELAY);
		setBankDragDelay(DEFAULT_DELAY);
	}
}
