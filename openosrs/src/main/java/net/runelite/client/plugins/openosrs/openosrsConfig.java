package net.runelite.client.plugins.openosrs;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Units;
import net.runelite.client.config.Range;
import net.runelite.client.plugins.OPRSExternalPluginManager;

@ConfigGroup("openosrs")
public interface openosrsConfig extends Config
{
	@Getter(AccessLevel.PUBLIC)
	@AllArgsConstructor
	enum SortStyle
	{
		CATEGORY("Category"),
		ALPHABETICALLY("Alphabetically"),
		REPOSITORY("Repository");

		private String name;

		@Override
		public String toString()
		{
			return getName();
		}
	}

	@Getter(AccessLevel.PUBLIC)
	@AllArgsConstructor
	enum BootStrap
	{
		NIGHTLY("NIGHTLY"),
		STABLE("STABLE");

		private String name;

		@Override
		public String toString()
		{
			return getName();
		}
	}

	@ConfigItem(
			position = 3,
			keyName = "shareLogs",
			name = "Share anonymous error data",
			description = "Share anonymous error data with the OpenOSRS developers"
	)
	default boolean shareLogs()
	{
		return true;
	}

	@ConfigItem(
			keyName = "enableOpacity",
			name = "Enable opacity",
			description = "Enables opacity for the whole window.<br>NOTE: This only stays enabled if your pc supports this!",
			position = 18,
			hidden = true
	)
	default boolean enableOpacity()
	{
		return false;
	}

	@Range(
			min = 15,
			max = 100
	)
	@ConfigItem(
			keyName = "opacityPercentage",
			name = "Opacity percentage",
			description = "Changes the opacity of the window if opacity is enabled",
			position = 19,
			hidden = true
	)
	@Units(Units.PERCENT)
	default int opacityPercentage()
	{
		return 100;
	}

	@ConfigItem(
			keyName = "localSync",
			name = "Sync local instances",
			description = "Enables multiple local instances of OpenOSRS to communicate (this enables syncing plugin state and config options)",
			position = 21
	)
	default boolean localSync()
	{
		return true;
	}

	@ConfigItem(
			keyName = "detachHotkey",
			name = "Detach Cam",
			description = "Detach Camera hotkey, press this and it will activate detached camera.",
			position = 22
	)
	default Keybind detachHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "askMode",
			name = "Ask Bootstrap",
			description = "Bootstrap Mode.",
			position = 23
	)
	default boolean askMode()
	{
		return true;
	}

	@ConfigItem(
			keyName = "bootstrapMode",
			name = "Bootstrap Mode",
			description = "Bootstrap Mode.",
			position = 24,
			hidden = false,
			hide = "askMode"
	)
	default BootStrap bootStrapMode()
	{
		return BootStrap.NIGHTLY;
	}

	@ConfigItem(
			keyName = "externalRepos",
			name = "",
			description = "",
			hidden = true
	)
	default String getExternalRepositories()
	{
		return OPRSExternalPluginManager.DEFAULT_PLUGIN_REPOS;
	}

	@ConfigItem(
			keyName = "externalRepos",
			name = "",
			description = "",
			hidden = true
	)
	void setExternalRepositories(String val);

	@ConfigItem(
			keyName = "warning",
			name = "",
			description = "",
			hidden = true
	)
	default boolean warning()
	{
		return true;
	}
}
