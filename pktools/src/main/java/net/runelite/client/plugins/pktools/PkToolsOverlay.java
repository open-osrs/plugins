package net.runelite.client.plugins.pktools;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.kit.KitType;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

@Singleton
public class PkToolsOverlay extends Overlay
{
	private final Client client;
	private final PkToolsPlugin pkToolsPlugin;
	private final PkToolsConfig config;
	private final SpriteManager spriteManager;
	private final PanelComponent imagePanelComponent = new PanelComponent();

	private static final Color NOT_ACTIVATED_BACKGROUND_COLOR = new Color(150, 0, 0, 150);

	Dimension panel_size = new Dimension(27, 40);

	public static Point lastEnemyLocation;

	@Inject
	private PkToolsOverlay(Client client, PkToolsPlugin plugin, SpriteManager spriteManager, PkToolsConfig config)
	{
		this.client = client;
		this.pkToolsPlugin = plugin;
		this.spriteManager = spriteManager;
		this.config = config;

		this.setPosition(OverlayPosition.BOTTOM_RIGHT);
		this.setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (this.client.getGameState() != GameState.LOGGED_IN)
		{
			return null;
		}

		Player lastEnemy = pkToolsPlugin.lastEnemy;

		ImageComponent PROTECT_MELEE_IMG = new ImageComponent(spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MELEE, 0));
		ImageComponent PROTECT_MISSILES_IMG = new ImageComponent(spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MISSILES, 0));
		ImageComponent PROTECT_MAGIC_IMG = new ImageComponent(spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MAGIC, 0));

		imagePanelComponent.getChildren().clear();
		imagePanelComponent.getChildren().add(TitleComponent.builder().text("PK").color(config.autoPrayerSwitcherEnabled() ? Color.GREEN : Color.red).build());
		imagePanelComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
		imagePanelComponent.setPreferredSize(panel_size);

		boolean PROTECT_MELEE = client.getVar(Prayer.PROTECT_FROM_MELEE.getVarbit()) != 0;
		boolean PROTECT_RANGED = client.getVar(Prayer.PROTECT_FROM_MISSILES.getVarbit()) != 0;
		boolean PROTECT_MAGIC = client.getVar(Prayer.PROTECT_FROM_MAGIC.getVarbit()) != 0;

		if (lastEnemy == null)
		{
			PkToolsOverlay.lastEnemyLocation = null;
		}
		else
		{
			int WEAPON_INT = Objects.requireNonNull(lastEnemy.getPlayerAppearance()).getEquipmentId(KitType.WEAPON);

			if (WEAPON_INT > 0)
			{
				if (Arrays.stream(PkToolsOverlay.MELEE_LIST).anyMatch(x -> x == WEAPON_INT))
				{
					if (config.prayerHelper())
					{
						imagePanelComponent.getChildren().add(PROTECT_MELEE_IMG);

						if (!PROTECT_MELEE)
						{
							imagePanelComponent.setBackgroundColor(PkToolsOverlay.NOT_ACTIVATED_BACKGROUND_COLOR);
						}
					}
				}
				else if (Arrays.stream(PkToolsOverlay.RANGED_LIST).anyMatch(x -> x == WEAPON_INT))
				{
					if (config.prayerHelper())
					{
						imagePanelComponent.getChildren().add(PROTECT_MISSILES_IMG);

						if (!PROTECT_RANGED)
						{
							imagePanelComponent.setBackgroundColor(PkToolsOverlay.NOT_ACTIVATED_BACKGROUND_COLOR);
						}
					}
				}
				else if (Arrays.stream(PkToolsOverlay.MAGIC_LIST).anyMatch(x -> x == WEAPON_INT))
				{
					if (config.prayerHelper())
					{
						imagePanelComponent.getChildren().add(PROTECT_MAGIC_IMG);

						if (!PROTECT_MAGIC)
						{
							imagePanelComponent.setBackgroundColor(PkToolsOverlay.NOT_ACTIVATED_BACKGROUND_COLOR);
						}
					}
				}
			}
		}

		return imagePanelComponent.render(graphics);
	}

	static final Integer[] MELEE_LIST = {ItemID.DRAGON_SCIMITAR, ItemID.RUNE_SCIMITAR, ItemID.GRANITE_MAUL,
		ItemID.ABYSSAL_WHIP, ItemID.ABYSSAL_WHIP_4178, ItemID.ABYSSAL_WHIP_20405, ItemID.DORGESHUUN_CROSSBOW, ItemID.BANDOS_GODSWORD, ItemID.ARMADYL_GODSWORD,
		ItemID.VOLCANIC_ABYSSAL_WHIP, ItemID.FROZEN_ABYSSAL_WHIP, ItemID.BARRELCHEST_ANCHOR,
		ItemID.DRAGON_WARHAMMER, ItemID.ELDER_MAUL, ItemID.ABYSSAL_TENTACLE, ItemID.GHRAZI_RAPIER, ItemID.GHRAZI_RAPIER_23628,
		ItemID.GRANITE_MAUL_12848, ItemID.GRANITE_MAUL_20557, ItemID.GRANITE_MAUL_24225, ItemID.GRANITE_MAUL_24227,
		ItemID.DRAGON_CLAWS_20784, ItemID.ARMADYL_GODSWORD_20593, ItemID.ARMADYL_GODSWORD_22665,
		ItemID.DRAGON_DAGGERP_5698, ItemID.DHAROKS_GREATAXE_100, ItemID.DHAROKS_GREATAXE_75,
		ItemID.DHAROKS_GREATAXE_50, ItemID.DHAROKS_GREATAXE_25, ItemID.DRAGON_CLAWS, ItemID.DRAGON_SCIMITAR_OR,
		ItemID.TZHAARKETOM, ItemID.VERACS_FLAIL_100, ItemID.VERACS_FLAIL_75, ItemID.VERACS_FLAIL_50,
		ItemID.VERACS_FLAIL_25, ItemID.ARCLIGHT, ItemID.WILDERNESS_SWORD_1, ItemID.ABYSSAL_DAGGER_P_13271,
		ItemID.DRAGON_DAGGER, ItemID.DRAGON_DAGGERP, ItemID.DRAGON_DAGGERP_5680, ItemID.DRAGON_DAGGER_20407,
		ItemID.DRAGON_LONGSWORD, ItemID.DRAGON_BATTLEAXE, ItemID.DRAGON_HALBERD, ItemID.DRAGON_2H_SWORD,
		ItemID.DRAGON_SCIMITAR_20406, ItemID.DRAGON_2H_SWORD_20559, ItemID.DRAGON_WARHAMMER_20785,
		ItemID.DRAGON_SWORD, ItemID.DRAGON_SWORD_21206,
		ItemID.BARRELCHEST_ANCHOR_10888,
		ItemID.ELDER_MAUL_21205, ItemID.TOKTZXILAK, ItemID.TOKTZXILEK, ItemID.TOKTZXILAK_20554,
		ItemID.LEAFBLADED_BATTLEAXE, ItemID.LEAFBLADED_SPEAR, ItemID.LEAFBLADED_SPEAR_4159, ItemID.LEAFBLADED_SWORD,
		ItemID.ZAMORAK_GODSWORD, ItemID.ZAMORAK_GODSWORD_OR, ItemID.SARADOMIN_GODSWORD,
		ItemID.SARADOMIN_GODSWORD_OR, ItemID.ARMADYL_GODSWORD_OR,
		ItemID.BANDOS_GODSWORD_20782, ItemID.BANDOS_GODSWORD_21060,
		ItemID.BANDOS_GODSWORD_OR, ItemID.SARACHNIS_CUDGEL, ItemID.SCYTHE_OF_VITUR, ItemID.SCYTHE_OF_VITUR_22664,
		ItemID.SCYTHE_OF_VITUR_UNCHARGED, ItemID.ZAMORAKIAN_SPEAR, ItemID.ZAMORAKIAN_HASTA, ItemID.VESTAS_LONGSWORD,
		ItemID.VESTAS_LONGSWORD_23615, ItemID.VESTAS_SPEAR, ItemID.STATIUSS_WARHAMMER,
		ItemID.STATIUSS_WARHAMMER_23620
	};

	static final Integer[] RANGED_LIST = {ItemID.RUNE_CROSSBOW, ItemID.RUNE_CROSSBOW_23601, ItemID.MAGIC_SHORTBOW_I, ItemID.ARMADYL_CROSSBOW,
		ItemID.ARMADYL_CROSSBOW_23611, ItemID.HEAVY_BALLISTA_23630,
		ItemID.TOXIC_BLOWPIPE, ItemID.DARK_BOW, ItemID.MAPLE_SHORTBOW, ItemID.LIGHT_BALLISTA, ItemID.HEAVY_BALLISTA,
		ItemID.MAGIC_SHORTBOW, ItemID.MAGIC_SHORTBOW_20558, ItemID.DRAGON_THROWNAXE, ItemID.DRAGON_HUNTER_CROSSBOW,
		ItemID.DRAGON_THROWNAXE_21207, ItemID.TOKTZXILUL, ItemID.NEW_CRYSTAL_BOW, ItemID.CRYSTAL_BOW_FULL,
		ItemID.CRYSTAL_BOW_910, ItemID.CRYSTAL_BOW_810, ItemID.CRYSTAL_BOW_710, ItemID.CRYSTAL_BOW_610,
		ItemID.CRYSTAL_BOW_510, ItemID.CRYSTAL_BOW_410, ItemID.CRYSTAL_BOW_310, ItemID.CRYSTAL_BOW_210,
		ItemID.CRYSTAL_BOW_110, ItemID.KARILS_CROSSBOW, ItemID.KARILS_CROSSBOW_100, ItemID.KARILS_CROSSBOW_75,
		ItemID.KARILS_CROSSBOW_50, ItemID.KARILS_CROSSBOW_25, ItemID.KARILS_CROSSBOW_0, ItemID.BRONZE_CROSSBOW,
		ItemID.BLURITE_CROSSBOW, ItemID.IRON_CROSSBOW, ItemID.STEEL_CROSSBOW, ItemID.MITH_CROSSBOW,
		ItemID.ADAMANT_CROSSBOW, ItemID.HUNTERS_CROSSBOW, ItemID._3RD_AGE_BOW, ItemID.DARK_BOW_12765,
		ItemID.DARK_BOW_12766, ItemID.DARK_BOW_12767, ItemID.DARK_BOW_12768, ItemID.DARK_BOW_20408,
		ItemID.TWISTED_BOW, ItemID.DRAGON_CROSSBOW, ItemID.CRAWS_BOW, ItemID.CRAWS_BOW_U, ItemID.DRAGON_KNIFE,
		ItemID.DRAGON_KNIFE_22812, ItemID.DRAGON_KNIFE_22814, ItemID.DRAGON_KNIFEP, ItemID.DRAGON_KNIFEP_22808,
		ItemID.DRAGON_KNIFEP_22810, ItemID.RUNE_THROWNAXE, ItemID.MORRIGANS_JAVELIN, ItemID.MORRIGANS_JAVELIN_23619,
		ItemID.MORRIGANS_THROWING_AXE
	};

	static final Integer[] MAGIC_LIST = {ItemID.TOXIC_STAFF_OF_THE_DEAD, ItemID.MYSTIC_SMOKE_STAFF,
		ItemID.STAFF_OF_THE_DEAD_23613, ItemID.KODAI_WAND_23626,
		ItemID.IBANS_STAFF_U, ItemID.ANCIENT_STAFF, ItemID.STAFF_OF_THE_DEAD,
		ItemID.TRIDENT_OF_THE_SEAS_FULL, ItemID.TRIDENT_OF_THE_SEAS,
		ItemID.TRIDENT_OF_THE_SEAS_E, ItemID.TRIDENT_OF_THE_SWAMP, ItemID.TRIDENT_OF_THE_SWAMP_E,
		ItemID.STAFF_OF_AIR, ItemID.MYSTIC_DUST_STAFF, ItemID.STAFF_OF_FIRE, ItemID.TOXIC_STAFF_UNCHARGED,
		ItemID.SARADOMIN_STAFF, ItemID.MYSTIC_WATER_STAFF, ItemID.STAFF_OF_LIGHT, ItemID.STAFF_OF_WATER,
		ItemID.WATER_BATTLESTAFF, ItemID.AHRIMS_STAFF, ItemID.STAFF_OF_EARTH, ItemID.MAGIC_STAFF,
		ItemID.BATTLESTAFF, ItemID.FIRE_BATTLESTAFF, ItemID.AIR_BATTLESTAFF, ItemID.EARTH_BATTLESTAFF,
		ItemID.MYSTIC_FIRE_STAFF, ItemID.MYSTIC_AIR_STAFF, ItemID.MYSTIC_EARTH_STAFF, ItemID.IBANS_STAFF,
		ItemID.GUTHIX_STAFF, ItemID.ZAMORAK_STAFF, ItemID.LAVA_BATTLESTAFF, ItemID.MYSTIC_LAVA_STAFF,
		ItemID.AHRIMS_STAFF_100, ItemID.AHRIMS_STAFF_75, ItemID.AHRIMS_STAFF_50, ItemID.AHRIMS_STAFF_25,
		ItemID.AHRIMS_STAFF_0, ItemID.AHRIMS_STAFF_23653, ItemID.MUD_BATTLESTAFF, ItemID.MYSTIC_MUD_STAFF, ItemID.WHITE_MAGIC_STAFF,
		ItemID.LUNAR_STAFF, ItemID.SMOKE_BATTLESTAFF, ItemID.STEAM_BATTLESTAFF_12795,
		ItemID.MYSTIC_STEAM_STAFF_12796, ItemID.ANCIENT_STAFF_20431, ItemID.MIST_BATTLESTAFF,
		ItemID.MYSTIC_MIST_STAFF, ItemID.DUST_BATTLESTAFF, ItemID.LAVA_BATTLESTAFF_21198,
		ItemID.MYSTIC_LAVA_STAFF_21200, ItemID.BEGINNER_WAND, ItemID.APPRENTICE_WAND, ItemID.TEACHER_WAND,
		ItemID.MASTER_WAND, ItemID.WAND, ItemID.INFUSED_WAND, ItemID._3RD_AGE_WAND, ItemID.BEGINNER_WAND_20553,
		ItemID.APPRENTICE_WAND_20556, ItemID.MASTER_WAND_20560, ItemID.KODAI_WAND, ItemID.TOKTZMEJTAL,
		ItemID.THAMMARONS_SCEPTRE, ItemID.THAMMARONS_SCEPTRE_U, ItemID.VOID_KNIGHT_MACE, ItemID.VOID_KNIGHT_MACE_L,
		ItemID.NIGHTMARE_STAFF, ItemID.ELDRITCH_NIGHTMARE_STAFF, ItemID.HARMONISED_NIGHTMARE_STAFF,
		ItemID.VOLATILE_NIGHTMARE_STAFF, ItemID.SLAYERS_STAFF, ItemID.SLAYERS_STAFF_E, ItemID.IVANDIS_FLAIL,
		ItemID.DAWNBRINGER, ItemID.ROD_OF_IVANDIS_1, ItemID.ROD_OF_IVANDIS_2, ItemID.ROD_OF_IVANDIS_3,
		ItemID.ROD_OF_IVANDIS_4, ItemID.ROD_OF_IVANDIS_5, ItemID.ROD_OF_IVANDIS_6, ItemID.ROD_OF_IVANDIS_7,
		ItemID.ROD_OF_IVANDIS_8, ItemID.ROD_OF_IVANDIS_9, ItemID.ROD_OF_IVANDIS_10, ItemID.SKULL_SCEPTRE,
		ItemID.SKULL_SCEPTRE_I, ItemID.DRAMEN_STAFF, ItemID.PHARAOHS_SCEPTRE, ItemID.PHARAOHS_SCEPTRE_1,
		ItemID.PHARAOHS_SCEPTRE_2, ItemID.PHARAOHS_SCEPTRE_3, ItemID.PHARAOHS_SCEPTRE_4, ItemID.PHARAOHS_SCEPTRE_5,
		ItemID.PHARAOHS_SCEPTRE_6, ItemID.PHARAOHS_SCEPTRE_7, ItemID.PHARAOHS_SCEPTRE_8, ItemID.SANGUINESTI_STAFF,
		ItemID.SANGUINESTI_STAFF_UNCHARGED, ItemID.STAFF_OF_BALANCE, ItemID.ZURIELS_STAFF,
		ItemID.ZURIELS_STAFF_23617
	};
}