package net.runelite.client.plugins.pktools.ScriptCommand;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.pktools.PkToolsConfig;
import net.runelite.client.plugins.pktools.PkToolsOverlay;
import net.runelite.client.plugins.pktools.PkToolsPlugin;

import java.awt.*;
import java.awt.event.MouseEvent;

import static net.runelite.client.plugins.pktools.PkToolsHotkeyListener.getTag;

public interface ScriptCommand
{
	void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager);

	default void clickPrayer(WidgetInfo widgetInfo, Client client, PkToolsPlugin plugin)
	{
		try
		{
			Widget prayer_widget = client.getWidget(widgetInfo);

			if (prayer_widget == null)
			{
				return;
			}

			if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
			{
				return;
			}

			plugin.entry = new MenuEntry("Activate", prayer_widget.getName(), 1, MenuOpcode.CC_OP.getId(), prayer_widget.getItemId(), prayer_widget.getId(), false);
			click(client);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	default void clickSpell(WidgetInfo widgetInfo, Client client, PkToolsPlugin plugin)
	{
		try
		{
			Widget spell_widget = client.getWidget(widgetInfo);

			if (spell_widget == null)
			{
				return;
			}

			plugin.entry = new MenuEntry(spell_widget.getTargetVerb(), spell_widget.getName(), 0, MenuOpcode.WIDGET_TYPE_2.getId(), spell_widget.getItemId(), spell_widget.getId(), false);
			click(client);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	default void click(Client client)
	{
		Point pos = client.getMouseCanvasPosition();

		if (client.isStretchedEnabled())
		{
			final Dimension stretched = client.getStretchedDimensions();
			final Dimension real = client.getRealDimensions();
			final double width = (stretched.width / real.getWidth());
			final double height = (stretched.height / real.getHeight());
			final Point point = new Point((int) (pos.getX() * width), (int) (pos.getY() * height));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			return;
		}

		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
	}
}

class RigourCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		if (plugin.getRigourVarbit() != 0 || client.getRealSkillLevel(Skill.PRAYER) < 74)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_RIGOUR, client, plugin);
	}
}

class AuguryCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		if (plugin.getAuguryVarbit() != 0 || client.getRealSkillLevel(Skill.PRAYER) < 77)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_AUGURY, client, plugin);
	}
}

class PietyCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		if (plugin.getPietyVarbit() != 0 || client.getRealSkillLevel(Skill.PRAYER) < 70)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_PIETY, client, plugin);
	}
}

class IncredibleReflexesCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		if (plugin.getIncredibleReflexesVarbit() != 0 || client.getRealSkillLevel(Skill.PRAYER) < 31)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_INCREDIBLE_REFLEXES, client, plugin);
	}
}

class UltimateStrengthCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		if (plugin.getUltimateStrengthVarbit() != 0 || client.getRealSkillLevel(Skill.PRAYER) < 34)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_ULTIMATE_STRENGTH, client, plugin);
	}
}

class SteelSkinCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		if (plugin.getSteelSkinVarbit() != 0 || client.getRealSkillLevel(Skill.PRAYER) < 28)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_STEEL_SKIN, client, plugin);
	}
}

class EagleEyeCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		if (plugin.getEagleEyeVarbit() != 0 || client.getRealSkillLevel(Skill.PRAYER) < 44)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_EAGLE_EYE, client, plugin);
	}
}

class MysticMightCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		if (plugin.getMysticMightVarbit() != 0 || client.getRealSkillLevel(Skill.PRAYER) < 45)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_MYSTIC_MIGHT, client, plugin);
	}
}

class ProtectItemCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		if (plugin.getProtectItemVarbit() != 0 || client.getRealSkillLevel(Skill.PRAYER) < 21)
		{
			return;
		}

		clickPrayer(WidgetInfo.PRAYER_PROTECT_ITEM, client, plugin);
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

			//InputHandler.leftClick(client, new Point(lastEnemyLoc.getX() + randx, lastEnemyLoc.getY() + randy));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
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
				clickSpell(WidgetInfo.SPELL_ICE_BLITZ, client, plugin);
			}
			else
			{
				clickSpell(WidgetInfo.SPELL_ICE_BARRAGE, client, plugin);
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class VengeanceCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			if (client.getBoostedSkillLevel(Skill.MAGIC) < 94)
			{
				return;
			}

			clickSpell(WidgetInfo.SPELL_VENGEANCE, client, plugin);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class TeleBlockCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			if (client.getBoostedSkillLevel(Skill.MAGIC) < 85)
			{
				return;
			}

			clickSpell(WidgetInfo.SPELL_TELE_BLOCK, client, plugin);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class EntangleCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			if (client.getBoostedSkillLevel(Skill.MAGIC) < 79)
			{
				return;
			}

			clickSpell(WidgetInfo.SPELL_ENTANGLE, client, plugin);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class SpecCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			boolean spec_enabled = (client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1);

			if (spec_enabled)
			{
				return;
			}

			plugin.entry = new MenuEntry("Use <col=00ff00>Special Attack</col>", "", 1, MenuOpcode.CC_OP.getId(), -1, 38862884, false);
			click(client);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

class DoubleSpecCommand implements ScriptCommand
{
	public void execute(Client client, PkToolsConfig config, PkToolsPlugin plugin, PkToolsOverlay overlay, ConfigManager configManager)
	{
		try
		{
			boolean spec_enabled = (client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1);

			if (spec_enabled)
			{
				return;
			}

			plugin.entry = new MenuEntry("Use <col=00ff00>Special Attack</col>", "", 1, MenuOpcode.CC_OP.getId(), -1, 38862884, false);
			click(client);

			Thread.sleep(config.clickDelay());

			plugin.entry = new MenuEntry("Use <col=00ff00>Special Attack</col>", "", 1, MenuOpcode.CC_OP.getId(), -1, 38862884, false);
			click(client);

			Thread.sleep(config.clickDelay());
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
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

			for (WidgetItem item : inventory.getWidgetItems())
			{
				if ("Group 1".equalsIgnoreCase(getTag(configManager, item.getId())))
				{
					plugin.entry = new MenuEntry("Wield", "<col=ff9040>" + plugin.itemManager.getItemDefinition(item.getId()).getName(), item.getId(), MenuOpcode.ITEM_SECOND_OPTION.getId(), item.getIndex(), 9764864, false);
					click(client);
					Thread.sleep(config.clickDelay());
				}
			}
		}
		catch (Throwable e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
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

			for (WidgetItem item : inventory.getWidgetItems())
			{
				if ("Group 2".equalsIgnoreCase(getTag(configManager, item.getId())))
				{
					plugin.entry = new MenuEntry("Wield", "<col=ff9040>" + plugin.itemManager.getItemDefinition(item.getId()).getName(), item.getId(), MenuOpcode.ITEM_SECOND_OPTION.getId(), item.getIndex(), 9764864, false);
					click(client);
					Thread.sleep(config.clickDelay());
				}
			}
		}
		catch (Throwable e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
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

			for (WidgetItem item : inventory.getWidgetItems())
			{
				if ("Group 3".equalsIgnoreCase(getTag(configManager, item.getId())))
				{
					plugin.entry = new MenuEntry("Wield", "<col=ff9040>" + plugin.itemManager.getItemDefinition(item.getId()).getName(), item.getId(), MenuOpcode.ITEM_SECOND_OPTION.getId(), item.getIndex(), 9764864, false);
					click(client);
					Thread.sleep(config.clickDelay());
				}
			}
		}
		catch (Throwable e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
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

			for (WidgetItem item : inventory.getWidgetItems())
			{
				if ("Group 4".equalsIgnoreCase(getTag(configManager, item.getId())))
				{
					plugin.entry = new MenuEntry("Wield", "<col=ff9040>" + plugin.itemManager.getItemDefinition(item.getId()).getName(), item.getId(), MenuOpcode.ITEM_SECOND_OPTION.getId(), item.getIndex(), 9764864, false);
					click(client);
					Thread.sleep(config.clickDelay());
				}
			}
		}
		catch (Throwable e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
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
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
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