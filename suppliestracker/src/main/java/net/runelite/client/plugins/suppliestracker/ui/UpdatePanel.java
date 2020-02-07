package net.runelite.client.plugins.suppliestracker.ui;


import java.io.IOException;
import javax.inject.Singleton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

@Singleton
class UpdatePanel extends PluginPanel
{

	private SuppliesTrackerPanel panel;

	UpdatePanel(SuppliesTrackerPanel suppliesTrackerPanel)
	{
		this.panel = suppliesTrackerPanel;
		setBorder(new EmptyBorder(6, 6, 6, 6));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		java.net.URL helpURL = UpdatePanel.class.getResource(
			"Info.html");

		if (helpURL != null)
		{
			try
			{
				editorPane.setPage(helpURL);
			}
			catch (IOException e)
			{
				throw new IllegalStateException("File not found");
			}
		}

		JButton close = new JButton("Close info");
		close.addActionListener(e ->
		{
			this.setVisible(false);
			panel.updateOverall();
			panel.getInfo().setVisible(true);
			panel.getLogsContainer().setVisible(true);
		});
		this.add(close);
		this.add(editorPane);

	}
}
