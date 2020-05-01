package net.runelite.client.plugins.nmzhelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("nmzhelper")
public interface NMZHelperConfig extends Config {
    @ConfigItem(
            keyName = "absorptionThreshold",
            name = "Absorption Threshold",
            description = "The amount of points to drink absorptions at.",
            position = 1
    )
    default int absorptionThreshold() {
        return 300;
    }

    @ConfigItem(
            keyName = "autoRockCake",
            name = "Rock Cake",
            description = "Automatically use rock cake to 1 hp?",
            position = 2
    )
    default boolean autoRockCake() {
        return true;
    }
}
