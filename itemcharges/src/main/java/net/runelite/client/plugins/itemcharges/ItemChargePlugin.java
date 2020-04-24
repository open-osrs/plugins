/*
 * Copyright (c) 2017, Seth <Sethtroll3@gmail.com>
 * Copyright (c) 2018, Hydrox6 <ikada@protonmail.ch>
 * Copyright (c) 2019, Aleios <https://github.com/aleios>
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
package net.runelite.client.plugins.itemcharges;

import com.google.common.primitives.Ints;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GraphicID;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import static net.runelite.api.ItemID.RING_OF_RECOIL;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.SpotAnimationChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Item Charges",
	description = "Show number of item charges remaining",
	tags = {"inventory", "notifications", "overlay"},
	type = PluginType.UTILITY
)
public class ItemChargePlugin extends Plugin
{
	private static final Pattern DODGY_CHECK_PATTERN = Pattern.compile(
		"Your dodgy necklace has (\\d+) charges? left\\.");
	private static final String CHAT_BRACELET_SLAUGHTER = "Your bracelet of slaughter prevents your slayer";
	private static final Pattern CHAT_BRACELET_SLAUGHTER_REGEX = Pattern.compile(
		"Your bracelet of slaughter prevents your slayer count decreasing. It has (\\d{1,2}) charge[s]? left.");
	private static final String CHAT_BRACELET_EXPEDITIOUS = "Your expeditious bracelet helps you progress your";
	private static final Pattern CHAT_BRACELET_EXPEDITIOUS_REGEX = Pattern.compile(
		"Your expeditious bracelet helps you progress your slayer (?:task )?faster. It has (\\d{1,2}) charge[s]? left.");
	private static final Pattern CHAT_BRACELET_SLAUGHTER_CHARGE_REGEX = Pattern.compile(
		"Your bracelet of slaughter has (\\d{1,2}) charge[s]? left.");
	private static final Pattern CHAT_BRACELET_EXPEDITIOUS_CHARGE_REGEX = Pattern.compile(
		"Your expeditious bracelet has (\\d{1,2}) charge[s]? left.");
	private static final Pattern DODGY_PROTECT_PATTERN = Pattern.compile(
		"Your dodgy necklace protects you\\..*It has (\\d+) charges? left\\.");
	private static final Pattern DODGY_BREAK_PATTERN = Pattern.compile(
		"Your dodgy necklace protects you\\..*It then crumbles to dust\\.");
	private static final String RING_OF_RECOIL_BREAK_MESSAGE = "<col=7f007f>Your Ring of Recoil has shattered.</col>";
	private static final Pattern BINDING_CHECK_PATTERN = Pattern.compile(
		"You have ([0-9]+|one) charges? left before your Binding necklace disintegrates.");
	private static final Pattern BINDING_USED_PATTERN = Pattern.compile(
		"You bind the temple's power into (mud|lava|steam|dust|smoke|mist) runes\\.");
	private static final String BINDING_BREAK_TEXT = "Your Binding necklace has disintegrated.";
	private static final Pattern XERIC_CHECK_CHARGE_PATTERN = Pattern.compile(
		"talisman has (\\d+|one) charges?");
	private static final Pattern XERIC_RECHARGEWIDGET_PATTERN = Pattern.compile(
		"Your talisman now has (\\d+|one) charges?\\.");
	private static final Pattern XERIC_OUT_OF_CHARGES = Pattern.compile(
		"Your talisman has run out of charges");
	private static final Pattern XERIC_UNCHARGE_PATTERN = Pattern.compile(
		"lizard fangs? from your talisman\\.");
	private static final Pattern SOULBEARER_RECHARGE_PATTERN = Pattern.compile(
		"You add (\\d+|a) charges? to your soul bearer.It now has (\\d+) charges\\.");
	private static final Pattern SOULBEARER_RECHARGE_PATTERN2 = Pattern.compile(
		"Your soul bearer now has one charge\\.");
	private static final Pattern SOULBEARER_CHECK_CHARGE_PATTERN = Pattern.compile(
		"soul bearer has (\\d+|one) charges?\\.");
	private static final Pattern SOULBEARER_UNCHARGE_PATTERN = Pattern.compile(
		"You remove the runes from the soul bearer\\.");
	private static final Pattern SOULBEARER_BANKHEADS_PATTERN = Pattern.compile(
		"Your soul bearer carries the ensouled heads? to your ?bank\\. It has (\\d+|one) charges? left\\.");
	private static final Pattern SOULBEARER_OUT_OF_CHARGES = Pattern.compile(
		"Your soul bearer carries the ensouled heads? to (.+)\\. It has run out of charges\\.");
	private static final Pattern CHRONICLE_CHECK_CHARGE_PATTERN = Pattern.compile(
		"Your book has (\\d+) charges left\\.");
	private static final Pattern CHRONICLE_ADD_CHARGE_PATTERN = Pattern.compile(
		"You add (\\d+|a single) charges? to your book. It now has (\\d+) charges\\.");
	private static final Pattern CHRONICLE_LAST_CHARGE_PATTERN = Pattern.compile(
		"You have one charge left in your book\\.");
	private static final Pattern CHRONICLE_OUT_OF_CHARGES_PATTERN = Pattern.compile(
		"Your book has run out of charges\\.");
	private static final Pattern RING_OF_FORGING_CHECK_PATTERN = Pattern.compile(
		"You can smelt ([0-9+]+|one) more pieces? of iron ore before a ring melts\\.");
	private static final String RING_OF_FORGING_USED_TEXT = "You retrieve a bar of iron.";
	private static final String RING_OF_FORGING_BREAK_TEXT = "<col=7f007f>Your Ring of Forging has melted.</col>";
	private static final Pattern AMULET_OF_CHEMISTRY_CHECK_PATTERN = Pattern.compile(
		"Your amulet of chemistry has (\\d) charges? left\\."
	);
	private static final Pattern AMULET_OF_CHEMISTRY_USED_PATTERN = Pattern.compile(
		"Your amulet of chemistry helps you create a 4-dose potion\\. (?:<col=ff0000>)?It has (\\d|one) charges? left\\."
	);
	private static final String AMULET_OF_CHEMISTRY_BREAK_TEXT = "Your amulet of chemistry helps you create a 4-dose potion. <col=ff0000>It then crumbles to dust.</col>";
	private static final Pattern AMULET_OF_BOUNTY_CHECK_PATTERN = Pattern.compile(
		"Your amulet of bounty has (\\d+) charges? left\\."
	);
	private static final Pattern AMULET_OF_BOUNTY_USED_PATTERN = Pattern.compile(
		"Your amulet of bounty saves some seeds for you\\. (?:<col=ff0000>)?It has (\\d) charges? left\\."
	);
	private static final String AMULET_OF_BOUNTY_BREAK_TEXT = "Your amulet of bounty saves some seeds for you. <col=ff0000>It then crumbles to dust.</col>";


	private static final int MAX_DODGY_CHARGES = 10;
	private static final int MAX_SLAUGHTER_CHARGES = 30;
	private static final int MAX_EXPEDITIOUS_CHARGES = 30;
	private static final int MAX_BINDING_CHARGES = 16;
	private static final int MAX_EXPLORER_RING_CHARGES = 30;
	private static final int MAX_RING_OF_FORGING_CHARGES = 140;
	private static final int MAX_AMULET_OF_CHEMISTRY_CHARGES = 5;
	private static final int MAX_AMULET_OF_BOUNTY_CHARGES = 10;

	@Getter(AccessLevel.PACKAGE)
	private boolean ringOfRecoilAvailable = false;

	@Getter(AccessLevel.PACKAGE)
	private boolean ringOfRecoilEquipped = false;

	@Getter(AccessLevel.PACKAGE)
	private BufferedImage recoilRingImage;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ItemChargeOverlay overlay;

	@Inject
	private ItemRecoilOverlay recoilOverlay;

	@Inject
	private ItemManager itemManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private Notifier notifier;

	@Inject
	private ItemChargeConfig config;

	// Limits destroy callback to once per tick
	private int lastCheckTick;

	@Provides
	ItemChargeConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ItemChargeConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(recoilOverlay);
		recoilRingImage = itemManager.getImage(RING_OF_RECOIL);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(recoilOverlay);
		infoBoxManager.removeIf(ItemChargeInfobox.class::isInstance);
		lastCheckTick = -1;
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("itemCharge"))
		{
			return;
		}

		if (!config.showInfoboxes())
		{
			infoBoxManager.removeIf(ItemChargeInfobox.class::isInstance);
			return;
		}

		if (!config.showTeleportCharges())
		{
			removeInfobox(ItemWithSlot.TELEPORT);
		}
		
		if (!config.showAmuletOfChemistryCharges())
		{
			removeInfobox(ItemWithSlot.AMULET_OF_CHEMISTY);
		}

		if (!config.showAmuletOfBountyCharges())
		{
			removeInfobox(ItemWithSlot.AMULET_OF_BOUNTY);
		}

		if (!config.showAbyssalBraceletCharges())
		{
			removeInfobox(ItemWithSlot.ABYSSAL_BRACELET);
		}

		if (!config.showDodgyCount())
		{
			removeInfobox(ItemWithSlot.DODGY_NECKLACE);
		}

		if (!config.showSlayerBracelets())
		{
			removeInfobox(ItemWithSlot.BRACELET_OF_SLAUGHTER);
			removeInfobox(ItemWithSlot.EXPEDITIOUS_BRACELET);
		}

		if (!config.showBindingNecklaceCharges())
		{
			removeInfobox(ItemWithSlot.BINDING_NECKLACE);
		}

		if (!config.showExplorerRingCharges())
		{
			removeInfobox(ItemWithSlot.EXPLORER_RING);
		}

		if (!config.showRingOfForgingCount())
		{
			removeInfobox(ItemWithSlot.RING_OF_FORGING);
		}
	}

	@Subscribe
	void onChatMessage(ChatMessage event)
	{
		if (event.getType() == ChatMessageType.GAMEMESSAGE || event.getType() == ChatMessageType.SPAM)
		{
			String message = event.getMessage();
			Matcher dodgyCheckMatcher = DODGY_CHECK_PATTERN.matcher(message);
			Matcher dodgyProtectMatcher = DODGY_PROTECT_PATTERN.matcher(message);
			Matcher dodgyBreakMatcher = DODGY_BREAK_PATTERN.matcher(message);
			Matcher bindingNecklaceCheckMatcher = BINDING_CHECK_PATTERN.matcher(event.getMessage());
			Matcher bindingNecklaceUsedMatcher = BINDING_USED_PATTERN.matcher(event.getMessage());
			Matcher xericRechargeMatcher = XERIC_CHECK_CHARGE_PATTERN.matcher(message);
			Matcher xericOutOfChargesMatcher = XERIC_OUT_OF_CHARGES.matcher(message);
			Matcher soulbearerCheckMatcher = SOULBEARER_CHECK_CHARGE_PATTERN.matcher(message);
			Matcher chronicleCheckMatcher = CHRONICLE_CHECK_CHARGE_PATTERN.matcher(message);
			Matcher chronicleRechargeMatcher = CHRONICLE_ADD_CHARGE_PATTERN.matcher(message);
			Matcher chronicleLastChargeMatcher = CHRONICLE_LAST_CHARGE_PATTERN.matcher(message);
			Matcher chronicleOutOfChargesMatcher = CHRONICLE_OUT_OF_CHARGES_PATTERN.matcher(message);
			Matcher ringOfForgingCheckMatcher = RING_OF_FORGING_CHECK_PATTERN.matcher(message);
			Matcher slaughterMatcher = CHAT_BRACELET_SLAUGHTER_REGEX.matcher(Text.removeTags(message));
			Matcher expeditiousMatcher = CHAT_BRACELET_EXPEDITIOUS_REGEX.matcher(Text.removeTags(message));
			Matcher slaughterChargeMatcher = CHAT_BRACELET_SLAUGHTER_CHARGE_REGEX.matcher(Text.removeTags(message));
			Matcher expeditiousChargeMatcher = CHAT_BRACELET_EXPEDITIOUS_CHARGE_REGEX.matcher(Text.removeTags(message));
			Matcher amuletOfChemistryCheckMatcher = AMULET_OF_CHEMISTRY_CHECK_PATTERN.matcher(message);
			Matcher amuletOfChemistryUsedMatcher = AMULET_OF_CHEMISTRY_USED_PATTERN.matcher(message);
			Matcher amuletOfBountyCheckMatcher = AMULET_OF_BOUNTY_CHECK_PATTERN.matcher(message);
			Matcher amuletOfBountyUsedMatcher = AMULET_OF_BOUNTY_USED_PATTERN.matcher(message);
			
			if (config.recoilNotification() && message.contains(RING_OF_RECOIL_BREAK_MESSAGE))
			{
				notifier.notify("Your Ring of Recoil has shattered");
			}
			else if (Text.removeTags(message).startsWith(CHAT_BRACELET_SLAUGHTER))
			{
				updateBraceletOfSlaughterCharges(slaughterMatcher.find() ? Integer.parseInt(slaughterMatcher.group(1)) : MAX_SLAUGHTER_CHARGES);
			}
			else if (Text.removeTags(message).startsWith(CHAT_BRACELET_EXPEDITIOUS))
			{
				updateExpeditiousCharges(expeditiousMatcher.find() ? Integer.parseInt(expeditiousMatcher.group(1)) : MAX_EXPEDITIOUS_CHARGES);
			}
			else if (slaughterChargeMatcher.find())
			{
				updateBraceletOfSlaughterCharges(Integer.parseInt(slaughterChargeMatcher.group(1)));
			}
			else if (expeditiousChargeMatcher.find())
			{
				updateExpeditiousCharges(Integer.parseInt(expeditiousChargeMatcher.group(1)));
			}
			else if (dodgyCheckMatcher.find())
			{
				updateDodgyNecklaceCharges(Integer.parseInt(dodgyCheckMatcher.group(1)));
			}
			else if (dodgyProtectMatcher.find())
			{
				updateDodgyNecklaceCharges(Integer.parseInt(dodgyProtectMatcher.group(1)));
			}
			else if (dodgyBreakMatcher.find())
			{
				if (config.dodgyNotification())
				{
					notifier.notify("Your dodgy necklace has crumbled to dust.");
				}

				updateDodgyNecklaceCharges(MAX_DODGY_CHARGES);
			}
			else if (amuletOfChemistryCheckMatcher.find())
			{
				updateAmuletOfChemistyCharges(Integer.parseInt(amuletOfChemistryCheckMatcher.group(1)));
			}
			else if (amuletOfChemistryUsedMatcher.find())
			{
				final String match = amuletOfChemistryUsedMatcher.group(1);

				int charges = 1;
				if (!match.equals("one"))
				{
					charges = Integer.parseInt(match);
				}

				updateAmuletOfChemistyCharges(charges);
			}
			else if (message.equals(AMULET_OF_CHEMISTRY_BREAK_TEXT))
			{
				updateAmuletOfChemistyCharges(MAX_AMULET_OF_CHEMISTRY_CHARGES);
			}
			else if (amuletOfBountyCheckMatcher.find())
			{
				updateAmuletOfBountyCharges(Integer.parseInt(amuletOfBountyCheckMatcher.group(1)));
			}
			else if (amuletOfBountyUsedMatcher.find())
			{
				updateAmuletOfBountyCharges(Integer.parseInt(amuletOfBountyUsedMatcher.group(1)));
			}
			else if (message.equals(AMULET_OF_BOUNTY_BREAK_TEXT))
			{
				updateAmuletOfBountyCharges(MAX_AMULET_OF_BOUNTY_CHARGES);
			}
			else if (message.contains(BINDING_BREAK_TEXT))
			{
				if (config.bindingNotification())
				{
					notifier.notify(BINDING_BREAK_TEXT);
				}

				// This chat message triggers before the used message so add 1 to the max charges to ensure proper sync
				updateBindingNecklaceCharges(MAX_BINDING_CHARGES + 1);
			}
			else if (bindingNecklaceUsedMatcher.find())
			{
				updateBindingNecklaceCharges(config.bindingNecklace() - 1);
			}
			else if (bindingNecklaceCheckMatcher.find())
			{
				final String match = bindingNecklaceCheckMatcher.group(1);

				int charges = 1;
				if (!match.equals("one"))
				{
					charges = Integer.parseInt(match);
				}

				updateBindingNecklaceCharges(charges);
			}
			else if (xericRechargeMatcher.find())
			{
				final int xericCharges = xericRechargeMatcher.group(1).equals("one") ? 1 : (Integer.parseInt(xericRechargeMatcher.group(1)));
				updateXericCharges(xericCharges);
			}
			else if (xericOutOfChargesMatcher.find())
			{
				final int xericCharges = 0;
				updateXericCharges(xericCharges);
			}
			else if (soulbearerCheckMatcher.find())
			{
				final int soulbearerCharges = soulbearerCheckMatcher.group(1).equals("one") ? 1 : (Integer.parseInt(soulbearerCheckMatcher.group(1)));
				updateSoulBearerCharges(soulbearerCharges);
			}
			else if (chronicleCheckMatcher.find())
			{
				final int chronicleCharges = chronicleCheckMatcher.group(1).equals("one") ? 1 : (Integer.parseInt(chronicleCheckMatcher.group(1)));
				updateChronicleCharges(chronicleCharges);
			}
			else if (chronicleRechargeMatcher.find())
			{
				final int chronicleCharges = chronicleRechargeMatcher.group(2).equals("one") ? 1 : (Integer.parseInt(chronicleRechargeMatcher.group(2)));
				updateChronicleCharges(chronicleCharges);
			}
			else if (chronicleLastChargeMatcher.find())
			{
				final int chronicleCharges = 1;
				updateChronicleCharges(chronicleCharges);
			}
			else if (chronicleOutOfChargesMatcher.find())
			{
				final int chronicleCharges = 0;
				updateChronicleCharges(chronicleCharges);
			}
			else if (ringOfForgingCheckMatcher.find())
			{
				final String match = ringOfForgingCheckMatcher.group(1);

				int charges = 1;
				if (!match.equals("one"))
				{
					charges = Integer.parseInt(match);
				}
				updateRingOfForgingCharges(charges);
			}
			else if (message.equals(RING_OF_FORGING_USED_TEXT))
			{
				final ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);

				// Determine if the player smelted with a Ring of Forging equipped.
				if (equipment == null)
				{
					return;
				}

				if (equipment.contains(ItemID.RING_OF_FORGING))
				{
					int charges = Ints.constrainToRange(config.ringOfForging() - 1, 0, MAX_RING_OF_FORGING_CHARGES);
					updateRingOfForgingCharges(charges);
				}
			}
			else if (message.equals(RING_OF_FORGING_BREAK_TEXT))
			{
				if (config.ringOfForgingNotification())
				{
					notifier.notify("Your ring of forging has melted.");
				}

				updateRingOfForgingCharges(MAX_RING_OF_FORGING_CHARGES);
			}
		}
	}

	@Subscribe
	private void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() != client.getItemContainer(InventoryID.EQUIPMENT) || !config.showInfoboxes())
		{
			return;
		}

		final Item[] items = event.getItemContainer().getItems();

		if (config.showTeleportCharges())
		{
			updateJewelleryInfobox(ItemWithSlot.TELEPORT, items);
		}

		if (config.showDodgyCount())
		{
			updateJewelleryInfobox(ItemWithSlot.DODGY_NECKLACE, items);
		}

		if (config.showAbyssalBraceletCharges())
		{
			updateJewelleryInfobox(ItemWithSlot.ABYSSAL_BRACELET, items);
		}

		if (config.showSlayerBracelets())
		{
			updateJewelleryInfobox(ItemWithSlot.BRACELET_OF_SLAUGHTER, items);
			updateJewelleryInfobox(ItemWithSlot.EXPEDITIOUS_BRACELET, items);
		}

		if (config.showBindingNecklaceCharges())
		{
			updateJewelleryInfobox(ItemWithSlot.BINDING_NECKLACE, items);
		}

		if (config.showExplorerRingCharges())
		{
			updateJewelleryInfobox(ItemWithSlot.EXPLORER_RING, items);
		}

		if (config.showRingOfForgingCount())
		{
			updateJewelleryInfobox(ItemWithSlot.RING_OF_FORGING, items);
		}
		
		if (config.showAmuletOfChemistryCharges())
		{
			updateJewelleryInfobox(ItemWithSlot.AMULET_OF_CHEMISTY, items);
		}

		if (config.showAmuletOfBountyCharges())
		{
			updateJewelleryInfobox(ItemWithSlot.AMULET_OF_BOUNTY, items);
		}
	}

	@Subscribe
	private void onGameTick(GameTick event)
	{
		Widget braceletBreakWidget = client.getWidget(WidgetInfo.DIALOG_SPRITE_TEXT);

		if (braceletBreakWidget != null)
		{
			String braceletText = Text.removeTags(braceletBreakWidget.getText()); //remove color and linebreaks
			if (braceletText.contains("bracelet of slaughter"))
			{
				config.slaughter(MAX_SLAUGHTER_CHARGES);
			}
			else if (braceletText.contains("expeditious bracelet"))
			{
				config.expeditious(MAX_EXPEDITIOUS_CHARGES);
			}
		}

		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		ringOfRecoilAvailable = false;
		ringOfRecoilEquipped = false;

		if (equipment != null && inventory != null)
		{
			for (Item item : equipment.getItems())
			{
				if (item.getId() == RING_OF_RECOIL)
				{
					ringOfRecoilEquipped = true;
					ringOfRecoilAvailable = true;
					break;
				}
			}

			for (Item item : inventory.getItems())
			{
				if (item.getId() == RING_OF_RECOIL)
				{
					ringOfRecoilAvailable = true;
					break;
				}
			}
		}

		Widget dialog1 = client.getWidget(WidgetInfo.DIALOG_SPRITE_TEXT);
		Widget dialog2 = client.getWidget(WidgetInfo.DIALOG2_SPRITE_TEXT);

		if (dialog1 != null)
		{
			String widgetText = Text.removeTags(dialog1.getText());
			Matcher xericRechargeMatcher = XERIC_RECHARGEWIDGET_PATTERN.matcher(widgetText);
			Matcher soulbearerRechargeMatcher = SOULBEARER_RECHARGE_PATTERN.matcher(widgetText);
			Matcher soulbearerRecharge2Matcher = SOULBEARER_RECHARGE_PATTERN2.matcher(widgetText);

			if (xericRechargeMatcher.find())
			{
				final int xericCharges = xericRechargeMatcher.group(1).equals("one") ? 1 : (Integer.parseInt(xericRechargeMatcher.group(1)));
				updateXericCharges(xericCharges);
			}
			else if (soulbearerRechargeMatcher.find())
			{
				final int soulbearerCharges = soulbearerRechargeMatcher.group(2).equals("one") ? 1 : (Integer.parseInt(soulbearerRechargeMatcher.group(2)));
				updateSoulBearerCharges(soulbearerCharges);
			}
			else if (soulbearerRecharge2Matcher.find())
			{
				final int soulbearerCharges = 1;
				updateSoulBearerCharges(soulbearerCharges);
			}
		}

		if (dialog2 != null)
		{
			String widgetText = Text.removeTags(dialog2.getText());
			Matcher xericUnchargeMatcher = XERIC_UNCHARGE_PATTERN.matcher(widgetText);
			Matcher soulbearerUnchargeMatcher = SOULBEARER_UNCHARGE_PATTERN.matcher(widgetText);
			Matcher soulbearerBankHeadsMatcher = SOULBEARER_BANKHEADS_PATTERN.matcher(widgetText);
			Matcher soulbearerOutOfCharges = SOULBEARER_OUT_OF_CHARGES.matcher(widgetText);

			if (xericUnchargeMatcher.find())
			{
				final int xericCharges = 0;
				updateXericCharges(xericCharges);
			}
			else if (soulbearerUnchargeMatcher.find() || soulbearerOutOfCharges.find())
			{
				final int soulbearerCharges = 0;
				updateSoulBearerCharges(soulbearerCharges);
			}
			else if (soulbearerBankHeadsMatcher.find())
			{
				final int soulbearerCharges = soulbearerBankHeadsMatcher.group(1).equals("one") ? 1 : (Integer.parseInt(soulbearerBankHeadsMatcher.group(1)));
				updateSoulBearerCharges(soulbearerCharges);
			}
		}
	}

	@Subscribe
	private void onSpotAnimationChanged(SpotAnimationChanged event)
	{
		if (event.getActor() == client.getLocalPlayer() && client.getLocalPlayer().getSpotAnimation() == GraphicID.XERIC_TELEPORT)
		{
			final int xericCharges = Math.max(config.xericTalisman() - 1, 0);
			updateXericCharges(xericCharges);
		}
	}

	@Subscribe
	private void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if (!"destroyOnOpKey".equals(event.getEventName()))
		{
			return;
		}

		final int yesOption = client.getIntStack()[client.getIntStackSize() - 1];
		if (yesOption == 1)
		{
			checkDestroyWidget();
		}
	}

	@Subscribe
	private void onVarbitChanged(VarbitChanged event)
	{
		int explorerRingCharge = client.getVar(Varbits.EXPLORER_RING_ALCHS);
		int lastExplorerRingCharge = -1;
		if (lastExplorerRingCharge != explorerRingCharge)
		{
			updateExplorerRingCharges(explorerRingCharge);
		}
	}

	private void updateDodgyNecklaceCharges(final int value)
	{
		config.dodgyNecklace(value);

		if (config.showInfoboxes() && config.showDodgyCount())
		{
			final ItemContainer itemContainer = client.getItemContainer(InventoryID.EQUIPMENT);

			if (itemContainer == null)
			{
				return;
			}

			updateJewelleryInfobox(ItemWithSlot.DODGY_NECKLACE, itemContainer.getItems());
		}
	}

	private void updateBraceletOfSlaughterCharges(final int value)
	{
		config.slaughter(value);

		if (config.showInfoboxes() && config.showSlayerBracelets())
		{
			final ItemContainer itemContainer = client.getItemContainer(InventoryID.EQUIPMENT);

			if (itemContainer == null)
			{
				return;
			}

			updateJewelleryInfobox(ItemWithSlot.BRACELET_OF_SLAUGHTER, itemContainer.getItems());
		}
	}

	private void updateExpeditiousCharges(final int value)
	{
		config.expeditious(value);

		if (config.showInfoboxes() && config.showSlayerBracelets())
		{
			final ItemContainer itemContainer = client.getItemContainer(InventoryID.EQUIPMENT);

			if (itemContainer == null)
			{
				return;
			}

			updateJewelleryInfobox(ItemWithSlot.EXPEDITIOUS_BRACELET, itemContainer.getItems());
		}
	}

	private void updateAmuletOfChemistyCharges(final int value)
	{
		config.amuletOfChemistry(value);

		if (config.showInfoboxes() && config.showAmuletOfChemistryCharges())
		{
			final ItemContainer itemContainer = client.getItemContainer(InventoryID.EQUIPMENT);

			if (itemContainer == null)
			{
				return;
			}

			updateJewelleryInfobox(ItemWithSlot.AMULET_OF_CHEMISTY, itemContainer.getItems());
		}
	}

	private void updateAmuletOfBountyCharges(final int value)
	{
		config.amuletOfBounty(value);

		if (config.showInfoboxes() && config.showAmuletOfBountyCharges())
		{
			final ItemContainer itemContainer = client.getItemContainer(InventoryID.EQUIPMENT);

			if (itemContainer == null)
			{
				return;
			}

			updateJewelleryInfobox(ItemWithSlot.AMULET_OF_BOUNTY, itemContainer.getItems());
		}
	}

	private void updateBindingNecklaceCharges(final int value)
	{
		config.bindingNecklace(value);

		if (config.showInfoboxes() && config.showBindingNecklaceCharges())
		{
			final ItemContainer itemContainer = client.getItemContainer(InventoryID.EQUIPMENT);

			if (itemContainer == null)
			{
				return;
			}

			updateJewelleryInfobox(ItemWithSlot.BINDING_NECKLACE, itemContainer.getItems());
		}
	}

	private void updateXericCharges(int xericCharges)
	{
		config.xericTalisman(xericCharges);
	}

	private void updateSoulBearerCharges(int soulBearerCharges)
	{
		config.soulBearer(soulBearerCharges);
	}

	private void updateChronicleCharges(int chronicleCharges)
	{
		config.chronicle(chronicleCharges);
	}

	private void updateExplorerRingCharges(final int value)
	{
		// Note: Varbit counts upwards. We count down from the maximum charges.
		config.explorerRing(MAX_EXPLORER_RING_CHARGES - value);

		if (config.showInfoboxes() && config.showExplorerRingCharges())
		{
			final ItemContainer itemContainer = client.getItemContainer(InventoryID.EQUIPMENT);

			if (itemContainer == null)
			{
				return;
			}

			updateJewelleryInfobox(ItemWithSlot.EXPLORER_RING, itemContainer.getItems());
		}
	}

	private void checkDestroyWidget()
	{
		final int currentTick = client.getTickCount();
		if (lastCheckTick == currentTick)
		{
			return;
		}
		lastCheckTick = currentTick;

		final Widget widgetDestroyItemName = client.getWidget(WidgetInfo.DESTROY_ITEM_NAME);
		if (widgetDestroyItemName == null)
		{
			return;
		}

		switch (widgetDestroyItemName.getText())
		{
			case "Binding necklace":
				updateBindingNecklaceCharges(MAX_BINDING_CHARGES);
				break;
			case "Dodgy necklace":
				updateDodgyNecklaceCharges(MAX_DODGY_CHARGES);
				break;
			case "Ring of forging":
				updateRingOfForgingCharges(MAX_RING_OF_FORGING_CHARGES);
				break;
			case "Amulet of chemistry":
				updateAmuletOfChemistyCharges(MAX_AMULET_OF_CHEMISTRY_CHARGES);
				break;
		}
	}

	private void updateJewelleryInfobox(ItemWithSlot item, Item[] items)
	{
		for (final EquipmentInventorySlot equipmentInventorySlot : item.getSlots())
		{
			updateJewelleryInfobox(item, items, equipmentInventorySlot);
		}
	}

	private void updateJewelleryInfobox(ItemWithSlot type, Item[] items, EquipmentInventorySlot slot)
	{
		removeInfobox(type, slot);

		if (slot.getSlotIdx() >= items.length)
		{
			return;
		}

		final int id = items[slot.getSlotIdx()].getId();
		if (id < 0)
		{
			return;
		}

		final ItemWithCharge itemWithCharge = ItemWithCharge.findItem(id);
		int charges = -1;

		if (itemWithCharge == null)
		{
			if (id == ItemID.DODGY_NECKLACE && type == ItemWithSlot.DODGY_NECKLACE)
			{
				charges = config.dodgyNecklace();
			}
			else if (id == ItemID.BRACELET_OF_SLAUGHTER && type == ItemWithSlot.BRACELET_OF_SLAUGHTER)
			{
				charges = config.slaughter();
			}
			else if (id == ItemID.EXPEDITIOUS_BRACELET && type == ItemWithSlot.EXPEDITIOUS_BRACELET)
			{
				charges = config.expeditious();
			}
			else if (id == ItemID.BINDING_NECKLACE && type == ItemWithSlot.BINDING_NECKLACE)
			{
				charges = config.bindingNecklace();
			}
			else if ((id >= ItemID.EXPLORERS_RING_1 && id <= ItemID.EXPLORERS_RING_4) && type == ItemWithSlot.EXPLORER_RING)
			{
				charges = config.explorerRing();
			}
			else if (id == ItemID.AMULET_OF_CHEMISTRY && type == ItemWithSlot.AMULET_OF_CHEMISTY)
			{
				charges = config.amuletOfChemistry();
			}
			else if (id == ItemID.AMULET_OF_BOUNTY && type == ItemWithSlot.AMULET_OF_BOUNTY)
			{
				charges = config.amuletOfBounty();
			}
		}
		else if (itemWithCharge.getType() == type.getType())
		{
			charges = itemWithCharge.getCharges();
		}

		if (charges <= 0)
		{
			return;
		}

		final String name = itemManager.getItemDefinition(id).getName();
		final BufferedImage image = itemManager.getImage(id);
		final ItemChargeInfobox infobox = new ItemChargeInfobox(this, image, name, charges, type, slot);
		infoBoxManager.addInfoBox(infobox);
	}

	private void updateRingOfForgingCharges(final int value)
	{
		config.ringOfForging(value);

		if (config.showInfoboxes() && config.showRingOfForgingCount())
		{
			final ItemContainer itemContainer = client.getItemContainer(InventoryID.EQUIPMENT);

			if (itemContainer == null)
			{
				return;
			}

			updateJewelleryInfobox(ItemWithSlot.RING_OF_FORGING, itemContainer.getItems());
		}
	}

	private void removeInfobox(final ItemWithSlot item)
	{
		infoBoxManager.removeIf(t -> t instanceof ItemChargeInfobox && ((ItemChargeInfobox) t).getItem() == item);
	}

	private void removeInfobox(final ItemWithSlot item, final EquipmentInventorySlot slot)
	{
		infoBoxManager.removeIf(t ->
		{
			if (!(t instanceof ItemChargeInfobox))
			{
				return false;
			}

			final ItemChargeInfobox i = (ItemChargeInfobox) t;
			return i.getItem() == item && i.getSlot() == slot;
		});
	}

	Color getColor(int charges)
	{
		Color color = Color.WHITE;
		if (charges <= config.veryLowWarning())
		{
			color = config.veryLowWarningColor();
		}
		else if (charges <= config.lowWarning())
		{
			color = config.lowWarningolor();
		}
		return color;
	}
}
