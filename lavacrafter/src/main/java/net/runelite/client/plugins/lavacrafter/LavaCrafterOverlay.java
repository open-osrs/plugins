package net.runelite.client.plugins.lavacrafter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import static net.runelite.api.MenuOpcode.RUNELITE_OVERLAY_CONFIG;
import net.runelite.client.ui.overlay.Overlay;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.table.TableAlignment;
import net.runelite.client.ui.overlay.components.table.TableComponent;

@Singleton
public class LavaCrafterOverlay extends Overlay
{
	private Client client;
	private LavaCrafterPlugin plugin;
	private LavaCrafterConfig config;
	private PanelComponent panelComponent = new PanelComponent();

	@Inject
	private LavaCrafterOverlay(Client client, LavaCrafterPlugin plugin, LavaCrafterConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;

		this.setPriority(OverlayPriority.HIGHEST);
		this.setPosition(OverlayPosition.BOTTOM_LEFT);
		this.getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Lava Crafter Overlay"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		try
		{
			if (config.disablePaint())
				return null;

			if (plugin == null)
				return null;

			if (!plugin.pluginStarted)
				return null;

			panelComponent.getChildren().clear();

			TableComponent tableComponent = new TableComponent();
			tableComponent.setColumnAlignments(TableAlignment.LEFT);
			tableComponent.setDefaultColor(Color.ORANGE);

			tableComponent.addRow("Lava Crafter");

			try
			{
				if (plugin.watch != null && plugin.watch.isStarted())
				{
					tableComponent.addRow("Timer: " + plugin.watch.toString());
				}
			} catch (Exception e) { }

			if (plugin.state != null)
			{
				tableComponent.addRow("State: " + plugin.state.name());
			}

			if (!tableComponent.isEmpty())
			{
				panelComponent.getChildren().add(tableComponent);
			}

			panelComponent.setPreferredSize(new Dimension(175, 100));
			panelComponent.setBackgroundColor(Color.BLACK);

			return panelComponent.render(graphics);
		}
		catch (Exception ex)
		{
			System.out.println("Error in LavaCrafterOverlay render()!");
			ex.printStackTrace();
		}

		return null;
	}
}
