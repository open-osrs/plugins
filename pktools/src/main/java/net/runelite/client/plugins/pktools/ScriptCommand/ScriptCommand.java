package net.runelite.client.plugins.pktools.ScriptCommand;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.pktools.PkToolsConfig;
import net.runelite.client.plugins.pktools.PkToolsOverlay;
import net.runelite.client.plugins.pktools.PkToolsPlugin;

import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.Instant;

import static net.runelite.client.plugins.pktools.PkToolsHotkeyListener.getTag;

public interface ScriptCommand
{
	void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager);

	default void clickPrayer(Client client, PkToolsConfig config, Point p)
	{
		try
		{
			InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.PRAYER_TAB_HOTKEY));
			Thread.sleep(config.clickDelay());
			InputHandler.leftClick(client, p);
			Thread.sleep(config.clickDelay());
			InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.INVENTORY_TAB_HOTKEY));
		}
		catch (Exception e)
		{
			//swallow
		}
	}
}

class RigourCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		Widget RIGOUR = client.getWidget(WidgetInfo.PRAYER_RIGOUR);

		Point p = InputHandler.getClickPoint(RIGOUR.getBounds());

		if (p == null)
		{
			return;
		}

		if (plugin.getRigourVarbit() != 0)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		if (client.getRealSkillLevel(Skill.PRAYER) < 74)
		{
			return;
		}

		clickPrayer(client, config, p);
	}
}

class AuguryCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		Widget AUGURY = client.getWidget(WidgetInfo.PRAYER_AUGURY);

		Point p = InputHandler.getClickPoint(AUGURY.getBounds());

		if (p == null)
		{
			return;
		}

		if (plugin.getAuguryVarbit() != 0)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		if (client.getRealSkillLevel(Skill.PRAYER) < 77)
		{
			return;
		}

		clickPrayer(client, config, p);
	}
}

class PietyCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		Widget PIETY = client.getWidget(WidgetInfo.PRAYER_PIETY);

		Point p = InputHandler.getClickPoint(PIETY.getBounds());

		if (p == null)
		{
			return;
		}

		if (plugin.getPietyVarbit() != 0)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		if (client.getRealSkillLevel(Skill.PRAYER) < 70)
		{
			return;
		}

		clickPrayer(client, config, p);
	}
}

class IncredibleReflexesCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		Widget INCREDIBLE_REFLEXES = client.getWidget(WidgetInfo.PRAYER_INCREDIBLE_REFLEXES);

		Point p = InputHandler.getClickPoint(INCREDIBLE_REFLEXES.getBounds());

		if (p == null)
		{
			return;
		}

		if (plugin.getIncredibleReflexesVarbit() != 0)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		if (client.getRealSkillLevel(Skill.PRAYER) < 31)
		{
			return;
		}

		clickPrayer(client, config, p);
	}
}

class UltimateStrengthCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		Widget ULTIMATE_STRENGTH = client.getWidget(WidgetInfo.PRAYER_ULTIMATE_STRENGTH);

		Point p = InputHandler.getClickPoint(ULTIMATE_STRENGTH.getBounds());

		if (p == null)
		{
			return;
		}

		if (plugin.getUltimateStrengthVarbit() != 0)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		if (client.getRealSkillLevel(Skill.PRAYER) < 34)
		{
			return;
		}

		clickPrayer(client, config, p);
	}
}

class SteelSkinCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		Widget STEEL_SKIN = client.getWidget(WidgetInfo.PRAYER_STEEL_SKIN);

		Point p = InputHandler.getClickPoint(STEEL_SKIN.getBounds());

		if (p == null)
		{
			return;
		}

		if (plugin.getSteelSkinVarbit() != 0)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		if (client.getRealSkillLevel(Skill.PRAYER) < 28)
		{
			return;
		}

		clickPrayer(client, config, p);
	}
}

class EagleEyeCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		Widget EAGLE_EYE = client.getWidget(WidgetInfo.PRAYER_EAGLE_EYE);

		Point p = InputHandler.getClickPoint(EAGLE_EYE.getBounds());

		if (p == null)
		{
			return;
		}

		if (plugin.getEagleEyeVarbit() != 0)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		if (client.getRealSkillLevel(Skill.PRAYER) < 44)
		{
			return;
		}

		clickPrayer(client, config, p);
	}
}

class MysticMightCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		Widget MYSTIC_MIGHT = client.getWidget(WidgetInfo.PRAYER_MYSTIC_MIGHT);

		Point p = InputHandler.getClickPoint(MYSTIC_MIGHT.getBounds());

		if (p == null)
		{
			return;
		}

		if (plugin.getMysticMightVarbit() != 0)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		if (client.getRealSkillLevel(Skill.PRAYER) < 45)
		{
			return;
		}

		clickPrayer(client, config, p);
	}
}

class ProtectItemCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		Widget PROTECT_ITEM = client.getWidget(WidgetInfo.PRAYER_PROTECT_ITEM);

		Point p = InputHandler.getClickPoint(PROTECT_ITEM.getBounds());

		if (p == null)
		{
			return;
		}

		if (plugin.getProtectItemVarbit() != 0)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		if (client.getRealSkillLevel(Skill.PRAYER) < 21)
		{
			return;
		}

		clickPrayer(client, config, p);
	}
}

class ClickEnemyCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			Point lastEnemyLoc = PkToolsOverlay.lastEnemyLocation;

			if (lastEnemyLoc == null)
			{
				return;
			}

			int randx = (int) (Math.random() * 5 + 1);
			int randy = (int) (Math.random() * 5 + 1);
			
			InputHandler.leftClick(client, new Point(lastEnemyLoc.getX() + randx, lastEnemyLoc.getY() + randy));
		}
		catch (Exception e)
		{
			//swallow
		}
	}
}

class FreezeCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{

		try
		{
			int boosted_level = client.getBoostedSkillLevel(Skill.MAGIC);

			if (boosted_level < 82)
			{
				return;
			}
			else if (boosted_level < 94)
			{
				InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.SPELLBOOK_TAB_HOTKEY));

				Thread.sleep(config.clickDelay() * 2);

				Widget IceBlitz = client.getWidget(WidgetInfo.SPELL_ICE_BLITZ);

				Point p2 = InputHandler.getClickPoint(IceBlitz.getBounds());

				if (p2 == null)
				{
					return;
				}
				
				InputHandler.leftClick(client, p2);
			}
			else
			{
				InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.SPELLBOOK_TAB_HOTKEY));

				Thread.sleep(config.clickDelay() * 2);

				Widget IceBarrage = client.getWidget(WidgetInfo.SPELL_ICE_BARRAGE);

				Point p1 = InputHandler.getClickPoint(IceBarrage.getBounds());


				if (p1 == null)
				{
					return;
				}
				
				InputHandler.leftClick(client, p1);
			}
			
			InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.INVENTORY_TAB_HOTKEY));
		}
		catch (Exception e)
		{
			//swallow
		}
	}
}

class VengeanceCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{

		try
		{
			int boosted_level = client.getBoostedSkillLevel(Skill.MAGIC);

			if (boosted_level < 94)
			{
				return;
			}
			else
			{
				InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.SPELLBOOK_TAB_HOTKEY));
				Thread.sleep(config.clickDelay() * 2);

				Widget Vengeance = client.getWidget(WidgetInfo.SPELL_VENGEANCE);

				Point p1 = InputHandler.getClickPoint(Vengeance.getBounds());

				if (p1 == null)
				{
					return;
				}
				
				InputHandler.leftClick(client, p1);
			}
			
			InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.INVENTORY_TAB_HOTKEY));
		}
		catch (Exception e)
		{
			//swallow
		}
	}
}

class TeleBlockCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			int boosted_level = client.getBoostedSkillLevel(Skill.MAGIC);

			if (boosted_level < 85)
			{
				return;
			}
			else
			{
				InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.SPELLBOOK_TAB_HOTKEY));

				Thread.sleep(config.clickDelay() * 2);

				Widget TeleBlock = client.getWidget(WidgetInfo.SPELL_TELE_BLOCK);

				Point p1 = InputHandler.getClickPoint(TeleBlock.getBounds());

				if (p1 == null)
				{
					return;
				}
				
				InputHandler.leftClick(client, p1);
			}

			InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.INVENTORY_TAB_HOTKEY));
		}
		catch (Exception e)
		{
			//swallow
		}
	}
}

class EntangleCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			int boosted_level = client.getBoostedSkillLevel(Skill.MAGIC);

			if (boosted_level < 79)
			{
				return;
			}
			else
			{
				InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.SPELLBOOK_TAB_HOTKEY));

				Thread.sleep(config.clickDelay() * 2);

				Widget Entangle = client.getWidget(WidgetInfo.SPELL_ENTANGLE);

				Point p1 = InputHandler.getClickPoint(Entangle.getBounds());

				if (p1 == null)
				{
					return;
				}
				
				InputHandler.leftClick(client, p1);
			}

			InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.INVENTORY_TAB_HOTKEY));
		}
		catch (Exception e)
		{
			//swallow
		}
	}
}

class SpecCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			Thread.sleep(config.clickDelay());
			InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.COMBAT_TAB_HOTKEY));
			Widget SPECBAR = client.getWidget(WidgetInfo.COMBAT_TOOLTIP);
			if (overlay.getSpecCheck() == 0)
			{
				Instant SPECTIMER = Instant.now();
				do
				{
					Thread.sleep(config.clickDelay());
				} while (overlay.getSpecCheck() == 0 && Duration.between(SPECTIMER, Instant.now()).getSeconds() < 1);
			}
			if (overlay.getSpecCheck() == 1)
			{
				Thread.sleep(config.clickDelay() * 2);
				Point p = InputHandler.getClickPoint(SPECBAR.getBounds());
				if (p == null)
				{
					return;
				}
				InputHandler.leftClick(client, p);
				Thread.sleep(config.clickDelay());
				InputHandler.sendKey(client.getCanvas(), KeyEvent.VK_ESCAPE);
				overlay.setSpecCheck(0);
			}
		}
		catch (Exception e)
		{
			//throw
		}
	}
}

class DoubleSpecCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			Thread.sleep(config.clickDelay());
			InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.COMBAT_TAB_HOTKEY));
			Widget SPECBAR = client.getWidget(WidgetInfo.COMBAT_TOOLTIP);
			if (overlay.getSpecCheck() == 0)
			{
				Instant SPECTIMER = Instant.now();
				do
				{
					Thread.sleep(config.clickDelay());
				} while (overlay.getSpecCheck() == 0 && Duration.between(SPECTIMER, Instant.now()).getSeconds() < 1);
			}
			if (overlay.getSpecCheck() == 1)
			{
				Thread.sleep(config.clickDelay() * 2);
				Point p = InputHandler.getClickPoint(SPECBAR.getBounds());
				if (p == null)
				{
					return;
				}
				InputHandler.leftClick(client, p);
				Thread.sleep(config.clickDelay());
				InputHandler.leftClick(client, p);
				Thread.sleep(config.clickDelay());
				InputHandler.sendKey(client.getCanvas(), KeyEvent.VK_ESCAPE);
				overlay.setSpecCheck(0);
			}
		}
		catch (Exception e)
		{
			//throw
		}
	}
}

class Group1Command implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

			if (inventory == null)
			{
				return;
			}

			InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.INVENTORY_TAB_HOTKEY));

			Thread.sleep(config.clickDelay());

			for (WidgetItem item : inventory.getWidgetItems())
			{
				String group = getTag(configManager, item.getId());

				if ("Group 1".equalsIgnoreCase(group))
				{
					Point p = InputHandler.getClickPoint(item.getCanvasBounds());
					if (p == null)
					{
						return;
					}
					InputHandler.leftClick(client, p);
					Thread.sleep(config.clickDelay());
				}
			}
		}
		catch (Throwable e)
		{
			//ignored
		}
	}
}

class Group2Command implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

			if (inventory == null)
			{
				return;
			}

			InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.INVENTORY_TAB_HOTKEY));

			Thread.sleep(config.clickDelay());

			for (WidgetItem item : inventory.getWidgetItems())
			{
				String group = getTag(configManager, item.getId());

				if ("Group 2".equalsIgnoreCase(group))
				{
					Point p = InputHandler.getClickPoint(item.getCanvasBounds());
					if (p == null)
					{
						return;
					}
					InputHandler.leftClick(client, p);
					Thread.sleep(config.clickDelay());
				}
			}
		}
		catch (Throwable e)
		{
			//ignored
		}
	}
}

class Group3Command implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

			if (inventory == null)
			{
				return;
			}

			InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.INVENTORY_TAB_HOTKEY));

			Thread.sleep(config.clickDelay());

			for (WidgetItem item : inventory.getWidgetItems())
			{
				String group = getTag(configManager, item.getId());

				if ("Group 3".equalsIgnoreCase(group))
				{
					Point p = InputHandler.getClickPoint(item.getCanvasBounds());
					if (p == null)
					{
						return;
					}
					InputHandler.leftClick(client, p);
					Thread.sleep(config.clickDelay());
				}
			}
		}
		catch (Throwable e)
		{
			//ignored
		}
	}
}

class Group4Command implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

			if (inventory == null)
			{
				return;
			}

			InputHandler.sendKey(client.getCanvas(), InputHandler.getTabHotkey(client, Varbits.INVENTORY_TAB_HOTKEY));

			Thread.sleep(config.clickDelay());

			for (WidgetItem item : inventory.getWidgetItems())
			{
				String group = getTag(configManager, item.getId());

				if ("Group 4".equalsIgnoreCase(group))
				{
					Point p = InputHandler.getClickPoint(item.getCanvasBounds());
					if (p == null)
					{
						return;
					}
					InputHandler.leftClick(client, p);
					Thread.sleep(config.clickDelay());
				}
			}
		}
		catch (Throwable e)
		{
			//ignored
		}
	}
}

class WaitCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			Thread.sleep(config.clickDelay());
		}
		catch (Exception ignored)
		{
			//swallow
		}
	}
}

class ExceptionCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		System.out.println("Command could not be read.");
	}
}