package net.runelite.client.plugins.questhelper.steps;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.questhelper.questhelpers.QuestHelper;
import net.runelite.client.plugins.questhelper.requirements.Requirement;

public class ItemStep extends DetailedQuestStep
{
	public ItemStep(QuestHelper questHelper, String text, Requirement... requirements)
	{
		super(questHelper, text, requirements);
	}

	@Override
	public void renderArrow(Graphics2D graphics)
	{
		tileHighlights.forEach((tile, ids) -> {
			BufferedImage arrow = getArrow();
			LocalPoint lp = tile.getLocalLocation();

			Point arrowPoint = Perspective.getCanvasImageLocation(client, lp, arrow, 30);
			if (arrowPoint != null)
			{
				graphics.drawImage(arrow, arrowPoint.getX(), arrowPoint.getY(), null);
			}
		});
	}
}
