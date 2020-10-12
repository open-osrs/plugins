/*
 * Copyright (c) 2017, Tyler <https://github.com/tylerthardy>
 * Copyright (c) 2018, Shaun Dreclin <shaundreclin@gmail.com>
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
package net.runelite.client.plugins.slayer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter(AccessLevel.PACKAGE)
enum Task
{

	/*
	 * format for enum is that the name of the task is first
	 * second is the item id image we use to represent the task graphically
	 * third is the list of names of monsters that can be killed on task (note that he task name is already handled
	 * so that is why for a task like ankou there is no need to include this list)
	 * fourth is the list of ids of monsters that can be killed on task (main reason for this is weird cases like baby dragons and the elf mourner)
	 */

	//<editor-fold desc="Enums">
	ABERRANT_SPECTRES("Aberrant spectres", ItemID.ABERRANT_SPECTRE,
		asList("Abhorrent spectre", "Deviant spectre", "Repugnant spectre"), emptyList()),
	ABYSSAL_DEMONS("Abyssal demons", ItemID.ABYSSAL_DEMON,
		singletonList("Abyssal Sire"), emptyList()),
	ABYSSAL_SIRE("Abyssal Sire", ItemID.ABYSSAL_ORPHAN),
	ADAMANT_DRAGONS("Adamant dragons", ItemID.ADAMANT_DRAGON_MASK),
	ALCHEMICAL_HYDRA("Alchemical Hydra", ItemID.IKKLE_HYDRA),
	ANKOU("Ankou", ItemID.ANKOU_MASK),
	AVIANSIES("Aviansies", ItemID.ENSOULED_AVIANSIE_HEAD,
		singletonList("Kree'arra"), emptyList()),
	BANDITS("Bandits", ItemID.BANDIT),
	BANSHEES("Banshees", ItemID.BANSHEE),
	BARROWS_BROTHERS("Barrows Brothers", ItemID.KARILS_COIF,
		asList("Ahrim the blighted", "Dharok the wretched", "Guthan the infested", "Karil the tainted", "Torag the corrupted", "Verac the defiled"), emptyList()),
	BASILISKS("Basilisks", ItemID.BASILISK),
	BATS("Bats", ItemID.GIRAL_BAT_2),
	BEARS("Bears", ItemID.ENSOULED_BEAR_HEAD,
		singletonList("Callisto"), emptyList()),
	BIRDS("Birds", ItemID.FEATHER,
		asList("Chicken", "Rooster", "Terrorbird", "Seagull", "Chompy bird", "Jubbly bird", "Oomlie bird", "Vulture"), emptyList()),
	BLACK_DEMONS("Black demons", ItemID.BLACK_DEMON_MASK,
		asList("Demonic gorilla", "Balfrug kreeyath", "Skotizo"), emptyList()),
	BLACK_DRAGONS("Black dragons", ItemID.BLACK_DRAGON_MASK,
		singletonList("Baby black dragon"), emptyList()),
	BLACK_KNIGHTS("Black Knights", ItemID.BLACK_FULL_HELM,
		singletonList("Black Knight"), emptyList()),
	BLOODVELD("Bloodveld", ItemID.BLOODVELD),
	BLUE_DRAGONS("Blue dragons", ItemID.BLUE_DRAGON_MASK,
		asList("Vorkath", "Baby blue dragon"), emptyList()),
	BRINE_RATS("Brine rats", ItemID.BRINE_RAT),
	BRONZE_DRAGONS("Bronze dragons", ItemID.BRONZE_DRAGON_MASK),
	CALLISTO("Callisto", ItemID.CALLISTO_CUB),
	CATABLEPON("Catablepon", ItemID.LEFT_SKULL_HALF),
	CAVE_BUGS("Cave bugs", ItemID.SWAMP_CAVE_BUG),
	CAVE_CRAWLERS("Cave crawlers", ItemID.CAVE_CRAWLER,
		singletonList("Chasm crawler"), emptyList()),
	CAVE_HORRORS("Cave horrors", ItemID.CAVE_HORROR,
		singletonList("Cave abomination"), emptyList()),
	CAVE_KRAKEN("Cave kraken", ItemID.CAVE_KRAKEN,
		singletonList("Kraken"), emptyList()),
	CAVE_SLIMES("Cave slimes", ItemID.SWAMP_CAVE_SLIME),
	CERBERUS("Cerberus", ItemID.HELLPUPPY),
	CHAOS_DRUIDS("Chaos druids", ItemID.ELDER_CHAOS_HOOD),
	CHAOS_ELEMENTAL("Chaos Elemental", ItemID.PET_CHAOS_ELEMENTAL),
	CHAOS_FANATIC("Chaos Fanatic", ItemID.ANCIENT_STAFF),
	COCKATRICE("Cockatrice", ItemID.COCKATRICE,
		singletonList("Cockathrice"), emptyList()),
	COWS("Cows", ItemID.COW_MASK),
	CRAWLING_HANDS("Crawling hands", ItemID.CRAWLING_HAND,
		singletonList("Crushing hand"), emptyList()),
	CRAZY_ARCHAEOLOGIST("Crazy Archaeologist", ItemID.FEDORA),
	CROCODILES("Crocodiles", ItemID.SWAMP_LIZARD),
	DAGANNOTH("Dagannoth", ItemID.DAGANNOTH,
		asList("Dagannoth Rex", "Dagannoth Prime", "Dagannoth Supreme"), emptyList()),
	DAGANNOTH_KINGS("Dagannoth Kings", ItemID.PET_DAGANNOTH_PRIME,
		asList("Dagannoth Rex", "Dagannoth Prime", "Dagannoth Supreme"), emptyList()),
	DARK_BEASTS("Dark beasts", ItemID.DARK_BEAST,
		singletonList("Night beast"), emptyList()),
	DARK_WARRIORS("Dark warriors", ItemID.BLACK_MED_HELM),
	DERANGED_ARCHAEOLOGIST("Deranged Archaeologist", ItemID.ARCHAEOLOGISTS_DIARY),
	DOGS("Dogs", ItemID.GUARD_DOG,
		singletonList("Jackal"), emptyList()),
	DRAKES("Drakes", ItemID.DRAKE),
	DUST_DEVILS("Dust devils", ItemID.DUST_DEVIL,
		singletonList("Choke devil"), emptyList()),
	DWARVES("Dwarves", ItemID.DWARVEN_HELMET,
		asList("Dwarf", "Black guard"), emptyList()),
	EARTH_WARRIORS("Earth warriors", ItemID.BRONZE_FULL_HELM_T),
	ELVES("Elves", ItemID.ELF,
		asList("Elf", "Iorwerth Warrior"), emptyList()),
	ENTS("Ents", ItemID.ENTS_ROOTS),
	FEVER_SPIDERS("Fever spiders", ItemID.FEVER_SPIDER),
	FIRE_GIANTS("Fire giants", ItemID.FIRE_BATTLESTAFF),
	FLESH_CRAWLERS("Flesh crawlers", ItemID.ENSOULED_SCORPION_HEAD),
	FOSSIL_ISLAND_WYVERNS("Fossil island wyverns", ItemID.FOSSIL_ISLAND_WYVERN,
		asList("Ancient wyvern", "Long-tailed wyvern", "Spitting wyvern", "Taloned wyvern"), emptyList()),
	GARGOYLES("Gargoyles", ItemID.GARGOYLE,
		asList("Dusk", "Dawn"), emptyList(), 9, ItemID.ROCK_HAMMER),
	GENERAL_GRAARDOR("General Graardor", ItemID.PET_GENERAL_GRAARDOR),
	GHOSTS("Ghosts", ItemID.GHOSTSPEAK_AMULET,
		singletonList("Tortured soul"), emptyList()),
	GHOULS("Ghouls", ItemID.ZOMBIE_HEAD),
	GIANT_MOLE("Giant Mole", ItemID.BABY_MOLE),
	GOBLINS("Goblins", ItemID.ENSOULED_GOBLIN_HEAD),
	GREATER_DEMONS("Greater demons", ItemID.GREATER_DEMON_MASK,
		asList("K'ril Tsutsaroth", "Tstanon Karlak", "Skotizo"), emptyList()),
	GREEN_DRAGONS("Green dragons", ItemID.GREEN_DRAGON_MASK,
		singletonList("Baby green dragon"), emptyList()),
	GROTESQUE_GUARDIANS("Grotesque Guardians", ItemID.MIDNIGHT,
		asList("Dusk", "Dawn"), emptyList(), 0, ItemID.ROCK_HAMMER),
	HARPIE_BUG_SWARMS("Harpie bug swarms", ItemID.SWARM),
	HELLHOUNDS("Hellhounds", ItemID.HELLHOUND,
		singletonList("Cerberus"), emptyList()),
	HILL_GIANTS("Hill giants", ItemID.ENSOULED_GIANT_HEAD,
		singletonList("Cyclops"), emptyList()),
	HOBGOBLINS("Hobgoblins", ItemID.HOBGOBLIN_GUARD),
	HYDRAS("Hydras", ItemID.HYDRA),
	ICEFIENDS("Icefiends", ItemID.ICE_DIAMOND),
	ICE_GIANTS("Ice giants", ItemID.ICE_DIAMOND),
	ICE_WARRIORS("Ice warriors", ItemID.MITHRIL_FULL_HELM_T),
	INFERNAL_MAGES("Infernal mages", ItemID.INFERNAL_MAGE,
		singletonList("Malevolent mage"), emptyList()),
	IRON_DRAGONS("Iron dragons", ItemID.IRON_DRAGON_MASK),
	JAD("TzTok-Jad", ItemID.TZREKJAD, 25250),
	JELLIES("Jellies", ItemID.JELLY,
		singletonList("Jelly"), emptyList()),
	JUNGLE_HORROR("Jungle horrors", ItemID.ENSOULED_HORROR_HEAD),
	KALPHITE("Kalphite", ItemID.KALPHITE_SOLDIER),
	KALPHITE_QUEEN("Kalphite Queen", ItemID.KALPHITE_PRINCESS),
	KILLERWATTS("Killerwatts", ItemID.KILLERWATT),
	KING_BLACK_DRAGON("King Black Dragon", ItemID.PRINCE_BLACK_DRAGON),
	KRAKEN("Cave Kraken Boss", ItemID.PET_KRAKEN,
		singletonList("Kraken"), emptyList()),
	KREEARRA("Kree'arra", ItemID.PET_KREEARRA),
	KRIL_TSUTSAROTH("K'ril Tsutsaroth", ItemID.PET_KRIL_TSUTSAROTH),
	KURASK("Kurask", ItemID.KURASK),
	LAVA_DRAGONS("Lava Dragons", ItemID.LAVA_SCALE),
	LESSER_DEMONS("Lesser demons", ItemID.LESSER_DEMON_MASK),
	LIZARDMEN("Lizardmen", ItemID.LIZARDMAN_FANG,
		singletonList("Lizardman"), emptyList()),
	LIZARDS("Lizards", ItemID.DESERT_LIZARD,
		asList("Desert lizard", "Sulphur lizard", "Small lizard", "Lizard"), emptyList(), 4, ItemID.ICE_COOLER),
	MAGIC_AXES("Magic axes", ItemID.IRON_BATTLEAXE),
	MAMMOTHS("Mammoths", ItemID.ATTACKER_HORN,
		singletonList("Mammoth"), emptyList()),
	MINIONS_OF_SCABARAS("Minions of scabaras", ItemID.GOLDEN_SCARAB,
		asList("Scarab swarm", "Locust rider", "Scarab mage"), emptyList()),
	MINOTAURS("Minotaurs", ItemID.ENSOULED_MINOTAUR_HEAD),
	MITHRIL_DRAGONS("Mithril dragons", ItemID.MITHRIL_DRAGON_MASK),
	MOGRES("Mogres", ItemID.MOGRE),
	MOLANISKS("Molanisks", ItemID.MOLANISK),
	MONKEYS("Monkeys", ItemID.ENSOULED_MONKEY_HEAD),
	MOSS_GIANTS("Moss giants", ItemID.HILL_GIANT_CLUB,
		singletonList("Bryophyta"), emptyList()),
	MUTATED_ZYGOMITES("Mutated zygomites", ItemID.MUTATED_ZYGOMITE,
		singletonList("Zygomite"), emptyList(), 7, ItemID.FUNGICIDE_SPRAY_0),
	NECHRYAEL("Nechryael", ItemID.NECHRYAEL,
		singletonList("Nechryarch"), emptyList()),
	OGRES("Ogres", ItemID.ENSOULED_OGRE_HEAD,
		singletonList("Enclave guard"), emptyList()),
	OTHERWORLDLY_BEING("Otherworldly beings", ItemID.GHOSTLY_HOOD),
	PIRATES("Pirates", ItemID.PIRATE_HAT,
		singletonList("Pirate"), emptyList()),
	PYREFIENDS("Pyrefiends", ItemID.PYREFIEND,
		singletonList("Flaming pyrelord"), emptyList()),
	RATS("Rats", ItemID.RATS_TAIL),
	RED_DRAGONS("Red dragons", ItemID.BABY_RED_DRAGON,
		singletonList("Baby red dragon"), emptyList()),
	REVENANTS("Revenants", ItemID.REVENANT_ETHER,
		asList("Revenant imp", "Revenant goblin", "Revenant pyrefiend", "Revenant hobgoblin", "Revenant cyclops", "Revenant hellhound", "Revenant demon", "Revenant ork", "Revenant dark beast", "Revenant knight", "Revenant dragon"), emptyList()),
	ROCKSLUGS("Rockslugs", ItemID.ROCKSLUG, 4, ItemID.BAG_OF_SALT),
	ROGUES("Rogues", ItemID.ROGUE_MASK,
		singletonList("Rogue"), emptyList()),
	RUNE_DRAGONS("Rune dragons", ItemID.RUNE_DRAGON_MASK),
	SARACHNIS("Sarachnis", ItemID.SRARACHA),
	SCORPIA("Scorpia", ItemID.SCORPIAS_OFFSPRING),
	SCORPIONS("Scorpions", ItemID.ENSOULED_SCORPION_HEAD,
		singletonList("Scorpia"), emptyList()),
	SEA_SNAKES("Sea snakes", ItemID.SNAKE_CORPSE),
	SHADES("Shades", ItemID.SHADE_ROBE_TOP,
		asList("Loar Shadow", "Loar Shade", "Phrin Shadow", "Phrin Shade", "Riyl Shadow", "Riyl Shade", "Asyn Shadow", "Asyn Shade", "Fiyr Shadow", "Fiyr Shade"), emptyList()),
	SHADOW_WARRIORS("Shadow warriors", ItemID.BLACK_FULL_HELM),
	SKELETAL_WYVERNS("Skeletal wyverns", ItemID.SKELETAL_WYVERN),
	SKELETONS("Skeletons", ItemID.SKELETON_GUARD),
	SMOKE_DEVILS("Smoke devils", ItemID.SMOKE_DEVIL),
	SPIDERS("Spiders", ItemID.HUGE_SPIDER,
		singletonList("Venenatis"), emptyList()),
	SPIRITUAL_CREATURES("Spiritual creatures", ItemID.DRAGON_BOOTS,
		asList("Spiritual ranger", "Spiritual mage", "Spiritual warrior"), emptyList()),
	STEEL_DRAGONS("Steel dragons", ItemID.STEEL_DRAGON),
	SULPHUR_LIZARDS("Sulphur Lizards", ItemID.SULPHUR_LIZARD),
	SUQAHS("Suqahs", ItemID.SUQAH_TOOTH),
	TEMPLE_SPIDERS("Temple Spiders", ItemID.RED_SPIDERS_EGGS),
	TERROR_DOGS("Terror dogs", ItemID.TERROR_DOG),
	THERMONUCLEAR_SMOKE_DEVIL("Thermonuclear Smoke Devil", ItemID.PET_SMOKE_DEVIL),
	TROLLS("Trolls", ItemID.TROLL_GUARD),
	TUROTH("Turoth", ItemID.TUROTH),
	TZHAAR("Tzhaar", ItemID.ENSOULED_TZHAAR_HEAD,
		singletonList("Tz-"), emptyList(), false),
	UNDEAD_DRUIDS("Undead Druids", ItemID.MASK_OF_RANUL),
	VAMPIRES("Vampires", ItemID.STAKE,
		asList("Vampyre", "Vyrewatch", "Vampire"), emptyList()),
	VENENATIS("Venenatis", ItemID.VENENATIS_SPIDERLING),
	VETION("Vet'ion", ItemID.VETION_JR),
	VORKATH("Vorkath", ItemID.VORKI),
	WALL_BEASTS("Wall beasts", ItemID.SWAMP_WALLBEAST),
	WATERFIENDS("Waterfiends", ItemID.WATER_ORB),
	WEREWOLVES("Werewolves", ItemID.WOLFBANE,
		singletonList("Werewolf"), emptyList()),
	WOLVES("Wolves", ItemID.GREY_WOLF_FUR,
		singletonList("Wolf"), emptyList()),
	WYRMS("Wyrms", ItemID.WYRM),
	ZILYANA("Zilyana", ItemID.PET_ZILYANA),
	ZOMBIES("Zombies", ItemID.ZOMBIE_HEAD,
		singletonList("Undead"), emptyList()),
	ZUK("TzKal-Zuk", ItemID.TZREKZUK, 101890),
	ZULRAH("Zulrah", ItemID.PET_SNAKELING);
	//</editor-fold>

	private static final Map<String, Task> tasks;
	static final List<String> LOCATIONS = List.of(
		"", // no location is a valid location
		"Abyss",
		"Ancient Cavern",
		"Asgarnian Ice Dungeon",
		"Brimhaven Dungeon",
		"Brine Rat Cavern",
		"Catacombs of Kourend",
		"Chasm of Fire",
		"Clan Wars",
		"Death Plateau",
		"Evil Chicken's Lair",
		"Fossil Island",
		"Forthos Dungeon",
		"Fremennik Slayer Dungeon",
		"God Wars Dungeon",
		"Iorwerth Dungeon",
		"Jormungand's Prison",
		"Kalphite Lair",
		"Karuulm Slayer Dungeon",
		"Keldagrim",
		"Kraken Cove",
		"Lighthouse",
		"Lithkren Vault",
		"Lizardman Canyon",
		"Lizardman Settlement",
		"Meiyerditch Laboratories",
		"Molch",
		"Mount Quidamortem",
		"Mourner Tunnels",
		"Myths' Guild Dungeon",
		"Ogre Enclave",
		"Slayer Tower",
		"Smoke Devil Dungeon",
		"Smoke Dungeon",
		"Stronghold of Security",
		"Stronghold Slayer Dungeon",
		"task-only Kalphite Cave",
		"Taverley Dungeon",
		"Troll Stronghold",
		"Waterbirth Island",
		"Waterfall Dungeon",
		"Wilderness",
		"Witchaven Dungeon",
		"Zanaris"
	);

	private final String name;
	private final int itemSpriteId;

	private final List<String> targetNames;
	private final List<Integer> npcIds;
	private final boolean checkAsTokens;
	private final int weaknessThreshold;
	private final int weaknessItem;
	private final int expectedKillExp;

	static
	{
		ImmutableMap.Builder<String, Task> builder = new ImmutableMap.Builder<>();

		for (Task task : values())
		{
			builder.put(task.getName().toLowerCase(), task);
		}

		tasks = builder.build();
	}

	Task(String name, int itemSpriteId)
	{
		Preconditions.checkArgument(itemSpriteId >= 0);
		this.name = name;
		this.itemSpriteId = itemSpriteId;
		this.weaknessThreshold = -1;
		this.weaknessItem = -1;
		this.targetNames = new ArrayList<>();
		this.npcIds = new ArrayList<>();
		this.checkAsTokens = true;
		this.expectedKillExp = 0;
	}

	Task(String name, int itemSpriteId, int weaknessThreshold, int weaknessItem)
	{
		Preconditions.checkArgument(itemSpriteId >= 0);
		this.name = name;
		this.itemSpriteId = itemSpriteId;
		this.weaknessThreshold = weaknessThreshold;
		this.weaknessItem = weaknessItem;
		this.targetNames = new ArrayList<>();
		this.npcIds = new ArrayList<>();
		this.checkAsTokens = true;
		this.expectedKillExp = 0;
	}

	Task(String name, int itemSpriteId, boolean checkAsTokens)
	{
		Preconditions.checkArgument(itemSpriteId >= 0);
		this.name = name;
		this.itemSpriteId = itemSpriteId;
		this.weaknessThreshold = -1;
		this.weaknessItem = -1;
		this.targetNames = new ArrayList<>();
		this.npcIds = new ArrayList<>();
		this.checkAsTokens = checkAsTokens;
		this.expectedKillExp = 0;
	}

	Task(String name, int itemSpriteId, int expectedKillExp)
	{
		Preconditions.checkArgument(itemSpriteId >= 0);
		this.name = name;
		this.itemSpriteId = itemSpriteId;
		this.weaknessThreshold = -1;
		this.weaknessItem = -1;
		this.targetNames = new ArrayList<>();
		this.npcIds = new ArrayList<>();
		this.checkAsTokens = true;
		this.expectedKillExp = expectedKillExp;
	}

	Task(String name, int itemSpriteId, List<String> targetNames, List<Integer> npcIds)
	{
		Preconditions.checkArgument(itemSpriteId >= 0);
		this.name = name;
		this.itemSpriteId = itemSpriteId;
		this.weaknessThreshold = -1;
		this.weaknessItem = -1;
		this.targetNames = targetNames;
		this.npcIds = npcIds;
		this.checkAsTokens = true;
		this.expectedKillExp = 0;
	}

	Task(String name, int itemSpriteId, List<String> targetNames, List<Integer> npcIds, int weaknessThreshold, int weaknessItem)
	{
		Preconditions.checkArgument(itemSpriteId >= 0);
		this.name = name;
		this.itemSpriteId = itemSpriteId;
		this.weaknessThreshold = weaknessThreshold;
		this.weaknessItem = weaknessItem;
		this.targetNames = targetNames;
		this.npcIds = npcIds;
		this.checkAsTokens = true;
		this.expectedKillExp = 0;
	}

	Task(String name, int itemSpriteId, List<String> targetNames, List<Integer> npcIds, boolean checkAsTokens)
	{
		Preconditions.checkArgument(itemSpriteId >= 0);
		this.name = name;
		this.itemSpriteId = itemSpriteId;
		this.weaknessThreshold = -1;
		this.weaknessItem = -1;
		this.targetNames = targetNames;
		this.npcIds = npcIds;
		this.checkAsTokens = checkAsTokens;
		this.expectedKillExp = 0;
	}

	@Nullable
	static Task getTask(String taskName)
	{
		return tasks.get(taskName.toLowerCase());
	}
}
