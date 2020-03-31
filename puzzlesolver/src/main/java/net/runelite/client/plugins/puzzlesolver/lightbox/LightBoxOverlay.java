/*
 * Copyright (c) 2018, Magic fTail
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
package net.runelite.client.plugins.puzzlesolver.lightbox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.puzzlesolver.PuzzleSolverPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class LightBoxOverlay extends Overlay
{
	private static final Color HIGHLIGHT_BORDER_COLOR = Color.ORANGE;
	private static final Color HIGHLIGHT_HOVER_BORDER_COLOR = HIGHLIGHT_BORDER_COLOR.darker();
	private static final Color HIGHLIGHT_FILL_COLOR = new Color(0, 255, 0, 20);

	private final Client client;
	private final PuzzleSolverPlugin plugin;

	@Inject
	public LightBoxOverlay(PuzzleSolverPlugin plugin, Client client)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.plugin = plugin;
		this.client = client;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		ArrayList<String> unclickedButtons = plugin.getUnclickedButtons();

		if (unclickedButtons.isEmpty())
		{
			return null;
		}

		Widget buttonContainer = client.getWidget(WidgetInfo.LIGHT_BOX_BUTTON_CONTAINER);

		if (buttonContainer == null)
		{
			return null;
		}

		int widgetId = Combination.find(unclickedButtons.get(0));

		highlightWidget(graphics, buttonContainer, client.getWidget(WidgetInfo.TO_GROUP(widgetId), WidgetInfo.TO_CHILD(widgetId)));

		return null;
	}

	private void highlightWidget(Graphics2D graphics, Widget window, Widget widget)
	{
		net.runelite.api.Point canvasLocation = widget.getCanvasLocation();

		if (canvasLocation == null)
		{
			return;
		}

		// Don't draw outside the light box window
		net.runelite.api.Point windowLocation = window.getCanvasLocation();

		if (windowLocation.getY() > canvasLocation.getY()
			|| windowLocation.getY() + window.getHeight() < canvasLocation.getY() + widget.getHeight())
		{
			return;
		}

		Area widgetArea = new Area(new Rectangle(canvasLocation.getX(), canvasLocation.getY(), widget.getWidth(), widget.getHeight()));

		OverlayUtil.renderHoverableArea(graphics, widgetArea, client.getMouseCanvasPosition(),
			HIGHLIGHT_FILL_COLOR, HIGHLIGHT_BORDER_COLOR, HIGHLIGHT_HOVER_BORDER_COLOR);
	}
}