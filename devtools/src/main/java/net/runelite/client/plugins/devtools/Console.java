package net.runelite.client.plugins.devtools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.inject.Inject;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.ClientUI;

@Slf4j
public class Console extends JFrame
{
	private final Client client;

	@Inject
	Console(Client client, DevToolsPlugin plugin)
	{
		this.client = client;

		setTitle("Console");
		setIconImage(ClientUI.ICON);

		setLayout(new BorderLayout());

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				close();
				plugin.getConsole().setActive(false);
			}
		});

		JTextArea outputText = new JTextArea(75, 20);
		outputText.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(outputText);
		scrollPane.setPreferredSize(new Dimension(500, 300));
		JTextField inputField = new JTextField(75);
		inputField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent actionEvent)
			{

				try
				{
					Object result = plugin.getGroovyShell().evaluate(inputField.getText());
					if (result != null)
					{
						outputText.append(result.toString() + "\n");
						inputField.setText("");
						outputText.setCaretPosition(outputText.getText().length());
					}
					else
					{
						inputField.setText("");
						outputText.setCaretPosition(outputText.getText().length());
					}
				}
				catch (Exception e)
				{
					outputText.append(e.getMessage() + "\n");
					inputField.setText("");
					outputText.setCaretPosition(outputText.getText().length());
				}
			}
		});

		add(scrollPane, BorderLayout.NORTH);
		add(inputField, BorderLayout.SOUTH);
		pack();
	}

	public void open()
	{
		setVisible(true);
		toFront();
		repaint();
	}

	public void close()
	{
		setVisible(false);
	}
}
