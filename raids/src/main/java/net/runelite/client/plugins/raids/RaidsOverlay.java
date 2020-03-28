/*
 * Copyright (c) 2018, Kamiel
 * Copyright (c) 2019, ganom <https://github.com/Ganom>
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
package net.runelite.client.plugins.raids;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import static net.runelite.api.MenuOpcode.RUNELITE_OVERLAY;
import static net.runelite.api.MenuOpcode.RUNELITE_OVERLAY_CONFIG;
import net.runelite.api.SpriteID;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.raids.solver.Room;
import net.runelite.client.ui.overlay.Overlay;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.components.table.TableAlignment;
import net.runelite.client.ui.overlay.components.table.TableComponent;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldRegion;
import net.runelite.http.api.worlds.WorldResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@Singleton
public class RaidsOverlay extends Overlay
{

	@Inject
	private WorldService worldService;

	private static final int OLM_PLANE = 0;
	private static final int ICON_SIZE = 32;
	private static final int SMALL_ICON_SIZE = 21;
	static final String BROADCAST_ACTION = "Broadcast layout";

	private final PanelComponent panelComponent = new PanelComponent();
	private final ItemManager itemManager;
	private final SpriteManager spriteManager;
	private final PanelComponent panelImages = new PanelComponent();
	private final Client client;
	private final RaidsPlugin plugin;
	private final RaidsConfig config;

	@Setter(AccessLevel.PACKAGE)
	private CachedRaid cachedRaid;

	@Setter(AccessLevel.PACKAGE)
	private boolean sharable = false;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private boolean scoutOverlayShown = false;

	@Getter(AccessLevel.PACKAGE)
	private boolean scouterActive = false;

	@Getter(AccessLevel.PACKAGE)
	private int width;

	@Getter(AccessLevel.PACKAGE)
	private int height;

	@Inject
	private RaidsOverlay(final Client client, final RaidsPlugin plugin, final RaidsConfig config, final ItemManager itemManager, final SpriteManager spriteManager)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.LOW);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.itemManager = itemManager;
		this.spriteManager = spriteManager;
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Raids overlay"));
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, BROADCAST_ACTION, "Raids overlay"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.scoutOverlay() || !scoutOverlayShown || plugin.isInRaidChambers() && client.getPlane() == OLM_PLANE)
		{
			return null;
		}

		scouterActive = false;
		panelComponent.getChildren().clear();

		if (config.hideBackground())
		{
			panelComponent.setBackgroundColor(null);
		}
		else
		{
			panelComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
		}

		if (plugin.getRaid() == null || plugin.getRaid().getLayout() == null)
		{
			panelComponent.getChildren().add(TitleComponent.builder()
				.text("Unable to scout this raid!")
				.color(Color.RED)
				.build());

			return panelComponent.render(graphics);
		}

		Color color = Color.WHITE;
		String layout = plugin.getRaid().getLayout().toCodeString();
		String displayLayout;
		if (config.displayFloorBreak())
		{
			displayLayout = plugin.getRaid().getLayout().toCode();
			displayLayout =
				displayLayout.substring(0, displayLayout.length() - 1).replaceAll("#", "").replaceFirst("¤", " | ");
		}
		else
		{
			displayLayout = layout;
		}

		if (config.enableLayoutWhitelist() && !plugin.getLayoutWhitelist().contains(layout.toLowerCase()))
		{
			color = Color.RED;
		}
		CachedRaid raid = getCachedRaid();
		String puzzleStr = "";

		if (raid.isTightrope())
		{
			puzzleStr = raid.isCrabs() ? "cr" : raid.isIceDemon() ? "ri" : raid.isThieving() ? "tr" : "?r";
		}

		if ((config.hideVanguards() && raid.isVanguards()) || (config.hideRopeless() && !raid.isTightrope()) ||
			(config.hideUnknownCombat() && raid.isVanguards()))
		{
			panelComponent.getChildren().add(TitleComponent.builder()
				.text("Bad Raid!")
				.color(Color.RED)
				.build());

			return panelComponent.render(graphics);
		}

		scouterActive = true;
		displayLayout = (config.enhanceScouterTitle() ? "" + raid.getCombatCount() + "c " + puzzleStr + " " : "") + displayLayout;

		panelComponent.getChildren().add(TitleComponent.builder()
			.text(displayLayout)
			.color(color)
			.build());

		if (plugin.recordRaid() != null)
		{
			panelComponent.getChildren().add(TitleComponent.builder()
				.text("Record Raid")
				.color(Color.GREEN)
				.build());
			panelComponent.setBackgroundColor(new Color(0, 255, 0, 10));
		}
		else
		{
			if (config.hideBackground())
			{
				panelComponent.setBackgroundColor(null);
			}
			else
			{
				panelComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
			}
		}

		TableComponent tableComponent = new TableComponent();
		tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
		color = Color.ORANGE;
		if (sharable || config.alwaysShowWorldAndCC())
		{
			String clanOwner = Text.removeTags(client.getWidget(WidgetInfo.CLAN_CHAT_OWNER).getText());
			if (clanOwner.equals("None"))
			{
				clanOwner = "Open CC Tab";
				color = Color.RED;
			}

			String worldString = "W" + client.getWorld();
			WorldResult worldResult = worldService.getWorlds();
			if (worldResult != null)
			{
				World world = worldResult.findWorld(client.getWorld());
				WorldRegion region = world.getRegion();
				if (region != null)
				{
					String countryCode = region.getAlpha2();
					worldString += " (" + countryCode + ")";
				}
			}

			tableComponent.addRow(ColorUtil.prependColorTag(worldString, Color.ORANGE),
				ColorUtil.prependColorTag("" + clanOwner, color));
		}

		for (Pair<String, String> pair : raid.getTableComponentSet())
		{
			tableComponent.addRow(pair.getLeft(), pair.getRight());
		}

		// TODO: rest of other rooms code

		panelComponent.getChildren().add(tableComponent);

		//add recommended items
		if (config.showRecommendedItems() && raid.getImageIds().size() > 0)
		{
			panelImages.getChildren().clear();

			Integer[] idArray = raid.getImageIds().toArray(new Integer[0]);
			boolean smallImages = false;

			panelImages.setBackgroundColor(null);

			panelImages.setOrientation(ComponentOrientation.HORIZONTAL);
			panelImages.setWrapping(4);

			for (Integer e : idArray)
			{
				final BufferedImage image = getImage(e, smallImages);
				if (image != null)
				{
					panelImages.getChildren().add(new ImageComponent(image));
				}
			}
			panelComponent.getChildren().add(panelImages);
		}

		Dimension panelDims = panelComponent.render(graphics);
		width = (int) panelDims.getWidth();
		height = (int) panelDims.getHeight();
		return panelDims;
	}

	private BufferedImage getImage(int id, boolean small)
	{
		BufferedImage bim;
		if (id != SpriteID.SPELL_ICE_BARRAGE)
		{
			bim = itemManager.getImage(id);
		}
		else
		{
			bim = spriteManager.getSprite(id, 0);
		}
		if (bim == null)
		{
			return null;
		}
		if (!small)
		{
			return ImageUtil.resizeCanvas(bim, ICON_SIZE, ICON_SIZE);
		}
		if (id != SpriteID.SPELL_ICE_BARRAGE)
		{
			return ImageUtil.resizeImage(bim, SMALL_ICON_SIZE, SMALL_ICON_SIZE);
		}
		return ImageUtil.resizeCanvas(bim, SMALL_ICON_SIZE, SMALL_ICON_SIZE);
	}

	private CachedRaid getCachedRaid()
	{
		if (cachedRaid != null)
		{
			return cachedRaid;
		}
		cachedRaid = new CachedRaid();
		cachedRaid.precompute(config);
		// TODO: implement
		return cachedRaid;
	}

	@Data
	class CachedRaid
	{
		int combatCount;
		List<Integer> iceRooms;
		List<Integer> scavRooms;
		List<Integer> scavsBeforeIceRooms;
		boolean crabs;
		boolean iceDemon;
		boolean tightrope;
		boolean thieving;
		boolean vanguards;
		boolean unknownCombat;
		int bossMatches;
		int bossCount;
		List<Pair<String, String>> tableComponentSet;
		Set<Integer> imageIds;
		int lastScavs;

		@Getter(AccessLevel.NONE)
		@Setter(AccessLevel.NONE)
		private boolean computed = false;
		@Getter(AccessLevel.NONE)
		@Setter(AccessLevel.NONE)
		int roomCountTmp;
		@Getter(AccessLevel.NONE)
		@Setter(AccessLevel.NONE)
		RaidsConfig config;

		public void precompute(RaidsConfig config)
		{
			if (computed)
			{
				return;
			}
			this.config = config;
			initFields();
			computePuzzles();
			computeScavs();
			computeTable();
			computed = true;
		}

		private void initFields()
		{
			combatCount = 0;
			iceRooms = new ArrayList<>();
			scavRooms = new ArrayList<>();
			scavsBeforeIceRooms = new ArrayList<>();
			crabs = false;
			iceDemon = false;
			tightrope = false;
			thieving = false;
			vanguards = false;
			unknownCombat = false;
			bossMatches = 0;
			bossCount = 0;
			tableComponentSet = new ArrayList<>();
			imageIds = new HashSet<>();
			lastScavs = 0;

			roomCountTmp = 0;
		}

		private void computePuzzles()
		{
			roomCountTmp = 0;
			for (Room layoutRoom : plugin.getRaid().getLayout().getRooms())
			{
				int position = layoutRoom.getPosition();
				RaidRoom room = plugin.getRaid().getRoom(position);

				if (room == null)
				{
					continue;
				}

				switch (room.getType())
				{
					case COMBAT:
						combatCount++;
						switch (room)
						{
							case VANGUARDS:
								vanguards = true;
								break;
							case UNKNOWN_COMBAT:
								unknownCombat = true;
								break;
						}
						break;
					case PUZZLE:
						switch (room)
						{
							case CRABS:
								crabs = true;
								break;
							case ICE_DEMON:
								iceDemon = true;
								iceRooms.add(roomCountTmp);
								break;
							case THIEVING:
								thieving = true;
								break;
							case TIGHTROPE:
								tightrope = true;
								break;
							default:
								break;
						}
						break;
					case SCAVENGERS:
						scavRooms.add(roomCountTmp);
						break;
				}
				roomCountTmp++;
			}
		}

		private void computeScavs()
		{
			for (Integer i : getIceRooms())
			{
				int prev = 0;
				for (Integer s : getScavRooms())
				{
					if (s > i)
					{
						break;
					}
					prev = s;
				}
				getScavsBeforeIceRooms().add(prev);
			}
			lastScavs = getScavRooms().get(getScavRooms().size() - 1);
		}

		private void computeTable()
		{
			roomCountTmp = 0;
			if (config.enableRotationWhitelist())
			{
				bossMatches = plugin.getRotationMatches();
			}

			Color color;
			for (Room layoutRoom : plugin.getRaid().getLayout().getRooms())
			{
				int position = layoutRoom.getPosition();
				RaidRoom room = plugin.getRaid().getRoom(position);

				if (room == null)
				{
					continue;
				}

				color = Color.WHITE;

				switch (room.getType())
				{
					case COMBAT:
						bossCount++;
						if (plugin.getRoomWhitelist().contains(room.getName().toLowerCase()))
						{
							color = Color.GREEN;
						}
						else if (plugin.getRoomBlacklist().contains(room.getName().toLowerCase())
							|| config.enableRotationWhitelist() && bossCount > bossMatches)
						{
							color = Color.RED;
						}

						String bossName = room.getName();
						String bossNameLC = bossName.toLowerCase();
						if (config.showRecommendedItems() && plugin.getRecommendedItemsList().get(bossNameLC) != null)
						{
							imageIds.addAll(plugin.getRecommendedItemsList().get(bossNameLC));
						}

						if (bossNameLC.startsWith("unknown"))
						{
							bossName = "Unknown";
						}
						tableComponentSet.add(new ImmutablePair<>(room.getType().getName(), ColorUtil.prependColorTag(bossName, color)));

						break;

					case PUZZLE:
						String puzzleName = room.getName();
						String puzzleNameLC = puzzleName.toLowerCase();
						if (plugin.getRecommendedItemsList().get(puzzleNameLC) != null)
						{
							imageIds.addAll(plugin.getRecommendedItemsList().get(puzzleNameLC));
						}
						if (plugin.getRoomWhitelist().contains(puzzleNameLC))
						{
							color = Color.GREEN;
						}
						else if (plugin.getRoomBlacklist().contains(puzzleNameLC))
						{
							color = Color.RED;
						}
						if (config.colorTightrope() && puzzleNameLC.equals("tightrope"))
						{
							color = config.tightropeColor();
						}
						if (config.crabHandler() && puzzleNameLC.equals("crabs"))
						{
							if (plugin.getGoodCrabs() == null)
							{
								color = Color.RED;
							}
							else
							{
								switch (plugin.getGoodCrabs())
								{
									case "Good Crabs":
										color = config.goodCrabColor();
										break;
									case "Rare Crabs":
										color = config.rareCrabColor();
										break;
								}
							}
						}

						if (puzzleNameLC.startsWith("unknown"))
						{
							puzzleName = "Unknown";
						}

						tableComponentSet.add(new ImmutablePair<>(room.getType().getName(), ColorUtil.prependColorTag(puzzleName, color)));
						break;
					case FARMING:
						if (config.showScavsFarms())
						{
							tableComponentSet.add(new ImmutablePair<>("", ColorUtil.prependColorTag(room.getType().getName(), new Color(181, 230, 29))));
						}
						break;
					case SCAVENGERS:
						if (config.scavsBeforeOlm() && roomCountTmp == lastScavs)
						{
							tableComponentSet.add(new ImmutablePair<>("OlmPrep", ColorUtil.prependColorTag("Scavs", config.scavPrepColor())));
						}
						else if (config.scavsBeforeIce() && scavsBeforeIceRooms.contains(roomCountTmp))
						{
							tableComponentSet.add(new ImmutablePair<>("IcePrep", ColorUtil.prependColorTag("Scavs", config.scavPrepColor())));
						}
						else if (config.showScavsFarms())
						{
							tableComponentSet.add(new ImmutablePair<>("", ColorUtil.prependColorTag("Scavs", new Color(181, 230, 29))));
						}
						break;
				}
				roomCountTmp++;
			}
		}
	}
}