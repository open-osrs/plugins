/*
 * Copyright (c) 2018 Abex
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
package net.runelite.client.plugins.devtools;

import com.google.inject.Inject;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.api.VarPlayer;
import net.runelite.api.VarbitDefinition;
import net.runelite.api.Varbits;
import net.runelite.api.events.VarClientIntChanged;
import net.runelite.api.events.VarClientStrChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import org.apache.commons.lang3.ArrayUtils;

@Slf4j
class VarInspector extends JFrame
{
	@Getter(AccessLevel.PACKAGE)
	private enum VarType
	{
		VARBIT("Varbit"),
		VARP("VarPlayer"),
		VARCINT("VarClientInt"),
		VARCSTR("VarClientStr");

		private final String name;
		private final JCheckBox checkBox;

		VarType(String name)
		{
			this.name = name;
			checkBox = new JCheckBox(name, true);
		}
	}

	private static final int MAX_LOG_ENTRIES = 10_000;
	private static final int VARBITS_GROUP = 14;

	private final Client client;
	private final ClientThread clientThread;
	private final EventBus eventBus;

	private final JPanel tracker = new JPanel();

	private int lastTick = 0;

	/**
	 * Varp ids mapped to their corresponding varbit ids
	 */
	private int[][] varbits;
	private int[] oldVarps;

	private Map<Integer, Object> varcs = null;

	@Inject
	VarInspector(Client client, ClientThread clientThread, EventBus eventBus, DevToolsPlugin plugin)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.eventBus = eventBus;

		setTitle("RuneLite Var Inspector");
		setIconImage(ClientUI.ICON);

		setLayout(new BorderLayout());

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				eventBus.unregister(this);
				close();
				plugin.getVarInspector().setActive(false);
			}
		});

		tracker.setLayout(new DynamicGridLayout(0, 1, 0, 3));

		final JPanel trackerWrapper = new JPanel();
		trackerWrapper.setLayout(new BorderLayout());
		trackerWrapper.add(tracker, BorderLayout.NORTH);

		final JScrollPane trackerScroller = new JScrollPane(trackerWrapper);
		trackerScroller.setPreferredSize(new Dimension(400, 400));

		final JScrollBar vertical = trackerScroller.getVerticalScrollBar();
		vertical.addAdjustmentListener(new AdjustmentListener()
		{
			int lastMaximum = actualMax();

			private int actualMax()
			{
				return vertical.getMaximum() - vertical.getModel().getExtent();
			}

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e)
			{
				if (vertical.getValue() >= lastMaximum)
				{
					vertical.setValue(actualMax());
				}
				lastMaximum = actualMax();
			}
		});

		add(trackerScroller, BorderLayout.CENTER);

		final JPanel trackerOpts = new JPanel();
		trackerOpts.setLayout(new FlowLayout());
		for (VarType cb : VarType.values())
		{
			trackerOpts.add(cb.getCheckBox());
		}

		final JButton clearBtn = new JButton("Clear");
		clearBtn.addActionListener(e ->
		{
			tracker.removeAll();
			tracker.revalidate();
		});
		trackerOpts.add(clearBtn);

		add(trackerOpts, BorderLayout.SOUTH);

		pack();

	}

	private void addVarLog(VarType type, String name, int old, int neew)
	{
		addVarLog(type, name, Integer.toString(old), Integer.toString(neew));
	}

	private void addVarLog(VarType type, String name, String old, String neew)
	{
		if (!type.getCheckBox().isSelected())
		{
			return;
		}

		int tick = client.getTickCount();
		SwingUtilities.invokeLater(() ->
		{
			if (tick != lastTick)
			{
				lastTick = tick;
				JLabel header = new JLabel("Tick " + tick);
				header.setFont(FontManager.getRunescapeSmallFont());
				header.setBorder(new CompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.LIGHT_GRAY_COLOR),
					BorderFactory.createEmptyBorder(3, 6, 0, 0)
				));
				tracker.add(header);
			}
			tracker.add(new JLabel(String.format("%s %s changed: %s -> %s", type.getName(), name, old, neew)));

			// Cull very old stuff
			while (tracker.getComponentCount() > MAX_LOG_ENTRIES)
			{
				tracker.remove(0);
			}

			tracker.revalidate();
		});
	}

	private void onVarbitChanged(VarbitChanged ev)
	{
		final int index = ev.getIndex();
		final int[] varps = client.getVarps().clone();
		boolean varbitChanged = false;

		if (varbits[index] != null)
		{
			// Check varbits
			for (int i : varbits[index])
			{
				int old = client.getVarbitValue(oldVarps, i);
				int neew = client.getVarbitValue(varps, i);
				if (old != neew)
				{
					varbitChanged = true;

					String name = String.valueOf(i);
					for (Varbits varbit : Varbits.values())
					{
						if (varbit.getId() == i)
						{
							name = String.format("%s(%d)", varbit.name(), i);
							break;
						}
					}
					addVarLog(VarType.VARBIT, name, old, neew);
				}
			}
		}

		if (!varbitChanged)
		{
			int old = oldVarps[index];
			int neew = varps[index];
			if (old != neew) // Is this ever not true?
			{
				String name = String.valueOf(index);
				for (VarPlayer varp : VarPlayer.values())
				{
					if (varp.getId() == index)
					{
						name = String.format("%s(%d)", varp.name(), index);
						break;
					}
				}
				addVarLog(VarType.VARP, name, old, neew);
			}
		}

		oldVarps = varps;
	}

	private void onVarClientIntChanged(VarClientIntChanged e)
	{
		int idx = e.getIndex();
		int neew = (Integer) client.getVarcMap().getOrDefault(idx, 0);
		int old = (Integer) varcs.getOrDefault(idx, 0);
		varcs.put(idx, neew);

		if (old != neew)
		{
			String name = String.format("%d", idx);
			for (VarClientInt varc : VarClientInt.values())
			{
				if (varc.getIndex() == idx)
				{
					name = String.format("%s(%d)", varc.name(), idx);
					break;
				}
			}
			addVarLog(VarType.VARCINT, name, old, neew);
		}
	}

	private void onVarClientStrChanged(VarClientStrChanged e)
	{
		int idx = e.getIndex();
		String neew = (String) client.getVarcMap().getOrDefault(idx, "");
		String old = (String) varcs.getOrDefault(idx, "");
		varcs.put(idx, neew);

		if (!Objects.equals(old, neew))
		{
			String name = String.format("%d", idx);
			for (VarClientStr varc : VarClientStr.values())
			{
				if (varc.getIndex() == idx)
				{
					name = String.format("%s(%d)", varc.name(), idx);
					break;
				}
			}
			if (old != null)
			{
				old = "\"" + old + "\"";
			}
			else
			{
				old = "null";
			}
			if (neew != null)
			{
				neew = "\"" + neew + "\"";
			}
			else
			{
				neew = "null";
			}
			addVarLog(VarType.VARCSTR, name, old, neew);
		}
	}

	public void open()
	{
		oldVarps = client.getVarps().clone();
		varbits = new int[oldVarps.length][];
		varcs = new HashMap<>(client.getVarcMap());

		clientThread.invoke(() ->
		{
			int n = 0;

			for (final int id : client.getConfigArchive().getFileIds(VARBITS_GROUP))
			{
				final VarbitDefinition def = client.getVarbitDefinition(id);
				if (def != null)
				{
					final int index = def.getIndex();
					varbits[index] = ArrayUtils.add(varbits[index], id);
					++n;
				}
			}

			log.debug("{} varbit definitions mapped to their corresponding varp", n);
		});

		eventBus.subscribe(VarbitChanged.class, this, this::onVarbitChanged);
		eventBus.subscribe(VarClientIntChanged.class, this, this::onVarClientIntChanged);
		eventBus.subscribe(VarClientStrChanged.class, this, this::onVarClientStrChanged);

		setVisible(true);
		toFront();
		repaint();
	}

	public void close()
	{
		tracker.removeAll();
		eventBus.unregister(this);
		setVisible(false);

		oldVarps = null;
		varbits = null;
		varcs = null;
	}
}
