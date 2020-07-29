package com.jagex.runescape377;

import com.jagex.runescape377.cache.Archive;
import com.jagex.runescape377.cache.Index;
import com.jagex.runescape377.cache.cfg.ChatCensor;
import com.jagex.runescape377.cache.cfg.Varbit;
import com.jagex.runescape377.cache.cfg.Varp;
import com.jagex.runescape377.cache.def.ActorDefinition;
import com.jagex.runescape377.cache.def.FloorDefinition;
import com.jagex.runescape377.cache.def.GameObjectDefinition;
import com.jagex.runescape377.cache.def.ItemDefinition;
import com.jagex.runescape377.cache.media.AnimationSequence;
import com.jagex.runescape377.cache.media.IdentityKit;
import com.jagex.runescape377.cache.media.ImageRGB;
import com.jagex.runescape377.cache.media.IndexedImage;
import com.jagex.runescape377.cache.media.SpotAnimation;
import com.jagex.runescape377.cache.media.TypeFace;
import com.jagex.runescape377.cache.media.Widget;
import com.jagex.runescape377.collection.Node;
import com.jagex.runescape377.config.Actions;
import com.jagex.runescape377.config.Configuration;
import static com.jagex.runescape377.config.Configuration.DEBUG_CONTEXT;
import static com.jagex.runescape377.config.Configuration.PASSWORD;
import static com.jagex.runescape377.config.Configuration.ROOFS_ENABLED;
import static com.jagex.runescape377.config.Configuration.USERNAME;
import static com.jagex.runescape377.config.Configuration.USE_STATIC_DETAILS;
import static com.jagex.runescape377.config.IncomingPacket.*;
import com.jagex.runescape377.config.MovementType;
import com.jagex.runescape377.media.Animation;
import com.jagex.runescape377.media.ProducingGraphicsBuffer;
import com.jagex.runescape377.media.Rasterizer;
import com.jagex.runescape377.media.Rasterizer3D;
import com.jagex.runescape377.media.renderable.GameAnimableObject;
import com.jagex.runescape377.media.renderable.GameObject;
import com.jagex.runescape377.media.renderable.Item;
import com.jagex.runescape377.media.renderable.Model;
import com.jagex.runescape377.media.renderable.Projectile;
import com.jagex.runescape377.media.renderable.Renderable;
import com.jagex.runescape377.media.renderable.actor.Actor;
import com.jagex.runescape377.media.renderable.actor.Npc;
import com.jagex.runescape377.media.renderable.actor.Player;
import com.jagex.runescape377.net.Buffer;
import com.jagex.runescape377.net.BufferedConnection;
import com.jagex.runescape377.net.ISAACCipher;
import com.jagex.runescape377.net.requester.OnDemandNode;
import com.jagex.runescape377.net.requester.OnDemandRequester;
import com.jagex.runescape377.scene.InteractiveObject;
import com.jagex.runescape377.scene.MapRegion;
import com.jagex.runescape377.scene.Scene;
import com.jagex.runescape377.scene.SpawnObjectNode;
import com.jagex.runescape377.scene.tile.FloorDecoration;
import com.jagex.runescape377.scene.tile.Wall;
import com.jagex.runescape377.scene.tile.WallDecoration;
import com.jagex.runescape377.scene.util.CollisionMap;
import com.jagex.runescape377.sound.SoundPlayer;
import com.jagex.runescape377.sound.SoundTrack;
import com.jagex.runescape377.util.ChatEncoder;
import com.jagex.runescape377.util.LinkedList;
import com.jagex.runescape377.util.MouseCapturer;
import com.jagex.runescape377.util.PacketConstants;
import com.jagex.runescape377.util.SignLink;
import com.jagex.runescape377.util.SkillConstants;
import com.jagex.runescape377.util.TextUtils;
import com.jagex.runescape377.world.GroundArray;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;

public class Game extends GameShell
{


	public static final int[][] playerColours = {
		{6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983, 54193},
		{8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003, 25239},
		{25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003},
		{4626, 11146, 6439, 12, 4758, 10270}, {4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574}};
	public static final int[] SKIN_COLOURS = {9104, 10275, 7595, 3610, 7975, 8526, 918, 38802, 24466, 10145, 58654,
		5027, 1457, 16565, 34991, 25486};
	public static Player localPlayer;
	public static int[] BITFIELD_MAX_VALUE;
	public static int pulseCycle;
	private static boolean fps;
	private static int anInt895;
	private static int[] SKILL_EXPERIENCE;
	private static boolean accountFlagged;
	private static int anInt978;
	private static int anInt1052;
	private static int anInt1082;
	private static int anInt1100;
	private static int anInt1139;
	private static int anInt1160;
	private static int anInt1165;
	private static int anInt1168;
	private static int anInt1235;
	private static int anInt1237;
	private static int drawCycle;
	private static String VALID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";

	static
	{
		SKILL_EXPERIENCE = new int[99];
		int value = 0;
		for (int level = 0; level < 99; level++)
		{
			int realLevel = level + 1;
			int expDiff = (int) ((double) realLevel + 300D * Math.pow(2D, (double) realLevel / 7D));
			value += expDiff;
			SKILL_EXPERIENCE[level] = value / 4;
		}

		BITFIELD_MAX_VALUE = new int[32];
		value = 2;
		for (int k = 0; k < 32; k++)
		{
			BITFIELD_MAX_VALUE[k] = value - 1;
			value += value;
		}

	}

	private final int[] soundVolume = new int[50];
	private final int[] objectTypes = {0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3};
	public int opcode;
	public int portOffset;
	public boolean lowMemory;
	public int openChatboxWidgetId = -1;
	public int[] widgetSettings = new int[2000];
	public int[] tabWidgetIds = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
	public int[] sound = new int[50];
	public int plane;
	public boolean loggedIn = false;
	public int openScreenWidgetId = -1;
	public boolean redrawTabArea = false;
	public Buffer buffer = Buffer.allocate(1);
	public Index[] stores = new Index[5];
	public boolean redrawChatbox = false;
	public Widget chatboxInterface = new Widget();
	public int currentTabId = 3;
	public OnDemandRequester onDemandRequester;
	public int world;
	public int itemSearchScroll;
	public boolean memberServer = true;
	public int inputType;
	int chatboxScroll;
	private int[] archiveHashes = new int[9];
	private byte[][] terrainData;
	private String reportedName = "";
	private int[] spokenPalette = {0xffff00, 0xff0000, 0x00ff00, 0x00ffff, 0xff00ff, 0xffffff};
	private int[] skillExperience = new int[SkillConstants.SKILL_COUNT];
	private int hintIconX;
	private int hintIconY;
	private int hintIconOffset;
	private int markerOffsetX;
	private int markerOffsetY;
	private String[] friendUsernames = new String[200];
	private int reconnectionAttempts;
	private int[] cameraAmplitude = new int[5];
	private int cameraOffsetX;
	private int cameraOffsetModifierX = 2;
	private int ignoresCount;
	private int[] mapCoordinates;
	private int[] terrainDataIds;
	private int[] objectDataIds;
	private int friendsCount;
	private int friendListStatus;
	private String lastItemSearchInput = "";
	private int itemSearchResultCount;
	private String[] itemSearchResultNames = new String[100];
	private int[] itemSearchResultIds = new int[100];
	private boolean messagePromptRaised = false;
	private int playerRights;
	private int packetSize;
	private int netCycle;
	private int netAliveCycle;
	private int idleLogout;
	private int anInt874;
	private int anInt875;
	private int anInt876;
	private int anInt877;
	private int anInt878;
	private int[][][] constructedMapPalette = new int[4][13][13];
	private IndexedImage imageRedstone1;
	private IndexedImage imageRedstone2;
	private IndexedImage imageRedstone3;
	private IndexedImage imageFlippedRedstone1;
	private IndexedImage imageFlippedRedstone2;
	private int[][] anIntArrayArray885 = new int[104][104];
	private int[][] tileRenderCount = new int[104][104];
	private int privateChatMode;
	private Archive titleArchive;
	private int chunkX;
	private int chunkY;
	private int[][][] intGroundArray;
	private int anInt893;
	private ImageRGB[] cursorCross = new ImageRGB[8];
	private ISAACCipher incomingRandom;
	private boolean useJaggrab;
	private long lastClickTime;
	private int lastOpcode;
	private int secondLastOpcode;
	private int thirdLastOpcode;
	private ProducingGraphicsBuffer aClass18_906;
	private ProducingGraphicsBuffer aClass18_907;
	private ProducingGraphicsBuffer aClass18_908;
	private ProducingGraphicsBuffer aClass18_909;
	private ProducingGraphicsBuffer aClass18_910;
	private ProducingGraphicsBuffer aClass18_911;
	private ProducingGraphicsBuffer aClass18_912;
	private ProducingGraphicsBuffer aClass18_913;
	private ProducingGraphicsBuffer aClass18_914;
	private int anInt915;
	private int cameraYawOffset;
	private int cameraYawModifier = 2;
	private int[] anIntArray920 = new int[151];
	private boolean[] customCameraActive = new boolean[5];
	private Buffer tempBuffer = Buffer.allocate(1);
	private long serverSeed;
	private int drawX = -1;
	private int drawY = -1;
	private int lastSoundType = -1;
	private String chatboxInputMessage = "";
	private int spokenCount;
	private int spokenMax = 50;
	private int[] spokenX = new int[spokenMax];
	private int[] spokenY = new int[spokenMax];
	private int[] spokenOffsetY = new int[spokenMax];
	private int[] spokenOffsetX = new int[spokenMax];
	private int[] spokenColour = new int[spokenMax];
	private int[] spokenEffect = new int[spokenMax];
	private int[] spokenCycle = new int[spokenMax];
	private String[] spoken = new String[spokenMax];
	private String inputInputMessage = "";
	private boolean drawTabIcons = false;
	private int tickDelta;
	private ImageRGB[] imageHeadIcons = new ImageRGB[32];
	private int bankInsertMode;
	private String statusLineOne = "";
	private String statusLineTwo = "";
	private int fullscreenWidgetChildId = -1;
	private int thisPlayerServerId = -1;
	private Buffer outBuffer = Buffer.allocate(1);
	private IndexedImage bottomChatBack;
	private IndexedImage tabBottomBack;
	private IndexedImage tabTopBack;
	private int maxPlayerCount = 2048;
	private int maxPlayerIndex = 2047;
	private Player[] players = new Player[maxPlayerCount];
	private int localPlayerCount;
	private int[] playerList = new int[maxPlayerCount];
	private int updatedPlayerCount;
	private int[] updatedPlayers = new int[maxPlayerCount];
	private Buffer[] cachedAppearances = new Buffer[maxPlayerCount];
	private IndexedImage[] tabIcon = new IndexedImage[13];
	private int loginScreenFocus;
	private int[] firstMenuOperand = new int[500];
	private int[] secondMenuOperand = new int[500];
	private int[] menuActionTypes = new int[500];
	private int[] selectedMenuActions = new int[500];
	private IndexedImage aClass50_Sub1_Sub1_Sub3_983;
	private IndexedImage aClass50_Sub1_Sub1_Sub3_984;
	private IndexedImage aClass50_Sub1_Sub1_Sub3_985;
	private IndexedImage aClass50_Sub1_Sub1_Sub3_986;
	private IndexedImage aClass50_Sub1_Sub1_Sub3_987;
	private int placementX;
	private int placementY;
	private int[] cameraFrequency = new int[5];
	private int membershipCreditRemaining;
	private int anInt993;
	private int anInt994;
	private int anInt995;
	private int anInt996;
	private int anInt997;
	private int showChatEffects;
	private int[] chatboxLineOffsets;
	private int[] sidebarOffsets;
	private int[] viewportOffsets;
	private int[] fullScreenTextureArray;
	private int anInt1004;
	private int[] anIntArray1005 = new int[2000];
	private int publicChatMode;
	private int cameraOffsetY;
	private int cameraOffsetModifierY = 2;
	private int lastClickX;
	private int lastClickY;
	private boolean rsAlreadyLoaded = false;
	private ImageRGB anImageRGB1226;
	private ImageRGB anImageRGB1227;
	private int[] anIntArray1019 = new int[151];
	private int crossX;
	private int crossY;
	private int crossIndex;
	private int crossType;
	private BufferedConnection gameConnection;
	private String chatMessage = "";
	private String aString1027;
	private int[] skillLevel = new int[SkillConstants.SKILL_COUNT];
	private int userWeight;
	private ImageRGB[] worldMapHintIcons = new ImageRGB[100];
	private int recoveryQuestionSetTime;
	private int currentSound;
	private ImageRGB mapFlagMarker;
	private ImageRGB aClass50_Sub1_Sub1_Sub1_1037;
	private boolean aBoolean1038 = true;
	private int nextTopLeftTileX;
	private int nextTopRightTileY;
	private int topLeftTileX;
	private int topLeftTileY;
	private int anInt1044;
	private int randomCycle2;
	private boolean welcomeScreenRaised = false;
	private int anInt1047;
	private int anInt1048;
	private int minimapState;
	private int fullscreenWidgetId = -1;
	private int[] skillMaxLevel = new int[SkillConstants.SKILL_COUNT];
	private int anInt1055 = 2;
	private int systemUpdateTime;
	private String clickToContinueString;
	private TypeFace fontSmall;
	private TypeFace fontNormal;
	private TypeFace fontBold;
	private TypeFace fontFancy;
	private int mouseInvInterfaceIndex;
	private int lastActiveInvInterface;
	private boolean menuOpen = false;
	private boolean aBoolean1067 = false;
	private int playerMembers;
	private String[] aStringArray1069 = new String[5];
	private boolean[] aBooleanArray1070 = new boolean[5];
	private int loadingStage;
	private long[] ignores = new long[100];
	private int minimapHintCount;
	private int[] minimapHintX = new int[1000];
	private int[] minimapHintY = new int[1000];
	private ImageRGB[] headiconsPrayer = new ImageRGB[32];
	private int anInt1080 = 0x4d4233;
	private int lastPasswordChangeTime;
	private int[] anIntArray1084;
	private int[] anIntArray1085;
	private ImageRGB aClass50_Sub1_Sub1_Sub1_1086;
	private CRC32 archiveCrc = new CRC32();
	private int tabAreaOverlayWidgetId = -1;
	private String username;
	private String password;
	private int anInt1094;
	private IndexedImage scrollbarUp;
	private IndexedImage scrollbarDown;
	private boolean genericLoadingError = false;
	private boolean reportMutePlayer = false;
	private int[] characterEditColors = new int[5];
	private int flameCycle;
	private ImageRGB aClass50_Sub1_Sub1_Sub1_1102;
	private ImageRGB aClass50_Sub1_Sub1_Sub1_1103;
	private String chatboxInput = "";
	private int[] cameraJitter = new int[5];
	private int anInt1106;
	private int chatboxScrollMax = 78;
	private ProducingGraphicsBuffer aClass18_1108;
	private ProducingGraphicsBuffer aClass18_1109;
	private ProducingGraphicsBuffer aClass18_1110;
	private int modifiedWidgetId;
	private int selectedInventorySlot;
	private int activeInterfaceType;
	private int anInt1114;
	private int anInt1115;
	private ImageRGB minimapCompass;
	private IndexedImage[] titleFlameEmblem;
	private int randomCycle1;
	private int anInt1119 = -30658;
	private int destinationX;
	private int destinationY;
	private ImageRGB minimapImage;
	private int[] anIntArray1123 = new int[4000];
	private int[] anIntArray1124 = new int[4000];
	private byte[][][] currentSceneTileFlags;
	private int anInt1126;
	private boolean aBoolean1127 = false;
	private int previousSong;
	private int anInt1129;
	private long[] friends = new long[200];
	private Buffer chatBuffer = new Buffer(new byte[5000]);
	private Npc[] npcs = new Npc[16384];
	private int npcCount;
	private int[] npcIds = new int[16384];
	private int renderCount;
	private long aLong1141;
	private ImageRGB[] moderatorIcon = new ImageRGB[2];
	private boolean characterEditChangeGenger = true;
	private int[] quakeTimes = new int[5];
	private int itemSelected;
	private int anInt1147;
	private int anInt1148;
	private int anInt1149;
	private String selectedItemName;
	private int otherPlayerId;
	private int anInt1152;
	private IndexedImage[] mapIcons = new IndexedImage[100];
	private boolean lastItemDragged = false;
	private ProducingGraphicsBuffer tabImageProducer;
	private ProducingGraphicsBuffer aClass18_1157;
	private ProducingGraphicsBuffer gameScreenImageProducer;
	private ProducingGraphicsBuffer chatboxProducingGraphicsBuffer;
	private byte aByte1161 = 97;
	private boolean loadGeneratedMap = false;
	private Scene currentScene;
	private int[] anIntArray1166 = new int[256];
	private int loginScreenUpdateTime;
	private int widgetSelected;
	private int anInt1172;
	private int selectedMask;
	private String selectedWidgetName;
	private int[] anIntArray1176;
	private int[] anIntArray1177;
	private int lastSoundPosition;
	private int[] anIntArray1180 = new int[33];
	private ImageRGB[] hitmarks = new ImageRGB[20];
	private int menuActionRow;
	private String[] menuActionTexts = new String[500];
	private IndexedImage inventoryBackgroundImage;
	private IndexedImage minimapBackgroundImage;
	private IndexedImage chatboxBackgroundImage;
	private int[][] cost = new int[104][104];
	private int dialogueId = -1;
	private ImageRGB mapdotItem;
	private ImageRGB mapdotActor;
	private ImageRGB mapdotPlayer;
	private ImageRGB mapdotFriend;
	private ImageRGB mapdotTeammate;
	private int headIconDrawType;
	private ProducingGraphicsBuffer aClass18_1198;
	private ProducingGraphicsBuffer aClass18_1199;
	private ProducingGraphicsBuffer aClass18_1200;
	private ProducingGraphicsBuffer flameLeftBackground;
	private ProducingGraphicsBuffer flameRightBackground;
	private ProducingGraphicsBuffer aClass18_1203;
	private ProducingGraphicsBuffer aClass18_1204;
	private ProducingGraphicsBuffer aClass18_1205;
	private ProducingGraphicsBuffer aClass18_1206;
	private boolean loadingMap = false;
	private LinkedList gameAnimableObjectQueue = new LinkedList();
	private boolean cutsceneActive = false;
	private boolean redrawChatMode = false;
	private int flashingTabId = -1;
	private int lastLoginTime;
	private int cameraX;
	private int cameraZ;
	private int cameraY;
	private int cameraVerticalRotation;
	private int cameraHorizontalRotation;
	private int friendsListAction;
	private int anInt1223;
	private Socket jaggrabSocket;
	private int loginScreenState;
	private int anInt1226;
	private int tradeMode;
	private long loadRegionTime;
	private int reportAbuseInterfaceID = -1;
	private byte[][] objectData;
	private int mapZoomOffset;
	private int mapZoomModifier = 1;
	private int anInt1238;
	private boolean aBoolean1239 = false;
	private int lastLoginAddress;
	private volatile boolean currentlyDrawingFlames = false;
	private byte[] aByteArray1245 = new byte[16384];
	private boolean inTutorialIsland;
	private ImageRGB minimapEdge;
	private MouseCapturer mouseCapturer;
	private long lastSoundTime;
	private int cameraVertical = 128;
	private int cameraHorizontal;
	private int cameraVelocityHorizontal;
	private int cameraVelocityVertical;
	private int cameraRandomisationA;
	private int cameraPitchModifier = 1;
	private int[] anIntArray1258 = new int[100];
	private int[] soundDelay = new int[50];
	private CollisionMap[] currentCollisionMap = new CollisionMap[4];
	private LinkedList spawnObjectList = new LinkedList();
	private int currentCameraPositionH;
	private int currentCameraPositionV;
	private int cameraMovedWriteDelay;
	private boolean cameraMovedWrite = false;
	private boolean musicEnabled = true;
	private int[] friendWorlds = new int[200];
	private int lastItemDragTime;
	private int nextSong;
	private boolean songChanging = true;
	private int lastSound = -1;
	private int unreadWebsiteMessages;
	private boolean windowFocused = true;
	private int lastRegionId = -1;
	private boolean characterModelChanged = false;
	private ImageRGB[] minimapHint = new ImageRGB[1000];
	private int walkableWidgetId = -1;
	private int anInt1280;
	private LinkedList projectileQueue = new LinkedList();
	private boolean loadingError = false;
	private int anInt1284;
	private int[] anIntArray1286 = new int[33];
	private ImageRGB[] headiconsPk = new ImageRGB[32];
	private int secondaryCameraVertical;
	private int[] anIntArray1290 = {17, 24, 34, 40};
	private IndexedImage titleboxImage;
	private IndexedImage titleboxButtonImage;
	private int enityUpdateCount;
	private int[] eneityUpdateIndices = new int[1000];
	private int[] chatTypes = new int[100];
	private String[] chatPlayerNames = new String[100];
	private String[] chatMessages = new String[100];
	private int duplicateClickCount;
	private int oneMouseButton;
	private boolean aBoolean1301 = true;
	private int anInt1302;
	private int anInt1303;
	private int menuScreenArea;
	private int menuOffsetX;
	private int menuOffsetY;
	private int menuWidth;
	private int menuHeight;
	private int[] anIntArray1310;
	private int[] anIntArray1311;
	private int[] anIntArray1312;
	private int[] anIntArray1313;
	private volatile boolean aBoolean1314 = false;
	private int anInt1315;
	private int anInt1319;
	private volatile boolean aBoolean1320 = false;
	private int[] soundType = new int[50];
	private int anInt1322;
	private GroundArray<LinkedList> groundItems = new GroundArray();
	private int runEnergy;
	private int[] characterEditIdentityKits = new int[7];
	private int currentSong = -1;
	private int atInventoryLoopCycle;
	private int anInt1330;
	private int anInt1331;
	private int atInventoryInterfaceType;

	private static String getCombatLevelColour(int user, int opponent)
	{
		int difference = user - opponent;
		if (difference < -9)
		{
			return "@red@";
		}
		if (difference < -6)
		{
			return "@or3@";
		}
		if (difference < -3)
		{
			return "@or2@";
		}
		if (difference < 0)
		{
			return "@or1@";
		}
		if (difference > 9)
		{
			return "@gre@";
		}
		if (difference > 6)
		{
			return "@gr3@";
		}
		if (difference > 3)
		{
			return "@gr2@";
		}
		if (difference > 0)
		{
			return "@gr1@";
		}
		else
		{
			return "@yel@";
		}
	}

	public static void main(String[] args)
	{
		try
		{
			System.out.println("RS2 user client - release #" + 377);
			Game game = new Game();
			game.world = 1;
			game.portOffset = 0;
			game.setHighMemory();
			game.memberServer = true;
			SignLink.storeId = 32;
			SignLink.initialize(InetAddress.getLocalHost());
			game.initializeApplication(765, 503);
		}
		catch (Exception exception)
		{
		}
	}

	private static String getFullAmountText(int amount)
	{
		String string = String.valueOf(amount);
		for (int index = string.length() - 3; index > 0; index -= 3)
		{
			string = string.substring(0, index) + "," + string.substring(index);
		}

		if (string.length() > 8)
		{
			string = "@gre@" + string.substring(0, string.length() - 8) + " million @whi@(" + string + ")";
		}
		else if (string.length() > 4)
		{
			string = "@cya@" + string.substring(0, string.length() - 4) + "K @whi@(" + string + ")";
		}
		return " " + string;
	}

	private static String getShortenedAmountText(int coins)
	{
		if (coins < 0x186a0)
		{
			return String.valueOf(coins);
		}
		if (coins < 0x989680)
		{
			return coins / 1000 + "K";
		}
		else
		{
			return coins / 0xf4240 + "M";
		}
	}

	public void setHighMemory()
	{
		Scene.lowMemory = false;
		Rasterizer3D.lowMemory = false;
		lowMemory = false;
		MapRegion.lowMemory = false;
		GameObjectDefinition.lowMemory = false;
	}

	private void setLowMemory()
	{
		Scene.lowMemory = true;
		Rasterizer3D.lowMemory = true;
		lowMemory = true;
		MapRegion.lowMemory = true;
		GameObjectDefinition.lowMemory = true;
	}

	private void addChatMessage(String name, String message, int type)
	{
		if (type == 0 && dialogueId != -1)
		{
			clickToContinueString = message;
			super.clickType = 0;
		}
		if (openChatboxWidgetId == -1)
		{
			redrawChatbox = true;
		}
		for (int index = 99; index > 0; index--)
		{
			chatTypes[index] = chatTypes[index - 1];
			chatPlayerNames[index] = chatPlayerNames[index - 1];
			chatMessages[index] = chatMessages[index - 1];
		}

		chatTypes[0] = type;
		chatPlayerNames[0] = name;
		chatMessages[0] = message;
	}

	private void addFriend(long name)
	{
		try
		{
			if (name == 0L)
			{
				return;
			}
			if (friendsCount >= 100 && playerMembers != 1)
			{
				addChatMessage("", "Your friendlist is full. Max of 100 for free users, and 200 for members", 0);
				return;
			}
			if (friendsCount >= 200)
			{
				addChatMessage("", "Your friendlist is full. Max of 100 for free users, and 200 for members", 0);
				return;
			}
			String username = TextUtils.formatName(TextUtils.longToName(name));
			for (int index = 0; index < friendsCount; index++)
			{
				if (friends[index] == name)
				{
					addChatMessage("", username + " is already on your friend list", 0);
					return;
				}
			}

			for (int index = 0; index < ignoresCount; index++)
			{
				if (ignores[index] == name)
				{
					addChatMessage("", "Please remove " + username + " from your ignore list first", 0);
					return;
				}
			}

			if (username.equals(localPlayer.playerName))
			{
				return;
			}
			friendUsernames[friendsCount] = username;
			friends[friendsCount] = name;
			friendWorlds[friendsCount] = 0;
			friendsCount++;
			redrawTabArea = true;
			outBuffer.putOpcode(120);
			outBuffer.putLongBE(name);
			return;
		}
		catch (RuntimeException runtimeexception)
		{
			SignLink.reportError("94629, " + name + ", " + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	private void addIgnore(long name)
	{
		try
		{
			if (name == 0L)
			{
				return;
			}
			if (ignoresCount >= 100)
			{
				addChatMessage("", "Your ignore list is full. Max of 100 hit", 0);
				return;
			}
			String username = TextUtils.formatName(TextUtils.longToName(name));
			for (int index = 0; index < ignoresCount; index++)
			{
				if (ignores[index] == name)
				{
					addChatMessage("", username + " is already on your ignore list", 0);
					return;
				}
			}

			for (int index = 0; index < friendsCount; index++)
			{
				if (friends[index] == name)
				{
					addChatMessage("", "Please remove " + username + " from your friend list first", 0);
					return;
				}
			}

			ignores[ignoresCount++] = name;
			redrawTabArea = true;
			outBuffer.putOpcode(217);
			outBuffer.putLongBE(name);
			return;
		}
		catch (RuntimeException runtimeexception)
		{
			SignLink.reportError("27939, " + -916 + ", " + name + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	private void adjustMidiVolume(boolean flag, byte byte0, int volume)
	{
		SignLink.midiVolume = volume;
		if (flag)
		{
			SignLink.midi = "voladjust";
		}
	}

	private void itemSearch(String input)
	{
		if (input == null || input.length() == 0)
		{
			itemSearchResultCount = 0;
			return;
		}
		String searchTerm = input;
		String[] splitString = new String[100];
		int j = 0;
		do
		{
			int spaceIndex = searchTerm.indexOf(" ");
			if (spaceIndex == -1)
			{
				break;
			}
			String first = searchTerm.substring(0, spaceIndex).trim();
			if (first.length() > 0)
			{
				splitString[j++] = first.toLowerCase();
			}
			searchTerm = searchTerm.substring(spaceIndex + 1);
		} while (true);
		searchTerm = searchTerm.trim();
		if (searchTerm.length() > 0)
		{
			splitString[j++] = searchTerm.toLowerCase();
		}
		itemSearchResultCount = 0;
		label0:
		for (int itemId = 0; itemId < ItemDefinition.count; itemId++)
		{
			ItemDefinition itemDefinition = ItemDefinition.lookup(itemId);
			if (itemDefinition.notedTemplateId != -1 || itemDefinition.name == null)
			{
				continue;
			}
			String itemName = itemDefinition.name.toLowerCase();
			for (int i1 = 0; i1 < j; i1++)
			{
				if (!itemName.contains(splitString[i1]))
				{
					continue label0;
				}
			}

			itemSearchResultNames[itemSearchResultCount] = itemDefinition.name;
			itemSearchResultIds[itemSearchResultCount] = itemId;

			itemSearchResultCount++;
			if (itemSearchResultCount >= itemSearchResultNames.length)
			{
				return;
			}
		}

	}

	private void closeWidgets()
	{
		outBuffer.putOpcode(110);
		if (tabAreaOverlayWidgetId != -1)
		{
			method44(tabAreaOverlayWidgetId);
			tabAreaOverlayWidgetId = -1;
			redrawTabArea = true;
			aBoolean1239 = false;
			drawTabIcons = true;
		}
		if (openChatboxWidgetId != -1)
		{
			method44(openChatboxWidgetId);
			openChatboxWidgetId = -1;
			redrawChatbox = true;
			aBoolean1239 = false;
		}
		if (fullscreenWidgetId != -1)
		{
			method44(fullscreenWidgetId);
			fullscreenWidgetId = -1;
			welcomeScreenRaised = true;
		}
		if (fullscreenWidgetChildId != -1)
		{
			method44(fullscreenWidgetChildId);
			fullscreenWidgetChildId = -1;
		}
		if (openScreenWidgetId != -1)
		{
			method44(openScreenWidgetId);
			openScreenWidgetId = -1;
		}
	}

	private void addNewPlayers(int size, Buffer buffer)
	{
		while (buffer.bitPosition + 10 < size * 8)
		{
			int id = buffer.getBits(11);

			if (id == 2047)
			{
				break;
			}

			if (players[id] == null)
			{
				players[id] = new Player();

				if (cachedAppearances[id] != null)
				{
					players[id].updateAppearance(cachedAppearances[id]);
				}
			}

			playerList[localPlayerCount++] = id;
			Player player = players[id];
			player.pulseCycle = pulseCycle;
			int x = buffer.getBits(5);

			if (x > 15)
			{
				x -= 32;
			}

			int updated = buffer.getBits(1);

			if (updated == 1)
			{
				updatedPlayers[updatedPlayerCount++] = id;
			}

			int discardQueue = buffer.getBits(1);
			int y = buffer.getBits(5);

			if (y > 15)
			{
				y -= 32;
			}

			player.setPosition(localPlayer.pathX[0] + x, localPlayer.pathY[0] + y, discardQueue == 1);
		}

		buffer.finishBitAccess();
	}

	private void processFlamesCycle()
	{
		aBoolean1320 = true;

		try
		{
			long startTime = System.currentTimeMillis();
			int cycle = 0;
			int interval = 20;

			while (currentlyDrawingFlames)
			{
				flameCycle++;

				calculateFlamePositions();
				calculateFlamePositions();
				renderFlames();

				if (++cycle > 10)
				{
					long currentTime = System.currentTimeMillis();
					int difference = (int) (currentTime - startTime) / 10 - interval;
					interval = 40 - difference;

					if (interval < 5)
					{
						interval = 5;
					}

					cycle = 0;
					startTime = currentTime;
				}
				try
				{
					Thread.sleep(interval);
				}
				catch (Exception ignored)
				{
				}
			}
		}
		catch (Exception ignored)
		{
		}

		aBoolean1320 = false;
	}

	private void method18(byte byte0)
	{
		if (byte0 != 3)
		{
			return;
		}
		for (SpawnObjectNode spawnObjectNode = (SpawnObjectNode) spawnObjectList.first(); spawnObjectNode != null; spawnObjectNode = (SpawnObjectNode) spawnObjectList
			.next())
		{
			if (spawnObjectNode.cycle == -1)
			{
				spawnObjectNode.spawnCycle = 0;
				method140((byte) -61, spawnObjectNode);
			}
			else
			{
				spawnObjectNode.remove();
			}
		}

	}

	private void openErrorWebPage(String s)
	{
		System.out.println(s);
		try
		{
//            getAppletContext().showDocument(new URL(getCodeBase(), "loaderror_" + s + ".html"));
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
		do
		{
			try
			{
				Thread.sleep(1000L);
			}
			catch (Exception _ex)
			{
			}
		}
		while (true);
	}

	private void method21(boolean flag)
	{
		if (flag)
		{
			return;
		}
		if (super.clickType == 1)
		{
			if (super.clickX >= 539 && super.clickX <= 573 && super.clickY >= 169 && super.clickY < 205
				&& tabWidgetIds[0] != -1)
			{
				redrawTabArea = true;
				currentTabId = 0;
				drawTabIcons = true;
			}
			if (super.clickX >= 569 && super.clickX <= 599 && super.clickY >= 168 && super.clickY < 205
				&& tabWidgetIds[1] != -1)
			{
				redrawTabArea = true;
				currentTabId = 1;
				drawTabIcons = true;
			}
			if (super.clickX >= 597 && super.clickX <= 627 && super.clickY >= 168 && super.clickY < 205
				&& tabWidgetIds[2] != -1)
			{
				redrawTabArea = true;
				currentTabId = 2;
				drawTabIcons = true;
			}
			if (super.clickX >= 625 && super.clickX <= 669 && super.clickY >= 168 && super.clickY < 203
				&& tabWidgetIds[3] != -1)
			{
				redrawTabArea = true;
				currentTabId = 3;
				drawTabIcons = true;
			}
			if (super.clickX >= 666 && super.clickX <= 696 && super.clickY >= 168 && super.clickY < 205
				&& tabWidgetIds[4] != -1)
			{
				redrawTabArea = true;
				currentTabId = 4;
				drawTabIcons = true;
			}
			if (super.clickX >= 694 && super.clickX <= 724 && super.clickY >= 168 && super.clickY < 205
				&& tabWidgetIds[5] != -1)
			{
				redrawTabArea = true;
				currentTabId = 5;
				drawTabIcons = true;
			}
			if (super.clickX >= 722 && super.clickX <= 756 && super.clickY >= 169 && super.clickY < 205
				&& tabWidgetIds[6] != -1)
			{
				redrawTabArea = true;
				currentTabId = 6;
				drawTabIcons = true;
			}
			if (super.clickX >= 540 && super.clickX <= 574 && super.clickY >= 466 && super.clickY < 502
				&& tabWidgetIds[7] != -1)
			{
				redrawTabArea = true;
				currentTabId = 7;
				drawTabIcons = true;
			}
			if (super.clickX >= 572 && super.clickX <= 602 && super.clickY >= 466 && super.clickY < 503
				&& tabWidgetIds[8] != -1)
			{
				redrawTabArea = true;
				currentTabId = 8;
				drawTabIcons = true;
			}
			if (super.clickX >= 599 && super.clickX <= 629 && super.clickY >= 466 && super.clickY < 503
				&& tabWidgetIds[9] != -1)
			{
				redrawTabArea = true;
				currentTabId = 9;
				drawTabIcons = true;
			}
			if (super.clickX >= 627 && super.clickX <= 671 && super.clickY >= 467 && super.clickY < 502
				&& tabWidgetIds[10] != -1)
			{
				redrawTabArea = true;
				currentTabId = 10;
				drawTabIcons = true;
			}
			if (super.clickX >= 669 && super.clickX <= 699 && super.clickY >= 466 && super.clickY < 503
				&& tabWidgetIds[11] != -1)
			{
				redrawTabArea = true;
				currentTabId = 11;
				drawTabIcons = true;
			}
			if (super.clickX >= 696 && super.clickX <= 726 && super.clickY >= 466 && super.clickY < 503
				&& tabWidgetIds[12] != -1)
			{
				redrawTabArea = true;
				currentTabId = 12;
				drawTabIcons = true;
			}
			if (super.clickX >= 724 && super.clickX <= 758 && super.clickY >= 466 && super.clickY < 502
				&& tabWidgetIds[13] != -1)
			{
				redrawTabArea = true;
				currentTabId = 13;
				drawTabIcons = true;
			}
		}
	}

	private void calculateCameraPosition()
	{
		try
		{
			int sceneX = localPlayer.worldX + this.cameraOffsetX;
			int sceneY = localPlayer.worldY + this.cameraOffsetY;
			if (currentCameraPositionH - sceneX < -500 || currentCameraPositionH - sceneX > 500 || currentCameraPositionV - sceneY < -500 || currentCameraPositionV - sceneY > 500)
			{
				currentCameraPositionH = sceneX;
				currentCameraPositionV = sceneY;
			}
			if (currentCameraPositionH != sceneX)
			{
				currentCameraPositionH += (sceneX - currentCameraPositionH) / 16;
			}
			if (currentCameraPositionV != sceneY)
			{
				currentCameraPositionV += (sceneY - currentCameraPositionV) / 16;
			}
			if (super.keyStatus[1] == 1)
			{
				cameraVelocityHorizontal += (-24 - cameraVelocityHorizontal) / 2;
			}
			else if (super.keyStatus[2] == 1)
			{
				cameraVelocityHorizontal += (24 - cameraVelocityHorizontal) / 2;
			}
			else
			{
				cameraVelocityHorizontal /= 2;
			}
			if (super.keyStatus[3] == 1)
			{
				cameraVelocityVertical += (12 - cameraVelocityVertical) / 2;
			}
			else if (super.keyStatus[4] == 1)
			{
				cameraVelocityVertical += (-12 - cameraVelocityVertical) / 2;
			}
			else
			{
				cameraVelocityVertical /= 2;
			}
			cameraHorizontal = cameraHorizontal + cameraVelocityHorizontal / 2 & 0x7ff;
			cameraVertical += cameraVelocityVertical / 2;
			if (cameraVertical < 128)
			{
				cameraVertical = 128;
			}
			if (cameraVertical > 383)
			{
				cameraVertical = 383;
			}
			int l = currentCameraPositionH >> 7;
			int i1 = currentCameraPositionV >> 7;
			int j1 = getFloorDrawHeight(plane, currentCameraPositionH, currentCameraPositionV);
			int k1 = 0;
			if (l > 3 && i1 > 3 && l < 100 && i1 < 100)
			{
				for (int l1 = l - 4; l1 <= l + 4; l1++)
				{
					for (int j2 = i1 - 4; j2 <= i1 + 4; j2++)
					{
						int k2 = plane;
						if (k2 < 3 && (currentSceneTileFlags[1][l1][j2] & 2) == 2)
						{
							k2++;
						}
						int l2 = j1 - intGroundArray[k2][l1][j2];
						if (l2 > k1)
						{
							k1 = l2;
						}
					}

				}

			}
			int i2 = k1 * 192;
			if (i2 > 98048)
			{
				i2 = 98048;
			}
			if (i2 < 32768)
			{
				i2 = 32768;
			}
			if (i2 > secondaryCameraVertical)
			{
				secondaryCameraVertical += (i2 - secondaryCameraVertical) / 24;
				return;
			}
			if (i2 < secondaryCameraVertical)
			{
				secondaryCameraVertical += (i2 - secondaryCameraVertical) / 80;
			}
		}
		catch (Exception _ex)
		{
			SignLink.reportError("glfc_ex " + localPlayer.worldX + ","
				+ localPlayer.worldY + "," + currentCameraPositionH + "," + currentCameraPositionV + "," + chunkX + ","
				+ chunkY + "," + nextTopLeftTileX + "," + nextTopRightTileY);
			throw new RuntimeException("eek");
		}
	}

	private boolean processFriendListClick(Widget widget)
	{
		int row = widget.contentType;
		if (row >= 1 && row <= 200 || row >= 701 && row <= 900)
		{
			if (row >= 801)
			{
				row -= 701;
			}
			else if (row >= 701)
			{
				row -= 601;
			}
			else if (row >= 101)
			{
				row -= 101;
			}
			else
			{
				row--;
			}
			menuActionTexts[menuActionRow] = "Remove @whi@" + friendUsernames[row];
			menuActionTypes[menuActionRow] = Actions.REMOVE_FRIEND;
			menuActionRow++;
			menuActionTexts[menuActionRow] = "Message @whi@" + friendUsernames[row];
			menuActionTypes[menuActionRow] = Actions.PRIVATE_MESSAGE;
			menuActionRow++;
			return true;
		}
		if (row >= 401 && row <= 500)
		{
			menuActionTexts[menuActionRow] = "Remove @whi@" + widget.disabledText;
			menuActionTypes[menuActionRow] = Actions.REMOVE_FRIEND;
			menuActionRow++;
			return true;
		}
		else
		{
			return false;
		}
	}

	private void saveMidi(boolean flag, byte[] abyte0)
	{
		if (musicEnabled)
		{
			SignLink.fadeMidi = flag ? 1 : 0;
			SignLink.saveMidi(abyte0, abyte0.length);
		}
	}

	private void changeGender()
	{
		characterModelChanged = true;
		for (int type = 0; type < 7; type++)
		{
			characterEditIdentityKits[type] = -1;
			for (int kit = 0; kit < IdentityKit.count; kit++)
			{
				if (IdentityKit.cache[kit].widgetDisplayed
					|| IdentityKit.cache[kit].partId != type + (characterEditChangeGenger ? 0 : 7))
				{
					continue;
				}
				characterEditIdentityKits[type] = kit;
				break;
			}

		}

	}

	private void processGroundItems(int x, int y)
	{
		LinkedList linkedList = groundItems.getTile(plane, x, y);
		if (linkedList == null)
		{
			currentScene.clearGroundItem(plane, x, y);
			return;
		}
		int maxValue = 0xfa0a1f01;
		Object mostValuable = null;
		for (Item item = (Item) linkedList.first(); item != null; item = (Item) linkedList
			.next())
		{
			ItemDefinition definition = ItemDefinition.lookup(item.itemId);
			int value = definition.value;
			if (definition.stackable)
			{
				value *= item.itemCount + 1;
			}
			if (value > maxValue)
			{
				maxValue = value;
				mostValuable = item;
			}
		}

		linkedList.push(((Node) (mostValuable)));
		Object first = null;
		Object second = null;
		for (Item item = (Item) linkedList.first(); item != null; item = (Item) linkedList
			.next())
		{
			if (item.itemId != ((Item) (mostValuable)).itemId && first == null)
			{
				first = item;
			}
			if (item.itemId != ((Item) (mostValuable)).itemId
				&& item.itemId != ((Item) (first)).itemId
				&& second == null)
			{
				second = item;
			}
		}

		int key = x + (y << 7) + 0x60000000;
		currentScene.addGroundItemTile(x, y, plane, getFloorDrawHeight(plane, x * 128 + 64, y * 128 + 64),
			key, ((Renderable) (mostValuable)), ((Renderable) (first)), ((Renderable) (second)));
	}

	private void updateGame()
	{
		if (systemUpdateTime > 1)
		{
			systemUpdateTime--;
		}
		if (idleLogout > 0)
		{
			idleLogout--;
		}
		for (int i = 0; i < 5; i++)
		{
			if (!parseIncomingPacket())
			{
				break;
			}
		}

		if (!loggedIn)
		{
			return;
		}
		synchronized (mouseCapturer.objectLock)
		{
			if (accountFlagged)
			{
				if (super.clickType != 0 || mouseCapturer.coord >= 40)
				{
					outBuffer.putOpcode(171);
					outBuffer.putByte(0);
					int originalOffset = outBuffer.currentPosition;
					int coordinateCount = 0;
					for (int c = 0; c < mouseCapturer.coord; c++)
					{
						if (originalOffset - outBuffer.currentPosition >= 240)
						{
							break;
						}
						coordinateCount++;
						int y = mouseCapturer.coordsY[c];
						if (y < 0)
						{
							y = 0;
						}
						else if (y > 502)
						{
							y = 502;
						}
						int x = mouseCapturer.coordsX[c];
						if (x < 0)
						{
							x = 0;
						}
						else if (x > 764)
						{
							x = 764;
						}
						int pixelOffset = y * 765 + x;
						if (mouseCapturer.coordsY[c] == -1 && mouseCapturer.coordsX[c] == -1)
						{
							x = -1;
							y = -1;
							pixelOffset = 0x7ffff;
						}
						if (x == lastClickX && y == lastClickY)
						{
							if (duplicateClickCount < 2047)
							{
								duplicateClickCount++;
							}
						}
						else
						{
							int differenceX = x - lastClickX;
							lastClickX = x;
							int differenceY = y - lastClickY;
							lastClickY = y;
							if (duplicateClickCount < 8 && differenceX >= -32 && differenceX <= 31 && differenceY >= -32 && differenceY <= 31)
							{
								differenceX += 32;
								differenceY += 32;
								outBuffer.putShortBE((duplicateClickCount << 12) + (differenceX << 6) + differenceY);
								duplicateClickCount = 0;
							}
							else if (duplicateClickCount < 8)
							{
								outBuffer.putMediumBE(0x800000 + (duplicateClickCount << 19) + pixelOffset);
								duplicateClickCount = 0;
							}
							else
							{
								outBuffer.putIntBE(0xc0000000 + (duplicateClickCount << 19) + pixelOffset);
								duplicateClickCount = 0;
							}
						}
					}

					outBuffer.putLength(outBuffer.currentPosition - originalOffset);
					if (coordinateCount >= mouseCapturer.coord)
					{
						mouseCapturer.coord = 0;
					}
					else
					{
						mouseCapturer.coord -= coordinateCount;
						for (int c = 0; c < mouseCapturer.coord; c++)
						{
							mouseCapturer.coordsX[c] = mouseCapturer.coordsX[c + coordinateCount];
							mouseCapturer.coordsY[c] = mouseCapturer.coordsY[c + coordinateCount];
						}

					}
				}
			}
			else
			{
				mouseCapturer.coord = 0;
			}
		}
		if (super.clickType != 0)
		{
			long timeBetweenClicks = (super.clickTime - lastClickTime) / 50L;
			if (timeBetweenClicks > 4095L)
			{
				timeBetweenClicks = 4095L;
			}
			lastClickTime = super.clickTime;
			int y = super.clickY;
			if (y < 0)
			{
				y = 0;
			}
			else if (y > 502)
			{
				y = 502;
			}
			int x = super.clickX;
			if (x < 0)
			{
				x = 0;
			}
			else if (x > 764)
			{
				x = 764;
			}
			int pixelOffset = y * 765 + x;
			int rightClick = 0;
			if (super.clickType == 2)
			{
				rightClick = 1;
			}
			int timeDifference = (int) timeBetweenClicks;
			outBuffer.putOpcode(19);
			outBuffer.putIntBE((timeDifference << 20) + (rightClick << 19) + pixelOffset);
		}
		if (cameraMovedWriteDelay > 0)
		{
			cameraMovedWriteDelay--;
		}
		if (super.keyStatus[1] == 1 || super.keyStatus[2] == 1 || super.keyStatus[3] == 1
			|| super.keyStatus[4] == 1)
		{
			cameraMovedWrite = true;
		}
		if (cameraMovedWrite && cameraMovedWriteDelay <= 0)
		{
			cameraMovedWriteDelay = 20;
			cameraMovedWrite = false;
			outBuffer.putOpcode(140);
			outBuffer.putShortLE(cameraVertical);
			outBuffer.putShortLE(cameraHorizontal);
		}
		if (super.awtFocus && !windowFocused)
		{
			windowFocused = true;
			outBuffer.putOpcode(187);
			outBuffer.putByte(1);
		}
		if (!super.awtFocus && windowFocused)
		{
			windowFocused = false;
			outBuffer.putOpcode(187);
			outBuffer.putByte(0);
		}
		loadingStages();
		processLocationCreation();
		processAudio();
		netCycle++;
		if (netCycle > 750)
		{
			dropClient();
		}
		processPlayers();
		processNPCs();
		processActorOverheadText();
		tickDelta++;
		if (crossType != 0)
		{
			crossIndex += 20;
			if (crossIndex >= 400)
			{
				crossType = 0;
			}
		}
		if (atInventoryInterfaceType != 0)
		{
			atInventoryLoopCycle++;
			if (atInventoryLoopCycle >= 15)
			{
				if (atInventoryInterfaceType == 2)
				{
					redrawTabArea = true;
				}
				if (atInventoryInterfaceType == 3)
				{
					redrawChatbox = true;
				}
				atInventoryInterfaceType = 0;
			}
		}
		if (activeInterfaceType != 0)
		{
			lastItemDragTime++;
			if (super.mouseX > anInt1114 + 5 || super.mouseX < anInt1114 - 5 || super.mouseY > anInt1115 + 5
				|| super.mouseY < anInt1115 - 5)
			{
				lastItemDragged = true;
			}
			if (super.mouseButtonPressed == 0)
			{
				if (activeInterfaceType == 2)
				{
					redrawTabArea = true;
				}
				if (activeInterfaceType == 3)
				{
					redrawChatbox = true;
				}
				activeInterfaceType = 0;
				if (lastItemDragged && lastItemDragTime >= 5)
				{
					lastActiveInvInterface = -1;
					processRightClick(-521);
					if (lastActiveInvInterface == modifiedWidgetId && mouseInvInterfaceIndex != selectedInventorySlot)
					{
						Widget childInterface = Widget.forId(modifiedWidgetId);
						int moveItemInsertionMode = 0;
						if (bankInsertMode == 1 && childInterface.contentType == 206)
						{
							moveItemInsertionMode = 1;
						}
						if (childInterface.items[mouseInvInterfaceIndex] <= 0)
						{
							moveItemInsertionMode = 0;
						}
						if (childInterface.itemDeletesDraged)
						{
							int slotStart = selectedInventorySlot;
							int slotEnd = mouseInvInterfaceIndex;
							childInterface.items[slotEnd] = childInterface.items[slotStart];
							childInterface.itemAmounts[slotEnd] = childInterface.itemAmounts[slotStart];
							childInterface.items[slotStart] = -1;
							childInterface.itemAmounts[slotStart] = 0;
						}
						else if (moveItemInsertionMode == 1)
						{
							int slotStart = selectedInventorySlot;
							for (int slotPointer = mouseInvInterfaceIndex; slotStart != slotPointer; )
							{
								if (slotStart > slotPointer)
								{
									childInterface.swapItems(slotStart, slotStart - 1);
									slotStart--;
								}
								else if (slotStart < slotPointer)
								{
									childInterface.swapItems(slotStart, slotStart + 1);
									slotStart++;
								}
							}

						}
						else
						{
							childInterface.swapItems(selectedInventorySlot, mouseInvInterfaceIndex);
						}
						outBuffer.putOpcode(123);
						outBuffer.putOffsetShortLE(mouseInvInterfaceIndex);
						outBuffer.putOffsetByte(moveItemInsertionMode);
						outBuffer.putOffsetShortBE(modifiedWidgetId);
						outBuffer.putShortLE(selectedInventorySlot);
					}
				}
				else if ((oneMouseButton == 1 || menuHasAddFriend(menuActionRow - 1, aByte1161)) && menuActionRow > 2)
				{
					determineMenuSize();
				}
				else if (menuActionRow > 0)
				{
					processMenuActions(menuActionRow - 1);
				}
				atInventoryLoopCycle = 10;
				super.clickType = 0;
			}
		}
		if (Scene.clickedTileX != -1)
		{
			int dstX = Scene.clickedTileX;
			int dstY = Scene.clickedTileY;
			boolean flag = walk(true, false, dstY, localPlayer.pathY[0], 0, 0, 0, 0, dstX, 0, 0,
				localPlayer.pathX[0]);
			Scene.clickedTileX = -1;
			if (flag)
			{
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 1;
				crossIndex = 0;
			}
		}
		if (super.clickType == 1 && clickToContinueString != null)
		{
			clickToContinueString = null;
			redrawChatbox = true;
			super.clickType = 0;
		}
		processMenuClick();
		if (fullscreenWidgetId == -1)
		{
			method146((byte) 4);
			method21(false);
			method39();
		}
		if (super.mouseButtonPressed == 1 || super.clickType == 1)
		{
			anInt1094++;
		}
		if (anInt1284 != 0 || anInt1044 != 0 || anInt1129 != 0)
		{
			if (anInt893 < 100)
			{
				anInt893++;
				if (anInt893 == 100)
				{
					if (anInt1284 != 0)
					{
						redrawChatbox = true;
					}
					if (anInt1044 != 0)
					{
						redrawTabArea = true;
					}
				}
			}
		}
		else if (anInt893 > 0)
		{
			anInt893--;
		}
		if (loadingStage == 2)
		{
			calculateCameraPosition();
		}
		if (loadingStage == 2 && cutsceneActive)
		{
			calculateCinematicCameraPosition();
		}
		for (int i = 0; i < 5; i++)
		{
			quakeTimes[i]++;
		}

		manageTextInputs();
		super.idleTime++;
		if (super.idleTime > 4500)
		{
			idleLogout = 250;
			super.idleTime -= 500;
			outBuffer.putOpcode(202);
		}
		randomCycle1++;
		if (randomCycle1 > 500)
		{
			randomCycle1 = 0;
			int random = (int) (Math.random() * 8D);
			if ((random & 1) == 1)
			{
				cameraOffsetX += cameraOffsetModifierX;
			}
			if ((random & 2) == 2)
			{
				cameraOffsetY += cameraOffsetModifierY;
			}
			if ((random & 4) == 4)
			{
				cameraRandomisationA += cameraPitchModifier;
			}
		}
		if (cameraOffsetX < -50)
		{
			cameraOffsetModifierX = 2;
		}
		if (cameraOffsetX > 50)
		{
			cameraOffsetModifierX = -2;
		}
		if (cameraOffsetY < -55)
		{
			cameraOffsetModifierY = 2;
		}
		if (cameraOffsetY > 55)
		{
			cameraOffsetModifierY = -2;
		}
		if (cameraRandomisationA < -40)
		{
			cameraPitchModifier = 1;
		}
		if (cameraRandomisationA > 40)
		{
			cameraPitchModifier = -1;
		}
		randomCycle2++;
		if (randomCycle2 > 500)
		{
			randomCycle2 = 0;
			int random = (int) (Math.random() * 8D);
			if ((random & 1) == 1)
			{
				cameraYawOffset += cameraYawModifier;
			}
			if ((random & 2) == 2)
			{
				mapZoomOffset += mapZoomModifier;
			}
		}
		if (cameraYawOffset < -60)
		{
			cameraYawModifier = 2;
		}
		if (cameraYawOffset > 60)
		{
			cameraYawModifier = -2;
		}
		if (mapZoomOffset < -20)
		{
			mapZoomModifier = 1;
		}
		if (mapZoomOffset > 10)
		{
			mapZoomModifier = -1;
		}
		netAliveCycle++;
		if (netAliveCycle > 50)
		{
			outBuffer.putOpcode(40);
		}
		try
		{
			if (gameConnection != null && outBuffer.currentPosition > 0)
			{
				gameConnection.write(outBuffer.currentPosition, 0, outBuffer.buffer);
				outBuffer.currentPosition = 0;
				netAliveCycle = 0;
			}
		}
		catch (IOException _ex)
		{
			dropClient();
		}
		catch (Exception exception)
		{
			logout();
		}
	}

	private void calculateCinematicCameraPosition()
	{
		int i = anInt874 * 128 + 64;
		int j = anInt875 * 128 + 64;
		int k = getFloorDrawHeight(plane, i, j) - anInt876;
		if (cameraX < i)
		{
			cameraX += anInt877 + ((i - cameraX) * anInt878) / 1000;
			if (cameraX > i)
			{
				cameraX = i;
			}
		}
		if (cameraX > i)
		{
			cameraX -= anInt877 + ((cameraX - i) * anInt878) / 1000;
			if (cameraX < i)
			{
				cameraX = i;
			}
		}
		if (cameraZ < k)
		{
			cameraZ += anInt877 + ((k - cameraZ) * anInt878) / 1000;
			if (cameraZ > k)
			{
				cameraZ = k;
			}
		}
		if (cameraZ > k)
		{
			cameraZ -= anInt877 + ((cameraZ - k) * anInt878) / 1000;
			if (cameraZ < k)
			{
				cameraZ = k;
			}
		}
		if (cameraY < j)
		{
			cameraY += anInt877 + ((j - cameraY) * anInt878) / 1000;
			if (cameraY > j)
			{
				cameraY = j;
			}
		}
		if (cameraY > j)
		{
			cameraY -= anInt877 + ((cameraY - j) * anInt878) / 1000;
			if (cameraY < j)
			{
				cameraY = j;
			}
		}
		i = anInt993 * 128 + 64;
		j = anInt994 * 128 + 64;
		k = getFloorDrawHeight(plane, i, j) - anInt995;
		int l = i - cameraX;
		int i1 = k - cameraZ;
		int j1 = j - cameraY;
		int k1 = (int) Math.sqrt(l * l + j1 * j1);
		int l1 = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
		int j2 = (int) (Math.atan2(l, j1) * -325.94900000000001D) & 0x7ff;
		if (l1 < 128)
		{
			l1 = 128;
		}
		if (l1 > 383)
		{
			l1 = 383;
		}
		if (cameraVerticalRotation < l1)
		{
			cameraVerticalRotation += anInt996 + ((l1 - cameraVerticalRotation) * anInt997) / 1000;
			if (cameraVerticalRotation > l1)
			{
				cameraVerticalRotation = l1;
			}
		}
		if (cameraVerticalRotation > l1)
		{
			cameraVerticalRotation -= anInt996 + ((cameraVerticalRotation - l1) * anInt997) / 1000;
			if (cameraVerticalRotation < l1)
			{
				cameraVerticalRotation = l1;
			}
		}
		int k2 = j2 - cameraHorizontalRotation;
		if (k2 > 1024)
		{
			k2 -= 2048;
		}
		if (k2 < -1024)
		{
			k2 += 2048;
		}
		if (k2 > 0)
		{
			cameraHorizontalRotation += anInt996 + (k2 * anInt997) / 1000;
			cameraHorizontalRotation &= 0x7ff;
		}
		if (k2 < 0)
		{
			cameraHorizontalRotation -= anInt996 + (-k2 * anInt997) / 1000;
			cameraHorizontalRotation &= 0x7ff;
		}
		int l2 = j2 - cameraHorizontalRotation;
		if (l2 > 1024)
		{
			l2 -= 2048;
		}
		if (l2 < -1024)
		{
			l2 += 2048;
		}
		if (l2 < 0 && k2 > 0 || l2 > 0 && k2 < 0)
		{
			cameraHorizontalRotation = j2;
		}
	}

	private void manageTextInputs()
	{
		while (true)
		{
			int key = readCharacter();
			if (key == -1)
			{
				break;
			}
			if (openScreenWidgetId != -1 && openScreenWidgetId == reportAbuseInterfaceID)
			{
				if (key == 8 && reportedName.length() > 0)
				{
					reportedName = reportedName.substring(0, reportedName.length() - 1);
				}
				if ((key >= 97 && key <= 122 || key >= 65 && key <= 90 || key >= 48 && key <= 57 || key == 32)
					&& reportedName.length() < 12)
				{
					reportedName += (char) key;
				}
			}
			else if (messagePromptRaised)
			{
				if (key >= 32 && key <= 122 && chatMessage.length() < 80)
				{
					chatMessage += (char) key;
					redrawChatbox = true;
				}
				if (key == 8 && chatMessage.length() > 0)
				{
					chatMessage = chatMessage.substring(0, chatMessage.length() - 1);
					redrawChatbox = true;
				}
				if (key == 13 || key == 10)
				{
					messagePromptRaised = false;
					redrawChatbox = true;
					if (friendsListAction == 1)
					{
						long l = TextUtils.nameToLong(chatMessage);
						addFriend(l);
					}
					if (friendsListAction == 2 && friendsCount > 0)
					{
						long l1 = TextUtils.nameToLong(chatMessage);
						removeFriend(l1);
					}
					if (friendsListAction == 3 && chatMessage.length() > 0)
					{
						outBuffer.putOpcode(227);
						outBuffer.putByte(0);
						int j = outBuffer.currentPosition;
						outBuffer.putLongBE(aLong1141);
						ChatEncoder.put(chatMessage, outBuffer);
						outBuffer.putLength(outBuffer.currentPosition - j);
						chatMessage = ChatEncoder.formatChatMessage(chatMessage);
						//chatMessage = ChatCensor.censorString(chatMessage);
						addChatMessage(TextUtils.formatName(TextUtils.longToName(aLong1141)),
							chatMessage, 6);
						if (privateChatMode == 2)
						{
							privateChatMode = 1;
							redrawChatMode = true;
							outBuffer.putOpcode(176);
							outBuffer.putByte(publicChatMode);
							outBuffer.putByte(privateChatMode);
							outBuffer.putByte(tradeMode);
						}
					}
					if (friendsListAction == 4 && ignoresCount < 100)
					{
						long nameAsLong = TextUtils.nameToLong(chatMessage);
						addIgnore(nameAsLong);
					}
					if (friendsListAction == 5 && ignoresCount > 0)
					{
						long nameAsLong = TextUtils.nameToLong(chatMessage);
						removeIgnore(nameAsLong);
					}
				}
			}
			else if (inputType == 1)
			{
				if (key >= 48 && key <= 57 && inputInputMessage.length() < 10)
				{
					inputInputMessage += (char) key;
					redrawChatbox = true;
				}
				if (key == 8 && inputInputMessage.length() > 0)
				{
					inputInputMessage = inputInputMessage.substring(0, inputInputMessage.length() - 1);
					redrawChatbox = true;
				}
				if (key == 13 || key == 10)
				{
					if (inputInputMessage.length() > 0)
					{
						int inputValue = 0;
						try
						{
							inputValue = Integer.parseInt(inputInputMessage);
						}
						catch (Exception _ex)
						{
							/* empty */
						}
						outBuffer.putOpcode(75);
						outBuffer.putIntBE(inputValue);
					}
					inputType = 0;
					redrawChatbox = true;
				}
			}
			else if (inputType == 2)
			{
				if (key >= 32 && key <= 122 && inputInputMessage.length() < 12)
				{
					inputInputMessage += (char) key;
					redrawChatbox = true;
				}
				if (key == 8 && inputInputMessage.length() > 0)
				{
					inputInputMessage = inputInputMessage.substring(0, inputInputMessage.length() - 1);
					redrawChatbox = true;
				}
				if (key == 13 || key == 10)
				{
					if (inputInputMessage.length() > 0)
					{
						outBuffer.putOpcode(206);
						outBuffer.putLongBE(TextUtils.nameToLong(inputInputMessage));
					}
					inputType = 0;
					redrawChatbox = true;
				}
			}
			else if (inputType == 3)
			{
				if (key >= 32 && key <= 122 && inputInputMessage.length() < 40)
				{
					inputInputMessage += (char) key;
					redrawChatbox = true;
				}
				if (key == 8 && inputInputMessage.length() > 0)
				{
					inputInputMessage = inputInputMessage.substring(0, inputInputMessage.length() - 1);
					redrawChatbox = true;
				}
			}
			else if (openChatboxWidgetId == -1 && fullscreenWidgetId == -1)
			{
				if (key >= 32 && key <= 122 && chatboxInput.length() < 80)
				{
					chatboxInput += (char) key;
					redrawChatbox = true;
				}
				if (key == 8 && chatboxInput.length() > 0)
				{
					chatboxInput = chatboxInput.substring(0, chatboxInput.length() - 1);
					redrawChatbox = true;
				}
				if ((key == 13 || key == 10) && chatboxInput.length() > 0)
				{
					if (true)
					{
						if (chatboxInput.equals("::clientdrop"))
						{
							dropClient();
						}
						if (chatboxInput.equals("::items"))
						{
							this.inputType = 3;
							this.openChatboxWidgetId = -1;
						}
						if (chatboxInput.equals("::lag"))
						{
							infoDump();
						}
						if (chatboxInput.equals("::prefetchmusic"))
						{
							for (int i_417_ = 0; i_417_ < onDemandRequester.fileCount(2); i_417_++)
							{
								onDemandRequester.setPriority((byte) 1, 2, i_417_);
							}

						}
						if (chatboxInput.equals("::fpson"))
						{
							fps = true;
						}
						if (chatboxInput.equals("::fpsoff"))
						{
							fps = false;
						}
						if (chatboxInput.equals("::noclip"))
						{
							for (int floorLevel = 0; floorLevel < 4; floorLevel++)
							{
								for (int width = 1; width < 103; width++)
								{
									for (int heigth = 1; heigth < 103; heigth++)
									{
										currentCollisionMap[floorLevel].clippingData[width][heigth] = 0;
									}

								}

							}

						}
					}
					if (chatboxInput.startsWith("::"))
					{
						outBuffer.putOpcode(56);
						outBuffer.putByte(chatboxInput.length() - 1);
						outBuffer.putString(chatboxInput.substring(2));
					}
					else
					{
						String s = chatboxInput.toLowerCase();
						int colourCode = 0;
						if (s.startsWith("yellow:"))
						{
							colourCode = 0;
							chatboxInput = chatboxInput.substring(7);
						}
						else if (s.startsWith("red:"))
						{
							colourCode = 1;
							chatboxInput = chatboxInput.substring(4);
						}
						else if (s.startsWith("green:"))
						{
							colourCode = 2;
							chatboxInput = chatboxInput.substring(6);
						}
						else if (s.startsWith("cyan:"))
						{
							colourCode = 3;
							chatboxInput = chatboxInput.substring(5);
						}
						else if (s.startsWith("purple:"))
						{
							colourCode = 4;
							chatboxInput = chatboxInput.substring(7);
						}
						else if (s.startsWith("white:"))
						{
							colourCode = 5;
							chatboxInput = chatboxInput.substring(6);
						}
						else if (s.startsWith("flash1:"))
						{
							colourCode = 6;
							chatboxInput = chatboxInput.substring(7);
						}
						else if (s.startsWith("flash2:"))
						{
							colourCode = 7;
							chatboxInput = chatboxInput.substring(7);
						}
						else if (s.startsWith("flash3:"))
						{
							colourCode = 8;
							chatboxInput = chatboxInput.substring(7);
						}
						else if (s.startsWith("glow1:"))
						{
							colourCode = 9;
							chatboxInput = chatboxInput.substring(6);
						}
						else if (s.startsWith("glow2:"))
						{
							colourCode = 10;
							chatboxInput = chatboxInput.substring(6);
						}
						else if (s.startsWith("glow3:"))
						{
							colourCode = 11;
							chatboxInput = chatboxInput.substring(6);
						}
						s = chatboxInput.toLowerCase();
						int effectCode = 0;
						if (s.startsWith("wave:"))
						{
							effectCode = 1;
							chatboxInput = chatboxInput.substring(5);
						}
						else if (s.startsWith("wave2:"))
						{
							effectCode = 2;
							chatboxInput = chatboxInput.substring(6);
						}
						else if (s.startsWith("shake:"))
						{
							effectCode = 3;
							chatboxInput = chatboxInput.substring(6);
						}
						else if (s.startsWith("scroll:"))
						{
							effectCode = 4;
							chatboxInput = chatboxInput.substring(7);
						}
						else if (s.startsWith("slide:"))
						{
							effectCode = 5;
							chatboxInput = chatboxInput.substring(6);
						}
						outBuffer.putOpcode(49);
						outBuffer.putByte(0);
						int bufferPos = outBuffer.currentPosition;
						outBuffer.putInvertedByte(colourCode);
						outBuffer.putOffsetByte(effectCode);
						chatBuffer.currentPosition = 0;
						ChatEncoder.put(chatboxInput, chatBuffer);
						outBuffer.putBytes(chatBuffer.buffer, 0,
							chatBuffer.currentPosition);
						outBuffer.putLength(outBuffer.currentPosition - bufferPos);
						chatboxInput = ChatEncoder.formatChatMessage(chatboxInput);
						chatboxInput = ChatCensor.censorString(chatboxInput);
						localPlayer.forcedChat = chatboxInput;
						localPlayer.textColour = colourCode;
						localPlayer.textEffect = effectCode;
						localPlayer.textCycle = 150;
						if (playerRights == 2)
						{
							addChatMessage("@cr2@" + localPlayer.playerName,
								localPlayer.forcedChat, 2);
						}
						else if (playerRights == 1)
						{
							addChatMessage("@cr1@" + localPlayer.playerName,
								localPlayer.forcedChat, 2);
						}
						else
						{
							addChatMessage(localPlayer.playerName, localPlayer.forcedChat, 2);
						}
						if (publicChatMode == 2)
						{
							publicChatMode = 3;
							redrawChatMode = true;
							outBuffer.putOpcode(176);
							outBuffer.putByte(publicChatMode);
							outBuffer.putByte(privateChatMode);
							outBuffer.putByte(tradeMode);
						}
					}
					chatboxInput = "";
					redrawChatbox = true;
				}
			}
		}
	}

	private DataInputStream openJaggrabStream(String request) throws IOException
	{
		useJaggrab = Configuration.JAGGRAB_ENABLED;

		if (!useJaggrab)
		{
			return SignLink.openURL(request);
		}

		if (jaggrabSocket != null)
		{
			try
			{
				jaggrabSocket.close();
			}
			catch (Exception ignored)
			{
			}

			jaggrabSocket = null;
		}

		byte[] buffer = String.format("JAGGRAB /%s\n\n", request).getBytes();
		jaggrabSocket = openSocket(Configuration.JAGGRAB_PORT);

		jaggrabSocket.setSoTimeout(10000);
		jaggrabSocket.getOutputStream().write(buffer);

		return new DataInputStream(jaggrabSocket.getInputStream());
	}

	public Socket openSocket(int port) throws IOException
	{
		return new Socket(InetAddress.getByName(getCodeBase().getHost()), port);
	}

	private boolean parseIncomingPacket()
	{
		if (gameConnection == null)
		{
			return false;
		}
		try
		{
			int available = gameConnection.getAvailable();
			if (available == 0)
			{
				return false;
			}
			if (opcode == -1)
			{
				gameConnection.read(buffer.buffer, 0, 1);
				opcode = buffer.buffer[0] & 0xff;
				if (incomingRandom != null)
				{
					opcode = opcode - incomingRandom.nextInt() & 0xff;
				}
				packetSize = PacketConstants.PACKET_SIZES[opcode];
				available--;
			}
			if (packetSize == -1)
			{
				if (available > 0)
				{
					gameConnection.read(buffer.buffer, 0, 1);
					packetSize = buffer.buffer[0] & 0xff;
					available--;
				}
				else
				{
					return false;
				}
			}
			if (packetSize == -2)
			{
				if (available > 1)
				{
					gameConnection.read(buffer.buffer, 0, 2);
					buffer.currentPosition = 0;
					packetSize = buffer.getUnsignedShortBE();
					available -= 2;
				}
				else
				{
					return false;
				}
			}
			if (available < packetSize)
			{
				return false;
			}
			buffer.currentPosition = 0;
			gameConnection.read(buffer.buffer, 0, packetSize);
			netCycle = 0;
			thirdLastOpcode = secondLastOpcode;
			secondLastOpcode = lastOpcode;
			lastOpcode = opcode;
			if (UPDATE_WIDGET_POSITION.equals(opcode))
			{
				int yOffset = buffer.getShortLE();
				int xOffset = buffer.getShortLE();
				int widgetId = buffer.getUnsignedShortBE();
				Widget widget = Widget.forId(widgetId);
				widget.xOffset = xOffset;
				widget.yOffset = yOffset;
				opcode = -1;
				return true;
			}
			if (UPDATE_WIDGET_MODEL_DISPLAY.equals(opcode))
			{
				int rotationX = buffer.getUnsignedNegativeOffsetShortBE();
				int widgetId = buffer.getUnsignedNegativeOffsetShortLE();
				int zoom = buffer.getUnsignedNegativeOffsetShortBE();
				int rotationY = buffer.getUnsignedShortLE();
				Widget.forId(widgetId).rotationX = rotationX;
				Widget.forId(widgetId).rotationY = rotationY;
				Widget.forId(widgetId).zoom = zoom;
				opcode = -1;
				return true;
			}
			if (SET_WIDGET_MODEL_1.equals(opcode))
			{
				int j1 = buffer.getUnsignedNegativeOffsetShortLE();
				int widgetId = buffer.getUnsignedNegativeOffsetShortLE();
				Widget.forId(widgetId).modelType = 1;
				Widget.forId(widgetId).modelId = j1;
				opcode = -1;
				return true;
			}
			if (PLAY_SOUND.equals(opcode))
			{
				int soundId = buffer.getUnsignedShortBE();
				int type = buffer.getUnsignedByte();
				int delay = buffer.getUnsignedShortBE();
				if (delay == 65535)
				{
					if (currentSound < 50)
					{
						sound[currentSound] = (short) soundId;
						soundType[currentSound] = type;
						soundDelay[currentSound] = 0;
						currentSound++;
					}
				}
				else if (aBoolean1301 && !lowMemory && currentSound < 50)
				{
					sound[currentSound] = soundId;
					soundType[currentSound] = type;
					soundDelay[currentSound] = delay + SoundTrack.trackDelays[soundId];
					currentSound++;
				}
				opcode = -1;
				return true;
			}
			if (UPDATE_WIDGET_SETTING_SMALL.equals(opcode))
			{
				int settingIndex = buffer.getUnsignedNegativeOffsetShortBE();
				byte settingValue = buffer.getPreNegativeOffsetByte();
				anIntArray1005[settingIndex] = settingValue;
				if (widgetSettings[settingIndex] != settingValue)
				{
					widgetSettings[settingIndex] = settingValue;
					updateVarp(0, settingIndex);
					redrawTabArea = true;
					if (dialogueId != -1)
					{
						redrawChatbox = true;
					}
				}
				opcode = -1;
				return true;
			}
			if (RESET_MOB_ANIMATIONS.equals(opcode))
			{
				for (int p = 0; p < players.length; p++)
				{
					if (players[p] != null)
					{
						players[p].emoteAnimation = -1;
					}
				}

				for (int n = 0; n < npcs.length; n++)
				{
					if (npcs[n] != null)
					{
						npcs[n].emoteAnimation = -1;
					}
				}

				opcode = -1;
				return true;
			}
			if (SET_MINIMAP_STATE.equals(opcode))
			{
				minimapState = buffer.getUnsignedByte();
				opcode = -1;
				return true;
			}
			if (SET_WIDGET_MODEL_2.equals(opcode))
			{
				int modelId = buffer.getUnsignedNegativeOffsetShortBE();
				int widgetId = buffer.getUnsignedShortLE();
				Widget.forId(widgetId).modelType = 2;
				Widget.forId(widgetId).modelId = modelId;
				opcode = -1;
				return true;
			}
			if (SHOW_CHATBOX_WIDGET.equals(opcode))
			{
				int chatboxWidgetId = buffer.getUnsignedShortBE();
				method112((byte) 36, chatboxWidgetId);
				if (tabAreaOverlayWidgetId != -1)
				{
					method44(tabAreaOverlayWidgetId);
					tabAreaOverlayWidgetId = -1;
					redrawTabArea = true;
					drawTabIcons = true;
				}
				if (fullscreenWidgetId != -1)
				{
					method44(fullscreenWidgetId);
					fullscreenWidgetId = -1;
					welcomeScreenRaised = true;
				}
				if (fullscreenWidgetChildId != -1)
				{
					method44(fullscreenWidgetChildId);
					fullscreenWidgetChildId = -1;
				}
				if (openScreenWidgetId != -1)
				{
					method44(openScreenWidgetId);
					openScreenWidgetId = -1;
				}
				if (openChatboxWidgetId != chatboxWidgetId)
				{
					method44(openChatboxWidgetId);
					openChatboxWidgetId = chatboxWidgetId;
				}
				aBoolean1239 = false;
				redrawChatbox = true;
				opcode = -1;
				return true;
			}
			if (PLAY_SONG.equals(opcode))
			{
				int songID = buffer.getUnsignedNegativeOffsetShortLE();
				if (songID == 65535)
				{
					songID = -1;
				}
				if (songID != currentSong && musicEnabled && !lowMemory && previousSong == 0)
				{
					nextSong = songID;
					songChanging = true;
					onDemandRequester.request(2, nextSong);
				}
				currentSong = songID;
				opcode = -1;
				return true;
			}
			if (PLAY_TEMP_SONG.equals(opcode))
			{
				int temporarySong = buffer.getUnsignedShortLE();
				int previousSong = buffer.getMediumME();
				if (musicEnabled && !lowMemory)
				{
					nextSong = temporarySong;
					songChanging = false;
					onDemandRequester.request(2, this.nextSong);
					this.previousSong = previousSong;
				}
				opcode = -1;
				return true;
			}
			if (SHOW_DIALOG.equals(opcode))
			{
				int widgetId = buffer.getShortLE();
				if (widgetId != dialogueId)
				{
					method44(dialogueId);
					dialogueId = widgetId;
				}
				redrawChatbox = true;
				opcode = -1;
				return true;
			}
			if (UPDATE_WIDGET_COLOR.equals(opcode))
			{
				int widgetId = buffer.getUnsignedShortBE();
				int rgb = buffer.getUnsignedNegativeOffsetShortBE();
				int j17 = rgb >> 10 & 0x1f;
				int j22 = rgb >> 5 & 0x1f;
				int l24 = rgb & 0x1f;
				Widget.forId(widgetId).disabledColor = (j17 << 19) + (j22 << 11) + (l24 << 3);
				opcode = -1;
				return true;
			}
			if (UPDATE_PLAYER_CONTEXT_OPTION.equals(opcode))
			{
				int slot = buffer.getUnsignedInvertedByte();
				String option = buffer.getString();
				int alwaysOnTop = buffer.getUnsignedByte();
				System.out.println(slot);
				System.out.println(option);
				System.out.println(alwaysOnTop);
				if (slot >= 1 && slot <= 5)
				{
					if (option.equalsIgnoreCase("null"))
					{
						option = null;
					}
					aStringArray1069[slot - 1] = option;
					aBooleanArray1070[slot - 1] = alwaysOnTop == 0;
				}
				opcode = -1;
				return true;
			}
			if (SET_CHAT_INPUT_TYPE_2.equals(opcode))
			{
				messagePromptRaised = false;
				inputType = 2;
				inputInputMessage = "";
				redrawChatbox = true;
				opcode = -1;
				return true;
			}
			if (UPDATE_CHAT_SETTINGS.equals(opcode))
			{
				publicChatMode = buffer.getUnsignedByte();
				privateChatMode = buffer.getUnsignedByte();
				tradeMode = buffer.getUnsignedByte();
				redrawChatMode = true;
				redrawChatbox = true;
				opcode = -1;
				return true;
			}
			if (SHOW_HINT_ICON.equals(opcode))
			{
				headIconDrawType = buffer.getUnsignedByte();
				if (headIconDrawType == 1)
				{
					anInt1226 = buffer.getUnsignedShortBE();
				}
				if (headIconDrawType >= 2 && headIconDrawType <= 6)
				{
					if (headIconDrawType == 2)
					{
						markerOffsetX = 64;
						markerOffsetY = 64;
					}
					if (headIconDrawType == 3)
					{
						markerOffsetX = 0;
						markerOffsetY = 64;
					}
					if (headIconDrawType == 4)
					{
						markerOffsetX = 128;
						markerOffsetY = 64;
					}
					if (headIconDrawType == 5)
					{
						markerOffsetX = 64;
						markerOffsetY = 0;
					}
					if (headIconDrawType == 6)
					{
						markerOffsetX = 64;
						markerOffsetY = 128;
					}
					headIconDrawType = 2;
					hintIconX = buffer.getUnsignedShortBE();
					hintIconY = buffer.getUnsignedShortBE();
					hintIconOffset = buffer.getUnsignedByte();
				}
				if (headIconDrawType == 10)
				{
					otherPlayerId = buffer.getUnsignedShortBE();
				}
				opcode = -1;
				return true;
			}
			if (MOVE_CUTSCENE_CAMERA.equals(opcode))
			{
				cutsceneActive = true;
				anInt993 = buffer.getUnsignedByte();
				anInt994 = buffer.getUnsignedByte();
				anInt995 = buffer.getUnsignedShortBE();
				anInt996 = buffer.getUnsignedByte();
				anInt997 = buffer.getUnsignedByte();
				if (anInt997 >= 100)
				{
					int i4 = anInt993 * 128 + 64;
					int l12 = anInt994 * 128 + 64;
					int l17 = getFloorDrawHeight(plane, i4, l12) - anInt995;
					int k22 = i4 - cameraX;
					int i25 = l17 - cameraZ;
					int k27 = l12 - cameraY;
					int i30 = (int) Math.sqrt(k22 * k22 + k27 * k27);
					cameraVerticalRotation = (int) (Math.atan2(i25, i30) * 325.94900000000001D) & 0x7ff;
					cameraHorizontalRotation = (int) (Math.atan2(k22, k27) * -325.94900000000001D) & 0x7ff;
					if (cameraVerticalRotation < 128)
					{
						cameraVerticalRotation = 128;
					}
					if (cameraVerticalRotation > 383)
					{
						cameraVerticalRotation = 383;
					}
				}
				opcode = -1;
				return true;
			}
			if (SEND_LOGOUT.equals(opcode))
			{
				logout();
				opcode = -1;
				return false;
			}
			if (UPDATE_WIDGET_SETTING_LARGE.equals(opcode))
			{
				int settingValue = buffer.getIntME2();
				int settingIndex = buffer.getUnsignedShortLE();
				anIntArray1005[settingIndex] = settingValue;
				if (widgetSettings[settingIndex] != settingValue)
				{
					widgetSettings[settingIndex] = settingValue;
					updateVarp(0, settingIndex);
					redrawTabArea = true;
					if (dialogueId != -1)
					{
						redrawChatbox = true;
					}
				}
				opcode = -1;
				return true;
			}
			if (CLOSE_ALL_WIDGETS.equals(opcode))
			{
				if (tabAreaOverlayWidgetId != -1)
				{
					method44(tabAreaOverlayWidgetId);
					tabAreaOverlayWidgetId = -1;
					redrawTabArea = true;
					drawTabIcons = true;
				}
				if (openChatboxWidgetId != -1)
				{
					method44(openChatboxWidgetId);
					openChatboxWidgetId = -1;
					redrawChatbox = true;
				}
				if (fullscreenWidgetId != -1)
				{
					method44(fullscreenWidgetId);
					fullscreenWidgetId = -1;
					welcomeScreenRaised = true;
				}
				if (fullscreenWidgetChildId != -1)
				{
					method44(fullscreenWidgetChildId);
					fullscreenWidgetChildId = -1;
				}
				if (openScreenWidgetId != -1)
				{
					method44(openScreenWidgetId);
					openScreenWidgetId = -1;
				}
				if (inputType != 0)
				{
					inputType = 0;
					redrawChatbox = true;
				}
				aBoolean1239 = false;
				opcode = -1;
				return true;
			}
			if (UPDATE_WELCOME_SCREEN.equals(opcode))
			{
				lastPasswordChangeTime = buffer.getUnsignedShortLE();
				buffer.getUnsignedNegativeOffsetShortLE(); // Never read anywhere... Junk?...
				buffer.getUnsignedShortBE(); // junk...
				buffer.getUnsignedShortBE(); // Never read anywhere... Junk?...
				loginScreenUpdateTime = buffer.getUnsignedShortLE();
				unreadWebsiteMessages = buffer.getUnsignedNegativeOffsetShortBE();
				lastLoginTime = buffer.getUnsignedNegativeOffsetShortBE();
				membershipCreditRemaining = buffer.getUnsignedShortBE();
				lastLoginAddress = buffer.getIntLE();
				recoveryQuestionSetTime = buffer.getUnsignedNegativeOffsetShortLE();
				buffer.getUnsignedPostNegativeOffsetByte(); // junk...
				SignLink.dnsLookup(TextUtils.decodeAddress(lastLoginAddress));
				opcode = -1;
				return true;
			}
			if (CHATBOX_MESSAGE.equals(opcode))
			{ // server message
				String message = buffer.getString();
				if (message.endsWith(":tradereq:"))
				{
					String s3 = message.substring(0, message.indexOf(":"));
					long l18 = TextUtils.nameToLong(s3);
					boolean flag1 = false;
					for (int l27 = 0; l27 < ignoresCount; l27++)
					{
						if (ignores[l27] != l18)
						{
							continue;
						}
						flag1 = true;
						break;
					}

					if (!flag1 && !inTutorialIsland)
					{
						addChatMessage(s3, "wishes to trade with you.", 4);
					}
				}
				else if (message.endsWith(":duelreq:"))
				{
					String s4 = message.substring(0, message.indexOf(":"));
					long l19 = TextUtils.nameToLong(s4);
					boolean flag2 = false;
					for (int i28 = 0; i28 < ignoresCount; i28++)
					{
						if (ignores[i28] != l19)
						{
							continue;
						}
						flag2 = true;
						break;
					}

					if (!flag2 && !inTutorialIsland)
					{
						addChatMessage(s4, "wishes to duel with you.", 8);
					}
				}
				else if (message.endsWith(":chalreq:"))
				{
					String s5 = message.substring(0, message.indexOf(":"));
					long l20 = TextUtils.nameToLong(s5);
					boolean flag3 = false;
					for (int j28 = 0; j28 < ignoresCount; j28++)
					{
						if (ignores[j28] != l20)
						{
							continue;
						}
						flag3 = true;
						break;
					}

					if (!flag3 && !inTutorialIsland)
					{
						String s8 = message.substring(message.indexOf(":") + 1, message.length() - 9);
						addChatMessage(s5, s8, 8);
					}
				}
				else
				{
					addChatMessage("", message, 0);
				}
				opcode = -1;
				return true;
			}
			if (SHOW_WALKABLE_WIDGET.equals(opcode))
			{
				int widgetId = buffer.getShortBE();
				if (widgetId >= 0)
				{
					method112((byte) 36, widgetId);
				}
				if (widgetId != walkableWidgetId)
				{
					method44(walkableWidgetId);
					walkableWidgetId = widgetId;
				}
				opcode = -1;
				return true;
			}
			if (HIDE_WIDGET.equals(opcode))
			{
				boolean hiddenUntilHovered = buffer.getUnsignedByte() == 1;
				int widgetId = buffer.getUnsignedShortBE();
				Widget.forId(widgetId).hiddenUntilHovered = hiddenUntilHovered;
				opcode = -1;
				return true;
			}
			if (UPDATE_CARRY_WEIGHT.equals(opcode))
			{
				if (currentTabId == 12)
				{
					redrawTabArea = true;
				}
				userWeight = buffer.getShortBE();
				opcode = -1;
				return true;
			}
			if (opcode == 233)
			{ // ???
				anInt1319 = buffer.getUnsignedByte();
				opcode = -1;
				return true;
			}
			if (opcode == 61)
			{ // ??? reset destination x? why?
				destinationX = 0;
				opcode = -1;
				return true;
			}
			if (SHOW_SIDEBAR_AND_GAME_WIDGET.equals(opcode))
			{
				int screenWidgetId = buffer.getUnsignedNegativeOffsetShortBE();
				int tabWidgetId = buffer.getUnsignedNegativeOffsetShortLE();
				if (openChatboxWidgetId != -1)
				{
					method44(openChatboxWidgetId);
					openChatboxWidgetId = -1;
					redrawChatbox = true;
				}
				if (fullscreenWidgetId != -1)
				{
					method44(fullscreenWidgetId);
					fullscreenWidgetId = -1;
					welcomeScreenRaised = true;
				}
				if (fullscreenWidgetChildId != -1)
				{
					method44(fullscreenWidgetChildId);
					fullscreenWidgetChildId = -1;
				}
				if (openScreenWidgetId != screenWidgetId)
				{
					method44(openScreenWidgetId);
					openScreenWidgetId = screenWidgetId;
				}
				if (tabAreaOverlayWidgetId != tabWidgetId)
				{
					method44(tabAreaOverlayWidgetId);
					tabAreaOverlayWidgetId = tabWidgetId;
				}
				if (inputType != 0)
				{
					inputType = 0;
					redrawChatbox = true;
				}
				redrawTabArea = true;
				drawTabIcons = true;
				aBoolean1239 = false;
				opcode = -1;
				return true;
			}
			if (CAMERA_SHAKE.equals(opcode))
			{
				int slot = buffer.getUnsignedByte();
				int jitter = buffer.getUnsignedByte();
				int amplitude = buffer.getUnsignedByte();
				int frequency = buffer.getUnsignedByte();
				customCameraActive[slot] = true;
				cameraJitter[slot] = jitter;
				cameraAmplitude[slot] = amplitude;
				cameraFrequency[slot] = frequency;
				quakeTimes[slot] = 0;
				opcode = -1;
				return true;
			}
			if (UPDATE_WIDGET_ITEMS_BY_SLOT.equals(opcode))
			{
				redrawTabArea = true;
				int widgetId = buffer.getUnsignedShortBE();
				Widget widget = Widget.forId(widgetId);
				while (buffer.currentPosition < packetSize)
				{
					int slot = buffer.getSmart();
					int id = buffer.getUnsignedShortBE();
					int amount = buffer.getUnsignedByte();
					if (amount == 255)
					{
						amount = buffer.getIntBE();
					}
					if (slot >= 0 && slot < widget.items.length)
					{
						widget.items[slot] = id;
						widget.itemAmounts[slot] = amount;
					}
				}
				opcode = -1;
				return true;
			}
			if (UPDATE_FRIEND.equals(opcode))
			{
				long friendNameLong = buffer.getLongBE();
				int worldId = buffer.getUnsignedByte();
				String friendName = TextUtils.formatName(TextUtils.longToName(friendNameLong));
				for (int k25 = 0; k25 < friendsCount; k25++)
				{
					if (friendNameLong != friends[k25])
					{
						continue;
					}
					if (friendWorlds[k25] != worldId)
					{
						friendWorlds[k25] = worldId;
						redrawTabArea = true;
						if (worldId > 0)
						{
							addChatMessage("", friendName + " has logged in.", 5);
						}
						if (worldId == 0)
						{
							addChatMessage("", friendName + " has logged out.", 5);
						}
					}
					friendName = null;
					break;
				}

				if (friendName != null && friendsCount < 200)
				{
					friends[friendsCount] = friendNameLong;
					friendUsernames[friendsCount] = friendName;
					friendWorlds[friendsCount] = worldId;
					friendsCount++;
					redrawTabArea = true;
				}
				for (boolean flag5 = false; !flag5; )
				{
					flag5 = true;
					// Reordering the list
					for (int i = 0; i < friendsCount - 1; i++)
					{
						if (friendWorlds[i] != world && friendWorlds[i + 1] == world
							|| friendWorlds[i] == 0 && friendWorlds[i + 1] != 0)
						{
							int world = friendWorlds[i];
							friendWorlds[i] = friendWorlds[i + 1];
							friendWorlds[i + 1] = world;
							String name = friendUsernames[i];
							friendUsernames[i] = friendUsernames[i + 1];
							friendUsernames[i + 1] = name;
							long friend = friends[i];
							friends[i] = friends[i + 1];
							friends[i + 1] = friend;
							redrawTabArea = true;
							flag5 = false;
						}
					}

				}

				opcode = -1;
				return true;
			}
			if (opcode == 58)
			{ // ??? enter amount interface?
				messagePromptRaised = false;
				inputType = 1;
				inputInputMessage = "";
				redrawChatbox = true;
				opcode = -1;
				return true;
			}
			if (SET_ACTIVE_TAB.equals(opcode))
			{
				currentTabId = buffer.getUnsignedInvertedByte();
				redrawTabArea = true;
				drawTabIcons = true;
				opcode = -1;
				return true;
			}
			if (CLEAR_GROUND_ITEMS_AND_LANDSCAPE_OBJECTS.equals(opcode))
			{
				placementY = buffer.getUnsignedPreNegativeOffsetByte();
				placementX = buffer.getUnsignedInvertedByte();
				for (int x = placementX; x < placementX + 8; x++)
				{
					for (int y = placementY; y < placementY + 8; y++)
					{
						if (!groundItems.isTileEmpty(plane, x, y))
						{
							groundItems.clearTile(plane, x, y);
							processGroundItems(x, y);
						}
					}

				}

				for (SpawnObjectNode spawnObjectNode = (SpawnObjectNode) spawnObjectList.first(); spawnObjectNode != null; spawnObjectNode = (SpawnObjectNode) spawnObjectList
					.next())
				{
					if (spawnObjectNode.x >= placementX && spawnObjectNode.x < placementX + 8
						&& spawnObjectNode.y >= placementY && spawnObjectNode.y < placementY + 8
						&& spawnObjectNode.plane == plane)
					{
						spawnObjectNode.cycle = 0;
					}
				}

				opcode = -1;
				return true;
			}
			if (SET_WIDGET_PLAYER_HEAD.equals(opcode))
			{
				int widgetId = buffer.getUnsignedNegativeOffsetShortLE();
				Widget.forId(widgetId).modelType = 3;
				if (localPlayer.npcDefinition == null) // maybe that is the appear as npc thing?
				{
					Widget.forId(widgetId).modelId = (localPlayer.appearanceColors[0] << 25) + (localPlayer.appearanceColors[4] << 20)
						+ (localPlayer.appearance[0] << 15) + (localPlayer.appearance[8] << 10)
						+ (localPlayer.appearance[11] << 5) + localPlayer.appearance[1];
				}
				else
				{
					Widget.forId(widgetId).modelId = (int) (0x12345678L + localPlayer.npcDefinition.id);
				}
				opcode = -1;
				return true;
			}
			if (PRIVATE_MESSAGE_RECEIVED.equals(opcode))
			{
				long fromPlayerIndex = buffer.getLongBE();
				int chatId = buffer.getIntBE();
				int fromPlayerRights = buffer.getUnsignedByte();
				boolean flag4 = false;
				for (int k28 = 0; k28 < 100; k28++)
				{
					if (anIntArray1258[k28] != chatId)
					{
						continue;
					}
					flag4 = true;
					break;
				}

				if (fromPlayerRights <= 1)
				{
					for (int k30 = 0; k30 < ignoresCount; k30++)
					{
						if (ignores[k30] != fromPlayerIndex)
						{
							continue;
						}
						flag4 = true;
						break;
					}

				}
				if (!flag4 && !inTutorialIsland)
				{
					try
					{
						anIntArray1258[anInt1152] = chatId;
						anInt1152 = (anInt1152 + 1) % 100;
						String s9 = ChatEncoder.get(packetSize - 13, buffer);
						if (fromPlayerRights != 3)
						{
							s9 = ChatCensor.censorString(s9);
						}
						if (fromPlayerRights == 2 || fromPlayerRights == 3)
						{
							addChatMessage("@cr2@" + TextUtils.formatName(TextUtils.longToName(fromPlayerIndex)),
								s9, 7);
						}
						else if (fromPlayerRights == 1)
						{
							addChatMessage("@cr1@" + TextUtils.formatName(TextUtils.longToName(fromPlayerIndex)),
								s9, 7);
						}
						else
						{
							addChatMessage(TextUtils.formatName(TextUtils.longToName(fromPlayerIndex)), s9, 3);
						}
					}
					catch (Exception exception1)
					{
						SignLink.reportError("cde1");
					}
				}
				opcode = -1;
				return true;
			}
			if (UPDATE_GROUND_ITEMS_AND_LANDSCAPE_OBJECTS.equals(opcode))
			{
				placementX = buffer.getUnsignedByte();
				placementY = buffer.getUnsignedPostNegativeOffsetByte();
				while (buffer.currentPosition < packetSize)
				{
					int subPacketId = buffer.getUnsignedByte();
					parsePlacementPacket(buffer, subPacketId);
				}
				opcode = -1;
				return true;
			}
			if (SHOW_GAME_WIDGET.equals(opcode))
			{
				int widgetId = buffer.getUnsignedNegativeOffsetShortLE();
				method112((byte) 36, widgetId);
				if (tabAreaOverlayWidgetId != -1)
				{
					method44(tabAreaOverlayWidgetId);
					tabAreaOverlayWidgetId = -1;
					redrawTabArea = true;
					drawTabIcons = true;
				}
				if (openChatboxWidgetId != -1)
				{
					method44(openChatboxWidgetId);
					openChatboxWidgetId = -1;
					redrawChatbox = true;
				}
				if (fullscreenWidgetId != -1)
				{
					method44(fullscreenWidgetId);
					fullscreenWidgetId = -1;
					welcomeScreenRaised = true;
				}
				if (fullscreenWidgetChildId != -1)
				{
					method44(fullscreenWidgetChildId);
					fullscreenWidgetChildId = -1;
				}
				if (openScreenWidgetId != widgetId)
				{
					method44(openScreenWidgetId);
					openScreenWidgetId = widgetId;
				}
				if (inputType != 0)
				{
					inputType = 0;
					redrawChatbox = true;
				}
				aBoolean1239 = false;
				opcode = -1;
				return true;
			}
			if (SHOW_SIDEBAR_OVERLAY_WIDGET.equals(opcode))
			{
				int widgetId = buffer.getUnsignedNegativeOffsetShortLE();
				method112((byte) 36, widgetId);
				if (openChatboxWidgetId != -1)
				{
					method44(openChatboxWidgetId);
					openChatboxWidgetId = -1;
					redrawChatbox = true;
				}
				if (fullscreenWidgetId != -1)
				{
					method44(fullscreenWidgetId);
					fullscreenWidgetId = -1;
					welcomeScreenRaised = true;
				}
				if (fullscreenWidgetChildId != -1)
				{
					method44(fullscreenWidgetChildId);
					fullscreenWidgetChildId = -1;
				}
				if (openScreenWidgetId != -1)
				{
					method44(openScreenWidgetId);
					openScreenWidgetId = -1;
				}
				if (tabAreaOverlayWidgetId != widgetId)
				{
					method44(tabAreaOverlayWidgetId);
					tabAreaOverlayWidgetId = widgetId;
				}
				if (inputType != 0)
				{
					inputType = 0;
					redrawChatbox = true;
				}
				redrawTabArea = true;
				drawTabIcons = true;
				aBoolean1239 = false;
				opcode = -1;
				return true;
			}
			if (UPDATE_SKILL.equals(opcode))
			{
				redrawTabArea = true;
				int skillIndex = buffer.getUnsignedInvertedByte();
				int level = buffer.getUnsignedByte();
				int xp = buffer.getIntBE();
				skillExperience[skillIndex] = xp;
				skillLevel[skillIndex] = level;
				skillMaxLevel[skillIndex] = 1;
				for (int l = 0; l < 98; l++)
				{
					if (xp >= SKILL_EXPERIENCE[l])
					{
						skillMaxLevel[skillIndex] = l + 2;
					}
				}

				opcode = -1;
				return true;
			}
			if (UPDATE_ALL_WIDGET_ITEMS.equals(opcode))
			{
				redrawTabArea = true;
				int widgetId = buffer.getUnsignedShortBE();
				Widget widget = Widget.forId(widgetId);
				int items = buffer.getUnsignedShortBE();
				for (int item = 0; item < items; item++)
				{
					widget.items[item] = buffer.getUnsignedNegativeOffsetShortLE();
					int amount = buffer.getUnsignedInvertedByte();
					if (amount == 255)
					{
						amount = buffer.getIntLE();
					}
					widget.itemAmounts[item] = amount;
				}

				for (int i26 = items; i26 < widget.items.length; i26++)
				{
					widget.items[i26] = 0;
					widget.itemAmounts[i26] = 0;
				}

				opcode = -1;
				return true;
			}
			if (UPDATE_ACTIVE_MAP_REGION.equals(opcode) || CONSTRUCT_MAP_REGION.equals(opcode))
			{
				int tmpChunkX = chunkX;
				int tmpChunkY = chunkY;
				if (UPDATE_ACTIVE_MAP_REGION.equals(opcode))
				{
					tmpChunkY = buffer.getUnsignedShortBE();
					tmpChunkX = buffer.getUnsignedNegativeOffsetShortLE();
					loadGeneratedMap = false;
				}
				if (CONSTRUCT_MAP_REGION.equals(opcode))
				{
					tmpChunkX = buffer.getUnsignedNegativeOffsetShortBE();
					buffer.initBitAccess();
					for (int z = 0; z < 4; z++)
					{
						for (int x = 0; x < 13; x++)
						{
							for (int y = 0; y < 13; y++)
							{
								int flag = buffer.getBits(1);
								if (flag == 1)
								{
									constructedMapPalette[z][x][y] = buffer.getBits(26);
								}
								else
								{
									constructedMapPalette[z][x][y] = -1;
								}
							}

						}

					}

					buffer.finishBitAccess();
					tmpChunkY = buffer.getUnsignedNegativeOffsetShortBE();
					loadGeneratedMap = true;
				}
				if (chunkX == tmpChunkX && chunkY == tmpChunkY && loadingStage == 2)
				{
					opcode = -1;
					return true;
				}
				chunkX = tmpChunkX;
				chunkY = tmpChunkY;
				nextTopLeftTileX = (chunkX - 6) * 8;
				nextTopRightTileY = (chunkY - 6) * 8;
				aBoolean1067 = false;
				if ((chunkX / 8 == 48 || chunkX / 8 == 49) && chunkY / 8 == 48)
				{
					aBoolean1067 = true;
				}
				if (chunkX / 8 == 48 && chunkY / 8 == 148)
				{
					aBoolean1067 = true;
				}
				loadingStage = 1;
				loadRegionTime = System.currentTimeMillis();
				method125(null, "Loading - please wait.");
				if (UPDATE_ACTIVE_MAP_REGION.equals(opcode))
				{
					int count = 0;
					for (int fileX = (chunkX - 6) / 8; fileX <= (chunkX + 6) / 8; fileX++)
					{
						for (int fileY = (chunkY - 6) / 8; fileY <= (chunkY + 6) / 8; fileY++)
						{
							count++;
						}

					}

					terrainData = new byte[count][];
					objectData = new byte[count][];
					mapCoordinates = new int[count];
					terrainDataIds = new int[count];
					objectDataIds = new int[count];
					count = 0;
					for (int fileX = (chunkX - 6) / 8; fileX <= (chunkX + 6) / 8; fileX++)
					{
						for (int fileY = (chunkY - 6) / 8; fileY <= (chunkY + 6) / 8; fileY++)
						{
							mapCoordinates[count] = (fileX << 8) + fileY;
							if (aBoolean1067
								&& (fileY == 49 || fileY == 149 || fileY == 147 || fileX == 50 || fileX == 49 && fileY == 47))
							{
								terrainDataIds[count] = -1;
								objectDataIds[count] = -1;
								count++;
							}
							else
							{
								int l30 = terrainDataIds[count] = onDemandRequester.regId(0, fileX, fileY, 0);
								if (l30 != -1)
								{
									onDemandRequester.request(3, l30);
								}
								int i32 = objectDataIds[count] = onDemandRequester.regId(0, fileX, fileY, 1);
								if (i32 != -1)
								{
									onDemandRequester.request(3, i32);
								}
								count++;
							}
						}

					}

				}
				if (CONSTRUCT_MAP_REGION.equals(opcode))
				{
					int uniqueCount = 0;
					int[] fileIndices = new int[676];
					for (int tileZ = 0; tileZ < 4; tileZ++)
					{
						for (int tileX = 0; tileX < 13; tileX++)
						{
							for (int tileY = 0; tileY < 13; tileY++)
							{
								int data = constructedMapPalette[tileZ][tileX][tileY];
								if (data != -1)
								{
									int chunkX = data >> 14 & 0x3ff;
									int chunkY = data >> 3 & 0x7ff;
									int fileIndex = (chunkX / 8 << 8) + chunkY / 8;
									for (int pos = 0; pos < uniqueCount; pos++)
									{
										if (fileIndices[pos] != fileIndex)
										{
											continue;
										}
										fileIndex = -1;
										break;
									}

									if (fileIndex != -1)
									{
										fileIndices[uniqueCount++] = fileIndex;
									}
								}
							}

						}

					}

					terrainData = new byte[uniqueCount][];
					objectData = new byte[uniqueCount][];
					mapCoordinates = new int[uniqueCount];
					terrainDataIds = new int[uniqueCount];
					objectDataIds = new int[uniqueCount];
					for (int pos = 0; pos < uniqueCount; pos++)
					{
						int j31 = mapCoordinates[pos] = fileIndices[pos];
						int fileX = j31 >> 8 & 0xff;
						int fileY = j31 & 0xff;
						int i34 = terrainDataIds[pos] = onDemandRequester.regId(0, fileX, fileY, 0);
						if (i34 != -1)
						{
							onDemandRequester.request(3, i34);
						}
						int k34 = objectDataIds[pos] = onDemandRequester.regId(0, fileX, fileY, 1);
						if (k34 != -1)
						{
							onDemandRequester.request(3, k34);
						}
					}

				}
				int deltaX = nextTopLeftTileX - topLeftTileX;
				int deltaY = nextTopRightTileY - topLeftTileY;
				topLeftTileX = nextTopLeftTileX;
				topLeftTileY = nextTopRightTileY;
				for (int id = 0; id < 16384; id++)
				{
					Npc npc = npcs[id];
					if (npc != null)
					{
						for (int pos = 0; pos < 10; pos++)
						{
							((Actor) (npc)).pathX[pos] -= deltaX;
							((Actor) (npc)).pathY[pos] -= deltaY;
						}

						npc.worldX -= deltaX * 128;
						npc.worldY -= deltaY * 128;
					}
				}

				for (int id = 0; id < maxPlayerCount; id++)
				{
					Player player = players[id];
					if (player != null)
					{
						for (int pos = 0; pos < 10; pos++)
						{
							((Actor) (player)).pathX[pos] -= deltaX;
							((Actor) (player)).pathY[pos] -= deltaY;
						}

						player.worldX -= deltaX * 128;
						player.worldY -= deltaY * 128;
					}
				}

				loadingMap = true;
				byte byte1 = 0;
				byte byte2 = 104;
				byte byte3 = 1;
				if (deltaX < 0)
				{
					byte1 = 103;
					byte2 = -1;
					byte3 = -1;
				}
				byte byte4 = 0;
				byte byte5 = 104;
				byte byte6 = 1;
				if (deltaY < 0)
				{
					byte4 = 103;
					byte5 = -1;
					byte6 = -1;
				}
				for (int i35 = byte1; i35 != byte2; i35 += byte3)
				{
					for (int j35 = byte4; j35 != byte5; j35 += byte6)
					{
						int k35 = i35 + deltaX;
						int l35 = j35 + deltaY;
						for (int i36 = 0; i36 < 4; i36++)
						{
							if (k35 >= 0 && l35 >= 0 && k35 < 104 && l35 < 104)
							{
								groundItems.setTile(i36, i35, j35, groundItems.getTile(i36, k35, l35));
							}
							else
							{
								groundItems.clearTile(i36, i35, j35);
							}
						}

					}

				}

				for (SpawnObjectNode spawnObjectNode_1 = (SpawnObjectNode) spawnObjectList.first(); spawnObjectNode_1 != null; spawnObjectNode_1 = (SpawnObjectNode) spawnObjectList
					.next())
				{
					spawnObjectNode_1.x -= deltaX;
					spawnObjectNode_1.y -= deltaY;
					if (spawnObjectNode_1.x < 0 || spawnObjectNode_1.y < 0 || spawnObjectNode_1.x >= 104
						|| spawnObjectNode_1.y >= 104)
					{
						spawnObjectNode_1.remove();
					}
				}

				if (destinationX != 0)
				{
					destinationX -= deltaX;
					destinationY -= deltaY;
				}
				cutsceneActive = false;
				opcode = -1;
				return true;
			}
			if (SYSTEM_UPDATE.equals(opcode))
			{
				systemUpdateTime = buffer.getUnsignedShortLE() * 30;
				opcode = -1;
				return true;
			}
			if (PLAY_POSITION_SOUND.equals(opcode) || UPDATE_GROUND_ITEM_AMOUNT.equals(opcode) || opcode == 203 || SET_PLAYER_GROUND_ITEM.equals(opcode)
				|| SHOW_STILL_GRAPHICS.equals(opcode) || SHOW_PROJECTILE.equals(opcode) || REMOVE_GROUND_ITEM.equals(opcode)
				|| SET_GROUND_ITEM.equals(opcode) || opcode == 142 || REMOVE_LANDSCAPE_OBJECT.equals(opcode) || SET_LANDSCAPE_OBJECT.equals(opcode))
			{
				parsePlacementPacket(buffer, opcode);
				opcode = -1;
				return true;
			}
			if (UPDATE_RUN_ENERGY.equals(opcode))
			{
				if (currentTabId == 12)
				{
					redrawTabArea = true;
				}
				runEnergy = buffer.getUnsignedByte();
				opcode = -1;
				return true;
			}
			if (SET_WIDGET_ITEM_MODEL.equals(opcode))
			{
				int scale = buffer.getUnsignedShortBE();
				int itemId = buffer.getUnsignedShortLE();
				int widgetId = buffer.getUnsignedNegativeOffsetShortLE();
				if (itemId == 65535)
				{
					Widget.forId(widgetId).modelType = 0;
					opcode = -1;
					return true;
				}
				else
				{
					ItemDefinition class16 = ItemDefinition.lookup(itemId);
					Widget.forId(widgetId).modelType = 4;
					Widget.forId(widgetId).modelId = itemId;
					Widget.forId(widgetId).rotationX = class16.modelRotationX;
					Widget.forId(widgetId).rotationY = class16.modelRotationY;
					Widget.forId(widgetId).zoom = (class16.modelScale * 100) / scale;
					opcode = -1;
					return true;
				}
			}
			if (opcode == 3)
			{ // camera? something with cutscenes? Set cutscene camera position?...
				cutsceneActive = true;
				anInt874 = buffer.getUnsignedByte();
				anInt875 = buffer.getUnsignedByte();
				anInt876 = buffer.getUnsignedShortBE();
				anInt877 = buffer.getUnsignedByte();
				anInt878 = buffer.getUnsignedByte();
				if (anInt878 >= 100)
				{
					cameraX = anInt874 * 128 + 64;
					cameraY = anInt875 * 128 + 64;
					cameraZ = getFloorDrawHeight(plane, cameraX, cameraY) - anInt876;
				}
				opcode = -1;
				return true;
			}
			if (SET_WIDGET_ANIMATION.equals(opcode))
			{
				int widgetId = buffer.getUnsignedNegativeOffsetShortLE();
				int animationId = buffer.getNegativeOffsetShortBE();
				Widget widget = Widget.forId(widgetId);
				if (widget.disabledAnimation != animationId || animationId == -1)
				{
					widget.disabledAnimation = animationId;
					widget.animationFrame = 0;
					widget.animationDuration = 0;
				}
				opcode = -1;
				return true;
			}
			if (NPC_UPDATING.equals(opcode))
			{
				updateNpcs(buffer, packetSize);
				opcode = -1;
				return true;
			}
			if (UPDATE_IGNORE_LIST.equals(opcode))
			{
				ignoresCount = packetSize / 8;
				for (int i = 0; i < ignoresCount; i++)
				{
					ignores[i] = buffer.getLongBE();
				}

				opcode = -1;
				return true;
			}
			if (SET_TAB_WIDGET.equals(opcode))
			{
				int sidebarIndex = buffer.getUnsignedPreNegativeOffsetByte();
				int widgetId = buffer.getUnsignedNegativeOffsetShortBE();
				if (widgetId == 65535)
				{
					widgetId = -1;
				}
				if (tabWidgetIds[sidebarIndex] != widgetId)
				{
					method44(tabWidgetIds[sidebarIndex]);
					tabWidgetIds[sidebarIndex] = widgetId;
				}
				redrawTabArea = true;
				drawTabIcons = true;
				opcode = -1;
				return true;
			}
			if (CLEAR_WIDGET_ITEMS.equals(opcode))
			{
				int widgetId = buffer.getUnsignedShortLE();
				Widget widget = Widget.forId(widgetId);
				for (int k21 = 0; k21 < widget.items.length; k21++)
				{
					widget.items[k21] = -1;
					widget.items[k21] = 0;
				}

				opcode = -1;
				return true;
			}
			if (FLASH_TAB_ICON.equals(opcode))
			{
				flashingTabId = buffer.getUnsignedByte();
				if (flashingTabId == currentTabId)
				{
					if (flashingTabId == 3)
					{
						currentTabId = 1;
					}
					else
					{
						currentTabId = 3;
					}
					redrawTabArea = true;
				}
				opcode = -1;
				return true;
			}
			if (RESET_CUTSCENE_CAMERA.equals(opcode))
			{
				cutsceneActive = false;
				for (int j9 = 0; j9 < 5; j9++)
				{
					customCameraActive[j9] = false;
				}

				opcode = -1;
				return true;
			}
			if (UPDATE_MEMBERSHIP_AND_WORLD_INDEX.equals(opcode))
			{
				playerMembers = buffer.getUnsignedByte();
				thisPlayerServerId = buffer.getUnsignedShortLE();
				opcode = -1;
				return true;
			}
			if (SEND_REFERENCE_POSITION.equals(opcode))
			{
				placementX = buffer.getUnsignedInvertedByte();
				placementY = buffer.getUnsignedPostNegativeOffsetByte();
				opcode = -1;
				return true;
			}
			if (SHOW_FULLSCREEN_WIDGET.equals(opcode))
			{
				int fullscreenWidgetChildId = buffer.getUnsignedShortLE();
				int fullscreenWidgetId = buffer.getUnsignedNegativeOffsetShortBE();
				method112((byte) 36, fullscreenWidgetId);
				if (fullscreenWidgetChildId != -1)
				{
					method112((byte) 36, fullscreenWidgetChildId);
				}
				if (openScreenWidgetId != -1)
				{
					method44(openScreenWidgetId);
					openScreenWidgetId = -1;
				}
				if (tabAreaOverlayWidgetId != -1)
				{
					method44(tabAreaOverlayWidgetId);
					tabAreaOverlayWidgetId = -1;
				}
				if (openChatboxWidgetId != -1)
				{
					method44(openChatboxWidgetId);
					openChatboxWidgetId = -1;
				}
				if (this.fullscreenWidgetId != fullscreenWidgetId)
				{
					method44(this.fullscreenWidgetId);
					this.fullscreenWidgetId = fullscreenWidgetId;
				}
				if (this.fullscreenWidgetChildId != fullscreenWidgetId)
				{
					method44(this.fullscreenWidgetChildId);
					this.fullscreenWidgetChildId = fullscreenWidgetChildId;
				}
				inputType = 0;
				aBoolean1239 = false;
				opcode = -1;
				return true;
			}
			if (UPDATE_FRIEND_LIST_STATUS.equals(opcode))
			{
				friendListStatus = buffer.getUnsignedByte();
				redrawTabArea = true;
				opcode = -1;
				return true;
			}
			if (opcode == 18)
			{ // ??? interface setting something
				int l9 = buffer.getUnsignedShortBE();
				int widgetId = buffer.getUnsignedNegativeOffsetShortBE();
				int l21 = buffer.getUnsignedShortLE();
				Widget.forId(widgetId).anInt218 = (l9 << 16) + l21;
				opcode = -1;
				return true;
			}
			if (PLAYER_UPDATING.equals(opcode))
			{
				updatePlayers(packetSize, buffer);
				loadingMap = false;
				opcode = -1;
				return true;
			}
			if (RESET_WIDGET_SETTINGS.equals(opcode))
			{
				for (int i = 0; i < widgetSettings.length; i++)
				{
					if (widgetSettings[i] != anIntArray1005[i])
					{
						widgetSettings[i] = anIntArray1005[i];
						updateVarp(0, i);
						redrawTabArea = true;
					}
				}

				opcode = -1;
				return true;
			}
			if (UPDATE_WIDGET_STRING.equals(opcode))
			{
				int widgetId = buffer.getUnsignedNegativeOffsetShortLE();
				String newText = buffer.getString();
				Widget.forId(widgetId).disabledText = newText;
				if (Widget.forId(widgetId).parentId == tabWidgetIds[currentTabId])
				{
					redrawTabArea = true;
				}
				opcode = -1;
				return true;
			}
			if (UPDATE_WIDGET_SCROLL_POSITION.equals(opcode))
			{
				int widgetId = buffer.getUnsignedShortBE();
				int scrollPosition = buffer.getUnsignedNegativeOffsetShortLE();
				Widget widget = Widget.forId(widgetId);
				if (widget != null && widget.type == 0)
				{
					if (scrollPosition < 0)
					{
						scrollPosition = 0;
					}
					if (scrollPosition > widget.scrollLimit - widget.height)
					{
						scrollPosition = widget.scrollLimit - widget.height;
					}
					widget.scrollPosition = scrollPosition;
				}
				opcode = -1;
				return true;
			}
			SignLink.reportError("T1 - " + opcode + "," + packetSize + " - " + secondLastOpcode + "," + thirdLastOpcode);
			logout();
		}
		catch (IOException _ex)
		{
			dropClient();
		}
		catch (Exception exception)
		{
			String s1 = "T2 - " + opcode + "," + secondLastOpcode + "," + thirdLastOpcode + " - " + packetSize + ","
				+ (nextTopLeftTileX + localPlayer.pathX[0]) + ","
				+ (nextTopRightTileY + localPlayer.pathY[0]) + " - ";
			for (int j16 = 0; j16 < packetSize && j16 < 50; j16++)
			{
				s1 = s1 + buffer.buffer[j16] + ",";
			}

			SignLink.reportError(s1);
			logout();

			exception.printStackTrace();
		}
		return true;
	}

	private void drawMenuTooltip()
	{
		if (menuActionRow < 2 && itemSelected == 0 && widgetSelected == 0)
		{
			return;
		}

		String str;

		if (itemSelected == 1 && menuActionRow < 2)
		{
			str = "Use " + selectedItemName + " with...";
		}
		else if (widgetSelected == 1 && menuActionRow < 2)
		{
			str = selectedWidgetName + "...";
		}
		else
		{
			str = menuActionTexts[menuActionRow - 1];
		}

		if (menuActionRow > 2)
		{
			str = str + "@whi@ / " + (menuActionRow - 2) + " more options";
		}

		fontBold.drawShadowedSeededAlphaString(str, 4, 15, 0xffffff, pulseCycle / 1000);
	}

	private boolean walk(boolean flag, boolean flag1, int dstY, int srcY, int k, int l, int packetType, int j1, int dstX, int l1,
							int i2, int srcX)
	{
		byte byte0 = 104;
		byte byte1 = 104;
		for (int x = 0; x < byte0; x++)
		{
			for (int y = 0; y < byte1; y++)
			{
				anIntArrayArray885[x][y] = 0;
				cost[x][y] = 0x5f5e0ff;
			}

		}

		int curX = srcX;
		int curY = srcY;
		anIntArrayArray885[srcX][srcY] = 99;
		cost[srcX][srcY] = 0;
		int k3 = 0;
		int l3 = 0;
		anIntArray1123[k3] = srcX;
		anIntArray1124[k3++] = srcY;
		boolean flag2 = false;
		int i4 = anIntArray1123.length;
		int[][] masks = currentCollisionMap[plane].clippingData;
		while (l3 != k3)
		{
			curX = anIntArray1123[l3];
			curY = anIntArray1124[l3];
			l3 = (l3 + 1) % i4;
			if (curX == dstX && curY == dstY)
			{
				flag2 = true;
				break;
			}
			if (j1 != 0)
			{
				if ((j1 < 5 || j1 == 10) && currentCollisionMap[plane].isWalkableA(curX, curY, dstX, dstY, j1 - 1, i2))
				{
					flag2 = true;
					break;
				}
				if (j1 < 10 && currentCollisionMap[plane].isWalkableB(curX, curY, dstX, dstY, j1 - 1, i2))
				{
					flag2 = true;
					break;
				}
			}
			if (k != 0 && l != 0 && currentCollisionMap[plane].reachedFacingObject(curX, curY, dstX, dstY, k, l, l1))
			{
				flag2 = true;
				break;
			}
			int nextCost = cost[curX][curY] + 1;
			if (curX > 0 && anIntArrayArray885[curX - 1][curY] == 0 && (masks[curX - 1][curY] & 0x1280108) == 0)
			{
				anIntArray1123[k3] = curX - 1;
				anIntArray1124[k3] = curY;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[curX - 1][curY] = 2;
				cost[curX - 1][curY] = nextCost;
			}
			if (curX < byte0 - 1 && anIntArrayArray885[curX + 1][curY] == 0 && (masks[curX + 1][curY] & 0x1280180) == 0)
			{
				anIntArray1123[k3] = curX + 1;
				anIntArray1124[k3] = curY;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[curX + 1][curY] = 8;
				cost[curX + 1][curY] = nextCost;
			}
			if (curY > 0 && anIntArrayArray885[curX][curY - 1] == 0 && (masks[curX][curY - 1] & 0x1280102) == 0)
			{
				anIntArray1123[k3] = curX;
				anIntArray1124[k3] = curY - 1;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[curX][curY - 1] = 1;
				cost[curX][curY - 1] = nextCost;
			}
			if (curY < byte1 - 1 && anIntArrayArray885[curX][curY + 1] == 0 && (masks[curX][curY + 1] & 0x1280120) == 0)
			{
				anIntArray1123[k3] = curX;
				anIntArray1124[k3] = curY + 1;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[curX][curY + 1] = 4;
				cost[curX][curY + 1] = nextCost;
			}
			if (curX > 0 && curY > 0 && anIntArrayArray885[curX - 1][curY - 1] == 0 && (masks[curX - 1][curY - 1] & 0x128010e) == 0
				&& (masks[curX - 1][curY] & 0x1280108) == 0 && (masks[curX][curY - 1] & 0x1280102) == 0)
			{
				anIntArray1123[k3] = curX - 1;
				anIntArray1124[k3] = curY - 1;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[curX - 1][curY - 1] = 3;
				cost[curX - 1][curY - 1] = nextCost;
			}
			if (curX < byte0 - 1 && curY > 0 && anIntArrayArray885[curX + 1][curY - 1] == 0
				&& (masks[curX + 1][curY - 1] & 0x1280183) == 0 && (masks[curX + 1][curY] & 0x1280180) == 0
				&& (masks[curX][curY - 1] & 0x1280102) == 0)
			{
				anIntArray1123[k3] = curX + 1;
				anIntArray1124[k3] = curY - 1;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[curX + 1][curY - 1] = 9;
				cost[curX + 1][curY - 1] = nextCost;
			}
			if (curX > 0 && curY < byte1 - 1 && anIntArrayArray885[curX - 1][curY + 1] == 0
				&& (masks[curX - 1][curY + 1] & 0x1280138) == 0 && (masks[curX - 1][curY] & 0x1280108) == 0
				&& (masks[curX][curY + 1] & 0x1280120) == 0)
			{
				anIntArray1123[k3] = curX - 1;
				anIntArray1124[k3] = curY + 1;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[curX - 1][curY + 1] = 6;
				cost[curX - 1][curY + 1] = nextCost;
			}
			if (curX < byte0 - 1 && curY < byte1 - 1 && anIntArrayArray885[curX + 1][curY + 1] == 0
				&& (masks[curX + 1][curY + 1] & 0x12801e0) == 0 && (masks[curX + 1][curY] & 0x1280180) == 0
				&& (masks[curX][curY + 1] & 0x1280120) == 0)
			{
				anIntArray1123[k3] = curX + 1;
				anIntArray1124[k3] = curY + 1;
				k3 = (k3 + 1) % i4;
				anIntArrayArray885[curX + 1][curY + 1] = 12;
				cost[curX + 1][curY + 1] = nextCost;
			}
		}
		anInt1126 = 0;
		if (!flag2)
		{
			if (flag)
			{
				int l4 = 1000;
				int j5 = 100;
				byte byte2 = 10;
				for (int i6 = dstX - byte2; i6 <= dstX + byte2; i6++)
				{
					for (int k6 = dstY - byte2; k6 <= dstY + byte2; k6++)
					{
						if (i6 >= 0 && k6 >= 0 && i6 < 104 && k6 < 104 && cost[i6][k6] < 100)
						{
							int i7 = 0;
							if (i6 < dstX)
							{
								i7 = dstX - i6;
							}
							else if (i6 > (dstX + k) - 1)
							{
								i7 = i6 - ((dstX + k) - 1);
							}
							int j7 = 0;
							if (k6 < dstY)
							{
								j7 = dstY - k6;
							}
							else if (k6 > (dstY + l) - 1)
							{
								j7 = k6 - ((dstY + l) - 1);
							}
							int k7 = i7 * i7 + j7 * j7;
							if (k7 < l4 || k7 == l4 && cost[i6][k6] < j5)
							{
								l4 = k7;
								j5 = cost[i6][k6];
								curX = i6;
								curY = k6;
							}
						}
					}

				}

				if (l4 == 1000)
				{
					return false;
				}
				if (curX == srcX && curY == srcY)
				{
					return false;
				}
				anInt1126 = 1;
			}
			else
			{
				return false;
			}
		}
		l3 = 0;
		if (flag1)
		{
			startup();
		}
		anIntArray1123[l3] = curX;
		anIntArray1124[l3++] = curY;
		int k5;
		for (int i5 = k5 = anIntArrayArray885[curX][curY]; curX != srcX || curY != srcY; i5 = anIntArrayArray885[curX][curY])
		{
			if (i5 != k5)
			{
				k5 = i5;
				anIntArray1123[l3] = curX;
				anIntArray1124[l3++] = curY;
			}
			if ((i5 & 2) != 0)
			{
				curX++;
			}
			else if ((i5 & 8) != 0)
			{
				curX--;
			}
			if ((i5 & 1) != 0)
			{
				curY++;
			}
			else if ((i5 & 4) != 0)
			{
				curY--;
			}
		}

		if (l3 > 0)
		{
			int j4 = l3;
			if (j4 > 25)
			{
				j4 = 25;
			}
			l3--;
			int l5 = anIntArray1123[l3];
			int j6 = anIntArray1124[l3];
			if (packetType == 0)
			{
				outBuffer.putOpcode(28);
				outBuffer.putByte(j4 + j4 + 3);
			}
			if (packetType == 1)
			{
				outBuffer.putOpcode(213);
				outBuffer.putByte(j4 + j4 + 3 + 14);
			}
			if (packetType == 2)
			{
				outBuffer.putOpcode(247);
				outBuffer.putByte(j4 + j4 + 3);
			}
			outBuffer.putOffsetShortLE(l5 + nextTopLeftTileX);
			outBuffer.putByte(super.keyStatus[5] != 1 ? 0 : 1);
			outBuffer.putOffsetShortLE(j6 + nextTopRightTileY);
			destinationX = anIntArray1123[0];
			destinationY = anIntArray1124[0];
			for (int l6 = 1; l6 < j4; l6++)
			{
				l3--;
				outBuffer.putByte(anIntArray1123[l3] - l5);
				outBuffer.putNegativeOffsetByte(anIntArray1124[l3] - j6);
			}

			return true;
		}
		return packetType != 1;
	}

	private void processLocationCreation()
	{
		if (loadingStage == 2)
		{
			for (SpawnObjectNode spawnObjectNode = (SpawnObjectNode) spawnObjectList.first(); spawnObjectNode != null; spawnObjectNode = (SpawnObjectNode) spawnObjectList
				.next())
			{
				if (spawnObjectNode.cycle > 0)
				{
					spawnObjectNode.cycle--;
				}
				if (spawnObjectNode.cycle == 0)
				{
					if (spawnObjectNode.index < 0
						|| MapRegion.method170(spawnObjectNode.type, spawnObjectNode.index))
					{
						addLocation(spawnObjectNode.x, spawnObjectNode.y, spawnObjectNode.plane, spawnObjectNode.index, spawnObjectNode.rotation,
							spawnObjectNode.type,
							spawnObjectNode.classType);
						spawnObjectNode.remove();
					}
				}
				else
				{
					if (spawnObjectNode.spawnCycle > 0)
					{
						spawnObjectNode.spawnCycle--;
					}
					if (spawnObjectNode.spawnCycle == 0
						&& spawnObjectNode.x >= 1
						&& spawnObjectNode.y >= 1
						&& spawnObjectNode.x <= 102
						&& spawnObjectNode.y <= 102
						&& (spawnObjectNode.locationIndex < 0 || MapRegion.method170(spawnObjectNode.locationType,
						spawnObjectNode.locationIndex)))
					{
						addLocation(spawnObjectNode.x, spawnObjectNode.y, spawnObjectNode.plane, spawnObjectNode.locationIndex, spawnObjectNode.locationRotation,
							spawnObjectNode.locationType,
							spawnObjectNode.classType);
						spawnObjectNode.spawnCycle = -1;
						if (spawnObjectNode.locationIndex == spawnObjectNode.index && spawnObjectNode.index == -1)
						{
							spawnObjectNode.remove();
						}
						else if (spawnObjectNode.locationIndex == spawnObjectNode.index
							&& spawnObjectNode.locationRotation == spawnObjectNode.rotation
							&& spawnObjectNode.locationType == spawnObjectNode.type)
						{
							spawnObjectNode.remove();
						}
					}
				}
			}

		}
	}

	private void processPlayerMenuOptions(Player player, int x, int y, int index)
	{
		if (player == localPlayer)
		{
			return;
		}
		if (menuActionRow >= 400)
		{
			return;
		}
		String s;
		if (player.skillLevel == 0)
		{
			s = player.playerName
				+ getCombatLevelColour(localPlayer.combatLevel, player.combatLevel) + " (level-"
				+ player.combatLevel + ")";
		}
		else
		{
			s = player.playerName + " (skill-" + player.skillLevel + ")";
		}
		if (itemSelected == 1)
		{
			menuActionTexts[menuActionRow] = "Use " + selectedItemName + " with @whi@" + s;
			menuActionTypes[menuActionRow] = 596;
			selectedMenuActions[menuActionRow] = index;
			firstMenuOperand[menuActionRow] = x;
			secondMenuOperand[menuActionRow] = y;
			menuActionRow++;
		}
		else if (widgetSelected == 1)
		{
			if ((selectedMask & 8) == 8)
			{
				menuActionTexts[menuActionRow] = selectedWidgetName + " @whi@" + s;
				menuActionTypes[menuActionRow] = 918;
				selectedMenuActions[menuActionRow] = index;
				firstMenuOperand[menuActionRow] = x;
				secondMenuOperand[menuActionRow] = y;
				menuActionRow++;
			}
		}
		else
		{
			for (int i1 = 4; i1 >= 0; i1--)
			{
				if (aStringArray1069[i1] != null)
				{
					menuActionTexts[menuActionRow] = aStringArray1069[i1] + " @whi@" + s;
					char c = '\0';
					if (aStringArray1069[i1].equalsIgnoreCase("attack"))
					{
						if (player.combatLevel > localPlayer.combatLevel)
						{
							c = '\u07D0';
						}
						if (localPlayer.teamId != 0 && player.teamId != 0)
						{
							if (localPlayer.teamId == player.teamId)
							{
								c = '\u07D0';
							}
							else
							{
								c = '\0';
							}
						}
					}
					else if (aBooleanArray1070[i1])
					{
						c = '\u07D0';
					}
					if (i1 == 0)
					{
						menuActionTypes[menuActionRow] = 200 + c;
					}
					if (i1 == 1)
					{
						menuActionTypes[menuActionRow] = 493 + c;
					}
					if (i1 == 2)
					{
						menuActionTypes[menuActionRow] = 408 + c;
					}
					if (i1 == 3)
					{
						menuActionTypes[menuActionRow] = 677 + c;
					}
					if (i1 == 4)
					{
						menuActionTypes[menuActionRow] = 876 + c;
					}
					selectedMenuActions[menuActionRow] = index;
					firstMenuOperand[menuActionRow] = x;
					secondMenuOperand[menuActionRow] = y;
					menuActionRow++;
				}
			}

		}
		for (int j1 = 0; j1 < menuActionRow; j1++)
		{
			if (menuActionTypes[j1] == 14)
			{
				menuActionTexts[j1] = "Walk here @whi@" + s;
				return;
			}
		}

	}

	private void method39()
	{
		if (super.clickType == 1)
		{
			if (super.clickX >= 6 && super.clickX <= 106 && super.clickY >= 467 && super.clickY <= 499)
			{
				publicChatMode = (publicChatMode + 1) % 4;
				redrawChatMode = true;
				redrawChatbox = true;
				outBuffer.putOpcode(176);
				outBuffer.putByte(publicChatMode);
				outBuffer.putByte(privateChatMode);
				outBuffer.putByte(tradeMode);
			}
			if (super.clickX >= 135 && super.clickX <= 235 && super.clickY >= 467 && super.clickY <= 499)
			{
				privateChatMode = (privateChatMode + 1) % 3;
				redrawChatMode = true;
				redrawChatbox = true;
				outBuffer.putOpcode(176);
				outBuffer.putByte(publicChatMode);
				outBuffer.putByte(privateChatMode);
				outBuffer.putByte(tradeMode);
			}
			if (super.clickX >= 273 && super.clickX <= 373 && super.clickY >= 467 && super.clickY <= 499)
			{
				tradeMode = (tradeMode + 1) % 3;
				redrawChatMode = true;
				redrawChatbox = true;
				outBuffer.putOpcode(176);
				outBuffer.putByte(publicChatMode);
				outBuffer.putByte(privateChatMode);
				outBuffer.putByte(tradeMode);
			}
			if (super.clickX >= 412 && super.clickX <= 512 && super.clickY >= 467 && super.clickY <= 499)
			{
				if (openScreenWidgetId == -1)
				{
					closeWidgets();
					reportedName = "";
					reportMutePlayer = false;
					reportAbuseInterfaceID = openScreenWidgetId = Widget.instanceWidgetParent;
				}
				else
				{
					addChatMessage("", "Please close the interface you have open before using 'report abuse'", 0);
				}
			}
			anInt1160++;
			if (anInt1160 > 161)
			{
				anInt1160 = 0;
				outBuffer.putOpcode(22);
				outBuffer.putShortBE(38304);
			}
		}
	}

	private void parsePlayerBlocks(Buffer buffer)
	{
		for (int i = 0; i < updatedPlayerCount; i++)
		{
			int id = updatedPlayers[i];
			Player player = players[id];
			int mask = buffer.getUnsignedByte();

			if ((mask & 0x20) != 0)
			{
				mask += buffer.getUnsignedByte() << 8;
			}

			parsePlayerBlock(id, player, mask, buffer);
		}
	}

	private void updateLocalPlayerMovement(Buffer buffer)
	{
		buffer.initBitAccess();

		int moved = buffer.getBits(1);

		if (moved == 0)
		{
			return;
		}

		MovementType moveType = MovementType.values()[buffer.getBits(2)];


		if (moveType == MovementType.NONE)
		{
			updatedPlayers[updatedPlayerCount++] = maxPlayerIndex;
			return;
		}

		if (moveType == MovementType.WALK)
		{
			int direction = buffer.getBits(3);

			localPlayer.move(direction, false);

			int blockUpdateRequired = buffer.getBits(1);

			if (blockUpdateRequired == 1)
			{
				updatedPlayers[updatedPlayerCount++] = maxPlayerIndex;
			}
			return;
		}

		if (moveType == MovementType.RUN)
		{
			int direction1 = buffer.getBits(3);

			localPlayer.move(direction1, true);

			int direction2 = buffer.getBits(3);

			localPlayer.move(direction2, true);

			int blockUpdateRequired = buffer.getBits(1);

			if (blockUpdateRequired == 1)
			{
				updatedPlayers[updatedPlayerCount++] = maxPlayerIndex;
			}
			return;
		}

		if (moveType == MovementType.TELEPORT)
		{
			int discardWalkingQueue = buffer.getBits(1);
			plane = buffer.getBits(2);
			int localY = buffer.getBits(7);
			int localX = buffer.getBits(7);
			int blockUpdateRequired = buffer.getBits(1);

			if (blockUpdateRequired == 1)
			{
				updatedPlayers[updatedPlayerCount++] = maxPlayerIndex;
			}

			localPlayer.setPosition(localX, localY, discardWalkingQueue == 1);
		}
	}

	private void scrollInterface(int i, int j, Widget class13, byte byte0, int k, int l, int i1, int j1, int k1)
	{
		if (aBoolean1127)
		{
			anInt1303 = 32;
		}
		else
		{
			anInt1303 = 0;
		}
		aBoolean1127 = false;
		if (byte0 != 102)
		{
			for (int l1 = 1; l1 > 0; l1++)
			{
				;
			}
		}
		if (i1 >= k1 && i1 < k1 + 16 && k >= j && k < j + 16)
		{
			class13.scrollPosition -= anInt1094 * 4;
			if (l == 1)
			{
				redrawTabArea = true;
			}
			if (l == 2 || l == 3)
			{
				redrawChatbox = true;
			}
			return;
		}
		if (i1 >= k1 && i1 < k1 + 16 && k >= (j + j1) - 16 && k < j + j1)
		{
			class13.scrollPosition += anInt1094 * 4;
			if (l == 1)
			{
				redrawTabArea = true;
			}
			if (l == 2 || l == 3)
			{
				redrawChatbox = true;
			}
			return;
		}
		if (i1 >= k1 - anInt1303 && i1 < k1 + 16 + anInt1303 && k >= j + 16 && k < (j + j1) - 16 && anInt1094 > 0)
		{
			int i2 = ((j1 - 32) * j1) / i;
			if (i2 < 8)
			{
				i2 = 8;
			}
			int j2 = k - j - 16 - i2 / 2;
			int k2 = j1 - 32 - i2;
			class13.scrollPosition = ((i - j1) * j2) / k2;
			if (l == 1)
			{
				redrawTabArea = true;
			}
			if (l == 2 || l == 3)
			{
				redrawChatbox = true;
			}
			aBoolean1127 = true;
		}
	}

	private void handleViewportMouse()
	{
		if (itemSelected == 0 && widgetSelected == 0)
		{
			menuActionTexts[menuActionRow] = "Walk here";
			menuActionTypes[menuActionRow] = 14;
			firstMenuOperand[menuActionRow] = super.mouseX;
			secondMenuOperand[menuActionRow] = super.mouseY;
			menuActionRow++;
		}
		int lastHash = -1;
		for (int idx = 0; idx < Model.resourceCount; idx++)
		{
			int hash = Model.hoveredHash[idx];
			int x = hash & 0x7f;
			int y = hash >> 7 & 0x7f;
			int type = hash >> 29 & 3;
			int index = hash >> 14 & 0x7fff;
			if (hash == lastHash)
			{
				continue;
			}
			lastHash = hash;
			if (type == 2 && currentScene.getArrangement(plane, x, y, hash) >= 0)
			{
				GameObjectDefinition gameObject = GameObjectDefinition.getDefinition(index);
				if (gameObject.childrenIds != null)
				{
					gameObject = gameObject.getChildDefinition();
				}
				if (gameObject == null)
				{
					continue;
				}
				if (itemSelected == 1)
				{
					menuActionTexts[menuActionRow] = "Use " + selectedItemName + " with @cya@" + gameObject.name;
					menuActionTypes[menuActionRow] = 467;
					selectedMenuActions[menuActionRow] = hash;
					firstMenuOperand[menuActionRow] = x;
					secondMenuOperand[menuActionRow] = y;
					menuActionRow++;
				}
				else if (widgetSelected == 1)
				{
					if ((selectedMask & 4) == 4)
					{
						menuActionTexts[menuActionRow] = selectedWidgetName + " @cya@" + gameObject.name;
						menuActionTypes[menuActionRow] = 376;
						selectedMenuActions[menuActionRow] = hash;
						firstMenuOperand[menuActionRow] = x;
						secondMenuOperand[menuActionRow] = y;
						menuActionRow++;
					}
				}
				else
				{
					if (gameObject.options != null)
					{
						for (int l1 = 4; l1 >= 0; l1--)
						{
							if (gameObject.options[l1] != null)
							{
								menuActionTexts[menuActionRow] = gameObject.options[l1] + " @cya@"
									+ gameObject.name;
								if (l1 == 0)
								{
									menuActionTypes[menuActionRow] = 35; // packet 181
								}
								if (l1 == 1)
								{
									menuActionTypes[menuActionRow] = 389; // packet 241
								}
								if (l1 == 2)
								{
									menuActionTypes[menuActionRow] = 888; // packet 50
								}
								if (l1 == 3)
								{
									menuActionTypes[menuActionRow] = 892; // packet 136
								}
								if (l1 == 4)
								{
									menuActionTypes[menuActionRow] = 1280; // packet 55
								}
								selectedMenuActions[menuActionRow] = hash;
								firstMenuOperand[menuActionRow] = x;
								secondMenuOperand[menuActionRow] = y;
								menuActionRow++;
							}
						}

					}
					StringBuilder examineText = new StringBuilder();
					examineText.append(MessageFormat.format("Examine <col=00ffff>{0}</col>", gameObject.name));
					if (DEBUG_CONTEXT)
					{
						examineText.append(" <col=00ff00>(</col>");
						examineText.append(
							MessageFormat.format("<col=ffffff>{0}</col>",
								Integer.toString(gameObject.id)
							)
						);
						examineText.append("<col=00ff00>) (</col>");
						examineText.append(
							MessageFormat.format("<col=ffffff>{0}, {1}</col>",
								Integer.toString(x + nextTopLeftTileX),
								Integer.toString(y + nextTopRightTileY)
							)
						);
						examineText.append("<col=00ff00>)</col>");


					}
					menuActionTexts[menuActionRow] = examineText.toString();
					menuActionTypes[menuActionRow] = 1412;
					selectedMenuActions[menuActionRow] = gameObject.id << 14;
					firstMenuOperand[menuActionRow] = x;
					secondMenuOperand[menuActionRow] = y;
					menuActionRow++;
				}
			}
			if (type == 1)
			{
				Npc npc = npcs[index];
				if (npc.npcDefinition.boundaryDimension == 1
					&& (npc.worldX & 0x7f) == 64
					&& (npc.worldY & 0x7f) == 64)
				{
					for (int i2 = 0; i2 < this.npcCount; i2++)
					{
						Npc npc1 = npcs[npcIds[i2]];
						if (npc1 != null
							&& npc1 != npc
							&& npc1.npcDefinition.boundaryDimension == 1
							&& npc1.worldX == npc.worldX
							&& npc1.worldY == npc.worldY)
						{
							processNpcMenuOptions(npc1.npcDefinition, y, x, npcIds[i2]);
						}
					}

					for (int i2 = 0; i2 < localPlayerCount; i2++)
					{
						Player player = players[playerList[i2]];
						if (player != null
							&& player.worldX == npc.worldX
							&& player.worldY == npc.worldY)
						{
							processPlayerMenuOptions(player, x, y, playerList[i2]);
						}
					}

				}
				processNpcMenuOptions(npc.npcDefinition, y, x, index);
			}
			if (type == 0)
			{
				Player player1 = players[index];
				if ((player1.worldX & 0x7f) == 64
					&& (player1.worldY & 0x7f) == 64)
				{
					for (int j2 = 0; j2 < npcCount; j2++)
					{
						Npc npc = npcs[npcIds[j2]];
						if (npc != null
							&& npc.npcDefinition.boundaryDimension == 1
							&& npc.worldX == player1.worldX
							&& npc.worldY == player1.worldY)
						{
							processNpcMenuOptions(npc.npcDefinition, y, x, npcIds[j2]);
						}
					}

					for (int l2 = 0; l2 < localPlayerCount; l2++)
					{
						Player player = players[playerList[l2]];
						if (player != null
							&& player != player1
							&& player.worldX == player1.worldX
							&& player.worldY == player1.worldY)
						{
							processPlayerMenuOptions(player, x, y, playerList[l2]);
						}
					}

				}
				processPlayerMenuOptions(player1, x, y, index);
			}
			if (type == 3)
			{
				LinkedList itemList = groundItems.getTile(plane, x, y);
				if (itemList != null)
				{
					for (Item item = (Item) itemList.last(); item != null; item = (Item) itemList
						.previous())
					{
						ItemDefinition itemDefinition = ItemDefinition.lookup(item.itemId);
						if (itemSelected == 1)
						{
							menuActionTexts[menuActionRow] = "Use " + selectedItemName + " with @lre@" + itemDefinition.name;
							menuActionTypes[menuActionRow] = 100;
							selectedMenuActions[menuActionRow] = item.itemId;
							firstMenuOperand[menuActionRow] = x;
							secondMenuOperand[menuActionRow] = y;
							menuActionRow++;
						}
						else if (widgetSelected == 1)
						{
							if ((selectedMask & 1) == 1)
							{
								menuActionTexts[menuActionRow] = selectedWidgetName + " @lre@" + itemDefinition.name;
								menuActionTypes[menuActionRow] = 199;
								selectedMenuActions[menuActionRow] = item.itemId;
								firstMenuOperand[menuActionRow] = x;
								secondMenuOperand[menuActionRow] = y;
								menuActionRow++;
							}
						}
						else
						{
							for (int i3 = 4; i3 >= 0; i3--)
							{
								if (itemDefinition.groundActions != null && itemDefinition.groundActions[i3] != null)
								{
									menuActionTexts[menuActionRow] = itemDefinition.groundActions[i3] + " @lre@" + itemDefinition.name;
									if (i3 == 0)
									{
										menuActionTypes[menuActionRow] = 68;
									}
									if (i3 == 1)
									{
										menuActionTypes[menuActionRow] = 26;
									}
									if (i3 == 2)
									{
										menuActionTypes[menuActionRow] = 684;
									}
									if (i3 == 3)
									{
										menuActionTypes[menuActionRow] = 930;
									}
									if (i3 == 4)
									{
										menuActionTypes[menuActionRow] = 270;
									}
									selectedMenuActions[menuActionRow] = item.itemId;
									firstMenuOperand[menuActionRow] = x;
									secondMenuOperand[menuActionRow] = y;
									menuActionRow++;
								}
								else if (i3 == 2)
								{
									menuActionTexts[menuActionRow] = "Take @lre@" + itemDefinition.name;
									menuActionTypes[menuActionRow] = 684;
									selectedMenuActions[menuActionRow] = item.itemId;
									firstMenuOperand[menuActionRow] = x;
									secondMenuOperand[menuActionRow] = y;
									menuActionRow++;
								}
							}
							StringBuilder examineText = new StringBuilder();
							examineText.append(MessageFormat.format("Examine <col=ff9040>{0}</col>", itemDefinition.name));
							if (DEBUG_CONTEXT)
							{
								examineText.append(" <col=00ff00>(</col>");
								examineText.append(
									MessageFormat.format("<col=ffffff>{0}</col>",
										Integer.toString(itemDefinition.id)
									)
								);
								examineText.append("<col=00ff00>)</col>");
							}
							menuActionTexts[menuActionRow] = examineText.toString();
							menuActionTypes[menuActionRow] = 1564;
							selectedMenuActions[menuActionRow] = item.itemId;
							firstMenuOperand[menuActionRow] = x;
							secondMenuOperand[menuActionRow] = y;
							menuActionRow++;
						}
					}

				}
			}
		}

	}

	private void method44(int i)
	{
		Widget.method200(i);
	}

	private void addLocation(int x, int y, int plane, int objectId, int objectFace, int objectType, int classType)
	{
		if (x >= 1 && y >= 1 && x <= 102 && y <= 102)
		{
			if (lowMemory && plane != this.plane)
			{
				return;
			}
			int locationHash = 0;
			if (classType == 0)
			{
				locationHash = currentScene.getWallObjectHash(x, y, plane);
			}
			if (classType == 1)
			{
				locationHash = currentScene.getWallDecorationHash(x, plane, y);
			}
			if (classType == 2)
			{
				locationHash = currentScene.getLocationHash(plane, x, y);
			}
			if (classType == 3)
			{
				locationHash = currentScene.getFloorDecorationHash(plane, x, y);
			}
			if (locationHash != 0)
			{
				int locationArrangement = currentScene.getArrangement(plane, x, y, locationHash);
				int locationIndex = locationHash >> 14 & 0x7fff;
				int locationType = locationArrangement & 0x1f;
				int locationRot = locationArrangement >> 6;
				if (classType == 0)
				{
					currentScene.removeWallObject(x, y, plane);
					GameObjectDefinition lc = GameObjectDefinition.getDefinition(locationIndex);
					if (lc.solid)
					{
						currentCollisionMap[plane].unmarkWall(locationRot, x, y, locationType, lc.walkable);
					}
				}
				if (classType == 1)
				{
					currentScene.removeWallDecoration(x, y, plane);
				}
				if (classType == 2)
				{
					currentScene.removeInteractiveObject(x, y, plane);
					GameObjectDefinition class47_1 = GameObjectDefinition.getDefinition(locationIndex);
					if (x + class47_1.sizeX > 103 || y + class47_1.sizeX > 103 || x + class47_1.sizeY > 103
						|| y + class47_1.sizeY > 103)
					{
						return;
					}
					if (class47_1.solid)
					{
						currentCollisionMap[plane].unmarkSolidOccupant(anInt1055, y, x, locationRot, class47_1.sizeY,
							class47_1.sizeX);
					}
				}
				if (classType == 3)
				{
					currentScene.method261(x, y, plane);
					GameObjectDefinition class47_2 = GameObjectDefinition.getDefinition(locationIndex);
					if (class47_2.solid && class47_2.hasActions)
					{
						currentCollisionMap[plane].unmarkConcealed(x, y);
					}
				}
			}
			if (objectId >= 0)
			{
				int objectPlane = plane;
				if (objectPlane < 3 && (currentSceneTileFlags[1][x][y] & 2) == 2)
				{
					objectPlane++;
				}
				MapRegion.forceRenderObject(x, y, plane, objectId, objectType, objectPlane, objectFace, currentScene, currentCollisionMap[plane],
					intGroundArray);
			}
		}
	}

	private void updateNpcMovement(Buffer buffer)
	{
		buffer.initBitAccess();
		int j = buffer.getBits(8);
		if (j < npcCount)
		{
			for (int k = j; k < npcCount; k++)
			{
				eneityUpdateIndices[enityUpdateCount++] = npcIds[k];
			}

		}
		if (j > npcCount)
		{
			SignLink.reportError(username + " Too many npcs");
			throw new RuntimeException("eek");
		}
		npcCount = 0;
		for (int l = 0; l < j; l++)
		{
			int i1 = npcIds[l];
			Npc npc = npcs[i1];
			int updateRequired = buffer.getBits(1);
			if (updateRequired == 0)
			{
				npcIds[npcCount++] = i1;
				npc.pulseCycle = pulseCycle;
			}
			else
			{
				MovementType moveType = MovementType.values()[buffer.getBits(2)];
				if (moveType == MovementType.NONE)
				{
					npcIds[npcCount++] = i1;
					npc.pulseCycle = pulseCycle;
					updatedPlayers[updatedPlayerCount++] = i1;
				}
				else if (moveType == MovementType.WALK)
				{
					npcIds[npcCount++] = i1;
					npc.pulseCycle = pulseCycle;
					int direction = buffer.getBits(3);
					npc.move(direction, false);
					int blockUpdateRequired = buffer.getBits(1);
					if (blockUpdateRequired == 1)
					{
						updatedPlayers[updatedPlayerCount++] = i1;
					}
				}
				else if (moveType == MovementType.RUN)
				{
					npcIds[npcCount++] = i1;
					npc.pulseCycle = pulseCycle;
					int direction1 = buffer.getBits(3);
					npc.move(direction1, true);
					int direction2 = buffer.getBits(3);
					npc.move(direction2, true);
					int blockUpdateRequired = buffer.getBits(1);
					if (blockUpdateRequired == 1)
					{
						updatedPlayers[updatedPlayerCount++] = i1;
					}
				}
				else if (moveType == MovementType.TELEPORT)
				{
					eneityUpdateIndices[enityUpdateCount++] = i1;
				}
			}
		}

	}

	private void updateNpcs(Buffer buffer, int packetSize)
	{
		enityUpdateCount = 0;
		updatedPlayerCount = 0;
		updateNpcMovement(buffer);
		processNewNpcs(buffer, packetSize);
		parseNpcUpdateMasks(buffer, packetSize, 838);
		for (int i = 0; i < enityUpdateCount; i++)
		{
			int npcIndex = eneityUpdateIndices[i];
			if (npcs[npcIndex].pulseCycle != pulseCycle)
			{
				npcs[npcIndex].npcDefinition = null;
				npcs[npcIndex] = null;
			}
		}

		if (buffer.currentPosition != packetSize)
		{
			SignLink.reportError(username + " size mismatch in getnpcpos - coord:" + buffer.currentPosition
				+ " psize:" + packetSize);
			throw new RuntimeException("eek");
		}
		for (int l = 0; l < npcCount; l++)
		{
			if (npcs[npcIds[l]] == null)
			{
				SignLink.reportError(username + " null entry in npc list - coord:" + l + " size:" + npcCount);
				throw new RuntimeException("eek");
			}
		}

	}

	private void resetModelCaches()
	{
		GameObjectDefinition.modelCache.removeAll();
		GameObjectDefinition.animatedModelCache.removeAll();
		ActorDefinition.modelCache.removeAll();
		ItemDefinition.modelCache.removeAll();
		ItemDefinition.rgbImageCache.removeAll();
		Player.modelCache.removeAll();
		SpotAnimation.modelCache.removeAll();
	}

	private void renderProjectiles()
	{
		Projectile projectile = (Projectile) projectileQueue.first();
		for (; projectile != null; projectile = (Projectile) projectileQueue
			.next())
		{
			if (projectile.sceneId != plane || pulseCycle > projectile.endCycle)
			{
				projectile.remove();
			}
			else if (pulseCycle >= projectile.delay)
			{
				if (projectile.targetedEntityId > 0)
				{
					Npc class50_sub1_sub4_sub3_sub1 = npcs[projectile.targetedEntityId - 1];
					if (class50_sub1_sub4_sub3_sub1 != null
						&& class50_sub1_sub4_sub3_sub1.worldX >= 0
						&& class50_sub1_sub4_sub3_sub1.worldX < 13312
						&& class50_sub1_sub4_sub3_sub1.worldY >= 0
						&& class50_sub1_sub4_sub3_sub1.worldY < 13312)
					{
						projectile.trackTarget(class50_sub1_sub4_sub3_sub1.worldX,
							class50_sub1_sub4_sub3_sub1.worldY, getFloorDrawHeight(
								projectile.sceneId, class50_sub1_sub4_sub3_sub1.worldX, class50_sub1_sub4_sub3_sub1.worldY
							)
								- projectile.endHeight, pulseCycle);
					}
				}
				if (projectile.targetedEntityId < 0)
				{
					int i = -projectile.targetedEntityId - 1;
					Player player;
					if (i == thisPlayerServerId)
					{
						player = localPlayer;
					}
					else
					{
						player = players[i];
					}
					if (player != null
						&& player.worldX >= 0
						&& player.worldX < 13312
						&& player.worldY >= 0
						&& player.worldY < 13312)
					{
						projectile.trackTarget(player.worldX,
							player.worldY, getFloorDrawHeight(
								projectile.sceneId, player.worldX, player.worldY
							)
								- projectile.endHeight, pulseCycle);
					}
				}
				projectile.move(tickDelta);
				currentScene.addEntity(plane, (int) projectile.currentX, (int) projectile.currentY, (int) projectile.currentHeight, projectile, -1,
					60, false,
					projectile.anInt1562);
			}
		}

		anInt1168++;
		if (anInt1168 > 51)
		{
			anInt1168 = 0;
			outBuffer.putOpcode(248);
		}
	}

	private void prepareTitle()
	{
		titleboxImage = new IndexedImage(titleArchive, "titlebox", 0);
		titleboxButtonImage = new IndexedImage(titleArchive, "titlebutton", 0);
		titleFlameEmblem = new IndexedImage[12];
		for (int i = 0; i < 12; i++)
		{
			titleFlameEmblem[i] = new IndexedImage(titleArchive, "runes", i);
		}

		anImageRGB1226 = new ImageRGB(128, 265);
		anImageRGB1227 = new ImageRGB(128, 265);
		System.arraycopy(flameLeftBackground.pixels, 0, anImageRGB1226.pixels, 0, (128 * 265));
		System.arraycopy(flameRightBackground.pixels, 0, anImageRGB1227.pixels, 0, (128 * 265));


		anIntArray1311 = new int[256];
		for (int l = 0; l < 64; l++)
		{
			anIntArray1311[l] = l * 0x40000;
		}

		for (int i1 = 0; i1 < 64; i1++)
		{
			anIntArray1311[i1 + 64] = 0xff0000 + 1024 * i1;
		}

		for (int j1 = 0; j1 < 64; j1++)
		{
			anIntArray1311[j1 + 128] = 0xffff00 + 4 * j1;
		}

		for (int k1 = 0; k1 < 64; k1++)
		{
			anIntArray1311[k1 + 192] = 0xffffff;
		}

		anIntArray1312 = new int[256];
		for (int l1 = 0; l1 < 64; l1++)
		{
			anIntArray1312[l1] = l1 * 1024;
		}

		for (int i2 = 0; i2 < 64; i2++)
		{
			anIntArray1312[i2 + 64] = 65280 + 4 * i2;
		}

		for (int j2 = 0; j2 < 64; j2++)
		{
			anIntArray1312[j2 + 128] = 65535 + 0x40000 * j2;
		}

		for (int k2 = 0; k2 < 64; k2++)
		{
			anIntArray1312[k2 + 192] = 0xffffff;
		}

		anIntArray1313 = new int[256];
		for (int l2 = 0; l2 < 64; l2++)
		{
			anIntArray1313[l2] = l2 * 4;
		}

		for (int i3 = 0; i3 < 64; i3++)
		{
			anIntArray1313[i3 + 64] = 255 + 0x40000 * i3;
		}

		for (int j3 = 0; j3 < 64; j3++)
		{
			anIntArray1313[j3 + 128] = 0xff00ff + 1024 * j3;
		}

		for (int k3 = 0; k3 < 64; k3++)
		{
			anIntArray1313[k3 + 192] = 0xffffff;
		}

		anIntArray1310 = new int[256];
		anIntArray1176 = new int[32768];
		anIntArray1177 = new int[32768];
		method83(null, 0);
		anIntArray1084 = new int[32768];
		anIntArray1085 = new int[32768];
		drawLoadingText(10, "Connecting to fileserver");
		if (!currentlyDrawingFlames)
		{
			aBoolean1314 = true;
			currentlyDrawingFlames = true;
			startRunnable(this, 2);
		}
	}

	private void removeFriend(long l)
	{
		try
		{
			if (l == 0L)
			{
				return;
			}
			for (int j = 0; j < friendsCount; j++)
			{
				if (friends[j] != l)
				{
					continue;
				}
				friendsCount--;
				redrawTabArea = true;
				for (int k = j; k < friendsCount; k++)
				{
					friendUsernames[k] = friendUsernames[k + 1];
					friendWorlds[k] = friendWorlds[k + 1];
					friends[k] = friends[k + 1];
				}

				outBuffer.putOpcode(141);
				outBuffer.putLongBE(l);
				break;
			}

		}
		catch (RuntimeException runtimeexception)
		{
			SignLink.reportError("38799, " + l + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	private void processMenuClick()
	{
		if (activeInterfaceType != 0)
		{
			return;
		}
		int meta = super.clickType;
		if (widgetSelected == 1 && super.clickX >= 516 && super.clickY >= 160 && super.clickX <= 765
			&& super.clickY <= 205)
		{
			meta = 0;
		}
		if (menuOpen)
		{
			if (meta != 1)
			{
				int x = super.mouseX;
				int y = super.mouseY;
				if (menuScreenArea == 0)
				{
					x -= 4;
					y -= 4;
				}
				if (menuScreenArea == 1)
				{
					x -= 553;
					y -= 205;
				}
				if (menuScreenArea == 2)
				{
					x -= 17;
					y -= 357;
				}
				if (x < menuOffsetX - 10 || x > menuOffsetX + menuWidth + 10 || y < menuOffsetY - 10
					|| y > menuOffsetY + menuHeight + 10)
				{
					menuOpen = false;
					if (menuScreenArea == 1)
					{
						redrawTabArea = true;
					}
					if (menuScreenArea == 2)
					{
						redrawChatbox = true;
					}
				}
			}
			if (meta == 1)
			{
				int menuX = menuOffsetX;
				int menuY = menuOffsetY;
				int dx = menuWidth;
				int x = super.clickX;
				int y = super.clickY;
				if (menuScreenArea == 0)
				{
					x -= 4;
					y -= 4;
				}
				if (menuScreenArea == 1)
				{
					x -= 553;
					y -= 205;
				}
				if (menuScreenArea == 2)
				{
					x -= 17;
					y -= 357;
				}
				int id = -1;
				for (int row = 0; row < menuActionRow; row++)
				{
					int k3 = menuY + 31 + (menuActionRow - 1 - row) * 15;
					if (x > menuX && x < menuX + dx && y > k3 - 13 && y < k3 + 3)
					{
						id = row;
					}
				}

				if (id != -1)
				{
					processMenuActions(id);
				}
				menuOpen = false;
				if (menuScreenArea == 1)
				{
					redrawTabArea = true;
				}
				if (menuScreenArea == 2)
				{
					redrawChatbox = true;
				}
			}
		}
		else
		{
			if (meta == 1 && menuActionRow > 0)
			{
				int action = menuActionTypes[menuActionRow - 1];
				if (action == 9 || action == 225 || action == 444 || action == 564 || action == 894 || action == 961 || action == 399 || action == 324
					|| action == 227 || action == 891 || action == 52 || action == Actions.EXAMINE_ITEM)
				{
					int item = firstMenuOperand[menuActionRow - 1];
					int id = secondMenuOperand[menuActionRow - 1];
					Widget widget = Widget.forId(id);
					if (widget.itemSwapable || widget.itemDeletesDraged)
					{
						lastItemDragged = false;
						lastItemDragTime = 0;
						modifiedWidgetId = id;
						selectedInventorySlot = item;
						activeInterfaceType = 2;
						anInt1114 = super.clickX;
						anInt1115 = super.clickY;
						if (Widget.forId(id).parentId == openScreenWidgetId)
						{
							activeInterfaceType = 1;
						}
						if (Widget.forId(id).parentId == openChatboxWidgetId)
						{
							activeInterfaceType = 3;
						}
						return;
					}
				}
			}
			if (meta == 1 && (oneMouseButton == 1 || menuHasAddFriend(menuActionRow - 1, aByte1161)) && menuActionRow > 2)
			{
				meta = 2;
			}
			if (meta == 1 && menuActionRow > 0)
			{
				processMenuActions(menuActionRow - 1);
			}
			if (meta == 2 && menuActionRow > 0)
			{
				determineMenuSize();
			}
		}
	}

	private void drawMinimapMark(ImageRGB sprite, int mapX, int mapY)
	{
		int len = mapX * mapX + mapY * mapY;

		if (len > 4225 && len < 90000)
		{
			int theta = cameraHorizontal + cameraYawOffset & 0x7ff;
			int sin = Model.SINE[theta];
			int cos = Model.COSINE[theta];
			sin = (sin * 256) / (mapZoomOffset + 256);
			cos = (cos * 256) / (mapZoomOffset + 256);
			int x = mapY * sin + mapX * cos >> 16;
			int y = mapY * cos - mapX * sin >> 16;
			double angle = Math.atan2(x, y);
			int drawX = (int) (Math.sin(angle) * 63D);
			int drawY = (int) (Math.cos(angle) * 57D);
			minimapEdge.drawRotated((94 + drawX + 4) - 10, 83 - drawY - 20, 15, 15, 20, 20, 256, angle);
		}
		else
		{
			drawOnMinimap(sprite, mapX, mapY);
		}
	}

	private void drawScrollBar(boolean flag, int i, int j, int k, int l, int i1)
	{
		scrollbarUp.drawImage(j, i1);
		scrollbarDown.drawImage(j, (i1 + k) - 16);
		int anInt931 = 0x23201b;
		Rasterizer.drawFilledRectangle(j, i1 + 16, 16, k - 32, anInt931);
		int j1 = ((k - 32) * k) / l;
		if (j1 < 8)
		{
			j1 = 8;
		}
		int k1 = ((k - 32 - j1) * i) / (l - k);
		Rasterizer.drawFilledRectangle(j, i1 + 16 + k1, 16, j1, anInt1080);
		int anInt1135 = 0x766654;
		Rasterizer.drawVerticalLine(j, i1 + 16 + k1, j1, anInt1135);
		Rasterizer.drawVerticalLine(j + 1, i1 + 16 + k1, j1, anInt1135);
		Rasterizer.drawHorizontalLine(j, i1 + 16 + k1, 16, anInt1135);
		Rasterizer.drawHorizontalLine(j, i1 + 17 + k1, 16, anInt1135);
		Rasterizer.drawVerticalLine(j + 15, i1 + 16 + k1, j1, 0x332d25);
		Rasterizer.drawVerticalLine(j + 14, i1 + 17 + k1, j1 - 1, 0x332d25);
		Rasterizer.drawHorizontalLine(j, i1 + 15 + k1 + j1, 16, 0x332d25);
		Rasterizer.drawHorizontalLine(j + 1, i1 + 14 + k1 + j1, 15, 0x332d25);
	}

	private void renderNPCs(boolean flag)
	{
		for (int n = 0; n < npcCount; n++)
		{
			Npc npc = npcs[npcIds[n]];
			int hash = 0x20000000 + (npcIds[n] << 14);
			if (npc == null || !npc.isVisible() || npc.npcDefinition.visible != flag || !npc.npcDefinition.isVisible())
			{
				continue;
			}
			int npcX = npc.worldX >> 7;
			int npcY = npc.worldY >> 7;
			if (npcX < 0 || npcX >= 104 || npcY < 0 || npcY >= 104)
			{
				continue;
			}
			if (npc.boundaryDimension == 1
				&& (npc.worldX & 0x7f) == 64
				&& (npc.worldY & 0x7f) == 64)
			{
				if (tileRenderCount[npcX][npcY] == renderCount)
				{
					continue;
				}
				tileRenderCount[npcX][npcY] = renderCount;
			}
			if (!npc.npcDefinition.clickable)
			{
				hash += 0x80000000;
			}
			currentScene.addEntity(plane, npc.worldX, npc.worldY, getFloorDrawHeight(
				plane, npc.worldX, npc.worldY
				), npc, hash,
				(npc.boundaryDimension - 1) * 64 + 60, npc.dynamic,
				npc.currentRotation);
		}

	}

	private void setWaveVolume(int volume)
	{
		SignLink.waveVolume = volume;
	}

	private void dropClient()
	{
		if (idleLogout > 0)
		{
			logout();
			return;
		}
		method125("Please wait - attempting to reestablish", "Connection lost");
		minimapState = 0;
		destinationX = 0;
		BufferedConnection connection = gameConnection;
		loggedIn = false;
		reconnectionAttempts = 0;
		login(username, password, true);
		if (!loggedIn)
		{
			logout();
		}
		try
		{
			connection.close();
		}
		catch (Exception _ex)
		{
		}
	}

	private boolean handleWidgetDynamicAction(Widget widget)
	{
		int type = widget.contentType;
		if (friendListStatus == 2)
		{
			if (type == 201)
			{
				redrawChatbox = true;
				inputType = 0;
				messagePromptRaised = true;
				chatMessage = "";
				friendsListAction = 1;
				chatboxInputMessage = "Enter name of friend to add to list";
			}
			if (type == 202)
			{
				redrawChatbox = true;
				inputType = 0;
				messagePromptRaised = true;
				chatMessage = "";
				friendsListAction = 2;
				chatboxInputMessage = "Enter name of friend to delete from list";
			}
		}
		if (type == 205)
		{
			idleLogout = 250;
			return true;
		}
		if (type == 501)
		{
			redrawChatbox = true;
			inputType = 0;
			messagePromptRaised = true;
			chatMessage = "";
			friendsListAction = 4;
			chatboxInputMessage = "Enter name of player to add to list";
		}
		if (type == 502)
		{
			redrawChatbox = true;
			inputType = 0;
			messagePromptRaised = true;
			chatMessage = "";
			friendsListAction = 5;
			chatboxInputMessage = "Enter name of player to delete from list";
		}
		if (type >= 300 && type <= 313)
		{
			int k = (type - 300) / 2;
			int j1 = type & 1;
			int i2 = characterEditIdentityKits[k];
			if (i2 != -1)
			{
				do
				{
					if (j1 == 0 && --i2 < 0)
					{
						i2 = IdentityKit.count - 1;
					}
					if (j1 == 1 && ++i2 >= IdentityKit.count)
					{
						i2 = 0;
					}
				} while (IdentityKit.cache[i2].widgetDisplayed
					|| IdentityKit.cache[i2].partId != k + (characterEditChangeGenger ? 0 : 7));
				characterEditIdentityKits[k] = i2;
				characterModelChanged = true;
			}
		}
		if (type >= 314 && type <= 323)
		{
			int l = (type - 314) / 2;
			int k1 = type & 1;
			int j2 = characterEditColors[l];
			if (k1 == 0 && --j2 < 0)
			{
				j2 = playerColours[l].length - 1;
			}
			if (k1 == 1 && ++j2 >= playerColours[l].length)
			{
				j2 = 0;
			}
			characterEditColors[l] = j2;
			characterModelChanged = true;
		}
		if (type == 324 && !characterEditChangeGenger)
		{
			characterEditChangeGenger = true;
			changeGender();
		}
		if (type == 325 && characterEditChangeGenger)
		{
			characterEditChangeGenger = false;
			changeGender();
		}
		if (type == 326)
		{
			outBuffer.putOpcode(163);
			outBuffer.putByte(characterEditChangeGenger ? 0 : 1);
			for (int i1 = 0; i1 < 7; i1++)
			{
				outBuffer.putByte(characterEditIdentityKits[i1]);
			}

			for (int l1 = 0; l1 < 5; l1++)
			{
				outBuffer.putByte(characterEditColors[l1]);
			}

			return true;
		}
		if (type == 620)
		{
			reportMutePlayer = !reportMutePlayer;
		}
		if (type >= 601 && type <= 613)
		{
			closeWidgets();
			if (reportedName.length() > 0)
			{
				outBuffer.putOpcode(184);
				outBuffer.putLongBE(TextUtils.nameToLong(reportedName));
				outBuffer.putByte(type - 601);
				outBuffer.putByte(reportMutePlayer ? 1 : 0);
			}
		}
		return false;
	}

	private Archive requestArchive(int id, String file, int expectedCrc, int x, String displayName)
	{
		byte[] archiveBuffer = null;
		int reconnectionDelay = 5;

		try
		{
			if (stores[0] != null)
			{
				archiveBuffer = stores[0].get(id);
			}
		}
		catch (Exception ignored)
		{
		}

		if (archiveBuffer != null && Configuration.JAGGRAB_ENABLED)
		{
			archiveCrc.reset();
			archiveCrc.update(archiveBuffer);

			int calculatedCrc = (int) archiveCrc.getValue();

			if (calculatedCrc != expectedCrc)
			{
				archiveBuffer = null;
			}
		}

		if (archiveBuffer != null)
		{
			return new Archive(archiveBuffer);
		}

		int attempts = 0;

		while (archiveBuffer == null)
		{
			String error = "Unknown error";

			drawLoadingText(x, "Requesting " + displayName);

			try
			{
				int currentPercentage = 0;
				DataInputStream jaggrabStream = openJaggrabStream(file + expectedCrc);
				byte[] bytes = new byte[6];

				jaggrabStream.readFully(bytes, 0, 6);

				Buffer buffer = new Buffer(bytes);
				buffer.currentPosition = 3;
				int archiveLength = buffer.getMediumBE() + 6;
				int archiveRead = 6;
				archiveBuffer = new byte[archiveLength];

				System.arraycopy(bytes, 0, archiveBuffer, 0, 6);

				while (archiveRead < archiveLength)
				{
					int remaining = archiveLength - archiveRead;

					if (remaining > 1000)
					{
						remaining = 1000;
					}

					int read = jaggrabStream.read(archiveBuffer, archiveRead, remaining);

					if (read < 0)
					{
						error = "Length error: " + archiveRead + "/" + archiveLength;
						throw new IOException("EOF");
					}

					archiveRead += read;
					int calculatedPercentage = (archiveRead * 100) / archiveLength;

					if (calculatedPercentage != currentPercentage)
					{
						drawLoadingText(x, "Loading " + displayName + " - " + calculatedPercentage + "%");
					}

					currentPercentage = calculatedPercentage;
				}

				jaggrabStream.close();

				try
				{
					if (stores[0] != null)
					{
						stores[0].put(archiveBuffer.length, archiveBuffer, id);
					}
				}
				catch (Exception _ex)
				{
					stores[0] = null;
				}

				if (Configuration.JAGGRAB_ENABLED)
				{
					archiveCrc.reset();
					archiveCrc.update(archiveBuffer);

					int calculatedCrc = (int) archiveCrc.getValue();

					if (calculatedCrc != expectedCrc)
					{
						archiveBuffer = null;
						attempts++;
						error = "Checksum error: " + calculatedCrc;
					}
				}
			}
			catch (IOException ex)
			{
				if (error.equals("Unknown error"))
				{
					error = "Connection error";
				}

				archiveBuffer = null;
			}
			catch (NullPointerException ex)
			{
				error = "Null error";
				archiveBuffer = null;

				if (!SignLink.reportError)
				{
					return null;
				}
			}
			catch (ArrayIndexOutOfBoundsException ex)
			{
				error = "Bounds error";
				archiveBuffer = null;

				if (!SignLink.reportError)
				{
					return null;
				}
			}
			catch (Exception ex)
			{
				error = "Unexpected error";
				archiveBuffer = null;

				if (!SignLink.reportError)
				{
					return null;
				}
			}

			if (archiveBuffer == null)
			{
				for (int time = reconnectionDelay; time > 0; time--)
				{
					if (attempts >= 3)
					{
						drawLoadingText(x, "Game updated - please reload page");
						time = 10;
					}
					else
					{
						drawLoadingText(x, error + " - Retrying in " + time);
					}

					try
					{
						Thread.sleep(1000L);
					}
					catch (Exception ignored)
					{
					}
				}

				reconnectionDelay *= 2;

				if (reconnectionDelay > 60)
				{
					reconnectionDelay = 60;
				}

				useJaggrab = !useJaggrab;
			}
		}

		return new Archive(archiveBuffer);
	}

	private void parseNpcUpdateMasks(Buffer buffer, int i, int j)
	{
		j = 24 / j;
		for (int k = 0; k < updatedPlayerCount; k++)
		{
			int l = updatedPlayers[k];
			Npc npc = npcs[l];
			int i1 = buffer.getUnsignedByte();
			if ((i1 & 1) != 0)
			{
				npc.npcDefinition = ActorDefinition.getDefinition(buffer.getUnsignedNegativeOffsetShortBE());
				npc.boundaryDimension = npc.npcDefinition.boundaryDimension;
				npc.turnSpeed = npc.npcDefinition.degreesToTurn;
				npc.walkAnimationId = npc.npcDefinition.walkAnimationId;
				npc.turnAroundAnimationId = npc.npcDefinition.turnAroundAnimationId;
				npc.turnRightAnimationId = npc.npcDefinition.turnRightAnimationId;
				npc.turnLeftAnimationId = npc.npcDefinition.turnLeftAnimationId;
				npc.idleAnimation = npc.npcDefinition.standAnimationId;
			}
			if ((i1 & 0x40) != 0)
			{
				npc.faceActor = buffer.getUnsignedShortLE();
				if (npc.faceActor == 65535)
				{
					npc.faceActor = -1;
				}
			}
			if ((i1 & 0x80) != 0)
			{
				int j1 = buffer.getUnsignedPostNegativeOffsetByte();
				int j2 = buffer.getUnsignedPostNegativeOffsetByte();
				npc.updateHits(j2, j1, pulseCycle);
				npc.endCycle = pulseCycle + 300;
				npc.anInt1596 = buffer.getUnsignedByte();
				npc.anInt1597 = buffer.getUnsignedPreNegativeOffsetByte();
			}
			if ((i1 & 4) != 0)
			{
				npc.graphic = buffer.getUnsignedShortBE();
				int k1 = buffer.getIntME1();
				npc.spotGraphicHeight = k1 >> 16;
				npc.spotGraphicDelay = pulseCycle + (k1 & 0xffff);
				npc.currentAnimation = 0;
				npc.animationCycle = 0;
				if (npc.spotGraphicDelay > pulseCycle)
				{
					npc.currentAnimation = -1;
				}
				if (npc.graphic == 65535)
				{
					npc.graphic = -1;
				}
			}
			if ((i1 & 0x20) != 0)
			{
				npc.forcedChat = buffer.getString();
				npc.textCycle = 100;
			}
			if ((i1 & 8) != 0)
			{
				npc.faceX = buffer.getUnsignedNegativeOffsetShortLE();
				npc.faceY = buffer.getUnsignedShortLE();
			}
			if ((i1 & 2) != 0)
			{
				int l1 = buffer.getUnsignedShortBE();
				if (l1 == 65535)
				{
					l1 = -1;
				}
				int k2 = buffer.getUnsignedPreNegativeOffsetByte();
				if (l1 == npc.emoteAnimation && l1 != -1)
				{
					int i3 = AnimationSequence.animations[l1].anInt307;
					if (i3 == 1)
					{
						npc.displayedEmoteFrames = 0;
						npc.animationSequence = 0;
						npc.animationDelay = k2;
						npc.animationResetCycle = 0;
					}
					if (i3 == 2)
					{
						npc.animationResetCycle = 0;
					}
				}
				else if (l1 == -1
					|| npc.emoteAnimation == -1
					|| AnimationSequence.animations[l1].anInt301 >= AnimationSequence.animations[npc.emoteAnimation].anInt301)
				{
					npc.emoteAnimation = l1;
					npc.displayedEmoteFrames = 0;
					npc.animationSequence = 0;
					npc.animationDelay = k2;
					npc.animationResetCycle = 0;
					npc.stillPathPosition = npc.pathLength;
				}
			}
			if ((i1 & 0x10) != 0)
			{
				int i2 = buffer.getUnsignedPreNegativeOffsetByte();
				int l2 = buffer.getUnsignedPreNegativeOffsetByte();
				npc.updateHits(l2, i2, pulseCycle);
				npc.endCycle = pulseCycle + 300;
				npc.anInt1596 = buffer.getUnsignedByte();
				npc.anInt1597 = buffer.getUnsignedInvertedByte();
			}
		}

	}

	private void parsePlayerBlock(int id, Player player, int mask, Buffer buffer)
	{
		if ((mask & 8) != 0)
		{
			int animation = buffer.getUnsignedShortBE();

			if (animation == 65535)
			{
				animation = -1;
			}

			int delay = buffer.getUnsignedPreNegativeOffsetByte();

			if (animation == player.emoteAnimation && animation != -1)
			{
				int mode = AnimationSequence.animations[animation].anInt307;

				if (mode == 1)
				{
					player.displayedEmoteFrames = 0;
					player.animationSequence = 0;
					player.animationDelay = delay;
					player.animationResetCycle = 0;
				}

				if (mode == 2)
				{
					player.animationResetCycle = 0;
				}
			}
			else if (animation == -1 || player.emoteAnimation == -1
				|| AnimationSequence.animations[animation].anInt301 >= AnimationSequence.animations[player.emoteAnimation].anInt301)
			{
				player.emoteAnimation = animation;
				player.displayedEmoteFrames = 0;
				player.animationSequence = 0;
				player.animationDelay = delay;
				player.animationResetCycle = 0;
				player.stillPathPosition = player.pathLength;
			}
		}

		if ((mask & 0x10) != 0)
		{
			player.forcedChat = buffer.getString();

			if (player.forcedChat.charAt(0) == '~')
			{
				player.forcedChat = player.forcedChat.substring(1);
				addChatMessage(player.playerName, player.forcedChat, 2);
			}
			else if (player == localPlayer)
			{
				addChatMessage(player.playerName, player.forcedChat, 2);
			}

			player.textColour = 0;
			player.textEffect = 0;
			player.textCycle = 150;
		}

		if ((mask & 0x100) != 0)
		{
			player.movementStartX = buffer.getUnsignedPostNegativeOffsetByte();
			player.movementStartY = buffer.getUnsignedInvertedByte();
			player.movementEndX = buffer.getUnsignedPreNegativeOffsetByte();
			player.movementEndY = buffer.getUnsignedByte();
			player.moveCycleEnd = buffer.getUnsignedShortBE() + pulseCycle;
			player.moveCycleStart = buffer.getUnsignedNegativeOffsetShortBE() + pulseCycle;
			player.moveDirection = buffer.getUnsignedByte();

			player.resetPath();
		}

		if ((mask & 1) != 0)
		{
			player.faceActor = buffer.getUnsignedNegativeOffsetShortBE();

			if (player.faceActor == 65535)
			{
				player.faceActor = -1;
			}
		}

		if ((mask & 2) != 0)
		{
			player.faceX = buffer.getUnsignedShortBE();
			player.faceY = buffer.getUnsignedShortBE();
		}

		if ((mask & 0x200) != 0)
		{
			player.graphic = buffer.getUnsignedNegativeOffsetShortBE();
			int heightAndDelay = buffer.getIntME1();
			player.spotGraphicHeight = heightAndDelay >> 16;
			player.spotGraphicDelay = pulseCycle + (heightAndDelay & 0xffff);
			player.currentAnimation = 0;
			player.animationCycle = 0;

			if (player.spotGraphicDelay > pulseCycle)
			{
				player.currentAnimation = -1;
			}

			if (player.graphic == 65535)
			{
				player.graphic = -1;
			}
		}

		if ((mask & 4) != 0)
		{
			int size = buffer.getUnsignedByte();
			byte[] bytes = new byte[size];
			Buffer appearance = new Buffer(bytes);

			buffer.getBytesReverse(bytes, 0, size);

			cachedAppearances[id] = appearance;

			player.updateAppearance(appearance);
		}

		if ((mask & 0x400) != 0)
		{
			int damage = buffer.getUnsignedPostNegativeOffsetByte();
			int type = buffer.getUnsignedPreNegativeOffsetByte();

			player.updateHits(type, damage, pulseCycle);

			player.endCycle = pulseCycle + 300;
			player.anInt1596 = buffer.getUnsignedInvertedByte();
			player.anInt1597 = buffer.getUnsignedByte();
		}

		if ((mask & 0x40) != 0)
		{
			int effectsAndColour = buffer.getUnsignedShortBE();
			int rights = buffer.getUnsignedInvertedByte();
			int length = buffer.getUnsignedPostNegativeOffsetByte();
			int currentPosition = buffer.currentPosition;

			if (player.playerName != null && player.visible)
			{
				long nameLong = TextUtils.nameToLong(player.playerName);
				boolean ignored = false;

				if (rights <= 1)
				{
					for (int i = 0; i < ignoresCount; i++)
					{
						if (ignores[i] != nameLong)
						{
							continue;
						}

						ignored = true;
						break;
					}

				}

				if (!ignored && !inTutorialIsland)
				{
					try
					{
						chatBuffer.currentPosition = 0;

						buffer.getBytesAdded(chatBuffer.buffer, 0, length);

						chatBuffer.currentPosition = 0;
						String message = ChatCensor.censorString(ChatEncoder.get(length, chatBuffer));
						player.forcedChat = message;
						player.textColour = effectsAndColour >> 8;
						player.textEffect = effectsAndColour & 0xff;
						player.textCycle = 150;

						if (rights == 2 || rights == 3)
						{
							addChatMessage("@cr2@" + player.playerName, message, 1);
						}
						else if (rights == 1)
						{
							addChatMessage("@cr1@" + player.playerName, message, 1);
						}
						else
						{
							addChatMessage(player.playerName, message, 2);
						}
					}
					catch (Exception exception)
					{
						SignLink.reportError("cde2");
					}
				}
			}

			buffer.currentPosition = currentPosition + length;
		}

		if ((mask & 0x80) != 0)
		{
			int damage = buffer.getUnsignedPreNegativeOffsetByte();
			int type = buffer.getUnsignedInvertedByte();

			player.updateHits(type, damage, pulseCycle);

			player.endCycle = pulseCycle + 300;
			player.anInt1596 = buffer.getUnsignedPreNegativeOffsetByte();
			player.anInt1597 = buffer.getUnsignedByte();
		}
	}

	private void resetTitleScreen()
	{
		if (aClass18_1198 != null)
		{
			return;
		}

		super.imageProducer = null;
		chatboxProducingGraphicsBuffer = null;
		aClass18_1157 = null;
		tabImageProducer = null;
		gameScreenImageProducer = null;
		aClass18_1108 = null;
		aClass18_1109 = null;

		flameLeftBackground = new ProducingGraphicsBuffer(128, 265, getParentComponent());
		Rasterizer.resetPixels();

		flameRightBackground = new ProducingGraphicsBuffer(128, 265, getParentComponent());
		Rasterizer.resetPixels();

		aClass18_1198 = new ProducingGraphicsBuffer(509, 171, getParentComponent());
		Rasterizer.resetPixels();

		aClass18_1199 = new ProducingGraphicsBuffer(360, 132, getParentComponent());
		Rasterizer.resetPixels();

		aClass18_1200 = new ProducingGraphicsBuffer(360, 200, getParentComponent());
		Rasterizer.resetPixels();

		aClass18_1203 = new ProducingGraphicsBuffer(202, 238, getParentComponent());
		Rasterizer.resetPixels();

		aClass18_1204 = new ProducingGraphicsBuffer(203, 238, getParentComponent());
		Rasterizer.resetPixels();

		aClass18_1205 = new ProducingGraphicsBuffer(74, 94, getParentComponent());
		Rasterizer.resetPixels();

		aClass18_1206 = new ProducingGraphicsBuffer(75, 94, getParentComponent());
		Rasterizer.resetPixels();

		if (titleArchive != null)
		{
			prepareTitleBackground();
			prepareTitle();
		}

		welcomeScreenRaised = true;
	}

	private void animateTexture(int i)
	{
		if (!lowMemory)
		{
			for (int k = 0; k < anIntArray1290.length; k++)
			{
				int l = anIntArray1290[k];
				if (Rasterizer3D.textureLastUsed[l] >= i)
				{
					IndexedImage class50_sub1_sub1_sub3 = Rasterizer3D.textureImages[l];
					int i1 = class50_sub1_sub1_sub3.imgWidth * class50_sub1_sub1_sub3.height - 1;
					int j1 = class50_sub1_sub1_sub3.imgWidth * tickDelta * 2;
					byte[] abyte0 = class50_sub1_sub1_sub3.imgPixels;
					byte[] abyte1 = aByteArray1245;
					for (int k1 = 0; k1 <= i1; k1++)
					{
						abyte1[k1] = abyte0[k1 - j1 & i1];
					}

					class50_sub1_sub1_sub3.imgPixels = abyte1;
					aByteArray1245 = abyte0;
					Rasterizer3D.resetTexture(l);
				}
			}

		}
	}

	private void method66(int i, Widget class13, int j, int k, int l, int i1, int j1, int k1)
	{
		if (j1 != 23658)
		{
			return;
		}
		if (class13.type != 0 || class13.children == null || class13.hiddenUntilHovered)
		{
			return;
		}
		if (i1 < l || k1 < i || i1 > l + class13.width || k1 > i + class13.height)
		{
			return;
		}
		int l1 = class13.children.length;
		for (int i2 = 0; i2 < l1; i2++)
		{
			int j2 = class13.childrenX[i2] + l;
			int k2 = (class13.childrenY[i2] + i) - k;
			Widget child = Widget.forId(class13.children[i2]);
			j2 += child.xOffset;
			k2 += child.yOffset;
			if ((child.hoveredPopup >= 0 || child.disabledHoveredColor != 0) && i1 >= j2 && k1 >= k2
				&& i1 < j2 + child.width && k1 < k2 + child.height)
			{
				if (child.hoveredPopup >= 0)
				{
					anInt915 = child.hoveredPopup;
				}
				else
				{
					anInt915 = child.id;
				}
			}
			if (child.type == 8 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width
				&& k1 < k2 + child.height)
			{
				anInt1315 = child.id;
			}
			if (child.type == 0)
			{
				method66(k2, child, j, child.scrollPosition, j2, i1, 23658, k1);
				if (child.scrollLimit > child.height)
				{
					scrollInterface(child.scrollLimit, k2, child, (byte) 102, k1, j, i1, child.height, j2
						+ child.width);
				}
			}
			else
			{
				if (child.actionType == 1 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width
					&& k1 < k2 + child.height)
				{
					boolean flag = false;
					if (child.contentType != 0)
					{
						flag = processFriendListClick(child);
					}
					if (!flag)
					{
						menuActionTexts[menuActionRow] = child.tooltip;
						menuActionTypes[menuActionRow] = 352;
						secondMenuOperand[menuActionRow] = child.id;
						menuActionRow++;
					}
				}
				if (child.actionType == 2 && widgetSelected == 0 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width
					&& k1 < k2 + child.height)
				{
					String circumfix = child.optionCircumfix;
					if (circumfix.indexOf(" ") != -1)
					{
						circumfix = circumfix.substring(0, circumfix.indexOf(" "));
					}
					menuActionTexts[menuActionRow] = circumfix + " @gre@" + child.optionText;
					menuActionTypes[menuActionRow] = Actions.USABLE_WIDGET;
					secondMenuOperand[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.actionType == 3 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width
					&& k1 < k2 + child.height)
				{
					menuActionTexts[menuActionRow] = "Close";
					if (j == 3)
					{
						menuActionTypes[menuActionRow] = 55;
					}
					else
					{
						menuActionTypes[menuActionRow] = Actions.CLOSE_WIDGETS;
					}
					secondMenuOperand[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.actionType == 4 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width
					&& k1 < k2 + child.height)
				{
					menuActionTexts[menuActionRow] = child.tooltip;
					menuActionTypes[menuActionRow] = 890;
					secondMenuOperand[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.actionType == 5 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width
					&& k1 < k2 + child.height)
				{
					menuActionTexts[menuActionRow] = child.tooltip;
					menuActionTypes[menuActionRow] = 518;
					secondMenuOperand[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.actionType == 6 && !aBoolean1239 && i1 >= j2 && k1 >= k2 && i1 < j2 + child.width
					&& k1 < k2 + child.height)
				{
					menuActionTexts[menuActionRow] = child.tooltip;
					menuActionTypes[menuActionRow] = Actions.CLICK_TO_CONTINUE;
					secondMenuOperand[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.type == 2)
				{
					int l2 = 0;
					for (int i3 = 0; i3 < child.height; i3++)
					{
						for (int j3 = 0; j3 < child.width; j3++)
						{
							int k3 = j2 + j3 * (32 + child.itemSpritePadsX);
							int l3 = k2 + i3 * (32 + child.itemSpritePadsY);
							if (l2 < 20)
							{
								k3 += child.imageX[l2];
								l3 += child.imageY[l2];
							}
							if (i1 >= k3 && k1 >= l3 && i1 < k3 + 32 && k1 < l3 + 32)
							{
								mouseInvInterfaceIndex = l2;
								lastActiveInvInterface = child.id;
								if (child.items[l2] > 0)
								{
									ItemDefinition definition = ItemDefinition.lookup(child.items[l2] - 1);
									if (itemSelected == 1 && child.isInventory)
									{
										if (child.id != anInt1148 || l2 != anInt1147)
										{
											menuActionTexts[menuActionRow] = "Use " + selectedItemName + " with @lre@"
												+ definition.name;
											menuActionTypes[menuActionRow] = 903;
											selectedMenuActions[menuActionRow] = definition.id;
											firstMenuOperand[menuActionRow] = l2;
											secondMenuOperand[menuActionRow] = child.id;
											menuActionRow++;
										}
									}
									else if (widgetSelected == 1 && child.isInventory)
									{
										if ((selectedMask & 0x10) == 16)
										{
											menuActionTexts[menuActionRow] = selectedWidgetName + " @lre@" + definition.name;
											menuActionTypes[menuActionRow] = 361;
											selectedMenuActions[menuActionRow] = definition.id;
											firstMenuOperand[menuActionRow] = l2;
											secondMenuOperand[menuActionRow] = child.id;
											menuActionRow++;
										}
									}
									else
									{
										if (child.isInventory)
										{
											for (int i4 = 4; i4 >= 3; i4--)
											{
												if (definition.inventoryActions != null
													&& definition.inventoryActions[i4] != null)
												{
													menuActionTexts[menuActionRow] = definition.inventoryActions[i4]
														+ " @lre@" + definition.name;
													if (i4 == 3)
													{
														menuActionTypes[menuActionRow] = 227;
													}
													if (i4 == 4)
													{
														menuActionTypes[menuActionRow] = 891;
													}
													selectedMenuActions[menuActionRow] = definition.id;
													firstMenuOperand[menuActionRow] = l2;
													secondMenuOperand[menuActionRow] = child.id;
													menuActionRow++;
												}
												else if (i4 == 4)
												{
													menuActionTexts[menuActionRow] = "Drop @lre@" + definition.name;
													menuActionTypes[menuActionRow] = 891;
													selectedMenuActions[menuActionRow] = definition.id;
													firstMenuOperand[menuActionRow] = l2;
													secondMenuOperand[menuActionRow] = child.id;
													menuActionRow++;
												}
											}

										}
										if (child.itemUsable)
										{
											menuActionTexts[menuActionRow] = "Use @lre@" + definition.name;
											menuActionTypes[menuActionRow] = 52;
											selectedMenuActions[menuActionRow] = definition.id;
											firstMenuOperand[menuActionRow] = l2;
											secondMenuOperand[menuActionRow] = child.id;
											menuActionRow++;
										}
										if (child.isInventory && definition.inventoryActions != null)
										{
											for (int j4 = 2; j4 >= 0; j4--)
											{
												if (definition.inventoryActions[j4] != null)
												{
													menuActionTexts[menuActionRow] = definition.inventoryActions[j4]
														+ " @lre@" + definition.name;
													if (j4 == 0)
													{
														menuActionTypes[menuActionRow] = 961;
													}
													if (j4 == 1)
													{
														menuActionTypes[menuActionRow] = 399;
													}
													if (j4 == 2)
													{
														menuActionTypes[menuActionRow] = 324;
													}
													selectedMenuActions[menuActionRow] = definition.id;
													firstMenuOperand[menuActionRow] = l2;
													secondMenuOperand[menuActionRow] = child.id;
													menuActionRow++;
												}
											}

										}
										if (child.options != null)
										{
											for (int k4 = 4; k4 >= 0; k4--)
											{
												if (child.options[k4] != null)
												{
													menuActionTexts[menuActionRow] = child.options[k4]
														+ " @lre@" + definition.name;
													if (k4 == 0)
													{
														menuActionTypes[menuActionRow] = 9;
													}
													if (k4 == 1)
													{
														menuActionTypes[menuActionRow] = 225;
													}
													if (k4 == 2)
													{
														menuActionTypes[menuActionRow] = 444;
													}
													if (k4 == 3)
													{
														menuActionTypes[menuActionRow] = 564;
													}
													if (k4 == 4)
													{
														menuActionTypes[menuActionRow] = 894;
													}
													selectedMenuActions[menuActionRow] = definition.id;
													firstMenuOperand[menuActionRow] = l2;
													secondMenuOperand[menuActionRow] = child.id;
													menuActionRow++;
												}
											}

										}
										StringBuilder examineText = new StringBuilder();
										examineText.append(MessageFormat.format("Examine <col=ff9040>{0}</col>", definition.name));
										if (DEBUG_CONTEXT)
										{
											examineText.append(" <col=00ff00>(</col>");
											examineText.append(
												MessageFormat.format("<col=ffffff>{0}</col>",
													Integer.toString(definition.id)
												)
											);
											examineText.append("<col=00ff00>)</col>");
										}
										menuActionTexts[menuActionRow] = examineText.toString();
										menuActionTypes[menuActionRow] = Actions.EXAMINE_ITEM;
										selectedMenuActions[menuActionRow] = definition.id;
										firstMenuOperand[menuActionRow] = l2;
										secondMenuOperand[menuActionRow] = child.id;
										menuActionRow++;
									}
								}
							}
							l2++;
						}

					}

				}
			}
		}

	}

	private void processNPCs()
	{
		for (int i = 0; i < npcCount; i++)
		{
			int npcIndex = npcIds[i];
			Npc npc = npcs[npcIndex];
			if (npc != null)
			{
				processActor(npc);
			}
		}
	}

	private void processActor(Actor actor)
	{
		if (actor.worldX < 128 || actor.worldY < 128
			|| actor.worldX >= 13184 || actor.worldY >= 13184)
		{
			actor.emoteAnimation = -1;
			actor.graphic = -1;
			actor.moveCycleEnd = 0;
			actor.moveCycleStart = 0;
			actor.worldX = actor.pathX[0] * 128
				+ actor.boundaryDimension * 64;
			actor.worldY = actor.pathY[0] * 128
				+ actor.boundaryDimension * 64;
			actor.resetPath();
		}
		if (actor == localPlayer
			&& (actor.worldX < 1536 || actor.worldY < 1536
			|| actor.worldX >= 11776 || actor.worldY >= 11776))
		{
			actor.emoteAnimation = -1;
			actor.graphic = -1;
			actor.moveCycleEnd = 0;
			actor.moveCycleStart = 0;
			actor.worldX = actor.pathX[0] * 128
				+ actor.boundaryDimension * 64;
			actor.worldY = actor.pathY[0] * 128
				+ actor.boundaryDimension * 64;
			actor.resetPath();
		}
		if (actor.moveCycleEnd > pulseCycle)
		{
			processActorLateMovement(actor);
		}
		else if (actor.moveCycleStart >= pulseCycle)
		{
			processActorMovementVariables(actor);
		}
		else
		{
			processActorMovement(actor, 0);
		}
		processActorRotation(actor);
		processActorSequence(actor);

	}

	private void processActorLateMovement(Actor actor)
	{
		int dt = actor.moveCycleEnd - pulseCycle;
		int destX = actor.movementStartX * 128 + actor.boundaryDimension * 64;
		int destY = actor.movementStartY * 128 + actor.boundaryDimension * 64;
		actor.worldX += (destX - actor.worldX) / dt;
		actor.worldY += (destY - actor.worldY) / dt;
		actor.resyncWalkCycle = 0;
		if (actor.moveDirection == 0)
		{
			actor.nextStepOrientation = 1024;
		}
		if (actor.moveDirection == 1)
		{
			actor.nextStepOrientation = 1536;
		}
		if (actor.moveDirection == 2)
		{
			actor.nextStepOrientation = 0;
		}
		if (actor.moveDirection == 3)
		{
			actor.nextStepOrientation = 512;
		}
	}

	private void processActorMovementVariables(Actor actor)
	{
		if (actor.moveCycleStart == pulseCycle
			|| actor.emoteAnimation == -1
			|| actor.animationDelay != 0
			|| actor.animationSequence + 1 > AnimationSequence.animations[actor.emoteAnimation]
			.getFrameLength(actor.displayedEmoteFrames))
		{
			int walkDt = actor.moveCycleStart - actor.moveCycleEnd;
			int dt = pulseCycle - actor.moveCycleEnd;
			int startX = actor.movementStartX * 128 + actor.boundaryDimension * 64;
			int startY = actor.movementStartY * 128 + actor.boundaryDimension * 64;
			int endX = actor.movementEndX * 128 + actor.boundaryDimension * 64;
			int endY = actor.movementEndY * 128 + actor.boundaryDimension * 64;
			actor.worldX = (startX * (walkDt - dt) + endX * dt) / walkDt;
			actor.worldY = (startY * (walkDt - dt) + endY * dt) / walkDt;
		}
		actor.resyncWalkCycle = 0;
		if (actor.moveDirection == 0)
		{
			actor.nextStepOrientation = 1024;
		}
		if (actor.moveDirection == 1)
		{
			actor.nextStepOrientation = 1536;
		}
		if (actor.moveDirection == 2)
		{
			actor.nextStepOrientation = 0;
		}
		if (actor.moveDirection == 3)
		{
			actor.nextStepOrientation = 512;
		}
		actor.currentRotation = actor.nextStepOrientation;
	}

	private void processActorMovement(Actor actor, int i)
	{
		actor.movementAnimation = actor.idleAnimation;
		if (actor.pathLength == 0)
		{
			actor.resyncWalkCycle = 0;
			return;
		}
		if (actor.emoteAnimation != -1 && actor.animationDelay == 0)
		{
			AnimationSequence animationSequence = AnimationSequence.animations[actor.emoteAnimation];
			if (actor.stillPathPosition > 0 && animationSequence.speedFlag == 0)
			{
				actor.resyncWalkCycle++;
				return;
			}
			if (actor.stillPathPosition <= 0 && animationSequence.priority == 0)
			{
				actor.resyncWalkCycle++;
				return;
			}
		}
		int sceneX = actor.worldX;
		int sceneY = actor.worldY;
		int destX = actor.pathX[actor.pathLength - 1] * 128
			+ actor.boundaryDimension * 64;
		int destY = actor.pathY[actor.pathLength - 1] * 128
			+ actor.boundaryDimension * 64;
		if (destX - sceneX > 256 || destX - sceneX < -256 || destY - sceneY > 256 || destY - sceneY < -256)
		{
			actor.worldX = destX;
			actor.worldY = destY;
			return;
		}
		if (sceneX < destX)
		{
			if (sceneY < destY)
			{
				actor.nextStepOrientation = 1280;
			}
			else if (sceneY > destY)
			{
				actor.nextStepOrientation = 1792;
			}
			else
			{
				actor.nextStepOrientation = 1536;
			}
		}
		else if (sceneX > destX)
		{
			if (sceneY < destY)
			{
				actor.nextStepOrientation = 768;
			}
			else if (sceneY > destY)
			{
				actor.nextStepOrientation = 256;
			}
			else
			{
				actor.nextStepOrientation = 512;
			}
		}
		else if (sceneY < destY)
		{
			actor.nextStepOrientation = 1024;
		}
		else
		{
			actor.nextStepOrientation = 0;
		}
		int angleDifference = actor.nextStepOrientation - actor.currentRotation & 0x7ff;
		if (angleDifference > 1024)
		{
			angleDifference -= 2048;
		}
		int index = actor.turnAroundAnimationId;
		if (i != 0)
		{
			outBuffer.putByte(34);
		}
		if (angleDifference >= -256 && angleDifference <= 256)
		{
			index = actor.walkAnimationId;
		}
		else if (angleDifference >= 256 && angleDifference < 768)
		{
			index = actor.turnLeftAnimationId;
		}
		else if (angleDifference >= -768 && angleDifference <= -256)
		{
			index = actor.turnRightAnimationId;
		}
		if (index == -1)
		{
			index = actor.walkAnimationId;
		}
		actor.movementAnimation = index;
		int speed = 4;
		if (actor.currentRotation != actor.nextStepOrientation
			&& actor.faceActor == -1 && actor.turnSpeed != 0)
		{
			speed = 2;
		}
		if (actor.pathLength > 2)
		{
			speed = 6;
		}
		if (actor.pathLength > 3)
		{
			speed = 8;
		}
		if (actor.resyncWalkCycle > 0 && actor.pathLength > 1)
		{
			speed = 8;
			actor.resyncWalkCycle--;
		}
		if (actor.runningQueue[actor.pathLength - 1])
		{
			speed <<= 1;
		}
		if (speed >= 8 && actor.movementAnimation == actor.walkAnimationId
			&& actor.runAnimationId != -1)
		{
			actor.movementAnimation = actor.runAnimationId;
		}
		if (sceneX < destX)
		{
			actor.worldX += speed;
			if (actor.worldX > destX)
			{
				actor.worldX = destX;
			}
		}
		else if (sceneX > destX)
		{
			actor.worldX -= speed;
			if (actor.worldX < destX)
			{
				actor.worldX = destX;
			}
		}
		if (sceneY < destY)
		{
			actor.worldY += speed;
			if (actor.worldY > destY)
			{
				actor.worldY = destY;
			}
		}
		else if (sceneY > destY)
		{
			actor.worldY -= speed;
			if (actor.worldY < destY)
			{
				actor.worldY = destY;
			}
		}
		if (actor.worldX == destX && actor.worldY == destY)
		{
			actor.pathLength--;
			if (actor.stillPathPosition > 0)
			{
				actor.stillPathPosition--;
			}
		}
	}

	private void processActorRotation(Actor actor)
	{
		if (actor.turnSpeed == 0)
		{
			return;
		}
		if (actor.faceActor != -1 && actor.faceActor < 32768)
		{
			Npc npc = npcs[actor.faceActor];
			if (npc != null)
			{
				int dx = actor.worldX - npc.worldX;
				int dy = actor.worldY - npc.worldY;
				if (dx != 0 || dy != 0)
				{
					actor.nextStepOrientation = (int) (Math.atan2(dx, dy) * 325.94900000000001D) & 0x7ff;
				}
			}
		}
		if (actor.faceActor >= 32768)
		{
			int playerIndex = actor.faceActor - 32768;
			if (playerIndex == thisPlayerServerId)
			{
				playerIndex = maxPlayerIndex;
			}
			Player player = players[playerIndex];
			if (player != null)
			{
				int dx = actor.worldX - player.worldX;
				int dy = actor.worldY - player.worldY;
				if (dx != 0 || dy != 0)
				{
					actor.nextStepOrientation = (int) (Math.atan2(dx, dy) * 325.94900000000001D) & 0x7ff;
				}
			}
		}
		if ((actor.faceX != 0 || actor.faceY != 0)
			&& (actor.pathLength == 0 || actor.resyncWalkCycle > 0))
		{
			int dx = actor.worldX - (actor.faceX - nextTopLeftTileX - nextTopLeftTileX) * 64;
			int dy = actor.worldY - (actor.faceY - nextTopRightTileY - nextTopRightTileY) * 64;
			if (dx != 0 || dy != 0)
			{
				actor.nextStepOrientation = (int) (Math.atan2(dx, dy) * 325.94900000000001D) & 0x7ff;
			}
			actor.faceX = 0;
			actor.faceY = 0;
		}
		int da = actor.nextStepOrientation - actor.currentRotation & 0x7ff;
		if (da != 0)
		{
			if (da < actor.turnSpeed || da > 2048 - actor.turnSpeed)
			{
				actor.currentRotation = actor.nextStepOrientation;
			}
			else if (da > 1024)
			{
				actor.currentRotation -= actor.turnSpeed;
			}
			else
			{
				actor.currentRotation += actor.turnSpeed;
			}
			actor.currentRotation &= 0x7ff;
			if (actor.movementAnimation == actor.idleAnimation
				&& actor.currentRotation != actor.nextStepOrientation)
			{
				if (actor.standTurnAnimationId != -1)
				{
					actor.movementAnimation = actor.standTurnAnimationId;
					return;
				}
				actor.movementAnimation = actor.walkAnimationId;
			}
		}
	}

	private void processActorSequence(Actor actor)
	{
		actor.dynamic = false;
		if (actor.movementAnimation != -1)
		{
			AnimationSequence animation = AnimationSequence.animations[actor.movementAnimation];
			actor.movementCycle++;
			if (actor.displayedMovementFrames < animation.frameCount
				&& actor.movementCycle > animation.getFrameLength(actor.displayedMovementFrames))
			{
				actor.movementCycle = 1;
				actor.displayedMovementFrames++;
			}
			if (actor.displayedMovementFrames >= animation.frameCount)
			{
				actor.movementCycle = 1;
				actor.displayedMovementFrames = 0;
			}
		}
		if (actor.graphic != -1 && pulseCycle >= actor.spotGraphicDelay)
		{
			if (actor.currentAnimation < 0)
			{
				actor.currentAnimation = 0;
			}
			AnimationSequence animationSequence = SpotAnimation.cache[actor.graphic].sequences;
			actor.animationCycle++;
			if (actor.currentAnimation < animationSequence.frameCount
				&& actor.animationCycle > animationSequence.getFrameLength(actor.currentAnimation))
			{
				actor.animationCycle = 1;
				actor.currentAnimation++;
			}
			if (actor.currentAnimation >= animationSequence.frameCount
				&& (actor.currentAnimation < 0 || actor.currentAnimation >= animationSequence.frameCount))
			{
				actor.graphic = -1;
			}
		}
		if (actor.emoteAnimation != -1 && actor.animationDelay <= 1)
		{
			AnimationSequence animation = AnimationSequence.animations[actor.emoteAnimation];
			if (animation.speedFlag == 1 && actor.stillPathPosition > 0
				&& actor.moveCycleEnd <= pulseCycle && actor.moveCycleStart < pulseCycle)
			{
				actor.animationDelay = 1;
				return;
			}
		}
		if (actor.emoteAnimation != -1 && actor.animationDelay == 0)
		{
			AnimationSequence animation = AnimationSequence.animations[actor.emoteAnimation];
			actor.animationSequence++;
			if (actor.displayedEmoteFrames < animation.frameCount
				&& actor.animationSequence > animation.getFrameLength(actor.displayedEmoteFrames))
			{
				actor.animationSequence = 1;
				actor.displayedEmoteFrames++;
			}
			if (actor.displayedEmoteFrames >= animation.frameCount)
			{
				actor.displayedEmoteFrames -= animation.frameStep;
				actor.animationResetCycle++;
				if (actor.animationResetCycle >= animation.resetCycle)
				{
					actor.emoteAnimation = -1;
				}
				if (actor.displayedEmoteFrames < 0 || actor.displayedEmoteFrames >= animation.frameCount)
				{
					actor.emoteAnimation = -1;
				}
			}
			actor.dynamic = animation.dynamic;
		}
		if (actor.animationDelay > 0)
		{
			actor.animationDelay--;
		}
	}

	private void drawGameScreen()
	{
		if (fullscreenWidgetId != -1 && (loadingStage == 2 || super.imageProducer != null))
		{
			if (loadingStage == 2)
			{
				handleSequences(tickDelta, fullscreenWidgetId);
				if (fullscreenWidgetChildId != -1)
				{
					handleSequences(tickDelta, fullscreenWidgetChildId);
				}
				tickDelta = 0;
				resetAllImageProducers();
				super.imageProducer.createRasterizer();
				Rasterizer3D.lineOffsets = fullScreenTextureArray;
				Rasterizer.resetPixels();
				welcomeScreenRaised = true;
				Widget widget = Widget.forId(fullscreenWidgetId);
				if (widget.width == 512 && widget.height == 334 && widget.type == 0)
				{
					widget.width = 765;
					widget.height = 503;
				}
				drawInterface(0, 0, widget, 0);
				if (fullscreenWidgetChildId != -1)
				{
					Widget widget1 = Widget.forId(fullscreenWidgetChildId);
					if (widget1.width == 512 && widget1.height == 334 && widget1.type == 0)
					{
						widget1.width = 765;
						widget1.height = 503;
					}
					drawInterface(0, 0, widget1, 0);
				}
				if (!menuOpen)
				{
					processRightClick(-521);
					drawMenuTooltip();
				}
				else
				{
					drawMenu();
				}
			}
			super.imageProducer.drawGraphics(0, 0, super.gameGraphics);
			return;
		}
		if (welcomeScreenRaised)
		{
			method122();
			welcomeScreenRaised = false;
			aClass18_906.drawGraphics(0, 4, super.gameGraphics);
			aClass18_907.drawGraphics(0, 357, super.gameGraphics);
			aClass18_908.drawGraphics(722, 4, super.gameGraphics);
			aClass18_909.drawGraphics(743, 205, super.gameGraphics);
			aClass18_910.drawGraphics(0, 0, super.gameGraphics);
			aClass18_911.drawGraphics(516, 4, super.gameGraphics);
			aClass18_912.drawGraphics(516, 205, super.gameGraphics);
			aClass18_913.drawGraphics(496, 357, super.gameGraphics);
			aClass18_914.drawGraphics(0, 338, super.gameGraphics);
			redrawTabArea = true;
			redrawChatbox = true;
			drawTabIcons = true;
			redrawChatMode = true;
			if (loadingStage != 2)
			{
				gameScreenImageProducer.drawGraphics(4, 4, super.gameGraphics);
				aClass18_1157.drawGraphics(550, 4, super.gameGraphics);
			}
			anInt1237++;
			if (anInt1237 > 85)
			{
				anInt1237 = 0;
				outBuffer.putOpcode(168);
			}
		}
		if (loadingStage == 2)
		{
			renderGameView();
		}
		if (menuOpen && menuScreenArea == 1)
		{
			redrawTabArea = true;
		}
		if (tabAreaOverlayWidgetId != -1)
		{
			boolean flag = handleSequences(tickDelta, tabAreaOverlayWidgetId);
			if (flag)
			{
				redrawTabArea = true;
			}
		}
		if (atInventoryInterfaceType == 2)
		{
			redrawTabArea = true;
		}
		if (activeInterfaceType == 2)
		{
			redrawTabArea = true;
		}
		if (redrawTabArea)
		{
			drawTabArea();
			redrawTabArea = false;
		}
		if (openChatboxWidgetId == -1 && inputType == 0)
		{
			chatboxInterface.scrollPosition = chatboxScrollMax - chatboxScroll - 77;
			if (super.mouseX > 448 && super.mouseX < 560 && super.mouseY > 332)
			{
				scrollInterface(chatboxScrollMax, 0, chatboxInterface, (byte) 102, super.mouseY - 357, -1, super.mouseX - 17, 77, 463);
			}
			int currentScroll = chatboxScrollMax - 77 - chatboxInterface.scrollPosition;
			if (currentScroll < 0)
			{
				currentScroll = 0;
			}
			if (currentScroll > chatboxScrollMax - 77)
			{
				currentScroll = chatboxScrollMax - 77;
			}
			if (chatboxScroll != currentScroll)
			{
				chatboxScroll = currentScroll;
				redrawChatbox = true;
			}
		}
		if (openChatboxWidgetId == -1 && inputType == 3)
		{
			int scrollMax = itemSearchResultCount * 14 + 7;
			chatboxInterface.scrollPosition = itemSearchScroll;
			if (super.mouseX > 448 && super.mouseX < 560 && super.mouseY > 332)
			{
				scrollInterface(scrollMax, 0, chatboxInterface, (byte) 102, super.mouseY - 357, -1, super.mouseX - 17, 77, 463);
			}
			int currentScroll = chatboxInterface.scrollPosition;
			if (currentScroll < 0)
			{
				currentScroll = 0;
			}
			if (currentScroll > scrollMax - 77)
			{
				currentScroll = scrollMax - 77;
			}
			if (itemSearchScroll != currentScroll)
			{
				itemSearchScroll = currentScroll;
				redrawChatbox = true;
			}
		}
		if (openChatboxWidgetId != -1)
		{
			boolean flag1 = handleSequences(tickDelta, openChatboxWidgetId);
			if (flag1)
			{
				redrawChatbox = true;
			}
		}
		if (atInventoryInterfaceType == 3)
		{
			redrawChatbox = true;
		}
		if (activeInterfaceType == 3)
		{
			redrawChatbox = true;
		}
		if (clickToContinueString != null)
		{
			redrawChatbox = true;
		}
		if (menuOpen && menuScreenArea == 2)
		{
			redrawChatbox = true;
		}
		if (redrawChatbox)
		{
			renderChatbox();
			redrawChatbox = false;
		}
		if (loadingStage == 2)
		{
			renderMinimap();
			aClass18_1157.drawGraphics(550, 4, super.gameGraphics);
		}
		if (flashingTabId != -1)
		{
			drawTabIcons = true;
		}
		if (drawTabIcons)
		{
			if (flashingTabId != -1 && flashingTabId == currentTabId)
			{
				flashingTabId = -1;
				outBuffer.putOpcode(119);
				outBuffer.putByte(currentTabId);
			}
			drawTabIcons = false;
			aClass18_1110.createRasterizer();
			tabTopBack.drawImage(0, 0);
			if (tabAreaOverlayWidgetId == -1)
			{
				if (tabWidgetIds[currentTabId] != -1)
				{
					if (currentTabId == 0)
					{
						imageRedstone1.drawImage(22, 10);
					}
					if (currentTabId == 1)
					{
						imageRedstone2.drawImage(54, 8);
					}
					if (currentTabId == 2)
					{
						imageRedstone2.drawImage(82, 8);
					}
					if (currentTabId == 3)
					{
						imageRedstone3.drawImage(110, 8);
					}
					if (currentTabId == 4)
					{
						imageFlippedRedstone2.drawImage(153, 8);
					}
					if (currentTabId == 5)
					{
						imageFlippedRedstone2.drawImage(181, 8);
					}
					if (currentTabId == 6)
					{
						imageFlippedRedstone1.drawImage(209, 9);
					}
				}
				if (tabWidgetIds[0] != -1 && (flashingTabId != 0 || pulseCycle % 20 < 10))
				{
					tabIcon[0].drawImage(29, 13);
				}
				if (tabWidgetIds[1] != -1 && (flashingTabId != 1 || pulseCycle % 20 < 10))
				{
					tabIcon[1].drawImage(53, 11);
				}
				if (tabWidgetIds[2] != -1 && (flashingTabId != 2 || pulseCycle % 20 < 10))
				{
					tabIcon[2].drawImage(82, 11);
				}
				if (tabWidgetIds[3] != -1 && (flashingTabId != 3 || pulseCycle % 20 < 10))
				{
					tabIcon[3].drawImage(115, 12);
				}
				if (tabWidgetIds[4] != -1 && (flashingTabId != 4 || pulseCycle % 20 < 10))
				{
					tabIcon[4].drawImage(153, 13);
				}
				if (tabWidgetIds[5] != -1 && (flashingTabId != 5 || pulseCycle % 20 < 10))
				{
					tabIcon[5].drawImage(180, 11);
				}
				if (tabWidgetIds[6] != -1 && (flashingTabId != 6 || pulseCycle % 20 < 10))
				{
					tabIcon[6].drawImage(208, 13);
				}
			}
			aClass18_1110.drawGraphics(516, 160, super.gameGraphics);
			aClass18_1109.createRasterizer();
			tabBottomBack.drawImage(0, 0);
			if (tabAreaOverlayWidgetId == -1)
			{
				if (tabWidgetIds[currentTabId] != -1)
				{
					if (currentTabId == 7)
					{
						aClass50_Sub1_Sub1_Sub3_983.drawImage(42, 0);
					}
					if (currentTabId == 8)
					{
						aClass50_Sub1_Sub1_Sub3_984.drawImage(74, 0);
					}
					if (currentTabId == 9)
					{
						aClass50_Sub1_Sub1_Sub3_984.drawImage(102, 0);
					}
					if (currentTabId == 10)
					{
						aClass50_Sub1_Sub1_Sub3_985.drawImage(130, 1);
					}
					if (currentTabId == 11)
					{
						aClass50_Sub1_Sub1_Sub3_987.drawImage(173, 0);
					}
					if (currentTabId == 12)
					{
						aClass50_Sub1_Sub1_Sub3_987.drawImage(201, 0);
					}
					if (currentTabId == 13)
					{
						aClass50_Sub1_Sub1_Sub3_986.drawImage(229, 0);
					}
				}
				if (tabWidgetIds[8] != -1 && (flashingTabId != 8 || pulseCycle % 20 < 10))
				{
					tabIcon[7].drawImage(74, 2);
				}
				if (tabWidgetIds[9] != -1 && (flashingTabId != 9 || pulseCycle % 20 < 10))
				{
					tabIcon[8].drawImage(102, 3);
				}
				if (tabWidgetIds[10] != -1 && (flashingTabId != 10 || pulseCycle % 20 < 10))
				{
					tabIcon[9].drawImage(137, 4);
				}
				if (tabWidgetIds[11] != -1 && (flashingTabId != 11 || pulseCycle % 20 < 10))
				{
					tabIcon[10].drawImage(174, 2);
				}
				if (tabWidgetIds[12] != -1 && (flashingTabId != 12 || pulseCycle % 20 < 10))
				{
					tabIcon[11].drawImage(201, 2);
				}
				if (tabWidgetIds[13] != -1 && (flashingTabId != 13 || pulseCycle % 20 < 10))
				{
					tabIcon[12].drawImage(226, 2);
				}
			}
			aClass18_1109.drawGraphics(496, 466, super.gameGraphics);
			gameScreenImageProducer.createRasterizer();
			Rasterizer3D.lineOffsets = viewportOffsets;
		}
		if (redrawChatMode)
		{
			redrawChatMode = false;
			aClass18_1108.createRasterizer();
			bottomChatBack.drawImage(0, 0);
			fontNormal.drawStringCenter("Public chat", 55, 28, 0xffffff, true);
			if (publicChatMode == 0)
			{
				fontNormal.drawStringCenter("On", 55, 41, 65280, true);
			}
			if (publicChatMode == 1)
			{
				fontNormal.drawStringCenter("Friends", 55, 41, 0xffff00, true);
			}
			if (publicChatMode == 2)
			{
				fontNormal.drawStringCenter("Off", 55, 41, 0xff0000, true);
			}
			if (publicChatMode == 3)
			{
				fontNormal.drawStringCenter("Hide", 55, 41, 65535, true);
			}
			fontNormal.drawStringCenter("Private chat", 184, 28, 0xffffff, true);
			if (privateChatMode == 0)
			{
				fontNormal.drawStringCenter("On", 184, 41, 65280, true);
			}
			if (privateChatMode == 1)
			{
				fontNormal.drawStringCenter("Friends", 184, 41, 0xffff00, true);
			}
			if (privateChatMode == 2)
			{
				fontNormal.drawStringCenter("Off", 184, 41, 0xff0000, true);
			}
			fontNormal.drawStringCenter("Trade/compete", 324, 28, 0xffffff, true);
			if (tradeMode == 0)
			{
				fontNormal.drawStringCenter("On", 324, 41, 65280, true);
			}
			if (tradeMode == 1)
			{
				fontNormal.drawStringCenter("Friends", 324, 41, 0xffff00, true);
			}
			if (tradeMode == 2)
			{
				fontNormal.drawStringCenter("Off", 324, 41, 0xff0000, true);
			}
			fontNormal.drawStringCenter("Report abuse", 458, 33, 0xffffff, true);
			aClass18_1108.drawGraphics(0, 453, super.gameGraphics);
			gameScreenImageProducer.createRasterizer();
			Rasterizer3D.lineOffsets = viewportOffsets;
		}
		tickDelta = 0;
	}

	private void renderSplitPrivateMessages()
	{
		if (anInt1223 == 0)
		{
			return;
		}

		TypeFace typeFace = fontNormal;
		int line = 0;

		if (systemUpdateTime != 0)
		{
			line = 1;
		}

		for (int i = 0; i < 100; i++)
		{
			if (chatMessages[i] != null)
			{
				int type = chatTypes[i];
				String name = chatPlayerNames[i];
				byte privilege = 0;

				if (name != null && name.startsWith("@cr1@"))
				{
					name = name.substring(5);
					privilege = 1;
				}

				if (name != null && name.startsWith("@cr2@"))
				{
					name = name.substring(5);
					privilege = 2;
				}

				if ((type == 3 || type == 7) && (type == 7 || privateChatMode == 0 || privateChatMode == 1 && hasFriend(name)))
				{
					int y = 329 - line * 13;
					int x = 4;

					typeFace.drawString("From", x, y, 0);
					typeFace.drawString("From", x, y - 1, 65535);

					x += typeFace.getStringEffectWidth("From ");

					if (privilege == 1)
					{
						moderatorIcon[0].drawImage(x, y - 12);
						x += 14;
					}

					if (privilege == 2)
					{
						moderatorIcon[1].drawImage(x, y - 12);
						x += 14;
					}

					typeFace.drawString(name + ": " + chatMessages[i], x, y, 0);
					typeFace.drawString(name + ": " + chatMessages[i], x, y - 1, 65535);

					if (++line >= 5)
					{
						return;
					}
				}

				if (type == 5 && privateChatMode < 2)
				{
					int y = 329 - line * 13;

					typeFace.drawString(chatMessages[i], 4, y, 0);
					typeFace.drawString(chatMessages[i], 4, y - 1, 65535);

					if (++line >= 5)
					{
						return;
					}
				}

				if (type == 6 && privateChatMode < 2)
				{
					int y = 329 - line * 13;

					typeFace.drawString("To " + name + ": " + chatMessages[i], 4, y, 0);
					typeFace.drawString("To " + name + ": " + chatMessages[i], 4, y - 1, 65535);

					if (++line >= 5)
					{
						return;
					}
				}
			}
		}
	}

	public void init()
	{
//        world = Integer.parseInt(getParameter("nodeid"));
//        portOffset = Integer.parseInt(getParameter("portoff"));
//        String s = getParameter("lowmem");
//        if (s != null && s.equals("1"))
//            setLowMemory();
//        else
//            setHighMemory();
//        String s1 = getParameter("free");
//        if (s1 != null && s1.equals("1"))
//            memberServer = false;
//        else
//            memberServer = true;
		initializeApplet(765, 503);
	}

	private void renderStationaryGraphics()
	{
		for (GameAnimableObject gameAnimableObject = (GameAnimableObject) gameAnimableObjectQueue.first(); gameAnimableObject != null; gameAnimableObject = (GameAnimableObject) gameAnimableObjectQueue
			.next())
		{
			if (gameAnimableObject.plane != plane || gameAnimableObject.transformCompleted)
			{
				gameAnimableObject.remove();
			}
			else if (pulseCycle >= gameAnimableObject.loopCycle)
			{
				gameAnimableObject.nextFrame(tickDelta);
				if (gameAnimableObject.transformCompleted)
				{
					gameAnimableObject.remove();
				}
				else
				{
					currentScene.addEntity(gameAnimableObject.plane, gameAnimableObject.x, gameAnimableObject.y, gameAnimableObject.z, gameAnimableObject, -1,
						60, false,
						0);
				}
			}
		}

	}

	private void processOnDemandQueue()
	{
		do
		{
			OnDemandNode onDemandNode;
			do
			{
				onDemandNode = onDemandRequester.next();
				if (onDemandNode == null)
				{
					return;
				}
				if (onDemandNode.type == 0)
				{
					Model.loadModelHeader(onDemandNode.buffer, onDemandNode.id);
					if ((onDemandRequester.modelId(onDemandNode.id) & 0x62) != 0)
					{
						redrawTabArea = true;
						if (openChatboxWidgetId != -1 || dialogueId != -1)
						{
							redrawChatbox = true;
						}
					}
				}
				if (onDemandNode.type == 1 && onDemandNode.buffer != null)
				{
					Animation.method236(onDemandNode.buffer);
				}
				if (onDemandNode.type == 2 && onDemandNode.id == nextSong && onDemandNode.buffer != null)
				{
					saveMidi(songChanging, onDemandNode.buffer);
				}
				if (onDemandNode.type == 3 && loadingStage == 1)
				{
					for (int i = 0; i < terrainData.length; i++)
					{
						if (terrainDataIds[i] == onDemandNode.id)
						{
							terrainData[i] = onDemandNode.buffer;
							if (onDemandNode.buffer == null)
							{
								terrainDataIds[i] = -1;
							}
							break;
						}
						if (objectDataIds[i] != onDemandNode.id)
						{
							continue;
						}
						objectData[i] = onDemandNode.buffer;
						if (onDemandNode.buffer == null)
						{
							objectDataIds[i] = -1;
						}
						break;
					}

				}
			} while (onDemandNode.type != 93 || !onDemandRequester.method334(onDemandNode.id, false));
			MapRegion.passiveRequestGameObjectModels(onDemandRequester, new Buffer(onDemandNode.buffer));
		} while (true);
	}

	private void login(String username, String password, boolean reconnecting)
	{
		SignLink.errorName = username;

		try
		{
			if (!reconnecting)
			{
				statusLineOne = "";
				statusLineTwo = "Connecting to server...";

				drawLoginScreen(true);
			}

			gameConnection = new BufferedConnection(this, openSocket(Configuration.GAME_PORT + portOffset));
			long base37name = TextUtils.nameToLong(username);
			int hash = (int) (base37name >> 16 & 31L);
			outBuffer.currentPosition = 0;

			outBuffer.putByte(14);
			outBuffer.putByte(hash);
			gameConnection.write(2, 0, outBuffer.buffer);

			for (int j = 0; j < 8; j++)
			{
				gameConnection.read();
			}

			int responseCode = gameConnection.read();
			int initialResponseCode = responseCode;

			if (responseCode == 0)
			{
				gameConnection.read(buffer.buffer, 0, 8);

				buffer.currentPosition = 0;
				serverSeed = buffer.getLongBE();
				int[] seed = new int[4];

				seed[0] = (int) (Math.random() * 99999999D);
				seed[1] = (int) (Math.random() * 99999999D);
				seed[2] = (int) (serverSeed >> 32);
				seed[3] = (int) serverSeed;

				outBuffer.currentPosition = 0;

				outBuffer.putByte(10);
				outBuffer.putIntBE(seed[0]);
				outBuffer.putIntBE(seed[1]);
				outBuffer.putIntBE(seed[2]);
				outBuffer.putIntBE(seed[3]);
				outBuffer.putIntBE(SignLink.uid);
				outBuffer.putString(username);
				outBuffer.putString(password);

				if (Configuration.RSA_ENABLED)
				{
					outBuffer.encrypt(Configuration.RSA_MODULUS, Configuration.RSA_PUBLIC_KEY);
				}

				tempBuffer.currentPosition = 0;

				if (reconnecting)
				{
					tempBuffer.putByte(18);
				}
				else
				{
					tempBuffer.putByte(16);
				}

				tempBuffer.putByte(outBuffer.currentPosition + 36 + 1 + 1 + 2);
				tempBuffer.putByte(255);
				tempBuffer.putShortBE(SignLink.CLIENT_REVISION);
				tempBuffer.putByte(lowMemory ? 1 : 0);

				for (int i = 0; i < 9; i++)
				{
					tempBuffer.putIntBE(archiveHashes[i]);
				}

				tempBuffer.putBytes(outBuffer.buffer, 0, outBuffer.currentPosition);

				outBuffer.random = new ISAACCipher(seed);

				for (int i = 0; i < 4; i++)
				{
					seed[i] += 50;
				}

				incomingRandom = new ISAACCipher(seed);

				gameConnection.write(tempBuffer.currentPosition, 0, tempBuffer.buffer);

				responseCode = gameConnection.read();
			}

			if (responseCode == 1)
			{
				try
				{
					Thread.sleep(2000L);
				}
				catch (Exception ignored)
				{
				}

				login(username, password, reconnecting);
				return;
			}

			if (responseCode == 2)
			{
				playerRights = gameConnection.read();
				accountFlagged = gameConnection.read() == 1;
				lastClickTime = 0L;
				duplicateClickCount = 0;
				mouseCapturer.coord = 0;
				super.awtFocus = true;
				windowFocused = true;
				loggedIn = true;
				outBuffer.currentPosition = 0;
				buffer.currentPosition = 0;
				opcode = -1;
				lastOpcode = -1;
				secondLastOpcode = -1;
				thirdLastOpcode = -1;
				packetSize = 0;
				netCycle = 0;
				systemUpdateTime = 0;
				idleLogout = 0;
				headIconDrawType = 0;
				menuActionRow = 0;
				menuOpen = false;
				super.idleTime = 0;

				for (int j1 = 0; j1 < 100; j1++)
				{
					chatMessages[j1] = null;
				}

				itemSelected = 0;
				widgetSelected = 0;
				loadingStage = 0;
				currentSound = 0;
				cameraOffsetX = (int) (Math.random() * 100D) - 50;
				cameraOffsetY = (int) (Math.random() * 110D) - 55;
				cameraRandomisationA = (int) (Math.random() * 80D) - 40;
				cameraYawOffset = (int) (Math.random() * 120D) - 60;
				mapZoomOffset = (int) (Math.random() * 30D) - 20;
				cameraHorizontal = (int) (Math.random() * 20D) - 10 & 0x7ff;
				minimapState = 0;
				lastRegionId = -1;
				destinationX = 0;
				destinationY = 0;
				localPlayerCount = 0;
				npcCount = 0;

				for (int i2 = 0; i2 < maxPlayerCount; i2++)
				{
					players[i2] = null;
					cachedAppearances[i2] = null;
				}

				for (int k2 = 0; k2 < 16384; k2++)
				{
					npcs[k2] = null;
				}

				localPlayer = players[maxPlayerIndex] = new Player();

				projectileQueue.clear();
				gameAnimableObjectQueue.clear();

				for (int l2 = 0; l2 < 4; l2++)
				{
					for (int i3 = 0; i3 < 104; i3++)
					{
						for (int k3 = 0; k3 < 104; k3++)
						{
							groundItems.clearTile(l2, i3, k3);
						}
					}
				}

				spawnObjectList = new LinkedList();
				friendListStatus = 0;
				friendsCount = 0;

				method44(dialogueId);
				dialogueId = -1;

				method44(openChatboxWidgetId);
				openChatboxWidgetId = -1;

				method44(openScreenWidgetId);
				openScreenWidgetId = -1;

				method44(fullscreenWidgetId);
				fullscreenWidgetId = -1;

				method44(fullscreenWidgetChildId);
				fullscreenWidgetChildId = -1;

				method44(tabAreaOverlayWidgetId);
				tabAreaOverlayWidgetId = -1;

				method44(walkableWidgetId);
				walkableWidgetId = -1;

				aBoolean1239 = false;
				currentTabId = 3;
				inputType = 0;
				menuOpen = false;
				messagePromptRaised = false;
				clickToContinueString = null;
				anInt1319 = 0;
				flashingTabId = -1;
				characterEditChangeGenger = true;

				changeGender();

				for (int j3 = 0; j3 < 5; j3++)
				{
					characterEditColors[j3] = 0;
				}

				for (int l3 = 0; l3 < 5; l3++)
				{
					aStringArray1069[l3] = null;
					aBooleanArray1070[l3] = false;
				}

				anInt1100 = 0;
				anInt1165 = 0;
				anInt1235 = 0;
				anInt1052 = 0;
				anInt1139 = 0;

				method122();
				return;
			}

			if (responseCode == 3)
			{
				statusLineOne = "";
				statusLineTwo = "Invalid username or password.";
				return;
			}

			if (responseCode == 4)
			{
				statusLineOne = "Your account has been disabled.";
				statusLineTwo = "Please check your message-centre for details.";
				return;
			}

			if (responseCode == 5)
			{
				statusLineOne = "Your account is already logged in.";
				statusLineTwo = "Try again in 60 secs...";
				return;
			}

			if (responseCode == 6)
			{
				statusLineOne = "RuneScape has been updated!";
				statusLineTwo = "Please reload this page.";
				return;
			}

			if (responseCode == 7)
			{
				statusLineOne = "This world is full.";
				statusLineTwo = "Please use a different world.";
				return;
			}

			if (responseCode == 8)
			{
				statusLineOne = "Unable to connect.";
				statusLineTwo = "Login server offline.";
				return;
			}

			if (responseCode == 9)
			{
				statusLineOne = "Login limit exceeded.";
				statusLineTwo = "Too many connections from your address.";
				return;
			}

			if (responseCode == 10)
			{
				statusLineOne = "Unable to connect.";
				statusLineTwo = "Bad session id.";
				return;
			}

			if (responseCode == 12)
			{
				statusLineOne = "You need a members account to login to this world.";
				statusLineTwo = "Please subscribe, or use a different world.";
				return;
			}

			if (responseCode == 13)
			{
				statusLineOne = "Could not complete login.";
				statusLineTwo = "Please try using a different world.";
				return;
			}

			if (responseCode == 14)
			{
				statusLineOne = "The server is being updated.";
				statusLineTwo = "Please wait 1 minute and try again.";
				return;
			}

			if (responseCode == 15)
			{
				loggedIn = true;
				outBuffer.currentPosition = 0;
				buffer.currentPosition = 0;
				opcode = -1;
				lastOpcode = -1;
				secondLastOpcode = -1;
				thirdLastOpcode = -1;
				packetSize = 0;
				netCycle = 0;
				systemUpdateTime = 0;
				menuActionRow = 0;
				menuOpen = false;
				loadRegionTime = System.currentTimeMillis();
				return;
			}

			if (responseCode == 16)
			{
				statusLineOne = "Login attempts exceeded.";
				statusLineTwo = "Please wait 1 minute and try again.";
				return;
			}

			if (responseCode == 17)
			{
				statusLineOne = "You are standing in a members-only area.";
				statusLineTwo = "To play on this world move to a free area first";
				return;
			}

			if (responseCode == 18)
			{
				statusLineOne = "Account locked as we suspect it has been stolen.";
				statusLineTwo = "Press 'recover a locked account' on front page.";
				return;
			}

			if (responseCode == 20)
			{
				statusLineOne = "Invalid loginserver requested";
				statusLineTwo = "Please try using a different world.";
				return;
			}

			if (responseCode == 21)
			{
				int time = gameConnection.read();

				for (time += 3; time >= 0; time--)
				{
					statusLineOne = "You have only just left another world";
					statusLineTwo = "Your profile will be transferred in: " + time;

					drawLoginScreen(true);

					try
					{
						Thread.sleep(1200L);
					}
					catch (Exception ignored)
					{
					}
				}

				login(username, password, reconnecting);
				return;
			}

			if (responseCode == 22)
			{
				statusLineOne = "Malformed login packet.";
				statusLineTwo = "Please try again.";
				return;
			}

			if (responseCode == 23)
			{
				statusLineOne = "No reply from loginserver.";
				statusLineTwo = "Please try again.";
				return;
			}

			if (responseCode == 24)
			{
				statusLineOne = "Error loading your profile.";
				statusLineTwo = "Please contact customer support.";
				return;
			}

			if (responseCode == 25)
			{
				statusLineOne = "Unexpected loginserver response.";
				statusLineTwo = "Please try using a different world.";
				return;
			}

			if (responseCode == 26)
			{
				statusLineOne = "This computers address has been blocked";
				statusLineTwo = "as it was used to break our rules";
				return;
			}

			if (responseCode == -1)
			{
				if (initialResponseCode == 0)
				{
					if (reconnectionAttempts < 2)
					{
						try
						{
							Thread.sleep(2000L);
						}
						catch (Exception ignored)
						{
						}

						reconnectionAttempts++;

						login(username, password, reconnecting);
						return;
					}
					else
					{
						statusLineOne = "No response from loginserver";
						statusLineTwo = "Please wait 1 minute and try again.";
						return;
					}
				}
				else
				{
					statusLineOne = "No response from server";
					statusLineTwo = "Please try using a different world.";
					return;
				}
			}
			else
			{
				System.out.println("response:" + responseCode);

				statusLineOne = "Unexpected server response";
				statusLineTwo = "Please try using a different world.";
				return;
			}
		}
		catch (IOException ex)
		{
			statusLineOne = "";
		}

		statusLineTwo = "Error connecting to server.";
	}

	private boolean method80(int dstY, int j, int dstX, int l)
	{
		int i1 = l >> 14 & 0x7fff;
		int j1 = currentScene.getArrangement(plane, dstX, dstY, l);
		if (j1 == -1)
		{
			return false;
		}
		int objectType = j1 & 0x1f;
		int l1 = j1 >> 6 & 3;
		if (objectType == 10 || objectType == 11 || objectType == 22)
		{
			GameObjectDefinition class47 = GameObjectDefinition.getDefinition(i1);
			int i2;
			int j2;
			if (l1 == 0 || l1 == 2)
			{
				i2 = class47.sizeX;
				j2 = class47.sizeY;
			}
			else
			{
				i2 = class47.sizeY;
				j2 = class47.sizeX;
			}
			int k2 = class47.anInt764;
			if (l1 != 0)
			{
				k2 = (k2 << l1 & 0xf) + (k2 >> 4 - l1);
			}
			walk(true, false, dstY, localPlayer.pathY[0], i2, j2, 2, 0, dstX, k2, 0,
				localPlayer.pathX[0]);
		}
		else
		{
			walk(true, false, dstY, localPlayer.pathY[0], 0, 0, 2, objectType + 1, dstX, 0, l1,
				localPlayer.pathX[0]);
		}
		crossX = super.clickX;
		crossY = super.clickY;
		crossType = 2;
		crossIndex = 0;
		packetSize += j;
		return true;
	}

	private void calculateFlamePositions()
	{ //TODO: Needs more refactoring
		int c = 256;

		for (int x = 10; x < 117; x++)
		{
			int rand = (int) (Math.random() * 100D);

			if (rand < 50)
			{
				anIntArray1084[x + (c - 2 << 7)] = 255;
			}
		}

		for (int i = 0; i < 100; i++)
		{
			int x = (int) (Math.random() * 124D) + 2;
			int y = (int) (Math.random() * 128D) + 128;
			int pixel = x + (y << 7);
			anIntArray1084[pixel] = 192;
		}

		for (int y = 1; y < c - 1; y++)
		{
			for (int x = 1; x < 127; x++)
			{
				int pixel = x + (y << 7);
				anIntArray1085[pixel] = (anIntArray1084[pixel - 1] + anIntArray1084[pixel + 1] + anIntArray1084[pixel - 128] + anIntArray1084[pixel + 128]) / 4;
			}
		}

		anInt1238 += 128;

		if (anInt1238 > anIntArray1176.length)
		{
			anInt1238 -= anIntArray1176.length;
			int rand = (int) (Math.random() * 12D);

			method83(titleFlameEmblem[rand], 0);
		}

		for (int y = 1; y < c - 1; y++)
		{
			for (int x = 1; x < 127; x++)
			{
				int pixel = x + (y << 7);
				int i4 = anIntArray1085[pixel + 128] - anIntArray1176[pixel + anInt1238 & anIntArray1176.length - 1] / 5;

				if (i4 < 0)
				{
					i4 = 0;
				}

				anIntArray1084[pixel] = i4;
			}
		}

		for (int i = 0; i < c - 1; i++)
		{
			anIntArray1166[i] = anIntArray1166[i + 1];
		}

		anIntArray1166[c - 1] = (int) (Math.sin((double) pulseCycle / 14D) * 16D + Math.sin((double) pulseCycle / 15D)
			* 14D + Math.sin((double) pulseCycle / 16D) * 12D);

		if (anInt1047 > 0)
		{
			anInt1047 -= 4;
		}
		if (anInt1048 > 0)
		{
			anInt1048 -= 4;
		}
		if (anInt1047 == 0 && anInt1048 == 0)
		{
			int rand = (int) (Math.random() * 2000D);

			if (rand == 0)
			{
				anInt1047 = 1024;
			}
			if (rand == 1)
			{
				anInt1048 = 1024;
			}
		}
	}

	private void processNpcMenuOptions(ActorDefinition actorDefinition, int i, int j, int k)
	{
		if (menuActionRow >= 400)
		{
			return;
		}
		if (actorDefinition.childrenIds != null)
		{
			actorDefinition = actorDefinition.getChildDefinition();
		}
		if (actorDefinition == null)
		{
			return;
		}
		if (!actorDefinition.clickable)
		{
			return;
		}
		String name = actorDefinition.name;
		if (actorDefinition.combatLevel != 0)
		{
			name = name + getCombatLevelColour(localPlayer.combatLevel, actorDefinition.combatLevel) + " (level-" + actorDefinition.combatLevel + ")";
		}
		if (itemSelected == 1)
		{
			menuActionTexts[menuActionRow] = "Use " + selectedItemName + " with @yel@" + name;
			menuActionTypes[menuActionRow] = 347;
			selectedMenuActions[menuActionRow] = k;
			firstMenuOperand[menuActionRow] = j;
			secondMenuOperand[menuActionRow] = i;
			menuActionRow++;
			return;
		}
		if (widgetSelected == 1)
		{
			if ((selectedMask & 2) == 2)
			{
				menuActionTexts[menuActionRow] = selectedWidgetName + " @yel@" + name;
				menuActionTypes[menuActionRow] = 67;
				selectedMenuActions[menuActionRow] = k;
				firstMenuOperand[menuActionRow] = j;
				secondMenuOperand[menuActionRow] = i;
				menuActionRow++;
			}
		}
		else
		{
			if (actorDefinition.actions != null)
			{
				for (int l = 4; l >= 0; l--)
				{
					if (actorDefinition.actions[l] != null && !actorDefinition.actions[l].equalsIgnoreCase("attack"))
					{
						menuActionTexts[menuActionRow] = actorDefinition.actions[l] + " @yel@" + name;
						if (l == 0)
						{
							menuActionTypes[menuActionRow] = 318; // packet 112
						}
						if (l == 1)
						{
							menuActionTypes[menuActionRow] = 921; // packet 67
						}
						if (l == 2)
						{
							menuActionTypes[menuActionRow] = 118; // packet 13
						}
						if (l == 3)
						{
							menuActionTypes[menuActionRow] = 553; // packet 42
						}
						if (l == 4)
						{
							menuActionTypes[menuActionRow] = 432; // packet 8
						}
						selectedMenuActions[menuActionRow] = k;
						firstMenuOperand[menuActionRow] = j;
						secondMenuOperand[menuActionRow] = i;
						menuActionRow++;
					}
				}

			}
			if (actorDefinition.actions != null)
			{
				for (int i1 = 4; i1 >= 0; i1--)
				{
					if (actorDefinition.actions[i1] != null && actorDefinition.actions[i1].equalsIgnoreCase("attack"))
					{
						char c = '\0';
						if (actorDefinition.combatLevel > localPlayer.combatLevel)
						{
							c = '\u07D0';
						}
						menuActionTexts[menuActionRow] = actorDefinition.actions[i1] + " @yel@" + name;
						if (i1 == 0)
						{
							menuActionTypes[menuActionRow] = 318 + c;
						}
						if (i1 == 1)
						{
							menuActionTypes[menuActionRow] = 921 + c;
						}
						if (i1 == 2)
						{
							menuActionTypes[menuActionRow] = 118 + c;
						}
						if (i1 == 3)
						{
							menuActionTypes[menuActionRow] = 553 + c;
						}
						if (i1 == 4)
						{
							menuActionTypes[menuActionRow] = 432 + c;
						}
						selectedMenuActions[menuActionRow] = k;
						firstMenuOperand[menuActionRow] = j;
						secondMenuOperand[menuActionRow] = i;
						menuActionRow++;
					}
				}

			}
			StringBuilder examineText = new StringBuilder();
			examineText.append(MessageFormat.format("Examine <col=ffff00>{0}</col>", name));
			if (DEBUG_CONTEXT)
			{
				examineText.append(" <col=00ff00>(</col>");
				examineText.append(
					MessageFormat.format("<col=ffffff>{0}</col>",
						Long.toString(actorDefinition.id)
					)
				);
				examineText.append("<col=00ff00>)</col>");
			}
			menuActionTexts[menuActionRow] = examineText.toString();
			menuActionTypes[menuActionRow] = 1668;
			selectedMenuActions[menuActionRow] = k;
			firstMenuOperand[menuActionRow] = j;
			secondMenuOperand[menuActionRow] = i;
			menuActionRow++;
		}
	}

	private void method83(IndexedImage class50_sub1_sub1_sub3, int i)
	{
		packetSize += i;
		int j = 256;
		for (int k = 0; k < anIntArray1176.length; k++)
		{
			anIntArray1176[k] = 0;
		}

		for (int l = 0; l < 5000; l++)
		{
			int i1 = (int) (Math.random() * 128D * (double) j);
			anIntArray1176[i1] = (int) (Math.random() * 256D);
		}

		for (int j1 = 0; j1 < 20; j1++)
		{
			for (int k1 = 1; k1 < j - 1; k1++)
			{
				for (int i2 = 1; i2 < 127; i2++)
				{
					int k2 = i2 + (k1 << 7);
					anIntArray1177[k2] = (anIntArray1176[k2 - 1] + anIntArray1176[k2 + 1] + anIntArray1176[k2 - 128] + anIntArray1176[k2 + 128]) / 4;
				}

			}

			int[] ai = anIntArray1176;
			anIntArray1176 = anIntArray1177;
			anIntArray1177 = ai;
		}

		if (class50_sub1_sub1_sub3 != null)
		{
			int l1 = 0;
			for (int j2 = 0; j2 < class50_sub1_sub1_sub3.height; j2++)
			{
				for (int l2 = 0; l2 < class50_sub1_sub1_sub3.imgWidth; l2++)
				{
					if (class50_sub1_sub1_sub3.imgPixels[l1++] != 0)
					{
						int i3 = l2 + 16 + class50_sub1_sub1_sub3.xDrawOffset;
						int j3 = j2 + 16 + class50_sub1_sub1_sub3.yDrawOffset;
						int k3 = i3 + (j3 << 7);
						anIntArray1176[k3] = 0;
					}
				}

			}

		}
	}

	private void renderChatbox()
	{
		chatboxProducingGraphicsBuffer.createRasterizer();

		Rasterizer3D.lineOffsets = chatboxLineOffsets;

		chatboxBackgroundImage.drawImage(0, 0);

		if (messagePromptRaised)
		{
			fontBold.drawStringLeft(chatboxInputMessage, 239, 40, 0);
			fontBold.drawStringLeft(chatMessage + "*", 239, 60, 128);
		}
		else if (inputType == 1)
		{
			fontBold.drawStringLeft("Enter amount:", 239, 40, 0);
			fontBold.drawStringLeft(inputInputMessage + "*", 239, 60, 128);
		}
		else if (inputType == 2)
		{
			fontBold.drawStringLeft("Enter name:", 239, 40, 0);
			fontBold.drawStringLeft(inputInputMessage + "*", 239, 60, 128);
		}
		else if (inputType == 3)
		{
			if (!inputInputMessage.equals(lastItemSearchInput))
			{
				itemSearch(inputInputMessage);
				lastItemSearchInput = inputInputMessage;
			}

			TypeFace typeFace = fontNormal;

			Rasterizer.setCoordinates(0, 0, 77, 463);

			for (int index = 0; index < itemSearchResultCount; index++)
			{
				int y = (18 + index * 14) - itemSearchScroll;

				if (y > 0 && y < 110)
				{
					StringBuilder examineText = new StringBuilder();
					examineText.append(MessageFormat.format("<col=000000>{0}</col>", itemSearchResultNames[index]));
					if (DEBUG_CONTEXT)
					{
						examineText.append(" <col=ffffff>[</col>");
						examineText.append(
							MessageFormat.format("<col=0000ff>{0}</col>",
								Integer.toString(itemSearchResultIds[index])
							)
						);
						examineText.append("<col=ffffff>]</col>");
					}
					typeFace.drawStringLeft(examineText.toString(), 239, y, 0);
				}
			}

			Rasterizer.resetCoordinates();

			if (itemSearchResultCount > 5)
			{
				drawScrollBar(true, itemSearchScroll, 463, 77, itemSearchResultCount * 14 + 7, 0);
			}

			if (inputInputMessage.length() == 0)
			{
				fontBold.drawStringLeft("Enter object name", 239, 40, 255);
			}
			else if (itemSearchResultCount == 0)
			{
				fontBold.drawStringLeft("No matching objects found, please shorten search", 239, 40, 0);
			}

			typeFace.drawStringLeft(inputInputMessage + "*", 239, 90, 0);
			Rasterizer.drawHorizontalLine(0, 77, 479, 0);
		}
		else if (clickToContinueString != null)
		{
			fontBold.drawStringLeft(clickToContinueString, 239, 40, 0);
			fontBold.drawStringLeft("Click to continue", 239, 60, 128);
		}
		else if (openChatboxWidgetId != -1)
		{
			drawInterface(0, 0, Widget.forId(openChatboxWidgetId), 0);
		}
		else if (dialogueId != -1)
		{
			drawInterface(0, 0, Widget.forId(dialogueId), 0);
		}
		else
		{
			TypeFace typeFace = fontNormal;
			int line = 0;

			Rasterizer.setCoordinates(0, 0, 77, 463);

			for (int i = 0; i < 100; i++)
			{
				if (chatMessages[i] != null)
				{
					String name = chatPlayerNames[i];
					int type = chatTypes[i];
					int y = (70 - line * 14) + chatboxScroll;
					byte privilege = 0;

					if (name != null && name.startsWith("@cr1@"))
					{
						name = name.substring(5);
						privilege = 1;
					}

					if (name != null && name.startsWith("@cr2@"))
					{
						name = name.substring(5);
						privilege = 2;
					}

					if (type == 0)
					{
						if (y > 0 && y < 110)
						{
							typeFace.drawString(chatMessages[i], 4, y, 0);
						}

						line++;
					}

					if ((type == 1 || type == 2) && (type == 1 || publicChatMode == 0 || publicChatMode == 1 && hasFriend(name)))
					{
						if (y > 0 && y < 110)
						{
							int x = 4;

							if (privilege == 1)
							{
								moderatorIcon[0].drawImage(x, y - 12);
								x += 14;
							}

							if (privilege == 2)
							{
								moderatorIcon[1].drawImage(x, y - 12);
								x += 14;
							}

							typeFace.drawString(name + ":", x, y, 0);

							x += typeFace.getStringEffectWidth(name) + 8;

							typeFace.drawString(chatMessages[i], x, y, 255);
						}

						line++;
					}

					if ((type == 3 || type == 7) && anInt1223 == 0
						&& (type == 7 || privateChatMode == 0 || privateChatMode == 1 && hasFriend(name)))
					{
						if (y > 0 && y < 110)
						{
							int x = 4;

							typeFace.drawString("From", x, y, 0);

							x += typeFace.getStringEffectWidth("From ");

							if (privilege == 1)
							{
								moderatorIcon[0].drawImage(x, y - 12);
								x += 14;
							}

							if (privilege == 2)
							{
								moderatorIcon[1].drawImage(x, y - 12);
								x += 14;
							}

							typeFace.drawString(name + ":", x, y, 0);

							x += typeFace.getStringEffectWidth(name) + 8;

							typeFace.drawString(chatMessages[i], x, y, 0x800000);
						}

						line++;
					}

					if (type == 4 && (tradeMode == 0 || tradeMode == 1 && hasFriend(name)))
					{
						if (y > 0 && y < 110)
						{
							typeFace.drawString(name + " " + chatMessages[i], 4, y, 0x800080);
						}

						line++;
					}

					if (type == 5 && anInt1223 == 0 && privateChatMode < 2)
					{
						if (y > 0 && y < 110)
						{
							typeFace.drawString(chatMessages[i], 4, y, 0x800000);
						}

						line++;
					}

					if (type == 6 && anInt1223 == 0 && privateChatMode < 2)
					{
						if (y > 0 && y < 110)
						{
							typeFace.drawString("To " + name + ":", 4, y, 0);
							typeFace.drawString(chatMessages[i], 12 + typeFace.getStringEffectWidth("To " + name), y, 0x800000);
						}

						line++;
					}

					if (type == 8 && (tradeMode == 0 || tradeMode == 1 && hasFriend(name)))
					{
						if (y > 0 && y < 110)
						{
							typeFace.drawString(name + " " + chatMessages[i], 4, y, 0x7e3200);
						}

						line++;
					}
				}
			}

			Rasterizer.resetCoordinates();

			chatboxScrollMax = line * 14 + 7;

			if (chatboxScrollMax < 78)
			{
				chatboxScrollMax = 78;
			}

			drawScrollBar(true, chatboxScrollMax - chatboxScroll - 77, 463, 77, chatboxScrollMax, 0);

			String name;

			if (localPlayer != null && localPlayer.playerName != null)
			{
				name = localPlayer.playerName;
			}
			else
			{
				name = TextUtils.formatName(username);
			}

			typeFace.drawString(name + ":", 4, 90, 0);
			typeFace.drawString(chatboxInput + "*", 6 + typeFace.getStringEffectWidth(name + ": "), 90, 255);
			Rasterizer.drawHorizontalLine(0, 77, 479, 0);
		}

		if (menuOpen && menuScreenArea == 2)
		{
			drawMenu();
		}

		chatboxProducingGraphicsBuffer.drawGraphics(17, 357, super.gameGraphics);
		gameScreenImageProducer.createRasterizer();

		Rasterizer3D.lineOffsets = viewportOffsets;
	}

	private void processActorOverheadText()
	{
		for (int i = -1; i < localPlayerCount; i++)
		{
			int index = i == -1 ? maxPlayerIndex : playerList[i];
			Player player = players[index];

			if (player != null && player.textCycle > 0)
			{
				player.textCycle--;

				if (player.textCycle == 0)
				{
					player.forcedChat = null;
				}
			}
		}

		for (int i = 0; i < npcCount; i++)
		{
			int index = npcIds[i];
			Npc npc = npcs[index];

			if (npc != null && npc.textCycle > 0)
			{
				npc.textCycle--;

				if (npc.textCycle == 0)
				{
					npc.forcedChat = null;
				}
			}
		}
	}

	private void requestArchiveCrcs()
	{
		int reconnectionDelay = 5;
		int attempts = 0;
		archiveHashes[8] = 0;

		while (archiveHashes[8] == 0)
		{
			String error = "Unknown problem";

			drawLoadingText(20, "Connecting to web server");

			try
			{
				DataInputStream stream = openJaggrabStream("crc" + (int) (Math.random() * 99999999D) + "-" + 377);
				Buffer jaggrab = new Buffer(new byte[40]);

				stream.readFully(jaggrab.buffer, 0, 40);
				stream.close();

				for (int i = 0; i < 9; i++)
				{
					archiveHashes[i] = jaggrab.getIntBE();
				}

				int expectedCrc = jaggrab.getIntBE();
				int calculatedCrc = 1234;

				for (int i = 0; i < 9; i++)
				{
					calculatedCrc = (calculatedCrc << 1) + archiveHashes[i];
				}

				if (expectedCrc != calculatedCrc)
				{
					error = "Checksum problem";
					archiveHashes[8] = 0;
				}
			}
			catch (EOFException _ex)
			{
				error = "EOF problem";
				archiveHashes[8] = 0;
			}
			catch (IOException _ex)
			{
				_ex.printStackTrace();
				error = "Connection problem";
				archiveHashes[8] = 0;
			}
			catch (Exception _ex)
			{
				error = "Logic problem";
				archiveHashes[8] = 0;

				if (!SignLink.reportError)
				{
					return;
				}
			}

			if (archiveHashes[8] == 0)
			{
				attempts++;

				for (int time = reconnectionDelay; time > 0; time--)
				{
					if (attempts >= 10)
					{
						drawLoadingText(10, "Game updated - please reload page");

						time = 10;
					}
					else
					{
						drawLoadingText(10, error + " - Will retry in " + time + " secs.");
					}

					try
					{
						Thread.sleep(1000L);
					}
					catch (Exception ignored)
					{
					}
				}

				reconnectionDelay *= 2;

				if (reconnectionDelay > 60)
				{
					reconnectionDelay = 60;
				}

				useJaggrab = !useJaggrab;
			}
		}
	}

	private void renderMinimap()
	{
		aClass18_1157.createRasterizer();

		if (minimapState == 2)
		{
			byte[] mmBackgroundPixels = minimapBackgroundImage.imgPixels;
			int[] rasterPixels = Rasterizer.pixels;
			int pixelCount = mmBackgroundPixels.length;

			for (int i = 0; i < pixelCount; i++)
			{
				if (mmBackgroundPixels[i] == 0)
				{
					rasterPixels[i] = 0;
				}
			}

			minimapCompass.shapeImageToPixels(0, 0, 33, 33, 256, 25, anIntArray1286, cameraHorizontal,
				anIntArray1180, 25);
			gameScreenImageProducer.createRasterizer();

			Rasterizer3D.lineOffsets = viewportOffsets;
			return;
		}

		int angle = cameraHorizontal + cameraYawOffset & 0x7ff;
		int centerX = 48 + localPlayer.worldX / 32;
		int centerY = 464 - localPlayer.worldY / 32;

		minimapImage.shapeImageToPixels(25, 5, 146, 151, 256 + mapZoomOffset, centerX, anIntArray920,
			angle, anIntArray1019, centerY);
		minimapCompass.shapeImageToPixels(0, 0, 33, 33, 256, 25, anIntArray1286,
			cameraHorizontal, anIntArray1180, 25);

		for (int i = 0; i < minimapHintCount; i++)
		{
			int hintX = (minimapHintX[i] * 4 + 2) - localPlayer.worldX / 32;
			int hintY = (minimapHintY[i] * 4 + 2) - localPlayer.worldY / 32;

			drawOnMinimap(minimapHint[i], hintX, hintY);
		}

		for (int x = 0; x < 104; x++)
		{
			for (int y = 0; y < 104; y++)
			{
				LinkedList itemList = groundItems.getTile(plane, x, y);

				if (itemList != null)
				{
					int itemX = (x * 4 + 2) - localPlayer.worldX / 32;
					int itemY = (y * 4 + 2) - localPlayer.worldY / 32;

					drawOnMinimap(mapdotItem, itemX, itemY);
				}
			}

		}

		for (int i = 0; i < npcCount; i++)
		{
			Npc npc = npcs[npcIds[i]];

			if (npc != null && npc.isVisible())
			{
				ActorDefinition definition = npc.npcDefinition;

				if (definition.childrenIds != null)
				{
					definition = definition.getChildDefinition();
				}

				if (definition != null && definition.minimapVisible && definition.clickable)
				{
					int npcX = npc.worldX / 32 - localPlayer.worldX / 32;
					int npcY = npc.worldY / 32 - localPlayer.worldY / 32;

					drawOnMinimap(mapdotActor, npcX, npcY);
				}
			}
		}

		for (int i = 0; i < localPlayerCount; i++)
		{
			Player player = players[playerList[i]];

			if (player != null && player.isVisible())
			{
				int playerX = player.worldX / 32 - localPlayer.worldX / 32;
				int playerY = player.worldY / 32 - localPlayer.worldY / 32;
				long name = TextUtils.nameToLong(player.playerName);
				boolean isFriend = false;
				boolean isTeammate = false;

				for (int x = 0; x < friendsCount; x++)
				{
					if (name != friends[x] || friendWorlds[x] == 0)
					{
						continue;
					}

					isFriend = true;
					break;
				}

				if (localPlayer.teamId != 0 && player.teamId != 0 && localPlayer.teamId == player.teamId)
				{
					isTeammate = true;
				}

				if (isFriend)
				{
					drawOnMinimap(mapdotFriend, playerX, playerY);
				}
				else if (isTeammate)
				{
					drawOnMinimap(mapdotTeammate, playerX, playerY);
				}
				else
				{
					drawOnMinimap(mapdotPlayer, playerX, playerY);
				}
			}
		}

		if (headIconDrawType != 0 && pulseCycle % 20 < 10)
		{
			if (headIconDrawType == 1 && anInt1226 >= 0 && anInt1226 < npcs.length)
			{
				Npc npc = npcs[anInt1226];

				if (npc != null)
				{
					int npcX = npc.worldX / 32 - localPlayer.worldX / 32;
					int npcY = npc.worldY / 32 - localPlayer.worldY / 32;

					drawMinimapMark(aClass50_Sub1_Sub1_Sub1_1037, npcX, npcY);
				}
			}

			if (headIconDrawType == 2)
			{
				int hintX = ((hintIconX - nextTopLeftTileX) * 4 + 2) - localPlayer.worldX / 32;
				int hintY = ((hintIconY - nextTopRightTileY) * 4 + 2) - localPlayer.worldY / 32;

				drawMinimapMark(aClass50_Sub1_Sub1_Sub1_1037, hintX, hintY);
			}

			if (headIconDrawType == 10 && otherPlayerId >= 0 && otherPlayerId < players.length)
			{
				Player player = players[otherPlayerId];

				if (player != null)
				{
					int playerX = player.worldX / 32 - localPlayer.worldX / 32;
					int playerY = player.worldY / 32 - localPlayer.worldY / 32;

					drawMinimapMark(aClass50_Sub1_Sub1_Sub1_1037, playerX, playerY);
				}
			}
		}

		if (destinationX != 0)
		{
			int flagX = (destinationX * 4 + 2) - localPlayer.worldX / 32;
			int flagY = (destinationY * 4 + 2) - localPlayer.worldY / 32;

			drawOnMinimap(mapFlagMarker, flagX, flagY);
		}

		Rasterizer.drawFilledRectangle(97, 78, 3, 3, 0xffffff);
		gameScreenImageProducer.createRasterizer();

		Rasterizer3D.lineOffsets = viewportOffsets;
	}

	public URL getCodeBase()
	{
		try
		{
			return new URL("http://" + Configuration.CODEBASE + ":" + (Configuration.HTTP_PORT + portOffset));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private boolean handleSequences(int tickDelta, int interfaceId)
	{
		boolean flag = false;
		Widget widget = Widget.forId(interfaceId);
		for (int k = 0; k < widget.children.length; k++)
		{
			if (widget.children[k] == -1)
			{
				break;
			}
			Widget widgetChild = Widget.forId(widget.children[k]);
			if (widgetChild.type == 0)
			{
				flag |= handleSequences(tickDelta, widgetChild.id);
			}
			if (widgetChild.type == 6 && (widgetChild.disabledAnimation != -1 || widgetChild.enabledAnimation != -1))
			{
				boolean flag1 = componentEnabled(widgetChild);
				int enabledState;
				if (flag1)
				{
					enabledState = widgetChild.enabledAnimation;
				}
				else
				{
					enabledState = widgetChild.disabledAnimation;
				}
				if (enabledState != -1)
				{
					AnimationSequence animationSequence = AnimationSequence.animations[enabledState];
					for (widgetChild.animationDuration += tickDelta; widgetChild.animationDuration > animationSequence.getFrameLength(widgetChild.animationFrame); )
					{
						widgetChild.animationDuration -= animationSequence.getFrameLength(widgetChild.animationFrame);
						widgetChild.animationFrame++;
						if (widgetChild.animationFrame >= animationSequence.frameCount)
						{
							widgetChild.animationFrame -= animationSequence.frameStep;
							if (widgetChild.animationFrame < 0 || widgetChild.animationFrame >= animationSequence.frameCount)
							{
								widgetChild.animationFrame = 0;
							}
						}
						flag = true;
					}

				}
			}
			if (widgetChild.type == 6 && widgetChild.anInt218 != 0)
			{
				int l = widgetChild.anInt218 >> 16;
				int j1 = (widgetChild.anInt218 << 16) >> 16;
				l *= tickDelta;
				j1 *= tickDelta;
				widgetChild.rotationX = widgetChild.rotationX + l & 0x7ff;
				widgetChild.rotationY = widgetChild.rotationY + j1 & 0x7ff;
				flag = true;
			}
		}

		return flag;
	}

	private String method89(int i, int j)
	{
		if (j < 8 || j > 8)
		{
			throw new NullPointerException();
		}
		if (i < 0x3b9ac9ff)
		{
			return String.valueOf(i);
		}
		else
		{
			return "*";
		}
	}

	private void processRightClick(int i)
	{
		if (activeInterfaceType != 0)
		{
			return;
		}
		menuActionTexts[0] = "Cancel";
		menuActionTypes[0] = 1016;
		menuActionRow = 1;
		if (i >= 0)
		{
			anInt1004 = incomingRandom.nextInt();
		}
		if (fullscreenWidgetId != -1)
		{
			anInt915 = 0;
			anInt1315 = 0;
			method66(0, Widget.forId(fullscreenWidgetId), 0, 0, 0, super.mouseX, 23658, super.mouseY);
			if (anInt915 != anInt1302)
			{
				anInt1302 = anInt915;
			}
			if (anInt1315 != anInt1129)
			{
				anInt1129 = anInt1315;
			}
			return;
		}
		method111();
		anInt915 = 0;
		anInt1315 = 0;
		if (super.mouseX > 4 && super.mouseY > 4 && super.mouseX < 516 && super.mouseY < 338)
		{
			if (openScreenWidgetId != -1)
			{
				method66(4, Widget.forId(openScreenWidgetId), 0, 0, 4, super.mouseX, 23658, super.mouseY);
			}
			else
			{
				handleViewportMouse();
			}
		}
		if (anInt915 != anInt1302)
		{
			anInt1302 = anInt915;
		}
		if (anInt1315 != anInt1129)
		{
			anInt1129 = anInt1315;
		}
		anInt915 = 0;
		anInt1315 = 0;
		if (super.mouseX > 553 && super.mouseY > 205 && super.mouseX < 743 && super.mouseY < 466)
		{
			if (tabAreaOverlayWidgetId != -1)
			{
				method66(205, Widget.forId(tabAreaOverlayWidgetId), 1, 0, 553, super.mouseX, 23658, super.mouseY);
			}
			else if (tabWidgetIds[currentTabId] != -1)
			{
				method66(205, Widget.forId(tabWidgetIds[currentTabId]), 1, 0, 553, super.mouseX, 23658,
					super.mouseY);
			}
		}
		if (anInt915 != anInt1280)
		{
			redrawTabArea = true;
			anInt1280 = anInt915;
		}
		if (anInt1315 != anInt1044)
		{
			redrawTabArea = true;
			anInt1044 = anInt1315;
		}
		anInt915 = 0;
		anInt1315 = 0;
		if (super.mouseX > 17 && super.mouseY > 357 && super.mouseX < 496 && super.mouseY < 453)
		{
			if (openChatboxWidgetId != -1)
			{
				method66(357, Widget.forId(openChatboxWidgetId), 2, 0, 17, super.mouseX, 23658, super.mouseY);
			}
			else if (dialogueId != -1)
			{
				method66(357, Widget.forId(dialogueId), 3, 0, 17, super.mouseX, 23658, super.mouseY);
			}
			else if (super.mouseY < 434 && super.mouseX < 426 && inputType == 0)
			{
				method113(466, super.mouseX - 17, super.mouseY - 357);
			}
		}
		if ((openChatboxWidgetId != -1 || dialogueId != -1) && anInt915 != anInt1106)
		{
			redrawChatbox = true;
			anInt1106 = anInt915;
		}
		if ((openChatboxWidgetId != -1 || dialogueId != -1) && anInt1315 != anInt1284)
		{
			redrawChatbox = true;
			anInt1284 = anInt1315;
		}
		for (boolean flag = false; !flag; )
		{
			flag = true;
			for (int j = 0; j < menuActionRow - 1; j++)
			{
				if (menuActionTypes[j] < 1000 && menuActionTypes[j + 1] > 1000)
				{
					String s = menuActionTexts[j];
					menuActionTexts[j] = menuActionTexts[j + 1];
					menuActionTexts[j + 1] = s;
					int k = menuActionTypes[j];
					menuActionTypes[j] = menuActionTypes[j + 1];
					menuActionTypes[j + 1] = k;
					k = firstMenuOperand[j];
					firstMenuOperand[j] = firstMenuOperand[j + 1];
					firstMenuOperand[j + 1] = k;
					k = secondMenuOperand[j];
					secondMenuOperand[j] = secondMenuOperand[j + 1];
					secondMenuOperand[j + 1] = k;
					k = selectedMenuActions[j];
					selectedMenuActions[j] = selectedMenuActions[j + 1];
					selectedMenuActions[j + 1] = k;
					flag = false;
				}
			}

		}

	}

	private void loadRegion()
	{
		try
		{
			lastRegionId = -1;
			gameAnimableObjectQueue.clear();
			projectileQueue.clear();
			Rasterizer3D.clearTextureCache();
			resetModelCaches();
			currentScene.initToNull();
			System.gc();
			for (int plane = 0; plane < 4; plane++)
			{
				currentCollisionMap[plane].reset();
			}

			for (int z = 0; z < 4; z++)
			{
				for (int x = 0; x < 104; x++)
				{
					for (int y = 0; y < 104; y++)
					{
						currentSceneTileFlags[z][x][y] = 0;
					}

				}

			}

			MapRegion mapRegion = new MapRegion(104, 104, currentSceneTileFlags, intGroundArray);
			int dataLength = terrainData.length;
			outBuffer.putOpcode(40);
			if (!loadGeneratedMap)
			{
				for (int pointer = 0; pointer < dataLength; pointer++)
				{
					int offsetX = (mapCoordinates[pointer] >> 8) * 64 - nextTopLeftTileX;
					int offsetY = (mapCoordinates[pointer] & 0xff) * 64 - nextTopRightTileY;
					byte[] data = terrainData[pointer];
					if (data != null)
					{
						mapRegion.loadTerrainBlock(offsetX, (chunkX - 6) * 8, offsetY, (chunkY - 6) * 8, data,
							currentCollisionMap);
					}
				}

				for (int pointer = 0; pointer < dataLength; pointer++)
				{
					int offsetX = (mapCoordinates[pointer] >> 8) * 64 - nextTopLeftTileX;
					int offsetY = (mapCoordinates[pointer] & 0xff) * 64 - nextTopRightTileY;
					byte[] data = terrainData[pointer];
					if (data == null && chunkY < 800)
					{
						mapRegion.initiateVertexHeights(offsetX, 64, offsetY, 64);
					}
				}

				outBuffer.putOpcode(40);
				for (int _region = 0; _region < dataLength; _region++)
				{
					byte[] data = objectData[_region];
					if (data != null)
					{
						int offsetX = (mapCoordinates[_region] >> 8) * 64 - nextTopLeftTileX;
						int offsetY = (mapCoordinates[_region] & 0xff) * 64 - nextTopRightTileY;
						mapRegion.loadObjectBlock(offsetX, offsetY, currentCollisionMap, currentScene, data);
					}
				}

			}
			if (loadGeneratedMap)
			{
				for (int k3 = 0; k3 < 4; k3++)
				{
					for (int l4 = 0; l4 < 13; l4++)
					{
						for (int k6 = 0; k6 < 13; k6++)
						{
							boolean flag = false;
							int i9 = constructedMapPalette[k3][l4][k6];
							if (i9 != -1)
							{
								int l9 = i9 >> 24 & 3;
								int j10 = i9 >> 1 & 3;
								int l10 = i9 >> 14 & 0x3ff;
								int j11 = i9 >> 3 & 0x7ff;
								int l11 = (l10 / 8 << 8) + j11 / 8;
								for (int j12 = 0; j12 < mapCoordinates.length; j12++)
								{
									if (mapCoordinates[j12] != l11 || terrainData[j12] == null)
									{
										continue;
									}
									mapRegion.method168(j10, (j11 & 7) * 8, false, terrainData[j12], k3, l9,
										l4 * 8, currentCollisionMap, k6 * 8, (l10 & 7) * 8);
									flag = true;
									break;
								}

							}
							if (!flag)
							{
								mapRegion.method166(k3, k6 * 8, l4 * 8);
							}
						}

					}

				}

				for (int i5 = 0; i5 < 13; i5++)
				{
					for (int l6 = 0; l6 < 13; l6++)
					{
						int i8 = constructedMapPalette[0][i5][l6];
						if (i8 == -1)
						{
							mapRegion.initiateVertexHeights(i5 * 8, 8, l6 * 8, 8);
						}
					}

				}

				outBuffer.putOpcode(40);
				for (int i7 = 0; i7 < 4; i7++)
				{
					for (int j8 = 0; j8 < 13; j8++)
					{
						for (int j9 = 0; j9 < 13; j9++)
						{
							int i10 = constructedMapPalette[i7][j8][j9];
							if (i10 != -1)
							{
								int k10 = i10 >> 24 & 3;
								int i11 = i10 >> 1 & 3;
								int k11 = i10 >> 14 & 0x3ff;
								int i12 = i10 >> 3 & 0x7ff;
								int k12 = (k11 / 8 << 8) + i12 / 8;
								for (int l12 = 0; l12 < mapCoordinates.length; l12++)
								{
									if (mapCoordinates[l12] != k12 || objectData[l12] == null)
									{
										continue;
									}
									mapRegion.method172(i7, currentCollisionMap, currentScene, false,
										objectData[l12], j9 * 8, i11, (k11 & 7) * 8, j8 * 8,
										(i12 & 7) * 8, k10);
									break;
								}

							}
						}

					}

				}

			}
			outBuffer.putOpcode(40);
			mapRegion.addTiles(currentCollisionMap, currentScene, 0);
			if (gameScreenImageProducer != null)
			{
				gameScreenImageProducer.createRasterizer();
				Rasterizer3D.lineOffsets = viewportOffsets;
			}
			outBuffer.putOpcode(40);
			int l3 = MapRegion.setZ;
			if (l3 > plane)
			{
				l3 = plane;
			}
			if (l3 < plane - 1)
			{
				l3 = plane - 1;
			}
			if (lowMemory)
			{
				currentScene.setHeightLevel(MapRegion.setZ);
			}
			else
			{
				currentScene.setHeightLevel(0);
			}
			for (int j5 = 0; j5 < 104; j5++)
			{
				for (int j7 = 0; j7 < 104; j7++)
				{
					processGroundItems(j5, j7);
				}

			}

			method18((byte) 3);
		}
		catch (Exception exception)
		{
		}
		GameObjectDefinition.modelCache.removeAll();
		if (super.gameFrame != null)
		{
			outBuffer.putOpcode(78);
			outBuffer.putIntBE(0x3f008edd);
		}
		if (lowMemory && SignLink.cacheData != null)
		{
			int k = onDemandRequester.fileCount(0);
			for (int j1 = 0; j1 < k; j1++)
			{
				int i2 = onDemandRequester.modelId(j1);
				if ((i2 & 0x79) == 0)
				{
					Model.resetModel(j1);
				}
			}

		}
		System.gc();
		Rasterizer3D.resetTextures(20);
		onDemandRequester.immediateRequestCount();
		int l = (chunkX - 6) / 8 - 1;
		int k1 = (chunkX + 6) / 8 + 1;
		int j2 = (chunkY - 6) / 8 - 1;
		int i3 = (chunkY + 6) / 8 + 1;
		if (aBoolean1067)
		{
			l = 49;
			k1 = 50;
			j2 = 49;
			i3 = 50;
		}
		for (int i4 = l; i4 <= k1; i4++)
		{
			for (int k5 = j2; k5 <= i3; k5++)
			{
				if (i4 == l || i4 == k1 || k5 == j2 || k5 == i3)
				{
					int k7 = onDemandRequester.regId(0, i4, k5, 0);
					if (k7 != -1)
					{
						onDemandRequester.passiveRequest(k7, 3);
					}
					int k8 = onDemandRequester.regId(0, i4, k5, 1);
					if (k8 != -1)
					{
						onDemandRequester.passiveRequest(k8, 3);
					}
				}
			}

		}

	}

	private void setCameraPosition(int x, int y, int z, int pitch, int yaw)
	{
		int pitchDifference = 2048 - pitch & 0x7ff;
		int yawDifference = 2048 - yaw & 0x7ff;
		int xOffset = 0;
		int zOffset = 0;
		int yOffset = cameraZoom + pitch * 3;

		if (pitchDifference != 0)
		{
			int sine = Model.SINE[pitchDifference];
			int cosine = Model.COSINE[pitchDifference];
			int temp = zOffset * cosine - yOffset * sine >> 16;
			yOffset = zOffset * sine + yOffset * cosine >> 16;
			zOffset = temp;
		}

		if (yawDifference != 0)
		{
			int sine = Model.SINE[yawDifference];
			int cosine = Model.COSINE[yawDifference];
			int temp = yOffset * sine + xOffset * cosine >> 16;
			yOffset = yOffset * cosine - xOffset * sine >> 16;
			xOffset = temp;
		}

		cameraX = x - xOffset;
		cameraZ = z - zOffset;
		cameraY = y - yOffset;
		cameraVerticalRotation = pitch;
		cameraHorizontalRotation = yaw;
	}

	private boolean componentEnabled(Widget widget)
	{
		if (widget.conditionTypes == null)
		{
			return false;
		}
		for (int id = 0; id < widget.conditionTypes.length; id++)
		{
			int value = parseCS1(widget, id);
			int requirement = widget.conditionValues[id];
			if (widget.conditionTypes[id] == 2)
			{
				if (value >= requirement)
				{
					return false;
				}
			}
			else if (widget.conditionTypes[id] == 3)
			{
				if (value <= requirement)
				{
					return false;
				}
			}
			else if (widget.conditionTypes[id] == 4)
			{
				if (value == requirement)
				{
					return false;
				}
			}
			else if (value != requirement)
			{
				return false;
			}
		}

		return true;
	}

	private void updatePlayers(int size, Buffer buffer)
	{
		enityUpdateCount = 0;
		updatedPlayerCount = 0;

		updateLocalPlayerMovement(buffer);
		updateOtherPlayerMovement(buffer);
		addNewPlayers(size, buffer);
		parsePlayerBlocks(buffer);

		for (int i = 0; i < enityUpdateCount; i++)
		{
			int index = eneityUpdateIndices[i];

			if (players[index].pulseCycle != pulseCycle)
			{
				players[index] = null;
			}
		}

		if (buffer.currentPosition != size)
		{
			SignLink.reportError("Error packet size mismatch in getplayer coord:" + buffer.currentPosition + " psize:" + size);
			throw new RuntimeException("eek");
		}

		for (int i = 0; i < localPlayerCount; i++)
		{
			if (players[playerList[i]] == null)
			{
				SignLink.reportError(username + " null entry in pl list - coord:" + i + " size:" + localPlayerCount);
				throw new RuntimeException("eek");
			}
		}
	}

	private void removeIgnore(long l)
	{
		try
		{
			if (l == 0L)
			{
				return;
			}
			for (int i = 0; i < ignoresCount; i++)
			{
				if (ignores[i] != l)
				{
					continue;
				}
				ignoresCount--;
				redrawTabArea = true;
				for (int j = i; j < ignoresCount; j++)
				{
					ignores[j] = ignores[j + 1];
				}

				outBuffer.putOpcode(160);
				outBuffer.putLongBE(l);
				break;
			}

			return;
		}
		catch (RuntimeException runtimeexception)
		{
			SignLink.reportError("45745, " + 325 + ", " + l + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	private void renderFlames()
	{ //TODO: Needs more refactoring
		int c = 256;

		if (anInt1047 > 0)
		{
			for (int j = 0; j < 256; j++)
			{
				if (anInt1047 > 768)
				{
					anIntArray1310[j] = method106(anIntArray1311[j], anIntArray1312[j], 1024 - anInt1047, 8);
				}
				else if (anInt1047 > 256)
				{
					anIntArray1310[j] = anIntArray1312[j];
				}
				else
				{
					anIntArray1310[j] = method106(anIntArray1312[j], anIntArray1311[j], 256 - anInt1047, 8);
				}
			}
		}
		else if (anInt1048 > 0)
		{
			for (int k = 0; k < 256; k++)
			{
				if (anInt1048 > 768)
				{
					anIntArray1310[k] = method106(anIntArray1311[k], anIntArray1313[k], 1024 - anInt1048, 8);
				}
				else if (anInt1048 > 256)
				{
					anIntArray1310[k] = anIntArray1313[k];
				}
				else
				{
					anIntArray1310[k] = method106(anIntArray1313[k], anIntArray1311[k], 256 - anInt1048, 8);
				}
			}
		}
		else
		{
			System.arraycopy(anIntArray1311, 0, anIntArray1310, 0, 256);
		}

		System.arraycopy(anImageRGB1226.pixels, 0, flameLeftBackground.pixels, 0, 33920);

		int j1 = 0;
		int k1 = 1152;

		for (int l1 = 1; l1 < c - 1; l1++)
		{
			int i2 = (anIntArray1166[l1] * (c - l1)) / c;
			int k2 = 22 + i2;

			if (k2 < 0)
			{
				k2 = 0;
			}

			j1 += k2;

			for (int i3 = k2; i3 < 128; i3++)
			{
				int k3 = anIntArray1084[j1++];

				if (k3 != 0)
				{
					int i4 = k3;
					int k4 = 256 - k3;
					k3 = anIntArray1310[k3];
					int i5 = flameLeftBackground.pixels[k1];
					flameLeftBackground.pixels[k1++] = ((k3 & 0xff00ff) * i4 + (i5 & 0xff00ff) * k4 & 0xff00ff00)
						+ ((k3 & 0xff00) * i4 + (i5 & 0xff00) * k4 & 0xff0000) >> 8;
				}
				else
				{
					k1++;
				}
			}

			k1 += k2;
		}

		flameLeftBackground.drawGraphics(0, 0, super.gameGraphics);

		System.arraycopy(anImageRGB1227.pixels, 0, flameRightBackground.pixels, 0, 33920);

		j1 = 0;
		k1 = 1176;

		for (int l2 = 1; l2 < c - 1; l2++)
		{
			int j3 = (anIntArray1166[l2] * (c - l2)) / c;
			int l3 = 103 - j3;
			k1 += j3;

			for (int j4 = 0; j4 < l3; j4++)
			{
				int l4 = anIntArray1084[j1++];

				if (l4 != 0)
				{
					int j5 = l4;
					int k5 = 256 - l4;
					l4 = anIntArray1310[l4];
					int l5 = flameRightBackground.pixels[k1];
					flameRightBackground.pixels[k1++] = ((l4 & 0xff00ff) * j5 + (l5 & 0xff00ff) * k5 & 0xff00ff00)
						+ ((l4 & 0xff00) * j5 + (l5 & 0xff00) * k5 & 0xff0000) >> 8;
				}
				else
				{
					k1++;
				}
			}

			j1 += 128 - l3;
			k1 += 128 - l3 - j3;
		}

		flameRightBackground.drawGraphics(637, 0, super.gameGraphics);
	}

	private void processPlayers()
	{
		for (int i = -1; i < localPlayerCount; i++)
		{
			int index;
			if (i == -1)
			{
				index = maxPlayerIndex;
			}
			else
			{
				index = playerList[i];
			}
			Player p = players[index];
			if (p != null)
			{
				processActor(p);
			}
		}

	}

	private void updateWidget(Widget widget)
	{
		int type = widget.contentType;
		if (type >= 1 && type <= 100 || type >= 701 && type <= 800)
		{
			if (type == 1 && friendListStatus == 0)
			{
				widget.disabledText = "Loading friend list";
				widget.actionType = 0;
				return;
			}
			if (type == 1 && friendListStatus == 1)
			{
				widget.disabledText = "Connecting to friendserver";
				widget.actionType = 0;
				return;
			}
			if (type == 2 && friendListStatus != 2)
			{
				widget.disabledText = "Please wait...";
				widget.actionType = 0;
				return;
			}
			int j = friendsCount;
			if (friendListStatus != 2)
			{
				j = 0;
			}
			if (type > 700)
			{
				type -= 601;
			}
			else
			{
				type--;
			}
			if (type >= j)
			{
				widget.disabledText = "";
				widget.actionType = 0;
				return;
			}
			else
			{
				widget.disabledText = friendUsernames[type];
				widget.actionType = 1;
				return;
			}
		}
		if (type >= 101 && type <= 200 || type >= 801 && type <= 900)
		{
			int count = friendsCount;
			if (friendListStatus != 2)
			{
				count = 0;
			}
			if (type > 800)
			{
				type -= 701;
			}
			else
			{
				type -= 101;
			}
			if (type >= count)
			{
				widget.disabledText = "";
				widget.actionType = 0;
				return;
			}
			if (friendWorlds[type] == 0)
			{
				widget.disabledText = "@red@Offline";
			}
			else if (friendWorlds[type] < 200)
			{
				if (friendWorlds[type] == world)
				{
					widget.disabledText = "@gre@World" + (friendWorlds[type] - 9);
				}
				else
				{
					widget.disabledText = "@yel@World" + (friendWorlds[type] - 9);
				}
			}
			else if (friendWorlds[type] == world)
			{
				widget.disabledText = "@gre@Classic" + (friendWorlds[type] - 219);
			}
			else
			{
				widget.disabledText = "@yel@Classic" + (friendWorlds[type] - 219);
			}
			widget.actionType = 1;
			return;
		}
		if (type == 203)
		{
			int count = friendsCount;
			if (friendListStatus != 2)
			{
				count = 0;
			}
			widget.scrollLimit = count * 15 + 20;
			if (widget.scrollLimit <= widget.height)
			{
				widget.scrollLimit = widget.height + 1;
			}
			return;
		}
		if (type >= 401 && type <= 500)
		{
			if ((type -= 401) == 0 && friendListStatus == 0)
			{
				widget.disabledText = "Loading ignore list";
				widget.actionType = 0;
				return;
			}
			if (type == 1 && friendListStatus == 0)
			{
				widget.disabledText = "Please wait...";
				widget.actionType = 0;
				return;
			}
			int count = ignoresCount;
			if (friendListStatus == 0)
			{
				count = 0;
			}
			if (type >= count)
			{
				widget.disabledText = "";
				widget.actionType = 0;
				return;
			}
			else
			{
				widget.disabledText = TextUtils.formatName(TextUtils.longToName(ignores[type]));
				widget.actionType = 1;
				return;
			}
		}
		if (type == 503)
		{
			widget.scrollLimit = ignoresCount * 15 + 20;
			if (widget.scrollLimit <= widget.height)
			{
				widget.scrollLimit = widget.height + 1;
			}
			return;
		}
		if (type == 327)
		{
			widget.rotationX = 150;
			widget.rotationY = (int) (Math.sin((double) pulseCycle / 40D) * 256D) & 0x7ff;
			if (characterModelChanged)
			{
				for (int j1 = 0; j1 < 7; j1++)
				{
					int i2 = characterEditIdentityKits[j1];
					if (i2 >= 0 && !IdentityKit.cache[i2].isBodyModelCached())
					{
						return;
					}
				}

				characterModelChanged = false;
				Model[] aclass50_sub1_sub4_sub4 = new Model[7];
				int j2 = 0;
				for (int k2 = 0; k2 < 7; k2++)
				{
					int l2 = characterEditIdentityKits[k2];
					if (l2 >= 0)
					{
						aclass50_sub1_sub4_sub4[j2++] = IdentityKit.cache[l2].getBodyModel();
					}
				}

				Model model = new Model(j2, aclass50_sub1_sub4_sub4);
				for (int i3 = 0; i3 < 5; i3++)
				{
					if (characterEditColors[i3] != 0)
					{
						model.replaceColor(playerColours[i3][0],
							playerColours[i3][characterEditColors[i3]]);
						if (i3 == 1)
						{
							model.replaceColor(SKIN_COLOURS[0], SKIN_COLOURS[characterEditColors[i3]]);
						}
					}
				}

				model.createBones();
				model.applyTransform(
					AnimationSequence.animations[localPlayer.idleAnimation].getPrimaryFrame[0]);
				model.applyLighting(64, 850, -30, -50, -30, true);
				widget.modelType = 5;
				widget.modelId = 0;
				Widget.setModel(5, model, 0);
			}
			return;
		}
		if (type == 324)
		{
			if (aClass50_Sub1_Sub1_Sub1_1102 == null)
			{
				aClass50_Sub1_Sub1_Sub1_1102 = widget.disabledImage;
				aClass50_Sub1_Sub1_Sub1_1103 = widget.enabledImage;
			}
			if (characterEditChangeGenger)
			{
				widget.disabledImage = aClass50_Sub1_Sub1_Sub1_1103;
				return;
			}
			else
			{
				widget.disabledImage = aClass50_Sub1_Sub1_Sub1_1102;
				return;
			}
		}
		if (type == 325)
		{
			if (aClass50_Sub1_Sub1_Sub1_1102 == null)
			{
				aClass50_Sub1_Sub1_Sub1_1102 = widget.disabledImage;
				aClass50_Sub1_Sub1_Sub1_1103 = widget.enabledImage;
			}
			if (characterEditChangeGenger)
			{
				widget.disabledImage = aClass50_Sub1_Sub1_Sub1_1102;
				return;
			}
			else
			{
				widget.disabledImage = aClass50_Sub1_Sub1_Sub1_1103;
				return;
			}
		}
		if (type == 600)
		{
			widget.disabledText = reportedName;
			if (pulseCycle % 20 < 10)
			{
				widget.disabledText += "|";
				return;
			}
			else
			{
				widget.disabledText += " ";
				return;
			}
		}
		if (type == 620)
		{
			if (playerRights >= 1)
			{
				if (reportMutePlayer)
				{
					widget.disabledColor = 0xff0000;
					widget.disabledText = "Moderator option: Mute player for 48 hours: <ON>";
				}
				else
				{
					widget.disabledColor = 0xffffff;
					widget.disabledText = "Moderator option: Mute player for 48 hours: <OFF>";
				}
			}
			else
			{
				widget.disabledText = "";
			}
		}
		if (type == 660)
		{
			int k1 = loginScreenUpdateTime - lastLoginTime;
			String s1;
			if (k1 <= 0)
			{
				s1 = "earlier today";
			}
			else if (k1 == 1)
			{
				s1 = "yesterday";
			}
			else
			{
				s1 = k1 + " days ago";
			}
			widget.disabledText = "You last logged in @red@" + s1 + "@bla@ from: @red@" + SignLink.dns;
		}
		if (type == 661)
		{
			if (recoveryQuestionSetTime == 0)
			{
				widget.disabledText = "\\nYou have not yet set any recovery questions.\\nIt is @lre@strongly@yel@ recommended that you do so.\\n\\nIf you don't you will be @lre@unable to recover your\\n@lre@password@yel@ if you forget it, or it is stolen.";
			}
			else if (recoveryQuestionSetTime <= loginScreenUpdateTime)
			{
				widget.disabledText = "\\n\\nRecovery Questions Last Set:\\n@gre@" + formatWelcomeScreenDate(recoveryQuestionSetTime);
			}
			else
			{
				int l1 = (loginScreenUpdateTime + 14) - recoveryQuestionSetTime;
				String s2;
				if (l1 <= 0)
				{
					s2 = "Earlier today";
				}
				else if (l1 == 1)
				{
					s2 = "Yesterday";
				}
				else
				{
					s2 = l1 + " days ago";
				}
				widget.disabledText = s2
					+ " you requested@lre@ new recovery\\n@lre@questions.@yel@ The requested change will occur\\non: @lre@"
					+ formatWelcomeScreenDate(recoveryQuestionSetTime)
					+ "\\n\\nIf you do not remember making this request\\ncancel it immediately, and change your password.";
			}
		}
		if (type == 662)
		{
			String s;
			if (unreadWebsiteMessages == 0)
			{
				s = "@yel@0 unread messages";
			}
			else if (unreadWebsiteMessages == 1)
			{
				s = "@gre@1 unread message";
			}
			else
			{
				s = "@gre@" + unreadWebsiteMessages + " unread messages";
			}
			widget.disabledText = "You have " + s + "\\nin your message centre.";
		}
		if (type == 663)
		{
			if (lastPasswordChangeTime <= 0 || lastPasswordChangeTime > loginScreenUpdateTime + 10)
			{
				widget.disabledText = "Last password change:\\n@gre@Never changed";
			}
			else
			{
				widget.disabledText = "Last password change:\\n@gre@" + formatWelcomeScreenDate(lastPasswordChangeTime);
			}
		}
		if (type == 665)
		{
			if (membershipCreditRemaining > 2 && !memberServer)
			{
				widget.disabledText = "This is a non-members\\nworld. To enjoy your\\nmembers benefits we\\nrecommend you play on a\\nmembers world instead.";
			}
			else if (membershipCreditRemaining > 2)
			{
				widget.disabledText = "\\nYou have @gre@" + membershipCreditRemaining + "@yel@ days of\\nmember credit remaining.";
			}
			else if (membershipCreditRemaining > 0)
			{
				widget.disabledText = "You have @gre@"
					+ membershipCreditRemaining
					+ "@yel@ days of\\nmember credit remaining.\\n\\n@lre@Credit low! Renew now\\n@lre@to avoid losing members.";
			}
			else
			{
				widget.disabledText = "You are not a member.\\n\\nChoose to subscribe and\\nyou'll get loads of extra\\nbenefits and features.";
			}
		}
		if (type == 667)
		{
			if (membershipCreditRemaining > 2 && !memberServer)
			{
				widget.disabledText = "To switch to a members-only world:\\n1) Logout and return to the world selection page.\\n2) Choose one of the members world with a gold star next to it's name.\\n\\nIf you prefer you can continue to use this world,\\nbut members only features will be unavailable here.";
			}
			else if (membershipCreditRemaining > 0)
			{
				widget.disabledText = "To extend or cancel a subscription:\\n1) Logout and return to the frontpage of this website.\\n2)Choose the relevant option from the 'membership' section.\\n\\nNote: If you are a credit card subscriber a top-up payment will\\nautomatically be taken when 3 days credit remain.\\n(unless you cancel your subscription, which can be done at any time.)";
			}
			else
			{
				widget.disabledText = "To initializeApplication a subscripton:\\n1) Logout and return to the frontpage of this website.\\n2) Choose 'Start a new subscription'";
			}
		}
		if (type == 668)
		{
			if (recoveryQuestionSetTime > loginScreenUpdateTime)
			{
				widget.disabledText = "To cancel this request:\\n1) Logout and return to the frontpage of this website.\\n2) Choose 'Cancel recovery questions'.";
				return;
			}
			widget.disabledText = "To change your recovery questions:\\n1) Logout and return to the frontpage of this website.\\n2) Choose 'Set new recovery questions'.";
		}
	}

	private String formatWelcomeScreenDate(int time)
	{
		if (time > loginScreenUpdateTime + 10)
		{
			return "Unknown";
		}
		else
		{
			long date = ((long) time + 11745L) * 0x5265c00L;
			Calendar calendar = Calendar.getInstance();

			calendar.setTime(new Date(date));

			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int month = calendar.get(Calendar.MONTH);
			int year = calendar.get(Calendar.YEAR);
			String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May",
				"Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

			return day + "-" + monthNames[month] + "-" + year;
		}
	}

	private void updateVarp(int i, int j)
	{
		packetSize += i;
		int action = Varp.cache[j].anInt712;
		if (action == 0)
		{
			return;
		}
		int config = widgetSettings[j];
		if (action == 1)
		{
			if (config == 1)
			{
				Rasterizer3D.calculatePalette(0.90000000000000002D);
			}
			if (config == 2)
			{
				Rasterizer3D.calculatePalette(0.80000000000000004D);
			}
			if (config == 3)
			{
				Rasterizer3D.calculatePalette(0.69999999999999996D);
			}
			if (config == 4)
			{
				Rasterizer3D.calculatePalette(0.59999999999999998D);
			}
			ItemDefinition.rgbImageCache.removeAll();
			welcomeScreenRaised = true;
		}
		if (action == 3)
		{
			boolean flag = musicEnabled;
			if (config == 0)
			{
				adjustMidiVolume(musicEnabled, (byte) 8, 0);
				musicEnabled = true;
			}
			if (config == 1)
			{
				adjustMidiVolume(musicEnabled, (byte) 8, -400);
				musicEnabled = true;
			}
			if (config == 2)
			{
				adjustMidiVolume(musicEnabled, (byte) 8, -800);
				musicEnabled = true;
			}
			if (config == 3)
			{
				adjustMidiVolume(musicEnabled, (byte) 8, -1200);
				musicEnabled = true;
			}
			if (config == 4)
			{
				musicEnabled = false;
			}
			if (musicEnabled != flag && !lowMemory)
			{
				if (musicEnabled)
				{
					nextSong = currentSong;
					songChanging = true;
					onDemandRequester.request(2, nextSong);
				}
				else
				{
					stopMidi();
				}
				previousSong = 0;
			}
		}
		if (action == 4)
		{
			SoundPlayer.setVolume(config);
			if (config == 0)
			{
				aBoolean1301 = true;
				setWaveVolume(0);
			}
			if (config == 1)
			{
				aBoolean1301 = true;
				setWaveVolume(-400);
			}
			if (config == 2)
			{
				aBoolean1301 = true;
				setWaveVolume(-800);
			}
			if (config == 3)
			{
				aBoolean1301 = true;
				setWaveVolume(-1200);
			}
			if (config == 4)
			{
				aBoolean1301 = false;
			}
		}
		if (action == 5)
		{
			oneMouseButton = config;
		}
		if (action == 6)
		{
			showChatEffects = config;
		}
		if (action == 8)
		{
			anInt1223 = config;
			redrawChatbox = true;
		}
		if (action == 9)
		{
			bankInsertMode = config;
		}
	}

//    public String getParameter(String s) {
//        if (SignLink.applet != null)
//            return SignLink.applet.getParameter(s);
//        else
//            return super.getParameter(s);
//    }

	private int method106(int i, int j, int k, int l)
	{
		if (l < 8 || l > 8)
		{
			outBuffer.putByte(235);
		}
		int i1 = 256 - k;
		return ((i & 0xff00ff) * i1 + (j & 0xff00ff) * k & 0xff00ff00)
			+ ((i & 0xff00) * i1 + (j & 0xff00) * k & 0xff0000) >> 8;
	}

	private void setTutorialIslandFlag()
	{
		int x = (localPlayer.worldX >> 7) + nextTopLeftTileX;
		int y = (localPlayer.worldY >> 7) + nextTopRightTileY;
		inTutorialIsland = false;

		if (x >= 3053 && x <= 3156 && y >= 3056 && y <= 3136)
		{
			inTutorialIsland = true;
		}
		if (x >= 3072 && x <= 3118 && y >= 9492 && y <= 9535)
		{
			inTutorialIsland = true;
		}
		if (inTutorialIsland && x >= 3139 && x <= 3199 && y >= 3008 && y <= 3062)
		{
			inTutorialIsland = false;
		}
	}

	private void determineMenuSize()
	{
		int width = fontBold.getStringEffectWidth("Choose Option");

		for (int i = 0; i < menuActionRow; i++)
		{
			int rowWidth = fontBold.getStringEffectWidth(menuActionTexts[i]);

			if (rowWidth > width)
			{
				width = rowWidth;
			}
		}

		width += 8;
		int height = 15 * menuActionRow + 21;

		if (super.clickX > 4 && super.clickY > 4 && super.clickX < 516 && super.clickY < 338)
		{
			int x = super.clickX - 4 - width / 2;

			if (x + width > 512)
			{
				x = 512 - width;
			}
			if (x < 0)
			{
				x = 0;
			}

			int y = super.clickY - 4;

			if (y + height > 334)
			{
				y = 334 - height;
			}
			if (y < 0)
			{
				y = 0;
			}

			menuOpen = true;
			menuScreenArea = 0;
			menuOffsetX = x;
			menuOffsetY = y;
			menuWidth = width;
			menuHeight = height + 1;
		}

		if (super.clickX > 553 && super.clickY > 205 && super.clickX < 743 && super.clickY < 466)
		{
			int x = super.clickX - 553 - width / 2;

			if (x < 0)
			{
				x = 0;
			}
			else if (x + width > 190)
			{
				x = 190 - width;
			}

			int y = super.clickY - 205;

			if (y < 0)
			{
				y = 0;
			}
			else if (y + height > 261)
			{
				y = 261 - height;
			}

			menuOpen = true;
			menuScreenArea = 1;
			menuOffsetX = x;
			menuOffsetY = y;
			menuWidth = width;
			menuHeight = height + 1;
		}

		if (super.clickX > 17 && super.clickY > 357 && super.clickX < 496 && super.clickY < 453)
		{
			int x = super.clickX - 17 - width / 2;

			if (x < 0)
			{
				x = 0;
			}
			else if (x + width > 479)
			{
				x = 479 - width;
			}

			int y = super.clickY - 357;

			if (y < 0)
			{
				y = 0;
			}
			else if (y + height > 96)
			{
				y = 96 - height;
			}

			menuOpen = true;
			menuScreenArea = 2;
			menuOffsetX = x;
			menuOffsetY = y;
			menuWidth = width;
			menuHeight = height + 1;
		}
	}

	private void draw3dScreen()
	{
		renderSplitPrivateMessages();

		if (crossType == 1)
		{
			cursorCross[crossIndex / 100].drawImage(crossX - 8 - 4, crossY - 8 - 4);
		}
		if (crossType == 2)
		{
			cursorCross[4 + crossIndex / 100].drawImage(crossX - 8 - 4, crossY - 8 - 4);
		}

		if (walkableWidgetId != -1)
		{
			handleSequences(tickDelta, walkableWidgetId);
			drawInterface(0, 0, Widget.forId(walkableWidgetId), 0);
		}

		if (openScreenWidgetId != -1)
		{
			handleSequences(tickDelta, openScreenWidgetId);
			drawInterface(0, 0, Widget.forId(openScreenWidgetId), 0);
		}

		setTutorialIslandFlag();

		if (!menuOpen)
		{
			processRightClick(-521);
			drawMenuTooltip();
		}
		else if (menuScreenArea == 0)
		{
			drawMenu();
		}

		if (anInt1319 == 1)
		{
			aClass50_Sub1_Sub1_Sub1_1086.drawImage(472, 296);
		}

		if (fps)
		{
			int y = 20;
			int colour = 0xffff00;

			if (super.fps < 30 && lowMemory)
			{
				colour = 0xff0000;
			}
			if (super.fps < 20 && !lowMemory)
			{
				colour = 0xff0000;
			}

			fontNormal.drawStringRight("Fps:" + super.fps, 507, y, colour);

			y += 15;
			Runtime runtime = Runtime.getRuntime();
			int memoryUsed = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
			colour = 0xffff00;

			if (memoryUsed > 0x2000000 && lowMemory)
			{
				colour = 0xff0000;
			}
			if (memoryUsed > 0x4000000 && !lowMemory)
			{
				colour = 0xff0000;
			}

			fontNormal.drawStringRight("Mem:" + memoryUsed + "k", 507, y, colour);
		}

		if (systemUpdateTime != 0)
		{
			int seconds = systemUpdateTime / 50;
			int minutes = seconds / 60;
			seconds %= 60;

			if (seconds < 10)
			{
				fontNormal.drawString("System update in: " + minutes + ":0" + seconds, 4, 329, 0xffff00);
			}
			else
			{
				fontNormal.drawString("System update in: " + minutes + ":" + seconds, 4, 329, 0xffff00);
			}

			anInt895++;

			if (anInt895 > 112)
			{
				anInt895 = 0;
				outBuffer.putOpcode(197);
				outBuffer.putIntBE(0);
			}
		}
	}

	public void run()
	{
		if (aBoolean1314)
		{
			processFlamesCycle();
		}
		else
		{
			super.run();
		}
	}

	void mouseWheelDragged(int i, int j)
	{
		if (!mouseWheelDown)
		{
			return;
		}
		this.cameraVelocityHorizontal += i * 3;
		this.cameraVelocityVertical += (j << 1);
	}

	public void startup()
	{
		drawLoadingText(20, "Starting up");

		if (SignLink.cacheData != null)
		{
			for (int type = 0; type < 5; type++)
			{
				stores[type] = new Index(type + 1, 0x927c0, SignLink.cacheData, SignLink.cacheIndex[type]);
			}
		}

		try
		{
			if (Configuration.JAGGRAB_ENABLED)
			{
				requestArchiveCrcs();
			}

			if (USE_STATIC_DETAILS)
			{
				username = USERNAME;
				password = PASSWORD;
			}
			else
			{
				username = "";
				password = "";
			}
			titleArchive = requestArchive(1, "title", archiveHashes[1], 25, "title screen");
			fontSmall = new TypeFace(false, titleArchive, "p11_full");
			fontNormal = new TypeFace(false, titleArchive, "p12_full");
			fontBold = new TypeFace(false, titleArchive, "b12_full");
			fontFancy = new TypeFace(true, titleArchive, "q8_full");

			prepareTitleBackground();
			prepareTitle();

			Archive configArchive = requestArchive(2, "config", archiveHashes[2], 30, "config");
			Archive archiveInterface = requestArchive(3, "interface", archiveHashes[3], 35, "interface");
			Archive archiveMedia = requestArchive(4, "media", archiveHashes[4], 40, "2d gameGraphics");
			Archive textureArchive = requestArchive(6, "textures", archiveHashes[6], 45, "textures");
			Archive chatArchive = requestArchive(7, "wordenc", archiveHashes[7], 50, "chat system");
			Archive soundArchive = requestArchive(8, "sounds", archiveHashes[8], 55, "sound effects");
			currentSceneTileFlags = new byte[4][104][104];
			intGroundArray = new int[4][105][105];
			currentScene = new Scene(intGroundArray);

			for (int j = 0; j < 4; j++)
			{
				currentCollisionMap[j] = new CollisionMap(104, 104);
			}

			minimapImage = new ImageRGB(512, 512);
			Archive versionListArchive = requestArchive(5, "versionlist", archiveHashes[5], 60, "update list");

			drawLoadingText(60, "Connecting to update server");

			onDemandRequester = new OnDemandRequester();
			onDemandRequester.init(versionListArchive, this);

			Animation.method235(onDemandRequester.animCount());
			Model.init(onDemandRequester.fileCount(0), onDemandRequester);

			if (!lowMemory)
			{
				nextSong = 0;

//                try {
//                    nextSong = Integer.parseInt(getParameter("music"));
//                } catch (Exception ignored) {
//                }

				songChanging = true;

				onDemandRequester.request(2, nextSong);

				while (onDemandRequester.method333() > 0)
				{
					processOnDemandQueue();

					try
					{
						Thread.sleep(100L);
					}
					catch (Exception ignored)
					{
					}

					if (onDemandRequester.requestFails > 3)
					{
						openErrorWebPage("ondemand");
						return;
					}
				}
			}

			drawLoadingText(65, "Requesting animations");

			int fileRequestCount = onDemandRequester.fileCount(1);

			for (int i = 0; i < fileRequestCount; i++)
			{
				onDemandRequester.request(1, i);
			}

			while (onDemandRequester.method333() > 0)
			{
				int total = fileRequestCount - onDemandRequester.method333();

				if (total > 0)
				{
					drawLoadingText(65, "Loading animations - " + (total * 100) / fileRequestCount + "%");
				}

				processOnDemandQueue();

				try
				{
					Thread.sleep(100L);
				}
				catch (Exception ignored)
				{
				}

				if (onDemandRequester.requestFails > 3)
				{
					openErrorWebPage("ondemand");
					return;
				}
			}

			drawLoadingText(70, "Requesting models");

			fileRequestCount = onDemandRequester.fileCount(0);

			for (int i = 0; i < fileRequestCount; i++)
			{
				int id = onDemandRequester.modelId(i);

				if ((id & 1) != 0)
				{
					onDemandRequester.request(0, i);
				}
			}

			fileRequestCount = onDemandRequester.method333();

			while (onDemandRequester.method333() > 0)
			{
				int total = fileRequestCount - onDemandRequester.method333();

				if (total > 0)
				{
					drawLoadingText(70, "Loading models - " + (total * 100) / fileRequestCount + "%");
				}

				processOnDemandQueue();

				try
				{
					Thread.sleep(100L);
				}
				catch (Exception ignored)
				{
				}
			}

			if (stores[0] != null)
			{
				drawLoadingText(75, "Requesting maps");
				onDemandRequester.request(3, onDemandRequester.regId(0, 47, 48, 0)); // these are the maps around tutorial island
				onDemandRequester.request(3, onDemandRequester.regId(0, 47, 48, 1));
				onDemandRequester.request(3, onDemandRequester.regId(0, 48, 48, 0));
				onDemandRequester.request(3, onDemandRequester.regId(0, 48, 48, 1));
				onDemandRequester.request(3, onDemandRequester.regId(0, 49, 48, 0));
				onDemandRequester.request(3, onDemandRequester.regId(0, 49, 48, 1));
				onDemandRequester.request(3, onDemandRequester.regId(0, 47, 47, 0));
				onDemandRequester.request(3, onDemandRequester.regId(0, 47, 47, 1));
				onDemandRequester.request(3, onDemandRequester.regId(0, 48, 47, 0));
				onDemandRequester.request(3, onDemandRequester.regId(0, 48, 47, 1));
				onDemandRequester.request(3, onDemandRequester.regId(0, 48, 148, 0));
				onDemandRequester.request(3, onDemandRequester.regId(0, 48, 148, 1));

				fileRequestCount = onDemandRequester.method333();

				while (onDemandRequester.method333() > 0)
				{
					int total = fileRequestCount - onDemandRequester.method333();

					if (total > 0)
					{
						drawLoadingText(75, "Loading maps - " + (total * 100) / fileRequestCount + "%");
					}

					processOnDemandQueue();

					try
					{
						Thread.sleep(100L);
					}
					catch (Exception ignored)
					{
					}
				}
			}

			fileRequestCount = onDemandRequester.fileCount(0);

			for (int i = 0; i < fileRequestCount; i++)
			{
				int id = onDemandRequester.modelId(i);
				byte priority = 0;

				if ((id & 8) != 0)
				{
					priority = 10;
				}
				else if ((id & 0x20) != 0)
				{
					priority = 9;
				}
				else if ((id & 0x10) != 0)
				{
					priority = 8;
				}
				else if ((id & 0x40) != 0)
				{
					priority = 7;
				}
				else if ((id & 0x80) != 0)
				{
					priority = 6;
				}
				else if ((id & 2) != 0)
				{
					priority = 5;
				}
				else if ((id & 4) != 0)
				{
					priority = 4;
				}
				if ((id & 1) != 0)
				{
					priority = 3;
				}

				if (priority != 0)
				{
					onDemandRequester.setPriority(priority, 0, i);
				}
			}

			onDemandRequester.preloadRegions(memberServer);

			if (!lowMemory)
			{
				fileRequestCount = onDemandRequester.fileCount(2);

				for (int i = 1; i < fileRequestCount; i++)
				{
					if (onDemandRequester.midiIdEqualsOne(i))
					{
						onDemandRequester.setPriority((byte) 1, 2, i);
					}
				}
			}

			fileRequestCount = onDemandRequester.fileCount(0);

			for (int i = 0; i < fileRequestCount; i++)
			{
				int id = onDemandRequester.modelId(i);

				if (id == 0 && onDemandRequester.anInt1350 < 200)
				{
					onDemandRequester.setPriority((byte) 1, 0, i);
				}
			}

			drawLoadingText(80, "Unpacking media");

			inventoryBackgroundImage = new IndexedImage(archiveMedia, "invback", 0);
			chatboxBackgroundImage = new IndexedImage(archiveMedia, "chatback", 0);
			minimapBackgroundImage = new IndexedImage(archiveMedia, "mapback", 0);
			bottomChatBack = new IndexedImage(archiveMedia, "backbase1", 0);
			tabBottomBack = new IndexedImage(archiveMedia, "backbase2", 0);
			tabTopBack = new IndexedImage(archiveMedia, "backhmid1", 0);

			for (int i = 0; i < 13; i++)
			{
				tabIcon[i] = new IndexedImage(archiveMedia, "sideicons", i);
			}

			minimapCompass = new ImageRGB(archiveMedia, "compass", 0);
			minimapEdge = new ImageRGB(archiveMedia, "mapedge", 0);
			minimapEdge.trim();

			for (int i = 0; i < 72; i++)
			{
				mapIcons[i] = new IndexedImage(archiveMedia, "mapscene", i);
			}

			for (int i = 0; i < 70; i++)
			{
				worldMapHintIcons[i] = new ImageRGB(archiveMedia, "mapfunction", i);
			}

			for (int i = 0; i < 5; i++)
			{
				hitmarks[i] = new ImageRGB(archiveMedia, "hitmarks", i);
			}

			for (int i = 0; i < 6; i++)
			{
				headiconsPk[i] = new ImageRGB(archiveMedia, "headicons_pk", i);
			}

			for (int i = 0; i < 9; i++)
			{
				headiconsPrayer[i] = new ImageRGB(archiveMedia, "headicons_prayer", i);
			}

			for (int i = 0; i < 6; i++)
			{
				imageHeadIcons[i] = new ImageRGB(archiveMedia, "headicons_hint", i);
			}

			aClass50_Sub1_Sub1_Sub1_1086 = new ImageRGB(archiveMedia, "overlay_multiway", 0);
			mapFlagMarker = new ImageRGB(archiveMedia, "mapmarker", 0);
			aClass50_Sub1_Sub1_Sub1_1037 = new ImageRGB(archiveMedia, "mapmarker", 1);

			for (int i = 0; i < 8; i++)
			{
				cursorCross[i] = new ImageRGB(archiveMedia, "cross", i);
			}

			mapdotItem = new ImageRGB(archiveMedia, "mapdots", 0);
			mapdotActor = new ImageRGB(archiveMedia, "mapdots", 1);
			mapdotPlayer = new ImageRGB(archiveMedia, "mapdots", 2);
			mapdotFriend = new ImageRGB(archiveMedia, "mapdots", 3);
			mapdotTeammate = new ImageRGB(archiveMedia, "mapdots", 4);
			scrollbarUp = new IndexedImage(archiveMedia, "scrollbar", 0);
			scrollbarDown = new IndexedImage(archiveMedia, "scrollbar", 1);
			imageRedstone1 = new IndexedImage(archiveMedia, "redstone1", 0);
			imageRedstone2 = new IndexedImage(archiveMedia, "redstone2", 0);
			imageRedstone3 = new IndexedImage(archiveMedia, "redstone3", 0);
			imageFlippedRedstone1 = new IndexedImage(archiveMedia, "redstone1", 0);
			imageFlippedRedstone1.flipHorizontal();

			imageFlippedRedstone2 = new IndexedImage(archiveMedia, "redstone2", 0);
			imageFlippedRedstone2.flipHorizontal();

			aClass50_Sub1_Sub1_Sub3_983 = new IndexedImage(archiveMedia, "redstone1", 0);
			aClass50_Sub1_Sub1_Sub3_983.flipVertical();

			aClass50_Sub1_Sub1_Sub3_984 = new IndexedImage(archiveMedia, "redstone2", 0);
			aClass50_Sub1_Sub1_Sub3_984.flipVertical();

			aClass50_Sub1_Sub1_Sub3_985 = new IndexedImage(archiveMedia, "redstone3", 0);
			aClass50_Sub1_Sub1_Sub3_985.flipVertical();

			aClass50_Sub1_Sub1_Sub3_986 = new IndexedImage(archiveMedia, "redstone1", 0);
			aClass50_Sub1_Sub1_Sub3_986.flipHorizontal();
			aClass50_Sub1_Sub1_Sub3_986.flipVertical();

			aClass50_Sub1_Sub1_Sub3_987 = new IndexedImage(archiveMedia, "redstone2", 0);
			aClass50_Sub1_Sub1_Sub3_987.flipHorizontal();
			aClass50_Sub1_Sub1_Sub3_987.flipVertical();

			for (int i = 0; i < 2; i++)
			{
				moderatorIcon[i] = new ImageRGB(archiveMedia, "mod_icons", i);
			}

			ImageRGB image = new ImageRGB(archiveMedia, "backleft1", 0);
			aClass18_906 = new ProducingGraphicsBuffer(image.width, image.height, getParentComponent());
			image.drawInverse(0, 0);

			image = new ImageRGB(archiveMedia, "backleft2", 0);
			aClass18_907 = new ProducingGraphicsBuffer(image.width, image.height, getParentComponent());
			image.drawInverse(0, 0);

			image = new ImageRGB(archiveMedia, "backright1", 0);
			aClass18_908 = new ProducingGraphicsBuffer(image.width, image.height, getParentComponent());
			image.drawInverse(0, 0);

			image = new ImageRGB(archiveMedia, "backright2", 0);
			aClass18_909 = new ProducingGraphicsBuffer(image.width, image.height, getParentComponent());
			image.drawInverse(0, 0);

			image = new ImageRGB(archiveMedia, "backtop1", 0);
			aClass18_910 = new ProducingGraphicsBuffer(image.width, image.height, getParentComponent());
			image.drawInverse(0, 0);

			image = new ImageRGB(archiveMedia, "backvmid1", 0);
			aClass18_911 = new ProducingGraphicsBuffer(image.width, image.height, getParentComponent());
			image.drawInverse(0, 0);

			image = new ImageRGB(archiveMedia, "backvmid2", 0);
			aClass18_912 = new ProducingGraphicsBuffer(image.width, image.height, getParentComponent());
			image.drawInverse(0, 0);

			image = new ImageRGB(archiveMedia, "backvmid3", 0);
			aClass18_913 = new ProducingGraphicsBuffer(image.width, image.height, getParentComponent());
			image.drawInverse(0, 0);

			image = new ImageRGB(archiveMedia, "backhmid2", 0);
			aClass18_914 = new ProducingGraphicsBuffer(image.width, image.height, getParentComponent());
			image.drawInverse(0, 0);

			int offset = (int) (Math.random() * 41D) - 20;
			int red = (int) ((Math.random() * 21D) - 10) + offset;
			int green = (int) ((Math.random() * 21D) - 10) + offset;
			int blue = (int) ((Math.random() * 21D) - 10) + offset;

			for (int i = 0; i < 100; i++)
			{
				if (worldMapHintIcons[i] != null)
				{
					worldMapHintIcons[i].adjustRGB(red, green, blue);
				}

				if (mapIcons[i] != null)
				{
					mapIcons[i].mixPalette(red, green, blue);
				}
			}

			drawLoadingText(83, "Unpacking textures");
			Rasterizer3D.unpackTextures(textureArchive);
			Rasterizer3D.calculatePalette(0.80000000000000004D);
			Rasterizer3D.resetTextures(20);
			drawLoadingText(86, "Unpacking config");
			AnimationSequence.load(configArchive);
			GameObjectDefinition.load(configArchive);
			FloorDefinition.load(configArchive);
			ItemDefinition.load(configArchive);
			ActorDefinition.load(configArchive);
			IdentityKit.load(configArchive);
			SpotAnimation.load(configArchive);
			Varp.load(configArchive);
			Varbit.load(configArchive);

			ItemDefinition.memberServer = memberServer;

			if (!lowMemory)
			{
				drawLoadingText(90, "Unpacking sounds");

				byte[] bytes = soundArchive.getFile("sounds.dat");
				Buffer buffer = new Buffer(bytes);

				SoundTrack.load(buffer);
			}

			drawLoadingText(95, "Unpacking interfaces");

			TypeFace[] typefaces = {fontSmall, fontNormal, fontBold, fontFancy};

			for (TypeFace typeFace : typefaces)
			{
				typeFace.setNameIcons(moderatorIcon, (int[]) null);
			}

			Widget.load(archiveInterface, typefaces, archiveMedia);
			drawLoadingText(100, "Preparing game engine");

			for (int y = 0; y < 33; y++)
			{
				int minWidth = 999;
				int maxWidth = 0;

				for (int x = 0; x < 34; x++)
				{
					if (minimapBackgroundImage.imgPixels[x + y * minimapBackgroundImage.imgWidth] == 0)
					{
						if (minWidth == 999)
						{
							minWidth = x;
						}

						continue;
					}

					if (minWidth == 999)
					{
						continue;
					}

					maxWidth = x;
					break;
				}

				anIntArray1180[y] = minWidth;
				anIntArray1286[y] = maxWidth - minWidth;
			}

			for (int y = 5; y < 156; y++)
			{
				int minWidth = 999;
				int maxWidth = 0;

				for (int x = 25; x < 172; x++)
				{
					if (minimapBackgroundImage.imgPixels[x + y * minimapBackgroundImage.imgWidth] == 0
						&& (x > 34 || y > 34))
					{
						if (minWidth == 999)
						{
							minWidth = x;
						}

						continue;
					}

					if (minWidth == 999)
					{
						continue;
					}

					maxWidth = x;
					break;
				}

				anIntArray1019[y - 5] = minWidth - 25;
				anIntArray920[y - 5] = maxWidth - minWidth;
			}

			Rasterizer3D.setBounds(765, 503);
			fullScreenTextureArray = Rasterizer3D.lineOffsets;

			Rasterizer3D.setBounds(479, 96);
			chatboxLineOffsets = Rasterizer3D.lineOffsets;

			Rasterizer3D.setBounds(190, 261);
			sidebarOffsets = Rasterizer3D.lineOffsets;

			Rasterizer3D.setBounds(512, 334);
			viewportOffsets = Rasterizer3D.lineOffsets;

			int[] ai = new int[9];

			for (int i = 0; i < 9; i++)
			{ //TODO: Needs refactoring
				int j9 = 128 + i * 32 + 15;
				int k9 = 600 + j9 * 3;
				int l9 = Rasterizer3D.SINE[j9];
				ai[i] = k9 * l9 >> 16;
			}

			Scene.method277(500, 800, 512, 334, ai);
			ChatCensor.load(chatArchive);

			mouseCapturer = new MouseCapturer(this);

			startRunnable(mouseCapturer, 10);

			GameObject.client = this;
			GameObjectDefinition.client = this;
			ActorDefinition.client = this;
			return;
		}
		catch (Exception exception)
		{
			SignLink.reportError("loaderror " + aString1027 + " " + anInt1322);
		}

		loadingError = true;
	}

	public void processGameLoop()
	{
		if (rsAlreadyLoaded || loadingError || genericLoadingError)
		{
			return;
		}
		pulseCycle++;
		if (!loggedIn)
		{
			updateLogin();
		}
		else
		{
			updateGame();
		}
		processOnDemandQueue();
	}

	public void shutdown()
	{
		players = null;
		playerList = null;
		updatedPlayers = null;
		cachedAppearances = null;
		eneityUpdateIndices = null;
		aClass18_906 = null;
		aClass18_907 = null;
		aClass18_908 = null;
		aClass18_909 = null;
		imageRedstone1 = null;
		imageRedstone2 = null;
		imageRedstone3 = null;
		imageFlippedRedstone1 = null;
		imageFlippedRedstone2 = null;
		aClass50_Sub1_Sub1_Sub3_983 = null;
		aClass50_Sub1_Sub1_Sub3_984 = null;
		aClass50_Sub1_Sub1_Sub3_985 = null;
		aClass50_Sub1_Sub1_Sub3_986 = null;
		aClass50_Sub1_Sub1_Sub3_987 = null;
		friendUsernames = null;
		friends = null;
		friendWorlds = null;
		aClass18_1108 = null;
		aClass18_1109 = null;
		aClass18_1110 = null;
		widgetSettings = null;
		mapCoordinates = null;
		terrainData = null;
		objectData = null;
		terrainDataIds = null;
		objectDataIds = null;
		aClass18_1203 = null;
		aClass18_1204 = null;
		aClass18_1205 = null;
		aClass18_1206 = null;
		anIntArrayArray885 = null;
		cost = null;
		anIntArray1123 = null;
		anIntArray1124 = null;
		mapdotItem = null;
		mapdotActor = null;
		mapdotPlayer = null;
		mapdotFriend = null;
		mapdotTeammate = null;
		if (mouseCapturer != null)
		{
			mouseCapturer.capturing = false;
		}
		mouseCapturer = null;
		bottomChatBack = null;
		tabBottomBack = null;
		tabTopBack = null;
		aClass18_910 = null;
		aClass18_911 = null;
		aClass18_912 = null;
		aClass18_913 = null;
		aClass18_914 = null;
		intGroundArray = null;
		currentSceneTileFlags = null;
		currentScene = null;
		currentCollisionMap = null;
		minimapImage = null;
		flameLeftBackground = null;
		flameRightBackground = null;
		aClass18_1198 = null;
		aClass18_1199 = null;
		aClass18_1200 = null;
		minimapCompass = null;
		hitmarks = null;
		headiconsPk = null;
		headiconsPrayer = null;
		imageHeadIcons = null;
		cursorCross = null;
		stopMidi();
		outBuffer = null;
		tempBuffer = null;
		buffer = null;
		tabImageProducer = null;
		aClass18_1157 = null;
		gameScreenImageProducer = null;
		chatboxProducingGraphicsBuffer = null;
		inventoryBackgroundImage = null;
		minimapBackgroundImage = null;
		chatboxBackgroundImage = null;
		try
		{
			if (gameConnection != null)
			{
				gameConnection.close();
			}
		}
		catch (Exception _ex)
		{
		}
		gameConnection = null;
		minimapHintX = null;
		minimapHintY = null;
		minimapHint = null;
		npcs = null;
		npcIds = null;
		aByteArray1245 = null;
		chatBuffer = null;
		mapIcons = null;
		worldMapHintIcons = null;
		tileRenderCount = null;
		tabIcon = null;
		projectileQueue = null;
		gameAnimableObjectQueue = null;
		aClass50_Sub1_Sub1_Sub1_1086 = null;
		if (onDemandRequester != null)
		{
			onDemandRequester.stop();
		}
		onDemandRequester = null;
		firstMenuOperand = null;
		secondMenuOperand = null;
		menuActionTypes = null;
		selectedMenuActions = null;
		menuActionTexts = null;
		groundItems = null;
		spawnObjectList = null;
		method141();
		GameObjectDefinition.method433(false);
		ActorDefinition.reset();
		ItemDefinition.dispose();
		Widget.reset();
		FloorDefinition.cache = null;
		IdentityKit.cache = null;
		AnimationSequence.animations = null;
		SpotAnimation.cache = null;
		SpotAnimation.modelCache = null;
		Varp.cache = null;
		super.imageProducer = null;
		Player.modelCache = null;
		Rasterizer3D.reset();
		Scene.nullLoader();
		Model.reset();
		Animation.reset();
		System.gc();
	}

	public void repaintGame()
	{
		if (rsAlreadyLoaded || loadingError || genericLoadingError)
		{
			showErrorScreen();
			return;
		}
		drawCycle++;
		if (!loggedIn)
		{
			drawLoginScreen(false);
		}
		else
		{
			drawGameScreen();
		}
		anInt1094 = 0;
	}

	public void redraw()
	{
		welcomeScreenRaised = true;
	}

//    public AppletContext getAppletContext() {
//        if (SignLink.applet != null)
//            return SignLink.applet.getAppletContext();
//        else
//            return super.getAppletContext();
//    }

	public Component getParentComponent()
	{
		if (super.gameFrame != null)
		{
			return super.gameFrame;
		}
		else
		{
			return this;
		}
	}

	public void startRunnable(Runnable runnable, int i)
	{
		if (i > 10)
		{
			i = 10;
		}
		super.startRunnable(runnable, i);
	}

	public void drawLoadingText(int i, String s)
	{
		anInt1322 = i;
		aString1027 = s;
		resetTitleScreen();
		if (titleArchive == null)
		{
			super.drawLoadingText(i, s);
			return;
		}
		aClass18_1200.createRasterizer();
		char c = '\u0168';
		char c1 = '\310';
		byte byte0 = 20;
		fontBold.drawStringLeft("RuneScape is loading - please wait...", c / 2, c1 / 2 - 26 - byte0, 0xffffff
		);
		int j = c1 / 2 - 18 - byte0;
		Rasterizer.drawUnfilledRectangle(c / 2 - 152, j, 304, 34, 0x8c1111);
		Rasterizer.drawUnfilledRectangle(c / 2 - 151, j + 1, 302, 32, 0);
		Rasterizer.drawFilledRectangle(c / 2 - 150, j + 2, i * 3, 30, 0x8c1111);
		Rasterizer.drawFilledRectangle((c / 2 - 150) + i * 3, j + 2, 300 - i * 3, 30, 0);
		fontBold.drawStringLeft(s, c / 2, (c1 / 2 + 5) - byte0, 0xffffff);
		aClass18_1200.drawGraphics(202, 171, super.gameGraphics);
		if (welcomeScreenRaised)
		{
			welcomeScreenRaised = false;
			if (!currentlyDrawingFlames)
			{
				flameLeftBackground.drawGraphics(0, 0, super.gameGraphics);
				flameRightBackground.drawGraphics(637, 0, super.gameGraphics);
			}
			aClass18_1198.drawGraphics(128, 0, super.gameGraphics);
			aClass18_1199.drawGraphics(202, 371, super.gameGraphics);
			aClass18_1203.drawGraphics(0, 265, super.gameGraphics);
			aClass18_1204.drawGraphics(562, 265, super.gameGraphics);
			aClass18_1205.drawGraphics(128, 171, super.gameGraphics);
			aClass18_1206.drawGraphics(562, 171, super.gameGraphics);
		}
	}

	private int getFloorDrawHeight(int plane, int x, int y)
	{
		int groundX = x >> 7;
		int groundY = y >> 7;
		if (groundX < 0 || groundY < 0 || groundX > 103 || groundY > 103)
		{
			return 0;
		}
		int groundZ = plane;
		if (groundZ < 3 && (currentSceneTileFlags[1][groundX][groundY] & 2) == 2)
		{
			groundZ++;
		}
		int _x = x & 0x7f;
		int _y = y & 0x7f;
		int i2 = intGroundArray[groundZ][groundX][groundY] * (128 - _x) + intGroundArray[groundZ][groundX + 1][groundY] * _x >> 7;
		int j2 = intGroundArray[groundZ][groundX][groundY + 1] * (128 - _x) + intGroundArray[groundZ][groundX + 1][groundY + 1] * _x >> 7;
		return i2 * (128 - _y) + j2 * _y >> 7;
	}

	private void method111()
	{
		if (anInt1223 == 0)
		{
			return;
		}
		int j = 0;
		if (systemUpdateTime != 0)
		{
			j = 1;
		}
		for (int k = 0; k < 100; k++)
		{
			if (chatMessages[k] != null)
			{
				int l = chatTypes[k];
				String s = chatPlayerNames[k];
				if (s != null && s.startsWith("@cr1@"))
				{
					s = s.substring(5);
				}
				if (s != null && s.startsWith("@cr2@"))
				{
					s = s.substring(5);
				}
				if ((l == 3 || l == 7) && (l == 7 || privateChatMode == 0 || privateChatMode == 1 && hasFriend(s)))
				{
					int i1 = 329 - j * 13;
					if (super.mouseX > 4 && super.mouseY - 4 > i1 - 10 && super.mouseY - 4 <= i1 + 3)
					{
						int j1 = fontNormal.getStringEffectWidth("From:  " + s + chatMessages[k]) + 25;
						if (j1 > 450)
						{
							j1 = 450;
						}
						if (super.mouseX < 4 + j1)
						{
							if (playerRights >= 1)
							{
								menuActionTexts[menuActionRow] = "Report abuse @whi@" + s;
								menuActionTypes[menuActionRow] = 2507;
								menuActionRow++;
							}
							menuActionTexts[menuActionRow] = "Add ignore @whi@" + s;
							menuActionTypes[menuActionRow] = 2574;
							menuActionRow++;
							menuActionTexts[menuActionRow] = "Add friend @whi@" + s;
							menuActionTypes[menuActionRow] = 2762;
							menuActionRow++;
						}
					}
					if (++j >= 5)
					{
						return;
					}
				}
				if ((l == 5 || l == 6) && privateChatMode < 2 && ++j >= 5)
				{
					return;
				}
			}
		}

	}

	private void method112(byte byte0, int i)
	{
		if (byte0 != 36)
		{
			outBuffer.putByte(6);
		}
		Widget widget = Widget.forId(i);
		for (int j = 0; j < widget.children.length; j++)
		{
			if (widget.children[j] == -1)
			{
				break;
			}
			Widget class13_1 = Widget.forId(widget.children[j]);
			if (class13_1.type == 1)
			{
				method112((byte) 36, class13_1.id);
			}
			class13_1.animationFrame = 0;
			class13_1.animationDuration = 0;
		}

	}

	private void method113(int i, int j, int k)
	{
		int l = 0;
		i = 44 / i;
		for (int i1 = 0; i1 < 100; i1++)
		{
			if (chatMessages[i1] == null)
			{
				continue;
			}
			int j1 = chatTypes[i1];
			int k1 = (70 - l * 14) + chatboxScroll + 4;
			if (k1 < -20)
			{
				break;
			}
			String s = chatPlayerNames[i1];
			if (s != null && s.startsWith("@cr1@"))
			{
				s = s.substring(5);
			}
			if (s != null && s.startsWith("@cr2@"))
			{
				s = s.substring(5);
			}
			if (j1 == 0)
			{
				l++;
			}
			if ((j1 == 1 || j1 == 2) && (j1 == 1 || publicChatMode == 0 || publicChatMode == 1 && hasFriend(s)))
			{
				if (k > k1 - 14 && k <= k1 && !s.equals(localPlayer.playerName))
				{
					if (playerRights >= 1)
					{
						menuActionTexts[menuActionRow] = "Report abuse @whi@" + s;
						menuActionTypes[menuActionRow] = 507;
						menuActionRow++;
					}
					menuActionTexts[menuActionRow] = "Add ignore @whi@" + s;
					menuActionTypes[menuActionRow] = 574;
					menuActionRow++;
					menuActionTexts[menuActionRow] = "Add friend @whi@" + s;
					menuActionTypes[menuActionRow] = 762;
					menuActionRow++;
				}
				l++;
			}
			if ((j1 == 3 || j1 == 7) && anInt1223 == 0
				&& (j1 == 7 || privateChatMode == 0 || privateChatMode == 1 && hasFriend(s)))
			{
				if (k > k1 - 14 && k <= k1)
				{
					if (playerRights >= 1)
					{
						menuActionTexts[menuActionRow] = "Report abuse @whi@" + s;
						menuActionTypes[menuActionRow] = 507;
						menuActionRow++;
					}
					menuActionTexts[menuActionRow] = "Add ignore @whi@" + s;
					menuActionTypes[menuActionRow] = 574;
					menuActionRow++;
					menuActionTexts[menuActionRow] = "Add friend @whi@" + s;
					menuActionTypes[menuActionRow] = 762;
					menuActionRow++;
				}
				l++;
			}
			if (j1 == 4 && (tradeMode == 0 || tradeMode == 1 && hasFriend(s)))
			{
				if (k > k1 - 14 && k <= k1)
				{
					menuActionTexts[menuActionRow] = "Accept trade @whi@" + s;
					menuActionTypes[menuActionRow] = 544;
					menuActionRow++;
				}
				l++;
			}
			if ((j1 == 5 || j1 == 6) && anInt1223 == 0 && privateChatMode < 2)
			{
				l++;
			}
			if (j1 == 8 && (tradeMode == 0 || tradeMode == 1 && hasFriend(s)))
			{
				if (k > k1 - 14 && k <= k1)
				{
					menuActionTexts[menuActionRow] = "Accept challenge @whi@" + s;
					menuActionTypes[menuActionRow] = 695;
					menuActionRow++;
				}
				l++;
			}
		}

	}

	private void updateOtherPlayerMovement(Buffer buffer)
	{
		int playerCount = buffer.getBits(8);

		if (playerCount < localPlayerCount)
		{
			for (int i = playerCount; i < localPlayerCount; i++)
			{
				eneityUpdateIndices[enityUpdateCount++] = playerList[i];
			}
		}

		if (playerCount > localPlayerCount)
		{
			SignLink.reportError(username + " Too many players");
			throw new RuntimeException("eek");
		}

		localPlayerCount = 0;

		for (int i = 0; i < playerCount; i++)
		{
			int id = playerList[i];
			Player player = players[id];
			int updated = buffer.getBits(1);

			if (updated == 0)
			{
				playerList[localPlayerCount++] = id;
				player.pulseCycle = pulseCycle;
			}
			else
			{
				MovementType moveType = MovementType.values()[buffer.getBits(2)];

				if (moveType == MovementType.NONE)
				{
					playerList[localPlayerCount++] = id;
					player.pulseCycle = pulseCycle;
					updatedPlayers[updatedPlayerCount++] = id;
				}
				else if (moveType == MovementType.WALK)
				{
					playerList[localPlayerCount++] = id;
					player.pulseCycle = pulseCycle;
					int direction = buffer.getBits(3);

					player.move(direction, false);

					int blockUpdateRequired = buffer.getBits(1);

					if (blockUpdateRequired == 1)
					{
						updatedPlayers[updatedPlayerCount++] = id;
					}
				}
				else if (moveType == MovementType.RUN)
				{
					playerList[localPlayerCount++] = id;
					player.pulseCycle = pulseCycle;
					int direction1 = buffer.getBits(3);

					player.move(direction1, true);

					int direction2 = buffer.getBits(3);

					player.move(direction2, true);

					int updateRequired = buffer.getBits(1);

					if (updateRequired == 1)
					{
						updatedPlayers[updatedPlayerCount++] = id;
					}
				}
				else if (moveType == MovementType.TELEPORT)
				{
					eneityUpdateIndices[enityUpdateCount++] = id;
				}
			}
		}
	}

	private void renderViewport(int plane)
	{
		int[] pixels = minimapImage.pixels;
		int pixelAmount = pixels.length;
		for (int pixel = 0; pixel < pixelAmount; pixel++)
		{
			pixels[pixel] = 0;
		}

		for (int viewportY = 1; viewportY < 103; viewportY++)
		{
			int drawPoint = 24628 + (103 - viewportY) * 512 * 4;
			for (int viewportX = 1; viewportX < 103; viewportX++)
			{
				if ((currentSceneTileFlags[plane][viewportX][viewportY] & 0x18) == 0)
				{
					currentScene.renderMinimapTile(pixels, drawPoint, 512, plane, viewportX, viewportY);
				}
				if (plane < 3 && (currentSceneTileFlags[plane + 1][viewportX][viewportY] & 8) != 0)
				{
					currentScene.renderMinimapTile(pixels, drawPoint, 512, plane + 1, viewportX, viewportY);
				}
				drawPoint += 4;
			}

		}

		int primaryColour = ((238 + (int) (Math.random() * 20D)) - 10 << 16) + ((238 + (int) (Math.random() * 20D)) - 10 << 8)
			+ ((238 + (int) (Math.random() * 20D)) - 10);
		int secondaryColour = (238 + (int) (Math.random() * 20D)) - 10 << 16;
		minimapImage.createRasterizer();
		for (int viewportY = 1; viewportY < 103; viewportY++)
		{
			for (int viewportX = 1; viewportX < 103; viewportX++)
			{
				if ((currentSceneTileFlags[plane][viewportX][viewportY] & 0x18) == 0)
				{
					method150(viewportY, plane, viewportX, secondaryColour, 563, primaryColour);
				}
				if (plane < 3 && (currentSceneTileFlags[plane + 1][viewportX][viewportY] & 8) != 0)
				{
					method150(viewportY, plane + 1, viewportX, secondaryColour, 563, primaryColour);
				}
			}

		}

		if (gameScreenImageProducer != null)
		{
			gameScreenImageProducer.createRasterizer();
			Rasterizer3D.lineOffsets = viewportOffsets;
		}
		anInt1082++;
		if (anInt1082 > 177)
		{
			anInt1082 = 0;
			outBuffer.putOpcode(173);
			outBuffer.putMediumBE(0x288b80);
		}
		minimapHintCount = 0;
		for (int viewportX = 0; viewportX < 104; viewportX++)
		{
			for (int viewportY = 0; viewportY < 104; viewportY++)
			{
				int floorHash = currentScene.getFloorDecorationHash(this.plane, viewportX, viewportY);
				if (floorHash != 0)
				{
					floorHash = floorHash >> 14 & 0x7fff;
					int icon = GameObjectDefinition.getDefinition(floorHash).icon;
					if (icon >= 0)
					{
						int drawPointX = viewportX;
						int drawPointY = viewportY;
						if (icon != 22 && icon != 29 && icon != 34 && icon != 36 && icon != 46 && icon != 47 && icon != 48)
						{
							byte regionWidth = 104;
							byte regionHeight = 104;
							int[][] flags = currentCollisionMap[this.plane].clippingData;
							for (int off = 0; off < 10; off++)
							{
								int randPlane = (int) (Math.random() * 4D);
								if (randPlane == 0 && drawPointX > 0 && drawPointX > viewportX - 3 && (flags[drawPointX - 1][drawPointY] & 0x1280108) == 0)
								{
									drawPointX--;
								}
								if (randPlane == 1 && drawPointX < regionWidth - 1 && drawPointX < viewportX + 3 && (flags[drawPointX + 1][drawPointY] & 0x1280180) == 0)
								{
									drawPointX++;
								}
								if (randPlane == 2 && drawPointY > 0 && drawPointY > viewportY - 3 && (flags[drawPointX][drawPointY - 1] & 0x1280102) == 0)
								{
									drawPointY--;
								}
								if (randPlane == 3 && drawPointY < regionHeight - 1 && drawPointY < viewportY + 3 && (flags[drawPointX][drawPointY + 1] & 0x1280120) == 0)
								{
									drawPointY++;
								}
							}

						}
						minimapHint[minimapHintCount] = worldMapHintIcons[icon];
						minimapHintX[minimapHintCount] = drawPointX;
						minimapHintY[minimapHintCount] = drawPointY;
						minimapHintCount++;
					}
				}
			}

		}

	}

	private boolean method116(int j, byte[] abyte0)
	{
		if (abyte0 == null)
		{
			return true;
		}
		else
		{
			return SignLink.saveWave(abyte0, j);
		}
	}

	private int getWorldDrawPlane()
	{
		if (!ROOFS_ENABLED)
		{
			return plane;
		}
		int i = 3;
		if (cameraVerticalRotation < 310)
		{
			anInt978++;
			if (anInt978 > 1457)
			{
				anInt978 = 0;
				outBuffer.putOpcode(244);
				outBuffer.putByte(0);
				int j = outBuffer.currentPosition;
				outBuffer.putByte(219);
				outBuffer.putShortBE(37745);
				outBuffer.putByte(61);
				outBuffer.putShortBE(43756);
				outBuffer.putShortBE((int) (Math.random() * 65536D));
				outBuffer.putByte((int) (Math.random() * 256D));
				outBuffer.putShortBE(51171);
				if ((int) (Math.random() * 2D) == 0)
				{
					outBuffer.putShortBE(15808);
				}
				outBuffer.putByte(97);
				outBuffer.putByte((int) (Math.random() * 256D));
				outBuffer.putLength(outBuffer.currentPosition - j);
			}
			int k = cameraX >> 7;
			int l = cameraY >> 7;
			int i1 = localPlayer.worldX >> 7;
			int j1 = localPlayer.worldY >> 7;
			if ((currentSceneTileFlags[plane][k][l] & 4) != 0)
			{
				i = plane;
			}
			int k1;
			if (i1 > k)
			{
				k1 = i1 - k;
			}
			else
			{
				k1 = k - i1;
			}
			int l1;
			if (j1 > l)
			{
				l1 = j1 - l;
			}
			else
			{
				l1 = l - j1;
			}
			if (k1 > l1)
			{
				int i2 = (l1 * 0x10000) / k1;
				int k2 = 32768;
				while (k != i1)
				{
					if (k < i1)
					{
						k++;
					}
					else if (k > i1)
					{
						k--;
					}
					if ((currentSceneTileFlags[plane][k][l] & 4) != 0)
					{
						i = plane;
					}
					k2 += i2;
					if (k2 >= 0x10000)
					{
						k2 -= 0x10000;
						if (l < j1)
						{
							l++;
						}
						else if (l > j1)
						{
							l--;
						}
						if ((currentSceneTileFlags[plane][k][l] & 4) != 0)
						{
							i = plane;
						}
					}
				}
			}
			else
			{
				int j2 = (k1 * 0x10000) / l1;
				int l2 = 32768;
				while (l != j1)
				{
					if (l < j1)
					{
						l++;
					}
					else if (l > j1)
					{
						l--;
					}
					if ((currentSceneTileFlags[plane][k][l] & 4) != 0)
					{
						i = plane;
					}
					l2 += j2;
					if (l2 >= 0x10000)
					{
						l2 -= 0x10000;
						if (k < i1)
						{
							k++;
						}
						else if (k > i1)
						{
							k--;
						}
						if ((currentSceneTileFlags[plane][k][l] & 4) != 0)
						{
							i = plane;
						}
					}
				}
			}
		}
		if ((currentSceneTileFlags[plane][localPlayer.worldX >> 7][localPlayer.worldY >> 7] & 4) != 0)
		{
			i = plane;
		}
		return i;
	}

	private int getCameraPlaneCutscene()
	{
		if (!ROOFS_ENABLED)
		{
			return plane;
		}
		int j = getFloorDrawHeight(plane, cameraX, cameraY);
		if (j - cameraZ < 800 && (currentSceneTileFlags[plane][cameraX >> 7][cameraY >> 7] & 4) != 0)
		{
			return plane;
		}
		else
		{
			return 3;
		}
	}

	private void processPlayerAdditions(boolean priority)
	{ // renderPlayers
		if (localPlayer.worldX >> 7 == destinationX
			&& localPlayer.worldY >> 7 == destinationY)
		{
			destinationX = 0;
		}
		int playersToRender = localPlayerCount;
		if (priority)
		{
			playersToRender = 1;
		}
		for (int p = 0; p < playersToRender; p++)
		{
			Player player;
			int hash;
			if (priority)
			{
				player = localPlayer;
				hash = maxPlayerIndex << 14;
			}
			else
			{
				player = players[playerList[p]];
				hash = playerList[p] << 14;
			}
			if (player == null || !player.isVisible())
			{
				continue;
			}
			player.preventRotation = (lowMemory && localPlayerCount > 50 || localPlayerCount > 200)
				&& !priority
				&& player.movementAnimation == player.idleAnimation;
			int viewportX = player.worldX >> 7;
			int viewportY = player.worldY >> 7;
			if (viewportX < 0 || viewportX >= 104 || viewportY < 0 || viewportY >= 104)
			{
				continue;
			}
			if (player.playerModel != null
				&& pulseCycle >= player.objectAppearanceStartTick
				&& pulseCycle < player.objectAppearanceEndTick)
			{
				player.preventRotation = false;
				player.drawHeight2 = getFloorDrawHeight(
					plane, player.worldX, player.worldY
				);
				currentScene.addEntity(player.anInt1768, player.anInt1769, plane, player.worldX, player.worldY, player.drawHeight2,
					player.currentRotation, player.anInt1771, player.anInt1770, player,
					hash);
				continue;
			}
			if ((player.worldX & 0x7f) == 64
				&& (player.worldY & 0x7f) == 64)
			{
				if (tileRenderCount[viewportX][viewportY] == renderCount)
				{
					continue;
				}
				tileRenderCount[viewportX][viewportY] = renderCount;
			}
			player.drawHeight2 = getFloorDrawHeight(plane, player.worldX, player.worldY
			);
			currentScene.addEntity(plane, player.worldX, player.worldY, player.drawHeight2, player, hash,
				60, player.dynamic,
				player.currentRotation);
		}
	}

	private void processMenuActions(int id)
	{
		if (id < 0)
		{
			return;
		}
		int first = firstMenuOperand[id];
		int second = secondMenuOperand[id];
		int action = menuActionTypes[id];
		int clicked = selectedMenuActions[id];
		if (action >= 2000)
		{
			action -= 2000;
		}
		if (inputType != 0 && action != 1016)
		{
			inputType = 0;
			redrawChatbox = true;
		}
		if (action == 200)
		{
			Player player = players[clicked];
			if (player != null)
			{
				walk(false, false, player.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					player.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(245);
				outBuffer.putOffsetShortLE(clicked);
			}
		}
		if (action == 227)
		{
			anInt1165++;
			if (anInt1165 >= 62)
			{
				outBuffer.putOpcode(165);
				outBuffer.putByte(206);
				anInt1165 = 0;
			}
			outBuffer.putOpcode(228);
			outBuffer.putShortLE(first);
			outBuffer.putOffsetShortBE(clicked);
			outBuffer.putShortBE(second);
			atInventoryLoopCycle = 0;
			anInt1330 = second;
			anInt1331 = first;
			atInventoryInterfaceType = 2;
			if (Widget.forId(second).parentId == openScreenWidgetId)
			{
				atInventoryInterfaceType = 1;
			}
			if (Widget.forId(second).parentId == openChatboxWidgetId)
			{
				atInventoryInterfaceType = 3;
			}
		}
		if (action == 876)
		{
			Player player = players[clicked];
			if (player != null)
			{
				walk(false, false, player.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					player.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(45);
				outBuffer.putOffsetShortBE(clicked);
			}
		}
		if (action == 921)
		{
			Npc npc = npcs[clicked];
			if (npc != null)
			{
				walk(false, false, npc.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					npc.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(67);
				outBuffer.putOffsetShortBE(clicked);
			}
		}
		if (action == 961)
		{
			anInt1139 += clicked;
			if (anInt1139 >= 115)
			{
				outBuffer.putOpcode(126);
				outBuffer.putByte(125);
				anInt1139 = 0;
			}
			outBuffer.putOpcode(203);
			outBuffer.putOffsetShortBE(second);
			outBuffer.putShortLE(first);
			outBuffer.putShortLE(clicked);
			atInventoryLoopCycle = 0;
			anInt1330 = second;
			anInt1331 = first;
			atInventoryInterfaceType = 2;
			if (Widget.forId(second).parentId == openScreenWidgetId)
			{
				atInventoryInterfaceType = 1;
			}
			if (Widget.forId(second).parentId == openChatboxWidgetId)
			{
				atInventoryInterfaceType = 3;
			}
		}
		if (action == 467 && method80(second, 0, first, clicked))
		{
			outBuffer.putOpcode(152);
			outBuffer.putShortLE(clicked >> 14 & 0x7fff);
			outBuffer.putShortLE(anInt1148);
			outBuffer.putShortLE(anInt1149);
			outBuffer.putShortLE(second + nextTopRightTileY);
			outBuffer.putShortBE(anInt1147);
			outBuffer.putOffsetShortLE(first + nextTopLeftTileX);
		}
		if (action == 9)
		{
			outBuffer.putOpcode(3);
			outBuffer.putOffsetShortBE(clicked);
			outBuffer.putShortBE(second);
			outBuffer.putShortBE(first);
			atInventoryLoopCycle = 0;
			anInt1330 = second;
			anInt1331 = first;
			atInventoryInterfaceType = 2;
			if (Widget.forId(second).parentId == openScreenWidgetId)
			{
				atInventoryInterfaceType = 1;
			}
			if (Widget.forId(second).parentId == openChatboxWidgetId)
			{
				atInventoryInterfaceType = 3;
			}
		}
		if (action == 553)
		{
			Npc npc = npcs[clicked];
			if (npc != null)
			{
				walk(false, false, npc.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					npc.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(42);
				outBuffer.putShortLE(clicked);
			}
		}
		if (action == 677)
		{
			Player player = players[clicked];
			if (player != null)
			{
				walk(false, false, player.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					player.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(116);
				outBuffer.putShortLE(clicked);
			}
		}
		if (action == Actions.ADD_FRIEND ||
			action == Actions.ADD_IGNORE ||
			action == Actions.REMOVE_FRIEND ||
			action == Actions.REMOVE_IGNORE)
		{
			String s = menuActionTexts[id];
			int l1 = s.indexOf("@whi@");
			if (l1 != -1)
			{
				long l3 = TextUtils.nameToLong(s.substring(l1 + 5).trim());
				if (action == Actions.ADD_FRIEND)
				{
					addFriend(l3);
				}
				if (action == Actions.ADD_IGNORE)
				{
					addIgnore(l3);
				}
				if (action == Actions.REMOVE_FRIEND)
				{
					removeFriend(l3);
				}
				if (action == Actions.REMOVE_IGNORE)
				{
					removeIgnore(l3);
				}
			}
		}
		if (action == 930)
		{
			boolean flag = walk(false, false, second, localPlayer.pathY[0], 0, 0, 2, 0, first, 0, 0,
				localPlayer.pathX[0]);
			if (!flag)
			{
				flag = walk(false, false, second, localPlayer.pathY[0], 1, 1, 2, 0, first, 0, 0,
					localPlayer.pathX[0]);
			}
			crossX = super.clickX;
			crossY = super.clickY;
			crossType = 2;
			crossIndex = 0;
			outBuffer.putOpcode(54);
			outBuffer.putOffsetShortBE(clicked);
			outBuffer.putShortLE(second + nextTopRightTileY);
			outBuffer.putShortBE(first + nextTopLeftTileX);
		}
		if (action == 399)
		{
			outBuffer.putOpcode(24);
			outBuffer.putShortLE(second);
			outBuffer.putShortLE(clicked);
			outBuffer.putOffsetShortBE(first);
			atInventoryLoopCycle = 0;
			anInt1330 = second;
			anInt1331 = first;
			atInventoryInterfaceType = 2;
			if (Widget.forId(second).parentId == openScreenWidgetId)
			{
				atInventoryInterfaceType = 1;
			}
			if (Widget.forId(second).parentId == openChatboxWidgetId)
			{
				atInventoryInterfaceType = 3;
			}
		}
		if (action == 347)
		{
			Npc class50_sub1_sub4_sub3_sub1_2 = npcs[clicked];
			if (class50_sub1_sub4_sub3_sub1_2 != null)
			{
				walk(false, false, class50_sub1_sub4_sub3_sub1_2.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					class50_sub1_sub4_sub3_sub1_2.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(57);
				outBuffer.putShortBE(clicked);
				outBuffer.putShortLE(anInt1149);
				outBuffer.putOffsetShortLE(anInt1148);
				outBuffer.putShortBE(anInt1147);
			}
		}
		if (action == Actions.TOGGLE_SETTING_WIDGET)
		{
			outBuffer.putOpcode(79);
			outBuffer.putShortBE(second);
			Widget widget = Widget.forId(second);
			if (widget.cs1opcodes != null && widget.cs1opcodes[0][0] == 5)
			{
				int setting = widget.cs1opcodes[0][1];
				widgetSettings[setting] = 1 - widgetSettings[setting];
				updateVarp(0, setting);
				redrawTabArea = true;
			}
		}
		if (action == 493)
		{
			Player class50_sub1_sub4_sub3_sub2_3 = players[clicked];
			if (class50_sub1_sub4_sub3_sub2_3 != null)
			{
				walk(false, false, class50_sub1_sub4_sub3_sub2_3.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					class50_sub1_sub4_sub3_sub2_3.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(233);
				outBuffer.putOffsetShortBE(clicked);
			}
		}
		if (action == 14)
		{
			if (!menuOpen)
			{
				currentScene.method279(0, super.clickX - 4, super.clickY - 4);
			}
			else
			{
				currentScene.method279(0, first - 4, second - 4);
			}
		}
		if (action == 903)
		{
			outBuffer.putOpcode(1);
			outBuffer.putShortBE(clicked);
			outBuffer.putShortLE(anInt1147);
			outBuffer.putShortLE(anInt1149);
			outBuffer.putOffsetShortLE(anInt1148);
			outBuffer.putOffsetShortBE(first);
			outBuffer.putOffsetShortBE(second);
			atInventoryLoopCycle = 0;
			anInt1330 = second;
			anInt1331 = first;
			atInventoryInterfaceType = 2;
			if (Widget.forId(second).parentId == openScreenWidgetId)
			{
				atInventoryInterfaceType = 1;
			}
			if (Widget.forId(second).parentId == openChatboxWidgetId)
			{
				atInventoryInterfaceType = 3;
			}
		}
		if (action == 361)
		{
			outBuffer.putOpcode(36);
			outBuffer.putShortBE(anInt1172);
			outBuffer.putOffsetShortBE(second);
			outBuffer.putOffsetShortBE(first);
			outBuffer.putOffsetShortBE(clicked);
			atInventoryLoopCycle = 0;
			anInt1330 = second;
			anInt1331 = first;
			atInventoryInterfaceType = 2;
			if (Widget.forId(second).parentId == openScreenWidgetId)
			{
				atInventoryInterfaceType = 1;
			}
			if (Widget.forId(second).parentId == openChatboxWidgetId)
			{
				atInventoryInterfaceType = 3;
			}
		}
		if (action == 118)
		{
			Npc class50_sub1_sub4_sub3_sub1_3 = npcs[clicked];
			if (class50_sub1_sub4_sub3_sub1_3 != null)
			{
				walk(false, false, class50_sub1_sub4_sub3_sub1_3.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					class50_sub1_sub4_sub3_sub1_3.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				anInt1235 += clicked;
				if (anInt1235 >= 143)
				{
					outBuffer.putOpcode(157);
					outBuffer.putIntBE(0);
					anInt1235 = 0;
				}
				outBuffer.putOpcode(13);
				outBuffer.putOffsetShortLE(clicked);
			}
		}
		if (action == 376 && method80(second, 0, first, clicked))
		{
			outBuffer.putOpcode(210);
			outBuffer.putShortBE(anInt1172);
			outBuffer.putShortLE(clicked >> 14 & 0x7fff);
			outBuffer.putOffsetShortBE(first + nextTopLeftTileX);
			outBuffer.putShortLE(second + nextTopRightTileY);
		}
		if (action == 432)
		{
			Npc class50_sub1_sub4_sub3_sub1_4 = npcs[clicked];
			if (class50_sub1_sub4_sub3_sub1_4 != null)
			{
				walk(false, false, class50_sub1_sub4_sub3_sub1_4.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					class50_sub1_sub4_sub3_sub1_4.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(8);
				outBuffer.putShortLE(clicked);
			}
		}
		if (action == Actions.CLOSE_WIDGETS)
		{
			closeWidgets();
		}
		if (action == 918)
		{
			Player clickedPlayer = players[clicked];
			if (clickedPlayer != null)
			{
				walk(false, false, clickedPlayer.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					clickedPlayer.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(31);
				outBuffer.putShortBE(clicked);
				outBuffer.putShortLE(anInt1172);
			}
		}
		if (action == 67)
		{
			Npc class50_sub1_sub4_sub3_sub1_5 = npcs[clicked];
			if (class50_sub1_sub4_sub3_sub1_5 != null)
			{
				walk(false, false, class50_sub1_sub4_sub3_sub1_5.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					class50_sub1_sub4_sub3_sub1_5.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(104);
				outBuffer.putOffsetShortBE(anInt1172);
				outBuffer.putShortLE(clicked);
			}
		}
		if (action == 68)
		{
			boolean flag1 = walk(false, false, second, localPlayer.pathY[0], 0, 0, 2, 0, first, 0, 0,
				localPlayer.pathX[0]);
			if (!flag1)
			{
				flag1 = walk(false, false, second, localPlayer.pathY[0], 1, 1, 2, 0, first, 0, 0,
					localPlayer.pathX[0]);
			}
			crossX = super.clickX;
			crossY = super.clickY;
			crossType = 2;
			crossIndex = 0;
			outBuffer.putOpcode(77);
			outBuffer.putOffsetShortBE(first + nextTopLeftTileX);
			outBuffer.putShortBE(second + nextTopRightTileY);
			outBuffer.putOffsetShortLE(clicked);
		}
		if (action == 684)
		{
			boolean flag2 = walk(false, false, second, localPlayer.pathY[0], 0, 0, 2, 0, first, 0, 0,
				localPlayer.pathX[0]);
			if (!flag2)
			{
				flag2 = walk(false, false, second, localPlayer.pathY[0], 1, 1, 2, 0, first, 0, 0,
					localPlayer.pathX[0]);
			}
			crossX = super.clickX;
			crossY = super.clickY;
			crossType = 2;
			crossIndex = 0;
			if ((clicked & 3) == 0)
			{
				anInt1052++;
			}
			if (anInt1052 >= 84)
			{
				outBuffer.putOpcode(222);
				outBuffer.putMediumBE(0xabc842);
				anInt1052 = 0;
			}
			outBuffer.putOpcode(71);
			outBuffer.putOffsetShortLE(clicked);
			outBuffer.putOffsetShortLE(first + nextTopLeftTileX);
			outBuffer.putOffsetShortBE(second + nextTopRightTileY);
		}
		if (action == Actions.ACCEPT_TRADE || action == Actions.ACCEPT_CHALLENGE)
		{
			String name = menuActionTexts[id];
			int colour = name.indexOf("@whi@");
			if (colour != -1)
			{
				name = name.substring(colour + 5).trim();
				String username = TextUtils.formatName(TextUtils.longToName(TextUtils.nameToLong(name)));
				boolean found = false;
				for (int index = 0; index < localPlayerCount; index++)
				{
					Player player = players[playerList[index]];
					if (player == null || player.playerName == null
						|| !player.playerName.equalsIgnoreCase(username))
					{
						continue;
					}
					walk(false, false, player.pathY[0],
						localPlayer.pathY[0], 1, 1, 2, 0,
						player.pathX[0], 0, 0,
						localPlayer.pathX[0]);
					if (action == Actions.ACCEPT_TRADE)
					{
						outBuffer.putOpcode(116);
						outBuffer.putShortLE(playerList[index]);
					}
					if (action == Actions.ACCEPT_CHALLENGE)
					{
						outBuffer.putOpcode(245);
						outBuffer.putOffsetShortLE(playerList[index]);
					}
					found = true;
					break;
				}

				if (!found)
				{
					addChatMessage("", "Unable to find " + username, 0);
				}
			}
		}
		if (action == 225)
		{
			outBuffer.putOpcode(177); // second item action
			outBuffer.putOffsetShortBE(first);
			outBuffer.putShortLE(clicked);
			outBuffer.putShortLE(second);
			atInventoryLoopCycle = 0;
			anInt1330 = second;
			anInt1331 = first;
			atInventoryInterfaceType = 2;
			if (Widget.forId(second).parentId == openScreenWidgetId)
			{
				atInventoryInterfaceType = 1;
			}
			if (Widget.forId(second).parentId == openChatboxWidgetId)
			{
				atInventoryInterfaceType = 3;
			}
		}
		if (action == Actions.USABLE_WIDGET)
		{
			Widget widget = Widget.forId(second);
			widgetSelected = 1;
			anInt1172 = second;
			selectedMask = widget.optionAttributes;
			itemSelected = 0;
			redrawTabArea = true;
			String prefix = widget.optionCircumfix;
			if (prefix.indexOf(" ") != -1)
			{
				prefix = prefix.substring(0, prefix.indexOf(" "));
			}
			String suffix = widget.optionCircumfix;
			if (suffix.indexOf(" ") != -1)
			{
				suffix = suffix.substring(suffix.indexOf(" ") + 1);
			}
			selectedWidgetName = prefix + " " + widget.optionText + " " + suffix;
			if (selectedMask == 16)
			{
				redrawTabArea = true;
				currentTabId = 3;
				drawTabIcons = true;
			}
			return;
		}
		if (action == 891)
		{
			outBuffer.putOpcode(4);
			outBuffer.putShortLE(first);
			outBuffer.putOffsetShortLE(clicked);
			outBuffer.putOffsetShortLE(second);
			atInventoryLoopCycle = 0;
			anInt1330 = second;
			anInt1331 = first;
			atInventoryInterfaceType = 2;
			if (Widget.forId(second).parentId == openScreenWidgetId)
			{
				atInventoryInterfaceType = 1;
			}
			if (Widget.forId(second).parentId == openChatboxWidgetId)
			{
				atInventoryInterfaceType = 3;
			}
		}
		if (action == 894)
		{
			outBuffer.putOpcode(158); // fifth item action event
			outBuffer.putOffsetShortLE(first);
			outBuffer.putOffsetShortLE(clicked);
			outBuffer.putShortLE(second);
			atInventoryLoopCycle = 0;
			anInt1330 = second;
			anInt1331 = first;
			atInventoryInterfaceType = 2;
			if (Widget.forId(second).parentId == openScreenWidgetId)
			{
				atInventoryInterfaceType = 1;
			}
			if (Widget.forId(second).parentId == openChatboxWidgetId)
			{
				atInventoryInterfaceType = 3;
			}
		}
		if (action == 1280)
		{
			method80(second, 0, first, clicked);
			outBuffer.putOpcode(55);
			outBuffer.putShortLE(clicked >> 14 & 0x7fff);
			outBuffer.putShortLE(second + nextTopRightTileY);
			outBuffer.putShortBE(first + nextTopLeftTileX);
		}
		if (action == 35)
		{
			method80(second, 0, first, clicked);
			outBuffer.putOpcode(181);
			outBuffer.putOffsetShortBE(first + nextTopLeftTileX);
			outBuffer.putShortLE(second + nextTopRightTileY);
			outBuffer.putShortLE(clicked >> 14 & 0x7fff);
		}
		if (action == 888)
		{
			method80(second, 0, first, clicked);
			outBuffer.putOpcode(50);
			outBuffer.putOffsetShortBE(second + nextTopRightTileY);
			outBuffer.putShortLE(clicked >> 14 & 0x7fff);
			outBuffer.putOffsetShortLE(first + nextTopLeftTileX);
		}
		if (action == 324)
		{
			outBuffer.putOpcode(161);
			outBuffer.putOffsetShortLE(first);
			outBuffer.putOffsetShortLE(clicked);
			outBuffer.putShortLE(second);
			atInventoryLoopCycle = 0;
			anInt1330 = second;
			anInt1331 = first;
			atInventoryInterfaceType = 2;
			if (Widget.forId(second).parentId == openScreenWidgetId)
			{
				atInventoryInterfaceType = 1;
			}
			if (Widget.forId(second).parentId == openChatboxWidgetId)
			{
				atInventoryInterfaceType = 3;
			}
		}
		if (action == Actions.EXAMINE_ITEM)
		{
			ItemDefinition definition = ItemDefinition.lookup(clicked);
			Widget widget = Widget.forId(second);
			String description;
			if (widget != null && widget.itemAmounts[first] >= 0x186a0)
			{
				description = widget.itemAmounts[first] + " x " + definition.name;
			}
			else if (definition.description != null)
			{
				description = new String(definition.description);
			}
			else
			{
				description = "It's a " + definition.name + ".";
			}
			addChatMessage("", description, 0);
		}
		if (action == 352)
		{
			Widget class13_2 = Widget.forId(second);
			boolean flag7 = true;
			if (class13_2.contentType > 0)
			{
				flag7 = handleWidgetDynamicAction(class13_2);
			}
			if (flag7)
			{
				outBuffer.putOpcode(79);
				outBuffer.putShortBE(second);
			}
		}
		if (action == 1412)
		{
			int k1 = clicked >> 14 & 0x7fff;
			GameObjectDefinition class47 = GameObjectDefinition.getDefinition(k1);
			String s9;
			if (class47.description != null)
			{
				s9 = new String(class47.description);
			}
			else
			{
				s9 = "It's a " + class47.name + ".";
			}
			addChatMessage("", s9, 0);
		}
		if (action == 575 && !aBoolean1239)
		{
			outBuffer.putOpcode(226);
			outBuffer.putShortBE(second);
			aBoolean1239 = true;
		}
		if (action == 892)
		{
			method80(second, 0, first, clicked);
			outBuffer.putOpcode(136);
			outBuffer.putShortBE(first + nextTopLeftTileX);
			outBuffer.putShortLE(second + nextTopRightTileY);
			outBuffer.putShortBE(clicked >> 14 & 0x7fff);
		}
		if (action == 270)
		{
			boolean flag3 = walk(false, false, second, localPlayer.pathY[0], 0, 0, 2, 0, first, 0, 0,
				localPlayer.pathX[0]);
			if (!flag3)
			{
				flag3 = walk(false, false, second, localPlayer.pathY[0], 1, 1, 2, 0, first, 0, 0,
					localPlayer.pathX[0]);
			}
			crossX = super.clickX;
			crossY = super.clickY;
			crossType = 2;
			crossIndex = 0;
			outBuffer.putOpcode(230);
			outBuffer.putShortLE(clicked);
			outBuffer.putOffsetShortBE(first + nextTopLeftTileX);
			outBuffer.putShortBE(second + nextTopRightTileY);
		}
		if (action == 596)
		{
			Player class50_sub1_sub4_sub3_sub2_5 = players[clicked];
			if (class50_sub1_sub4_sub3_sub2_5 != null)
			{
				walk(false, false, class50_sub1_sub4_sub3_sub2_5.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					class50_sub1_sub4_sub3_sub2_5.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(143);
				outBuffer.putShortLE(anInt1149);
				outBuffer.putOffsetShortLE(anInt1147);
				outBuffer.putShortBE(anInt1148);
				outBuffer.putOffsetShortBE(clicked);
			}
		}
		if (action == 100)
		{
			boolean flag4 = walk(false, false, second, localPlayer.pathY[0], 0, 0, 2, 0, first, 0, 0,
				localPlayer.pathX[0]);
			if (!flag4)
			{
				flag4 = walk(false, false, second, localPlayer.pathY[0], 1, 1, 2, 0, first, 0, 0,
					localPlayer.pathX[0]);
			}
			crossX = super.clickX;
			crossY = super.clickY;
			crossType = 2;
			crossIndex = 0;
			outBuffer.putOpcode(211);
			outBuffer.putOffsetShortLE(anInt1147);
			outBuffer.putOffsetShortBE(anInt1149);
			outBuffer.putOffsetShortLE(second + nextTopRightTileY);
			outBuffer.putOffsetShortLE(first + nextTopLeftTileX);
			outBuffer.putShortLE(anInt1148);
			outBuffer.putShortLE(clicked);
		}
		if (action == 1668)
		{
			Npc class50_sub1_sub4_sub3_sub1_6 = npcs[clicked];
			if (class50_sub1_sub4_sub3_sub1_6 != null)
			{
				ActorDefinition class37 = class50_sub1_sub4_sub3_sub1_6.npcDefinition;
				if (class37.childrenIds != null)
				{
					class37 = class37.getChildDefinition();
				}
				if (class37 != null)
				{
					String s10;
					if (class37.description != null)
					{
						s10 = new String(class37.description);
					}
					else
					{
						s10 = "It's a " + class37.name + ".";
					}
					addChatMessage("", s10, 0);
				}
			}
		}
		if (action == 26)
		{
			boolean flag5 = walk(false, false, second, localPlayer.pathY[0], 0, 0, 2, 0, first, 0, 0,
				localPlayer.pathX[0]);
			if (!flag5)
			{
				flag5 = walk(false, false, second, localPlayer.pathY[0], 1, 1, 2, 0, first, 0, 0,
					localPlayer.pathX[0]);
			}
			crossX = super.clickX;
			crossY = super.clickY;
			crossType = 2;
			crossIndex = 0;
			anInt1100++;
			if (anInt1100 >= 120)
			{
				outBuffer.putOpcode(95);
				outBuffer.putIntBE(0);
				anInt1100 = 0;
			}
			outBuffer.putOpcode(100);
			outBuffer.putShortBE(first + nextTopLeftTileX);
			outBuffer.putOffsetShortBE(second + nextTopRightTileY);
			outBuffer.putOffsetShortLE(clicked);
		}
		if (action == 444)
		{
			outBuffer.putOpcode(91); // third item action
			outBuffer.putShortLE(clicked);
			outBuffer.putOffsetShortLE(first);
			outBuffer.putShortBE(second);
			atInventoryLoopCycle = 0;
			anInt1330 = second;
			anInt1331 = first;
			atInventoryInterfaceType = 2;
			if (Widget.forId(second).parentId == openScreenWidgetId)
			{
				atInventoryInterfaceType = 1;
			}
			if (Widget.forId(second).parentId == openChatboxWidgetId)
			{
				atInventoryInterfaceType = 3;
			}
		}
		if (action == 507)
		{
			String string = menuActionTexts[id];
			int whiteIndex = string.indexOf("@whi@");
			if (whiteIndex != -1)
			{
				if (openScreenWidgetId == -1)
				{
					closeWidgets();
					reportedName = string.substring(whiteIndex + 5).trim();
					reportMutePlayer = false;
					reportAbuseInterfaceID = openScreenWidgetId = Widget.instanceWidgetParent;
				}
				else
				{
					addChatMessage("", "Please close the interface you have open before using 'report abuse'", 0);
				}
			}
		}
		if (action == 389)
		{
			method80(second, 0, first, clicked);
			outBuffer.putOpcode(241);
			outBuffer.putShortBE(clicked >> 14 & 0x7fff);
			outBuffer.putShortBE(first + nextTopLeftTileX);
			outBuffer.putOffsetShortBE(second + nextTopRightTileY);
		}
		if (action == 564)
		{
			outBuffer.putOpcode(231); // fourth item action event
			outBuffer.putOffsetShortLE(second);
			outBuffer.putShortLE(first);
			outBuffer.putShortBE(clicked);
			atInventoryLoopCycle = 0;
			anInt1330 = second;
			anInt1331 = first;
			atInventoryInterfaceType = 2;
			if (Widget.forId(second).parentId == openScreenWidgetId)
			{
				atInventoryInterfaceType = 1;
			}
			if (Widget.forId(second).parentId == openChatboxWidgetId)
			{
				atInventoryInterfaceType = 3;
			}
		}
		if (action == 984)
		{
			String s3 = menuActionTexts[id];
			int l2 = s3.indexOf("@whi@");
			if (l2 != -1)
			{
				long l4 = TextUtils.nameToLong(s3.substring(l2 + 5).trim());
				int k3 = -1;
				for (int i4 = 0; i4 < friendsCount; i4++)
				{
					if (friends[i4] != l4)
					{
						continue;
					}
					k3 = i4;
					break;
				}

				if (k3 != -1 && friendWorlds[k3] > 0)
				{
					redrawChatbox = true;
					inputType = 0;
					messagePromptRaised = true;
					chatMessage = "";
					friendsListAction = 3;
					aLong1141 = friends[k3];
					chatboxInputMessage = "Enter message to send to " + friendUsernames[k3];
				}
			}
		}
		if (action == Actions.RESET_SETTING_WIDGET)
		{
			outBuffer.putOpcode(79);
			outBuffer.putShortBE(second);
			Widget widget = Widget.forId(second);
			if (widget.cs1opcodes != null && widget.cs1opcodes[0][0] == 5)
			{
				int operand = widget.cs1opcodes[0][1];
				if (widgetSettings[operand] != widget.conditionValues[0])
				{
					widgetSettings[operand] = widget.conditionValues[0];
					updateVarp(0, operand);
					redrawTabArea = true;
				}
			}
		}
		if (action == 318)
		{
			Npc npc = npcs[clicked];
			if (npc != null)
			{
				walk(false, false, npc.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					npc.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(112);
				outBuffer.putShortLE(clicked);
			}
		}
		if (action == 199)
		{
			boolean flag6 = walk(false, false, second, localPlayer.pathY[0], 0, 0, 2, 0, first, 0, 0,
				localPlayer.pathX[0]);
			if (!flag6)
			{
				flag6 = walk(false, false, second, localPlayer.pathY[0], 1, 1, 2, 0, first, 0, 0,
					localPlayer.pathX[0]);
			}
			crossX = super.clickX;
			crossY = super.clickY;
			crossType = 2;
			crossIndex = 0;
			outBuffer.putOpcode(83);
			outBuffer.putShortLE(clicked);
			outBuffer.putShortBE(second + nextTopRightTileY);
			outBuffer.putShortLE(anInt1172);
			outBuffer.putOffsetShortLE(first + nextTopLeftTileX);
		}
		if (action == 55)
		{
			method44(dialogueId);
			dialogueId = -1;
			redrawChatbox = true;
		}
		if (action == 52)
		{
			itemSelected = 1;
			anInt1147 = first;
			anInt1148 = second;
			anInt1149 = clicked;
			selectedItemName = String.valueOf(ItemDefinition.lookup(clicked).name);
			widgetSelected = 0;
			redrawTabArea = true;
			return;
		}
		if (action == 1564)
		{
			ItemDefinition class16_1 = ItemDefinition.lookup(clicked);
			String s6;
			if (class16_1.description != null)
			{
				s6 = new String(class16_1.description);
			}
			else
			{
				s6 = "It's a " + class16_1.name + ".";
			}
			addChatMessage("", s6, 0);
		}
		if (action == 408)
		{
			Player class50_sub1_sub4_sub3_sub2_6 = players[clicked];
			if (class50_sub1_sub4_sub3_sub2_6 != null)
			{
				walk(false, false, class50_sub1_sub4_sub3_sub2_6.pathY[0],
					localPlayer.pathY[0], 1, 1, 2, 0,
					class50_sub1_sub4_sub3_sub2_6.pathX[0], 0, 0,
					localPlayer.pathX[0]);
				crossX = super.clickX;
				crossY = super.clickY;
				crossType = 2;
				crossIndex = 0;
				outBuffer.putOpcode(194);
				outBuffer.putShortLE(clicked);
			}
		}
		itemSelected = 0;
		widgetSelected = 0;
		redrawTabArea = true;
	}

	private void drawScene2d(boolean flag)
	{
		spokenCount = 0;
		for (int i = -1; i < localPlayerCount + npcCount; i++)
		{
			Object obj;
			if (i == -1)
			{
				obj = localPlayer;
			}
			else if (i < localPlayerCount)
			{
				obj = players[playerList[i]];
			}
			else
			{
				obj = npcs[npcIds[i - localPlayerCount]];
			}
			if (obj == null || !((Actor) (obj)).isVisible())
			{
				continue;
			}
			if (obj instanceof Npc)
			{
				ActorDefinition npc = ((Npc) obj).npcDefinition;
				if (npc.childrenIds != null)
				{
					npc = npc.getChildDefinition();
				}
				if (npc == null)
				{
					continue;
				}
			}
			if (i < localPlayerCount)
			{
				int k = 30;
				Player otherPlayer = (Player) obj;
				if (otherPlayer.isSkulled != -1 || otherPlayer.headIcon != -1)
				{
					setDrawXY(((Actor) (obj)), ((Actor) (obj)).modelHeight + 15);
					if (drawX > -1)
					{
						if (otherPlayer.isSkulled != -1)
						{
							headiconsPk[otherPlayer.isSkulled].drawImage(drawX - 12, drawY
								- k);
							k += 25;
						}
						if (otherPlayer.headIcon != -1)
						{
							headiconsPrayer[otherPlayer.headIcon].drawImage(drawX - 12, drawY
								- k);
							k += 25;
						}
					}
				}
				if (i >= 0 && headIconDrawType == 10 && otherPlayerId == playerList[i])
				{
					setDrawXY(((Actor) (obj)), ((Actor) (obj)).modelHeight + 15);
					if (drawX > -1)
					{
						imageHeadIcons[1].drawImage(drawX - 12, drawY - k);
					}
				}
			}
			else
			{
				ActorDefinition class37_1 = ((Npc) obj).npcDefinition;
				if (class37_1.headIcon >= 0 && class37_1.headIcon < headiconsPrayer.length)
				{
					setDrawXY(((Actor) (obj)), ((Actor) (obj)).modelHeight + 15);
					if (drawX > -1)
					{
						headiconsPrayer[class37_1.headIcon].drawImage(drawX - 12, drawY - 30
						);
					}
				}
				if (headIconDrawType == 1 && anInt1226 == npcIds[i - localPlayerCount] && pulseCycle % 20 < 10)
				{
					setDrawXY(((Actor) (obj)), ((Actor) (obj)).modelHeight + 15);
					if (drawX > -1)
					{
						imageHeadIcons[0].drawImage(drawX - 12, drawY - 28);
					}
				}
			}
			if (((Actor) (obj)).forcedChat != null
				&& (i >= localPlayerCount || publicChatMode == 0 || publicChatMode == 3 || publicChatMode == 1
				&& hasFriend(((Player) obj).playerName)))
			{
				setDrawXY(((Actor) (obj)), ((Actor) (obj)).modelHeight);
				if (drawX > -1 && spokenCount < spokenMax)
				{
					spokenOffsetX[spokenCount] = fontBold.getDisplayedWidth(((Actor) (obj)).forcedChat
					) / 2;
					spokenOffsetY[spokenCount] = fontBold.characterDefaultHeight;
					spokenX[spokenCount] = drawX;
					spokenY[spokenCount] = drawY;
					spokenColour[spokenCount] = ((Actor) (obj)).textColour;
					spokenEffect[spokenCount] = ((Actor) (obj)).textEffect;
					spokenCycle[spokenCount] = ((Actor) (obj)).textCycle;
					spoken[spokenCount++] = ((Actor) (obj)).forcedChat;
					if (showChatEffects == 0 && ((Actor) (obj)).textEffect >= 1 && ((Actor) (obj)).textEffect <= 3)
					{
						spokenOffsetY[spokenCount] += 10;
						spokenY[spokenCount] += 5;
					}
					if (showChatEffects == 0 && ((Actor) (obj)).textEffect == 4)
					{
						spokenOffsetX[spokenCount] = 60;
					}
					if (showChatEffects == 0 && ((Actor) (obj)).textEffect == 5)
					{
						spokenOffsetY[spokenCount] += 5;
					}
				}
			}
			if (((Actor) (obj)).endCycle > pulseCycle)
			{
				setDrawXY(((Actor) (obj)), ((Actor) (obj)).modelHeight + 15);
				if (drawX > -1)
				{
					int l = (((Actor) (obj)).anInt1596 * 30) / ((Actor) (obj)).anInt1597;
					if (l > 30)
					{
						l = 30;
					}
					Rasterizer.drawFilledRectangle(drawX - 15, drawY - 3, l, 5, 65280);
					Rasterizer.drawFilledRectangle((drawX - 15) + l, drawY - 3, 30 - l, 5, 0xff0000);
				}
			}
			for (int i1 = 0; i1 < 4; i1++)
			{
				if (((Actor) (obj)).hitCycles[i1] > pulseCycle)
				{
					setDrawXY(((Actor) (obj)), ((Actor) (obj)).modelHeight / 2);
					if (drawX > -1)
					{
						if (i1 == 1)
						{
							drawY -= 20;
						}
						if (i1 == 2)
						{
							drawX -= 15;
							drawY -= 10;
						}
						if (i1 == 3)
						{
							drawX += 15;
							drawY -= 10;
						}
						hitmarks[((Actor) (obj)).hitTypes[i1]].drawImage(
							drawX - 12, drawY - 12);
						fontSmall.drawStringLeft(String
							.valueOf(((Actor) (obj)).hitDamages[i1]), drawX, drawY + 4, 0);
						fontSmall.drawStringLeft(String
							.valueOf(((Actor) (obj)).hitDamages[i1]), drawX - 1, drawY + 3, 0xffffff);
					}
				}
			}

		}

		for (int j = 0; j < spokenCount; j++)
		{
			int j1 = spokenX[j];
			int k1 = spokenY[j];
			int l1 = spokenOffsetX[j];
			int i2 = spokenOffsetY[j];
			boolean flag1 = true;
			while (flag1)
			{
				flag1 = false;
				for (int j2 = 0; j2 < j; j2++)
				{
					if (k1 + 2 > spokenY[j2] - spokenOffsetY[j2] && k1 - i2 < spokenY[j2] + 2
						&& j1 - l1 < spokenX[j2] + spokenOffsetX[j2]
						&& j1 + l1 > spokenX[j2] - spokenOffsetX[j2]
						&& spokenY[j2] - spokenOffsetY[j2] < k1)
					{
						k1 = spokenY[j2] - spokenOffsetY[j2];
						flag1 = true;
					}
				}

			}
			drawX = spokenX[j];
			drawY = spokenY[j] = k1;
			String s = spoken[j];
			if (showChatEffects == 0)
			{
				int k2 = 0xffff00;
				if (spokenColour[j] < 6)
				{
					k2 = spokenPalette[spokenColour[j]];
				}
				if (spokenColour[j] == 6)
				{
					k2 = renderCount % 20 >= 10 ? 0xffff00 : 0xff0000;
				}
				if (spokenColour[j] == 7)
				{
					k2 = renderCount % 20 >= 10 ? 65535 : 255;
				}
				if (spokenColour[j] == 8)
				{
					k2 = renderCount % 20 >= 10 ? 0x80ff80 : 45056;
				}
				if (spokenColour[j] == 9)
				{
					int l2 = 150 - spokenCycle[j];
					if (l2 < 50)
					{
						k2 = 0xff0000 + 1280 * l2;
					}
					else if (l2 < 100)
					{
						k2 = 0xffff00 - 0x50000 * (l2 - 50);
					}
					else if (l2 < 150)
					{
						k2 = 65280 + 5 * (l2 - 100);
					}
				}
				if (spokenColour[j] == 10)
				{
					int i3 = 150 - spokenCycle[j];
					if (i3 < 50)
					{
						k2 = 0xff0000 + 5 * i3;
					}
					else if (i3 < 100)
					{
						k2 = 0xff00ff - 0x50000 * (i3 - 50);
					}
					else if (i3 < 150)
					{
						k2 = (255 + 0x50000 * (i3 - 100)) - 5 * (i3 - 100);
					}
				}
				if (spokenColour[j] == 11)
				{
					int j3 = 150 - spokenCycle[j];
					if (j3 < 50)
					{
						k2 = 0xffffff - 0x50005 * j3;
					}
					else if (j3 < 100)
					{
						k2 = 65280 + 0x50005 * (j3 - 50);
					}
					else if (j3 < 150)
					{
						k2 = 0xffffff - 0x50000 * (j3 - 100);
					}
				}
				if (spokenEffect[j] == 0)
				{
					fontBold.drawStringLeft(s, drawX, drawY + 1, 0);
					fontBold.drawStringLeft(s, drawX, drawY, k2);
				}
				if (spokenEffect[j] == 1)
				{
					fontBold.drawCenteredStringWaveY(s, drawX, drawY + 1, renderCount, 0);
					fontBold.drawCenteredStringWaveY(s, drawX, drawY, renderCount, k2);
				}
				if (spokenEffect[j] == 2)
				{
					fontBold.drawCenteredStringWaveXY(s, drawX, drawY + 1, renderCount, 0);
					fontBold.drawCenteredStringWaveXY(s, drawX, drawY, renderCount, k2);
				}
				if (spokenEffect[j] == 3)
				{
					fontBold.drawCenteredStringWaveXYMove(s, drawX, drawY + 1, renderCount, 150 - spokenCycle[j], 0
					);
					fontBold.drawCenteredStringWaveXYMove(s, drawX, drawY, renderCount, 150 - spokenCycle[j], k2
					);
				}
				if (spokenEffect[j] == 4)
				{
					int k3 = fontBold.getDisplayedWidth(s);
					int i4 = ((150 - spokenCycle[j]) * (k3 + 100)) / 150;
					Rasterizer.setCoordinates(0, drawX - 50, 334, drawX + 50);
					fontBold.drawString(s, (drawX + 50) - i4, drawY + 1, 0);
					fontBold.drawString(s, (drawX + 50) - i4, drawY, k2);
					Rasterizer.resetCoordinates();
				}
				if (spokenEffect[j] == 5)
				{
					int l3 = 150 - spokenCycle[j];
					int j4 = 0;
					if (l3 < 25)
					{
						j4 = l3 - 25;
					}
					else if (l3 > 125)
					{
						j4 = l3 - 125;
					}
					Rasterizer.setCoordinates(drawY - fontBold.characterDefaultHeight - 1, 0, drawY + 5,
						512);
					fontBold.drawStringLeft(s, drawX, drawY + 1 + j4, 0);
					fontBold.drawStringLeft(s, drawX, drawY + j4, k2);
					Rasterizer.resetCoordinates();
				}
			}
			else
			{
				fontBold.drawStringLeft(s, drawX, drawY + 1, 0);
				fontBold.drawStringLeft(s, drawX, drawY, 0xffff00);
			}
		}

		if (flag)
		{
			opcode = -1;
		}
	}

	private void method122()
	{
		if (chatboxProducingGraphicsBuffer == null)
		{
			method141();
			super.imageProducer = null;
			aClass18_1198 = null;
			aClass18_1199 = null;
			aClass18_1200 = null;
			flameLeftBackground = null;
			flameRightBackground = null;
			aClass18_1203 = null;
			aClass18_1204 = null;
			aClass18_1205 = null;
			aClass18_1206 = null;
			chatboxProducingGraphicsBuffer = new ProducingGraphicsBuffer(479, 96, getParentComponent());
			aClass18_1157 = new ProducingGraphicsBuffer(172, 156, getParentComponent());
			Rasterizer.resetPixels();
			minimapBackgroundImage.drawImage(0, 0);
			tabImageProducer = new ProducingGraphicsBuffer(190, 261, getParentComponent());
			gameScreenImageProducer = new ProducingGraphicsBuffer(512, 334, getParentComponent());
			Rasterizer.resetPixels();
			aClass18_1108 = new ProducingGraphicsBuffer(496, 50, getParentComponent());
			aClass18_1109 = new ProducingGraphicsBuffer(269, 37, getParentComponent());
			aClass18_1110 = new ProducingGraphicsBuffer(249, 45, getParentComponent());
			welcomeScreenRaised = true;
			gameScreenImageProducer.createRasterizer();
			Rasterizer3D.lineOffsets = viewportOffsets;
		}
	}

	private void showErrorScreen()
	{
		Graphics g = getParentComponent().getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 765, 503);
		setFrameRate(1);
		if (loadingError)
		{
			currentlyDrawingFlames = false;
			g.setFont(new Font("Helvetica", Font.BOLD, 16));
			g.setColor(Color.yellow);
			int currentPositionY = 35;
			g.drawString("Sorry, an error has occured whilst loading RuneScape", 30, currentPositionY);
			currentPositionY += 50;
			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, currentPositionY);
			currentPositionY += 50;
			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", Font.BOLD, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, currentPositionY);
			currentPositionY += 30;
			g.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, currentPositionY);
			currentPositionY += 30;
			g.drawString("3: Try using a different game-world", 30, currentPositionY);
			currentPositionY += 30;
			g.drawString("4: Try rebooting your computer", 30, currentPositionY);
			currentPositionY += 30;
			g.drawString("5: Try selecting a different version of Java from the play-game menu", 30, currentPositionY);
		}
		if (genericLoadingError)
		{
			currentlyDrawingFlames = false;
			g.setFont(new Font("Helvetica", Font.BOLD, 20));
			g.setColor(Color.white);
			g.drawString("Error - unable to load game!", 50, 50);
			g.drawString("To play RuneScape make sure you play from", 50, 100);
			g.drawString("http://www.runescape.com", 50, 150);
		}
		if (rsAlreadyLoaded)
		{
			currentlyDrawingFlames = false;
			g.setColor(Color.yellow);
			int currentPositionY = 35;
			g.drawString("Error a copy of RuneScape already appears to be loaded", 30, currentPositionY);
			currentPositionY += 50;
			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, currentPositionY);
			currentPositionY += 50;
			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", Font.BOLD, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, currentPositionY);
			currentPositionY += 30;
			g.drawString("2: Try rebooting your computer, and reloading", 30, currentPositionY);
			currentPositionY += 30;
		}
	}

	private void logout()
	{
		try
		{
			if (gameConnection != null)
			{
				gameConnection.close();
			}
		}
		catch (Exception _ex)
		{
		}
		gameConnection = null;
		loggedIn = false;
		loginScreenState = 0;
		if (USE_STATIC_DETAILS)
		{
			username = USERNAME;
			password = PASSWORD;
		}
		else
		{
			username = "";
			password = "";
		}

		resetModelCaches();
		currentScene.initToNull();
		for (int plane = 0; plane < 4; plane++)
		{
			currentCollisionMap[plane].reset();
		}

		System.gc();

		stopMidi();
		currentSong = -1;
		nextSong = -1;
		previousSong = 0;
	}

	private void method125(String s, String s1)
	{
		if (gameScreenImageProducer != null)
		{
			gameScreenImageProducer.createRasterizer();
			Rasterizer3D.lineOffsets = viewportOffsets;
			int j = 151;
			if (s != null)
			{
				j -= 7;
			}
			fontNormal.drawStringLeft(s1, 257, j, 0);
			fontNormal.drawStringLeft(s1, 256, j - 1, 0xffffff);
			j += 15;
			if (s != null)
			{
				fontNormal.drawStringLeft(s, 257, j, 0);
				fontNormal.drawStringLeft(s, 256, j - 1, 0xffffff);
			}
			gameScreenImageProducer.drawGraphics(4, 4, super.gameGraphics);
			return;
		}
		if (super.imageProducer != null)
		{
			super.imageProducer.createRasterizer();
			Rasterizer3D.lineOffsets = fullScreenTextureArray;
			int k = 251;
			char c = '\u012C';
			byte byte0 = 50;
			Rasterizer.drawFilledRectangle(383 - c / 2, k - 5 - byte0 / 2, c, byte0, 0);
			Rasterizer.drawUnfilledRectangle(383 - c / 2, k - 5 - byte0 / 2, c, byte0, 0xffffff);
			if (s != null)
			{
				k -= 7;
			}
			fontNormal.drawStringLeft(s1, 383, k, 0);
			fontNormal.drawStringLeft(s1, 382, k - 1, 0xffffff);
			k += 15;
			if (s != null)
			{
				fontNormal.drawStringLeft(s, 383, k, 0);
				fontNormal.drawStringLeft(s, 382, k - 1, 0xffffff);
			}
			super.imageProducer.drawGraphics(0, 0, super.gameGraphics);
		}
	}

	private boolean menuHasAddFriend(int i, byte byte0)
	{
		if (i < 0)
		{
			return false;
		}
		int j = menuActionTypes[i];
		if (byte0 != 97)
		{
			throw new NullPointerException();
		}
		if (j >= 2000)
		{
			j -= 2000;
		}
		return j == 762;
	}

	private void drawMarker()
	{
		if (headIconDrawType != 2)
		{
			return;
		}
		setDrawXY((hintIconX - nextTopLeftTileX << 7) + markerOffsetX, (hintIconY - nextTopRightTileY << 7) + markerOffsetY, hintIconOffset * 2);
		if (drawX > -1 && pulseCycle % 20 < 10)
		{
			imageHeadIcons[0].drawImage(drawX - 12, drawY - 28);
		}
	}

	private void drawMenu()
	{
		int offsetX = menuOffsetX;
		int offsetY = menuOffsetY;
		int width = menuWidth;
		int height = menuHeight;
		int colour = 0x5d5447;
		Rasterizer.drawFilledRectangleAlpha(offsetX, offsetY, width, height, colour, 120);
		Rasterizer.drawFilledRectangle(offsetX + 1, offsetY + 1, width - 2, 16, 0);
		Rasterizer.drawUnfilledRectangle(offsetX + 1, offsetY + 18, width - 2, height - 19, 0);
		fontBold.drawString("Choose Option", offsetX + 3, offsetY + 14, colour);
		int x = super.mouseX;
		int y = super.mouseY;
		if (menuScreenArea == 0)
		{
			x -= 4;
			y -= 4;
		}
		if (menuScreenArea == 1)
		{
			x -= 553;
			y -= 205;
		}
		if (menuScreenArea == 2)
		{
			x -= 17;
			y -= 357;
		}
		for (int action = 0; action < menuActionRow; action++)
		{
			int actionY = offsetY + 31 + (menuActionRow - 1 - action) * 15;
			int actionColour = 0xffffff;
			if (x > offsetX && x < offsetX + width && y > actionY - 13 && y < actionY + 3)
			{
				actionColour = 0xffff00;
			}
			fontBold.drawShadowedString(menuActionTexts[action], offsetX + 3, actionY, true, actionColour);
		}

	}

	private int parseCS1(Widget widget, int id)
	{
		if (widget.cs1opcodes == null || id >= widget.cs1opcodes.length)
		{
			return -2;
		}
		try
		{
			int[] opcodes = widget.cs1opcodes[id];
			int result = 0;
			int counter = 0;
			int type = 0;
			do
			{
				int opcode = opcodes[counter++];
				int value = 0;
				byte tempType = 0;
				if (opcode == 0)
				{
					return result;
				}
				if (opcode == 1)
				{
					value = skillLevel[opcodes[counter++]];
				}
				if (opcode == 2)
				{
					value = skillMaxLevel[opcodes[counter++]];
				}
				if (opcode == 3)
				{
					value = skillExperience[opcodes[counter++]];
				}
				if (opcode == 4)
				{
					Widget widget1 = Widget.forId(opcodes[counter++]);
					int itemId = opcodes[counter++];

					if (itemId >= 0 && itemId < ItemDefinition.count && (!ItemDefinition.lookup(itemId).members || memberServer))
					{
						for (int item = 0; item < widget1.items.length; item++)
						{
							if (widget1.items[item] == itemId + 1)
							{
								value += widget1.itemAmounts[item];

							}
						}

					}

				}
				if (opcode == 5)
				{
					value = widgetSettings[opcodes[counter++]];
				}
				if (opcode == 6)
				{
					value = SKILL_EXPERIENCE[skillMaxLevel[opcodes[counter++]] - 1];
				}
				if (opcode == 7)
				{
					value = (widgetSettings[opcodes[counter++]] * 100) / 46875;
				}
				if (opcode == 8)
				{
					value = localPlayer.combatLevel;
				}
				if (opcode == 9)
				{
					for (int l1 = 0; l1 < SkillConstants.SKILL_COUNT; l1++)
					{
						if (SkillConstants.SKILL_TOGGLES[l1])
						{
							value += skillMaxLevel[l1];
						}
					}

				}
				if (opcode == 10)
				{
					Widget widget1 = Widget.forId(opcodes[counter++]);
					int itemId = opcodes[counter++] + 1;
					if (itemId >= 0 && itemId < ItemDefinition.count && (!ItemDefinition.lookup(itemId).members || memberServer))
					{
						for (int item = 0; item < widget1.items.length; item++)
						{
							if (widget1.items[item] == itemId)
							{
								continue;
							}
							value = 0;
							break;
						}

					}
				}
				if (opcode == 11)
				{
					value = runEnergy;
				}
				if (opcode == 12)
				{
					value = userWeight;
				}
				if (opcode == 13)
				{
					int i2 = widgetSettings[opcodes[counter++]];
					int i3 = opcodes[counter++];
					value = (i2 & 1 << i3) == 0 ? 0 : 1;
				}
				if (opcode == 14)
				{
					int j2 = opcodes[counter++];
					Varbit varbit = Varbit.cache[j2];
					int l3 = varbit.configId;
					int i4 = varbit.leastSignificantBit;
					int j4 = varbit.mostSignificantBit;
					int k4 = BITFIELD_MAX_VALUE[j4 - i4];
					value = widgetSettings[l3] >> i4 & k4;
				}
				if (opcode == 15)
				{
					tempType = 1;
				}
				if (opcode == 16)
				{
					tempType = 2;
				}
				if (opcode == 17)
				{
					tempType = 3;
				}
				if (opcode == 18)
				{
					value = (localPlayer.worldX >> 7) + nextTopLeftTileX;
				}
				if (opcode == 19)
				{
					value = (localPlayer.worldY >> 7) + nextTopRightTileY;
				}
				if (opcode == 20)
				{
					value = opcodes[counter++];
				}
				if (tempType == 0)
				{
					if (type == 0)
					{
						result += value;
					}
					if (type == 1)
					{
						result -= value;
					}
					if (type == 2 && value != 0)
					{
						result /= value;
					}
					if (type == 3)
					{
						result *= value;
					}
					type = 0;
				}
				else
				{
					type = tempType;
				}
			} while (true);
		}
		catch (Exception _ex)
		{
			return -1;
		}
	}

	private void drawOnMinimap(ImageRGB sprite, int x, int y)
	{
		if (sprite == null)
		{
			return;
		}
		int k = cameraHorizontal + cameraYawOffset & 0x7ff;
		int l = x * x + y * y;
		if (l > 6400)
		{
			return;
		}
		int sine = Model.SINE[k];
		int cosine = Model.COSINE[k];
		sine = (sine * 256) / (mapZoomOffset + 256);
		cosine = (cosine * 256) / (mapZoomOffset + 256);
		int k1 = y * sine + x * cosine >> 16;
		int l1 = y * cosine - x * sine >> 16;
		if (l > 2500)
		{
			sprite.drawTo(minimapBackgroundImage, ((94 + k1) - sprite.maxWidth / 2) + 4, 83 - l1 - sprite.maxHeight
				/ 2 - 4);
		}
		else
		{
			sprite.drawImage(((94 + k1) - sprite.maxWidth / 2) + 4, 83 - l1 - sprite.maxHeight / 2 - 4
			);
		}
	}

	private void drawLoginScreen(boolean flag)
	{
		resetTitleScreen();
		aClass18_1200.createRasterizer();
		titleboxImage.drawImage(0, 0);
		char c = '\u0168';
		char c1 = '\310';
		if (loginScreenState == 0)
		{
			int j = c1 / 2 + 80;
			fontSmall.drawStringCenter(onDemandRequester.message, c / 2, j, 0x75a9a9, true);
			j = c1 / 2 - 20;
			fontBold.drawStringCenter("Welcome to RuneScape", c / 2, j, 0xffff00, true);
			j += 30;
			int i1 = c / 2 - 80;
			int l1 = c1 / 2 + 20;
			titleboxButtonImage.drawImage(i1 - 73, l1 - 20);
			fontBold.drawStringCenter("New User", i1, l1 + 5, 0xffffff, true);
			i1 = c / 2 + 80;
			titleboxButtonImage.drawImage(i1 - 73, l1 - 20);
			fontBold.drawStringCenter("Existing User", i1, l1 + 5, 0xffffff, true);
		}
		if (loginScreenState == 2)
		{
			int k = c1 / 2 - 40;
			if (statusLineOne.length() > 0)
			{
				fontBold.drawStringCenter(statusLineOne, c / 2, k - 15, 0xffff00, true);
				fontBold.drawStringCenter(statusLineTwo, c / 2, k, 0xffff00, true);
				k += 30;
			}
			else
			{
				fontBold.drawStringCenter(statusLineTwo, c / 2, k - 7, 0xffff00, true);
				k += 30;
			}
			fontBold.drawShadowedString("Username: " + username
				+ ((loginScreenFocus == 0) & (pulseCycle % 40 < 20) ? "@yel@|" : ""), c / 2 - 90, k, true, 0xffffff);
			k += 15;
			fontBold.drawShadowedString("Password: "
				+ TextUtils.censorPassword(password) + ((loginScreenFocus == 1) & (pulseCycle % 40 < 20) ? "@yel@|" : ""), c / 2 - 88, k, true, 0xffffff
			);
			k += 15;
			if (!flag)
			{
				int j1 = c / 2 - 80;
				int i2 = c1 / 2 + 50;
				titleboxButtonImage.drawImage(j1 - 73, i2 - 20);
				fontBold.drawStringCenter("Login", j1, i2 + 5, 0xffffff, true);
				j1 = c / 2 + 80;
				titleboxButtonImage.drawImage(j1 - 73, i2 - 20);
				fontBold.drawStringCenter("Cancel", j1, i2 + 5, 0xffffff, true);
			}
		}
		if (loginScreenState == 3)
		{
			fontBold.drawStringCenter("Create a free account", c / 2, c1 / 2 - 60, 0xffff00, true
			);
			int l = c1 / 2 - 35;
			fontBold.drawStringCenter("To create a new account you need to", c / 2, l, 0xffffff, true
			);
			l += 15;
			fontBold.drawStringCenter("go back to the main RuneScape webpage", c / 2, l, 0xffffff, true
			);
			l += 15;
			fontBold.drawStringCenter("and choose the 'create account'", c / 2, l, 0xffffff, true
			);
			l += 15;
			fontBold.drawStringCenter("button near the top of that page.", c / 2, l, 0xffffff, true
			);
			l += 15;
			int k1 = c / 2;
			int j2 = c1 / 2 + 50;
			titleboxButtonImage.drawImage(k1 - 73, j2 - 20);
			fontBold.drawStringCenter("Cancel", k1, j2 + 5, 0xffffff, true);
		}
		aClass18_1200.drawGraphics(202, 171, super.gameGraphics);
		if (welcomeScreenRaised)
		{
			welcomeScreenRaised = false;
			aClass18_1198.drawGraphics(128, 0, super.gameGraphics);
			aClass18_1199.drawGraphics(202, 371, super.gameGraphics);
			aClass18_1203.drawGraphics(0, 265, super.gameGraphics);
			aClass18_1204.drawGraphics(562, 265, super.gameGraphics);
			aClass18_1205.drawGraphics(128, 171, super.gameGraphics);
			aClass18_1206.drawGraphics(562, 171, super.gameGraphics);
		}
	}

	private void processNewNpcs(Buffer buffer, int i)
	{
		while (buffer.bitPosition + 21 < i * 8)
		{
			int j = buffer.getBits(14);
			if (j == 16383)
			{
				break;
			}
			if (npcs[j] == null)
			{
				npcs[j] = new Npc();
			}
			Npc npc = npcs[j];
			npcIds[npcCount++] = j;
			npc.pulseCycle = pulseCycle;
			int k = buffer.getBits(1);
			if (k == 1)
			{
				updatedPlayers[updatedPlayerCount++] = j;
			}
			int l = buffer.getBits(5);
			if (l > 15)
			{
				l -= 32;
			}
			int i1 = buffer.getBits(5);
			if (i1 > 15)
			{
				i1 -= 32;
			}
			int j1 = buffer.getBits(1);
			npc.npcDefinition = ActorDefinition.getDefinition(buffer.getBits(13));
			npc.boundaryDimension = npc.npcDefinition.boundaryDimension;
			npc.turnSpeed = npc.npcDefinition.degreesToTurn;
			npc.walkAnimationId = npc.npcDefinition.walkAnimationId;
			npc.turnAroundAnimationId = npc.npcDefinition.turnAroundAnimationId;
			npc.turnRightAnimationId = npc.npcDefinition.turnRightAnimationId;
			npc.turnLeftAnimationId = npc.npcDefinition.turnLeftAnimationId;
			npc.idleAnimation = npc.npcDefinition.standAnimationId;
			npc.setPosition(localPlayer.pathX[0] + i1, localPlayer.pathY[0] + l,
				j1 == 1);
		}
		buffer.finishBitAccess();
	}

	public void playSong(int id)
	{
		if (currentSong != id)
		{
			nextSong = id;
			songChanging = true;
			onDemandRequester.request(2, nextSong);
			currentSong = id;
		}
	}

	private void stopMidi()
	{
		SignLink.music.stop();
		SignLink.fadeMidi = 0;
		SignLink.midi = "stop";
	}

	private void adjustMidiVolume(boolean updateMidi, int volume)
	{
		SignLink.setVolume(volume);
		if (updateMidi)
		{
			SignLink.midi = "voladjust";
		}
	}

	public void playSound(int id, int type, int delay, int volume)
	{
		sound[currentSound] = id;
		soundType[currentSound] = type;
		soundDelay[currentSound] = delay + SoundTrack.trackDelays[id];
		soundVolume[currentSound] = volume;
		currentSound++;
	}

	private void parsePlacementPacket(Buffer buf, int opcode)
	{
		if (opcode == 203)
		{ // ??? something with object spawning?
			int landscapeObjectId = buf.getUnsignedShortBE();
			int landscapeObjectData = buf.getUnsignedByte();
			int typeIndex = landscapeObjectData >> 2;
			int rotation = landscapeObjectData & 3;
			int type = objectTypes[typeIndex];
			byte byte0 = buf.getInvertedByte();
			int offset = buf.getUnsignedPostNegativeOffsetByte();
			int x = placementX + (offset >> 4 & 7);
			int y = placementY + (offset & 7);
			byte byte1 = buf.getPostNegativeOffsetByte();
			int duration = buf.getUnsignedNegativeOffsetShortBE();
			int playerId = buf.getUnsignedShortLE();
			byte byte2 = buf.getByte();
			byte byte3 = buf.getPostNegativeOffsetByte();
			int startDelay = buf.getUnsignedShortBE();
			Player player;
			if (playerId == thisPlayerServerId)
			{
				player = localPlayer;
			}
			else
			{
				player = players[playerId];
			}
			if (player != null)
			{
				GameObjectDefinition object = GameObjectDefinition.getDefinition(landscapeObjectId);
				int vertexHeight = this.intGroundArray[plane][x][y];
				int vertexHeightRight = this.intGroundArray[plane][x + 1][y];
				int vertexHeightTopRight = this.intGroundArray[plane][x + 1][y + 1];
				int vertexHeightTop = this.intGroundArray[plane][x][y + 1];
				Model model = object.getGameObjectModel(typeIndex, rotation, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
				if (model != null)
				{
					createObjectSpawnRequest(plane, x, 0, duration + 1, 0, -1, startDelay + 1, type, y);
					player.objectAppearanceStartTick = startDelay + pulseCycle;
					player.objectAppearanceEndTick = duration + pulseCycle;
					player.playerModel = model;
					int i23 = object.sizeX;
					int j23 = object.sizeY;
					if (rotation == 1 || rotation == 3)
					{
						i23 = object.sizeY;
						j23 = object.sizeX;
					}
					player.anInt1743 = x * 128 + i23 * 64;
					player.anInt1745 = y * 128 + j23 * 64;
					player.drawHeight = getFloorDrawHeight(plane, player.anInt1743, player.anInt1745
					);
					if (byte1 > byte0)
					{
						byte byte4 = byte1;
						byte1 = byte0;
						byte0 = byte4;
					}
					if (byte3 > byte2)
					{
						byte byte5 = byte3;
						byte3 = byte2;
						byte2 = byte5;
					}
					player.anInt1768 = x + byte1;
					player.anInt1770 = x + byte0;
					player.anInt1769 = y + byte3;
					player.anInt1771 = y + byte2;
				}
			}
		}
		if (SET_PLAYER_GROUND_ITEM.equals(opcode))
		{
			int offset = buf.getUnsignedPostNegativeOffsetByte();
			int x = placementX + (offset >> 4 & 7);
			int y = placementY + (offset & 7);
			int amount = buf.getUnsignedNegativeOffsetShortLE();
			int itemId = buf.getUnsignedNegativeOffsetShortBE();
			int playerId = buf.getUnsignedNegativeOffsetShortBE();
			if (x >= 0 && y >= 0 && x < 104 && y < 104 && playerId != thisPlayerServerId)
			{
				Item item = new Item();
				item.itemId = itemId;
				item.itemCount = amount;
				if (groundItems.isTileEmpty(plane, x, y))
				{
					groundItems.setTile(plane, x, y, new LinkedList());
				}
				groundItems.getTile(plane, x, y).pushBack(item);
				processGroundItems(x, y);
			}
			return;
		}
		if (opcode == 142)
		{ // object animation???
			int animationId = buf.getUnsignedShortBE();
			int landscapeObjectData = buf.getUnsignedPostNegativeOffsetByte();
			int typeIndex = landscapeObjectData >> 2;
			int rotation = landscapeObjectData & 3;
			int type = objectTypes[typeIndex];
			int offset = buf.getUnsignedByte();
			int x = placementX + (offset >> 4 & 7);
			int y = placementY + (offset & 7);
			if (x >= 0 && y >= 0 && x < 103 && y < 103)
			{
				int vertixHeight = intGroundArray[plane][x][y];
				int vertixHeightRight = intGroundArray[plane][x + 1][y];
				int vertixHeightTopRight = intGroundArray[plane][x + 1][y + 1];
				int vertixHeightTop = intGroundArray[plane][x][y + 1];
				if (type == 0)
				{
					Wall wall = currentScene.getWallObject(plane, x, y);
					if (wall != null)
					{
						int landscapeObjectId = wall.uid >> 14 & 0x7fff;
						if (typeIndex == 2)
						{
							wall.primary = new GameObject(landscapeObjectId, 4 + rotation, 2, vertixHeightRight, vertixHeightTopRight, vertixHeight, vertixHeightTop, animationId,
								false);
							wall.secondary = new GameObject(landscapeObjectId, rotation + 1 & 3, 2, vertixHeightRight, vertixHeightTopRight, vertixHeight, vertixHeightTop, animationId,
								false);
						}
						else
						{
							wall.primary = new GameObject(landscapeObjectId, rotation, typeIndex, vertixHeightRight, vertixHeightTopRight, vertixHeight, vertixHeightTop, animationId,
								false);
						}
					}
				}
				if (type == 1)
				{
					WallDecoration wallDecoration = currentScene.getWallDecoration(plane, y, x);
					if (wallDecoration != null)
					{
						wallDecoration.renderable = new GameObject(wallDecoration.uid >> 14 & 0x7fff, 0, 4, vertixHeightRight, vertixHeightTopRight, vertixHeight, vertixHeightTop, animationId,
							false);
					}
				}
				if (type == 2)
				{
					InteractiveObject interactiveObject = currentScene.method265(x, y, plane);
					if (typeIndex == 11)
					{
						typeIndex = 10;
					}
					if (interactiveObject != null)
					{
						interactiveObject.renderable = new GameObject(interactiveObject.uid >> 14 & 0x7fff, rotation, typeIndex, vertixHeightRight, vertixHeightTopRight, vertixHeight, vertixHeightTop, animationId,
							false);
					}
				}
				if (type == 3)
				{
					FloorDecoration floorDecoration = currentScene.getFloorDecoration(plane, y, x);
					if (floorDecoration != null)
					{
						floorDecoration.renderable = new GameObject(floorDecoration.uid >> 14 & 0x7fff, rotation, 22, vertixHeightRight, vertixHeightTopRight, vertixHeight, vertixHeightTop, animationId,
							false);
					}
				}
			}
			return;
		}
		if (SET_GROUND_ITEM.equals(opcode))
		{
			int itemId = buf.getUnsignedShortBE();
			int offset = buf.getUnsignedInvertedByte();
			int x = placementX + (offset >> 4 & 7);
			int y = placementY + (offset & 7);
			int amount = buf.getUnsignedNegativeOffsetShortBE();
			if (x >= 0 && y >= 0 && x < 104 && y < 104)
			{
				Item item = new Item();
				item.itemId = itemId;
				item.itemCount = amount;
				if (groundItems.isTileEmpty(plane, x, y))
				{
					groundItems.setTile(plane, x, y, new LinkedList());
				}
				groundItems.getTile(plane, x, y).pushBack(item);
				processGroundItems(x, y);
			}
			return;
		}
		if (UPDATE_GROUND_ITEM_AMOUNT.equals(opcode))
		{
			int offset = buf.getUnsignedByte();
			int x = placementX + (offset >> 4 & 7);
			int y = placementY + (offset & 7);
			int itemId = buf.getUnsignedShortBE();
			int oldAmount = buf.getUnsignedShortBE();
			int newAmount = buf.getUnsignedShortBE();
			if (x >= 0 && y >= 0 && x < 104 && y < 104)
			{
				LinkedList list = groundItems.getTile(plane, x, y);
				if (list != null)
				{
					for (Item item = (Item) list.first(); item != null; item = (Item) list.next())
					{
						if (item.itemId != (itemId & 0x7fff) || item.itemCount != oldAmount)
						{
							continue;
						}
						item.itemCount = newAmount;
						break;
					}

					processGroundItems(x, y);
				}
			}
			return;
		}
		if (SHOW_PROJECTILE.equals(opcode))
		{
			int offset = buf.getUnsignedByte();
			int startX = placementX + (offset >> 4 & 7);
			int startY = placementY + (offset & 7);
			int endX = startX + buf.getByte();
			int endY = startY + buf.getByte();
			int entityIndex = buf.getShortBE();
			int graphicsId = buf.getUnsignedShortBE();
			int startHeight = buf.getUnsignedByte() * 4;
			int endHeight = buf.getUnsignedByte() * 4;
			int delay = buf.getUnsignedShortBE();
			int speed = buf.getUnsignedShortBE();
			int startSlope = buf.getUnsignedByte();
			int startDistance = buf.getUnsignedByte();
			if (startX >= 0 && startY >= 0 && startX < 104 && startY < 104 && endX >= 0 && endY >= 0 && endX < 104 && endY < 104
				&& graphicsId != 65535)
			{
				startX = startX * 128 + 64;
				startY = startY * 128 + 64;
				endX = endX * 128 + 64;
				endY = endY * 128 + 64;
				Projectile projectile = new Projectile(plane, endHeight, startDistance, startY,
					graphicsId, speed + pulseCycle, startSlope, entityIndex, getFloorDrawHeight(plane, startX, startY) - startHeight, startX, delay + pulseCycle);
				projectile.trackTarget(endX, endY, getFloorDrawHeight(plane, endX, endY) - endHeight, delay
					+ pulseCycle);
				projectileQueue.pushBack(projectile);
			}
			return;
		}
		if (PLAY_POSITION_SOUND.equals(opcode))
		{
			int offset = buf.getUnsignedByte();
			int x = placementX + (offset >> 4 & 7);
			int y = placementY + (offset & 7);
			int soundId = buf.getUnsignedShortBE();
			int soundData = buf.getUnsignedByte();
			int radius = soundData >> 4 & 0xf;
			int type = soundData & 7; // what types exist?
			if (localPlayer.pathX[0] >= x - radius
				&& localPlayer.pathX[0] <= x + radius
				&& localPlayer.pathY[0] >= y - radius
				&& localPlayer.pathY[0] <= y + radius && aBoolean1301 && !lowMemory
				&& currentSound < 50)
			{
				sound[currentSound] = soundId;
				soundType[currentSound] = type;
				soundDelay[currentSound] = SoundTrack.trackDelays[soundId];
				currentSound++;
			}
		}
		if (SHOW_STILL_GRAPHICS.equals(opcode))
		{
			int offset = buf.getUnsignedByte();
			int x = placementX + (offset >> 4 & 7);
			int y = placementY + (offset & 7);
			int graphicsId = buf.getUnsignedShortBE();
			int graphicsHeight = buf.getUnsignedByte();
			int delay = buf.getUnsignedShortBE();
			if (x >= 0 && y >= 0 && x < 104 && y < 104)
			{
				x = x * 128 + 64;
				y = y * 128 + 64;
				GameAnimableObject gameAnimableObject = new GameAnimableObject(plane, pulseCycle, delay, graphicsId, getFloorDrawHeight(plane, x, y) - graphicsHeight, y, x);
				gameAnimableObjectQueue.pushBack(gameAnimableObject);
			}
			return;
		}
		if (SET_LANDSCAPE_OBJECT.equals(opcode))
		{
			int landscapeObjectData = buf.getUnsignedInvertedByte();
			int typeIndex = landscapeObjectData >> 2;
			int rotation = landscapeObjectData & 3;
			int type = objectTypes[typeIndex];
			int landscapeObjectId = buf.getUnsignedNegativeOffsetShortLE();
			int offset = buf.getUnsignedPostNegativeOffsetByte();
			int x = placementX + (offset >> 4 & 7);
			int y = placementY + (offset & 7);
			if (x >= 0 && y >= 0 && x < 104 && y < 104)
			{
				createObjectSpawnRequest(plane, x, rotation, -1, typeIndex, landscapeObjectId, 0, type, y);
			}
			return;
		}
		if (REMOVE_GROUND_ITEM.equals(opcode))
		{
			int itemId = buf.getUnsignedNegativeOffsetShortBE();
			int offset = buf.getUnsignedPostNegativeOffsetByte();
			int x = placementX + (offset >> 4 & 7);
			int y = placementY + (offset & 7);
			if (x >= 0 && y >= 0 && x < 104 && y < 104)
			{
				LinkedList list = groundItems.getTile(plane, x, y);
				if (list != null)
				{
					for (Item item = (Item) list.first(); item != null; item = (Item) list.next())
					{
						if (item.itemId != (itemId & 0x7fff))
						{
							continue;
						}
						item.remove();
						break;
					}

					if (list.first() == null)
					{
						groundItems.clearTile(plane, x, y);
					}
					processGroundItems(x, y);
				}
			}
			return;
		}
		if (REMOVE_LANDSCAPE_OBJECT.equals(opcode))
		{
			int offset = buf.getUnsignedPreNegativeOffsetByte();
			int x = placementX + (offset >> 4 & 7);
			int y = placementY + (offset & 7);
			int landscapeObjectData = buf.getUnsignedPreNegativeOffsetByte();
			int typeIndex = landscapeObjectData >> 2;
			int rotation = landscapeObjectData & 3;
			int type = objectTypes[typeIndex];
			if (x >= 0 && y >= 0 && x < 104 && y < 104)
			{
				createObjectSpawnRequest(plane, x, rotation, -1, typeIndex, -1, 0, type, y);
			}
		}
	}

	private void drawTabArea()
	{
		tabImageProducer.createRasterizer();
		Rasterizer3D.lineOffsets = sidebarOffsets;
		inventoryBackgroundImage.drawImage(0, 0);
		if (tabAreaOverlayWidgetId != -1)
		{
			drawInterface(0, 0, Widget.forId(tabAreaOverlayWidgetId), 0);
		}
		else if (tabWidgetIds[currentTabId] != -1)
		{
			drawInterface(0, 0, Widget.forId(tabWidgetIds[currentTabId]), 0);
		}
		if (menuOpen && menuScreenArea == 1)
		{
			drawMenu();
		}
		tabImageProducer.drawGraphics(553, 205, super.gameGraphics);
		gameScreenImageProducer.createRasterizer();
		Rasterizer3D.lineOffsets = viewportOffsets;
	}

	private void setDrawXY(Actor actor, int offsetZ)
	{
		setDrawXY(actor.worldX, actor.worldY, offsetZ);
	}

	private void setDrawXY(int x, int y, int offsetZ)
	{
		if (x < 128 || y < 128 || x > 13056 || y > 13056)
		{
			drawX = -1;
			drawY = -1;
			return;
		}
		int z = getFloorDrawHeight(plane, x, y) - offsetZ;
		x -= cameraX;
		z -= cameraZ;
		y -= cameraY;
		int pitchSine = Model.SINE[cameraVerticalRotation];
		int pitchCosine = Model.COSINE[cameraVerticalRotation];
		int yawSine = Model.SINE[cameraHorizontalRotation];
		int yawCosine = Model.COSINE[cameraHorizontalRotation];
		int i = y * yawSine + x * yawCosine >> 16;
		y = y * yawCosine - x * yawSine >> 16;
		x = i;
		i = z * pitchCosine - y * pitchSine >> 16;
		y = z * pitchSine + y * pitchCosine >> 16;
		z = i;
		if (y >= 50)
		{
			drawX = Rasterizer3D.center_x + (x << 9) / y;
			drawY = Rasterizer3D.center_y + (z << 9) / y;
		}
		else
		{
			drawX = -1;
			drawY = -1;
		}
	}

	private void infoDump()
	{
		System.out.println("============");
		System.out.println("flame-cycle:" + flameCycle);
		if (onDemandRequester != null)
		{
			System.out.println("Od-cycle:" + onDemandRequester.cycle);
		}
		System.out.println("loop-cycle:" + pulseCycle);
		System.out.println("draw-cycle:" + drawCycle);
		System.out.println("ptype:" + opcode);
		System.out.println("psize:" + packetSize);
		if (gameConnection != null)
		{
			gameConnection.printDebug();
		}
		super.dumpRequested = true;
	}

	private void prepareTitleBackground()
	{
		byte[] abyte0 = titleArchive.getFile("title.dat");
		ImageRGB imageRGB = new ImageRGB(abyte0, this);
		flameLeftBackground.createRasterizer();
		imageRGB.drawInverse(0, 0);
		flameRightBackground.createRasterizer();
		imageRGB.drawInverse(-637, 0);
		aClass18_1198.createRasterizer();
		imageRGB.drawInverse(-128, 0);
		aClass18_1199.createRasterizer();
		imageRGB.drawInverse(-202, -371);
		aClass18_1200.createRasterizer();
		imageRGB.drawInverse(-202, -171);
		aClass18_1203.createRasterizer();
		imageRGB.drawInverse(0, -265);
		aClass18_1204.createRasterizer();
		imageRGB.drawInverse(-562, -265);
		aClass18_1205.createRasterizer();
		imageRGB.drawInverse(-128, -171);
		aClass18_1206.createRasterizer();
		imageRGB.drawInverse(-562, -171);
		int[] ai = new int[imageRGB.width];
		for (int i = 0; i < imageRGB.height; i++)
		{
			for (int j = 0; j < imageRGB.width; j++)
			{
				ai[j] = imageRGB.pixels[(imageRGB.width - j - 1)
					+ imageRGB.width * i];
			}

			for (int l = 0; l < imageRGB.width; l++)
			{
				imageRGB.pixels[l + imageRGB.width * i] = ai[l];
			}

		}

		flameLeftBackground.createRasterizer();
		imageRGB.drawInverse(382, 0);
		flameRightBackground.createRasterizer();
		imageRGB.drawInverse(-255, 0);
		aClass18_1198.createRasterizer();
		imageRGB.drawInverse(254, 0);
		aClass18_1199.createRasterizer();
		imageRGB.drawInverse(180, -371);
		aClass18_1200.createRasterizer();
		imageRGB.drawInverse(180, -171);
		aClass18_1203.createRasterizer();
		imageRGB.drawInverse(382, -265);
		aClass18_1204.createRasterizer();
		imageRGB.drawInverse(-180, -265);
		aClass18_1205.createRasterizer();
		imageRGB.drawInverse(254, -171);
		aClass18_1206.createRasterizer();
		imageRGB.drawInverse(-180, -171);
		imageRGB = new ImageRGB(titleArchive, "logo", 0);
		aClass18_1198.createRasterizer();
		imageRGB.drawImage(382 - imageRGB.width / 2 - 128, 18);
		imageRGB = null;
		abyte0 = null;
		ai = null;
		System.gc();
	}

	private void method140(byte byte0, SpawnObjectNode spawnObjectNode)
	{
		int i = 0;
		int j = -1;
		int k = 0;
		int l = 0;
		if (byte0 != -61)
		{
			outBuffer.putByte(175);
		}
		if (spawnObjectNode.classType == 0)
		{
			i = currentScene.getWallObjectHash(spawnObjectNode.x, spawnObjectNode.y, spawnObjectNode.plane);
		}
		if (spawnObjectNode.classType == 1)
		{
			i = currentScene.getWallDecorationHash(spawnObjectNode.x, spawnObjectNode.plane,
				spawnObjectNode.y);
		}
		if (spawnObjectNode.classType == 2)
		{
			i = currentScene.getLocationHash(spawnObjectNode.plane, spawnObjectNode.x, spawnObjectNode.y);
		}
		if (spawnObjectNode.classType == 3)
		{
			i = currentScene.getFloorDecorationHash(spawnObjectNode.plane, spawnObjectNode.x, spawnObjectNode.y);
		}
		if (i != 0)
		{
			int i1 = currentScene.getArrangement(spawnObjectNode.plane, spawnObjectNode.x, spawnObjectNode.y, i);
			j = i >> 14 & 0x7fff;
			k = i1 & 0x1f;
			l = i1 >> 6;
		}
		spawnObjectNode.index = j;
		spawnObjectNode.type = k;
		spawnObjectNode.rotation = l;
	}

	private void method141()
	{
		currentlyDrawingFlames = false;
		while (aBoolean1320)
		{
			currentlyDrawingFlames = false;
			try
			{
				Thread.sleep(50L);
			}
			catch (Exception _ex)
			{
			}
		}
		titleboxImage = null;
		titleboxButtonImage = null;
		titleFlameEmblem = null;
		anIntArray1310 = null;
		anIntArray1311 = null;
		anIntArray1312 = null;
		anIntArray1313 = null;
		anIntArray1176 = null;
		anIntArray1177 = null;
		anIntArray1084 = null;
		anIntArray1085 = null;
		anImageRGB1226 = null;
		anImageRGB1227 = null;
	}

	private void drawInterface(int i, int j, Widget class13, int k)
	{
		if (class13.type != 0 || class13.children == null)
		{
			return;
		}
		if (class13.hiddenUntilHovered && anInt1302 != class13.id && anInt1280 != class13.id
			&& anInt1106 != class13.id)
		{
			return;
		}
		int i1 = Rasterizer.topX;
		int j1 = Rasterizer.topY;
		int k1 = Rasterizer.bottomX;
		int l1 = Rasterizer.bottomY;
		Rasterizer.setCoordinates(i, j, i + class13.height, j + class13.width);
		int i2 = class13.children.length;
		if (8 != 8)
		{
			opcode = -1;
		}
		for (int j2 = 0; j2 < i2; j2++)
		{
			int k2 = class13.childrenX[j2] + j;
			int l2 = (class13.childrenY[j2] + i) - k;
			Widget child = Widget.forId(class13.children[j2]);
			k2 += child.xOffset;
			l2 += child.yOffset;
			if (child.contentType > 0)
			{
				updateWidget(child);
			}
			if (child.type == 0)
			{
				if (child.scrollPosition > child.scrollLimit - child.height)
				{
					child.scrollPosition = child.scrollLimit - child.height;
				}
				if (child.scrollPosition < 0)
				{
					child.scrollPosition = 0;
				}
				drawInterface(l2, k2, child, child.scrollPosition);
				if (child.scrollLimit > child.height)
				{
					drawScrollBar(true, child.scrollPosition, k2 + child.width, child.height, child.scrollLimit,
						l2);
				}
			}
			else if (child.type != 1)
			{
				if (child.type == 2)
				{
					int i3 = 0;
					for (int i4 = 0; i4 < child.height; i4++)
					{
						for (int j5 = 0; j5 < child.width; j5++)
						{
							int i6 = k2 + j5 * (32 + child.itemSpritePadsX);
							int l6 = l2 + i4 * (32 + child.itemSpritePadsY);
							if (i3 < 20)
							{
								i6 += child.imageX[i3];
								l6 += child.imageY[i3];
							}
							if (child.items[i3] > 0)
							{
								int i7 = 0;
								int j8 = 0;
								int l10 = child.items[i3] - 1;
								if (i6 > Rasterizer.topX - 32 && i6 < Rasterizer.bottomX
									&& l6 > Rasterizer.topY - 32 && l6 < Rasterizer.bottomY
									|| activeInterfaceType != 0 && selectedInventorySlot == i3)
								{
									int k11 = 0;
									if (itemSelected == 1 && anInt1147 == i3 && anInt1148 == child.id)
									{
										k11 = 0xffffff;
									}
									ImageRGB imageRGB = ItemDefinition.sprite(
										l10, child.itemAmounts[i3], k11);
									if (imageRGB != null)
									{
										if (activeInterfaceType != 0 && selectedInventorySlot == i3 && modifiedWidgetId == child.id)
										{
											i7 = super.mouseX - anInt1114;
											j8 = super.mouseY - anInt1115;
											if (i7 < 5 && i7 > -5)
											{
												i7 = 0;
											}
											if (j8 < 5 && j8 > -5)
											{
												j8 = 0;
											}
											if (lastItemDragTime < 5)
											{
												i7 = 0;
												j8 = 0;
											}
											imageRGB.drawImageAlpha(i6 + i7, l6 + j8, 128);
											if (l6 + j8 < Rasterizer.topY && class13.scrollPosition > 0)
											{
												int i12 = (tickDelta * (Rasterizer.topY - l6 - j8)) / 3;
												if (i12 > tickDelta * 10)
												{
													i12 = tickDelta * 10;
												}
												if (i12 > class13.scrollPosition)
												{
													i12 = class13.scrollPosition;
												}
												class13.scrollPosition -= i12;
												anInt1115 += i12;
											}
											if (l6 + j8 + 32 > Rasterizer.bottomY
												&& class13.scrollPosition < class13.scrollLimit - class13.height)
											{
												int j12 = (tickDelta * ((l6 + j8 + 32) - Rasterizer.bottomY)) / 3;
												if (j12 > tickDelta * 10)
												{
													j12 = tickDelta * 10;
												}
												if (j12 > class13.scrollLimit - class13.height - class13.scrollPosition)
												{
													j12 = class13.scrollLimit - class13.height - class13.scrollPosition;
												}
												class13.scrollPosition += j12;
												anInt1115 -= j12;
											}
										}
										else if (atInventoryInterfaceType != 0 && anInt1331 == i3 && anInt1330 == child.id)
										{
											imageRGB.drawImageAlpha(i6, l6, 128);
										}
										else
										{
											imageRGB.drawImage(i6, l6);
										}
										if (imageRGB.maxWidth == 33 || child.itemAmounts[i3] != 1)
										{
											int k12 = child.itemAmounts[i3];
											fontSmall.drawString(getShortenedAmountText(k12), i6 + 1 + i7, l6 + 10 + j8, 0
											);
											fontSmall.drawString(getShortenedAmountText(k12), i6 + i7, l6 + 9 + j8, 0xffff00
											);
										}
									}
								}
							}
							else if (child.images != null && i3 < 20)
							{
								ImageRGB imageRGB = child.images[i3];
								if (imageRGB != null)
								{
									imageRGB.drawImage(i6, l6);
								}
							}
							i3++;
						}

					}

				}
				else if (child.type == 3)
				{
					boolean flag = false;
					if (anInt1106 == child.id || anInt1280 == child.id
						|| anInt1302 == child.id)
					{
						flag = true;
					}
					int j3;
					if (componentEnabled(child))
					{
						j3 = child.enabledColor;
						if (flag && child.enabledHoveredColor != 0)
						{
							j3 = child.enabledHoveredColor;
						}
					}
					else
					{
						j3 = child.disabledColor;
						if (flag && child.disabledHoveredColor != 0)
						{
							j3 = child.disabledHoveredColor;
						}
					}
					if (child.alpha == 0)
					{
						if (child.filled)
						{
							Rasterizer.drawFilledRectangle(k2, l2, child.width, child.height, j3);
						}
						else
						{
							Rasterizer.drawUnfilledRectangle(k2, l2, child.width, child.height, j3);
						}
					}
					else if (child.filled)
					{
						Rasterizer.drawFilledRectangleAlpha(k2, l2, child.width, child.height, j3,
							256 - (child.alpha & 0xff));
					}
					else
					{
						Rasterizer.drawUnfilledRectangleAlpha(k2, l2, child.width, child.height, j3,
							256 - (child.alpha & 0xff));
					}
				}
				else if (child.type == 4)
				{
					TypeFace typeFace = child.typeFaces;
					String s = child.disabledText;
					boolean flag1 = false;
					if (anInt1106 == child.id || anInt1280 == child.id
						|| anInt1302 == child.id)
					{
						flag1 = true;
					}
					int j4;
					if (componentEnabled(child))
					{
						j4 = child.enabledColor;
						if (flag1 && child.enabledHoveredColor != 0)
						{
							j4 = child.enabledHoveredColor;
						}
						if (child.enabledText.length() > 0)
						{
							s = child.enabledText;
						}
					}
					else
					{
						j4 = child.disabledColor;
						if (flag1 && child.disabledHoveredColor != 0)
						{
							j4 = child.disabledHoveredColor;
						}
					}
					if (child.actionType == 6 && aBoolean1239)
					{
						s = "Please wait...";
						j4 = child.disabledColor;
					}
					if (Rasterizer.width == 479)
					{
						if (j4 == 0xffff00)
						{
							j4 = 255;
						}
						if (j4 == 49152)
						{
							j4 = 0xffffff;
						}
					}
					for (int j7 = l2 + typeFace.characterDefaultHeight; s.length() > 0; j7 += typeFace.characterDefaultHeight)
					{
						if (s.indexOf("%") != -1)
						{
							do
							{
								int k8 = s.indexOf("%1");
								if (k8 == -1)
								{
									break;
								}
								s = s.substring(0, k8) + method89(parseCS1(child, 0), 8) + s.substring(k8 + 2);
							} while (true);
							do
							{
								int l8 = s.indexOf("%2");
								if (l8 == -1)
								{
									break;
								}
								s = s.substring(0, l8) + method89(parseCS1(child, 1), 8) + s.substring(l8 + 2);
							} while (true);
							do
							{
								int i9 = s.indexOf("%3");
								if (i9 == -1)
								{
									break;
								}
								s = s.substring(0, i9) + method89(parseCS1(child, 2), 8) + s.substring(i9 + 2);
							} while (true);
							do
							{
								int j9 = s.indexOf("%4");
								if (j9 == -1)
								{
									break;
								}
								s = s.substring(0, j9) + method89(parseCS1(child, 3), 8) + s.substring(j9 + 2);
							} while (true);
							do
							{
								int k9 = s.indexOf("%5");
								if (k9 == -1)
								{
									break;
								}
								s = s.substring(0, k9) + method89(parseCS1(child, 4), 8) + s.substring(k9 + 2);
							} while (true);
						}
						int l9 = s.indexOf("\\n");
						String s3;
						if (l9 != -1)
						{
							s3 = s.substring(0, l9);
							s = s.substring(l9 + 2);
						}
						else
						{
							s3 = s;
							s = "";
						}
						if (child.typeFaceCentered)
						{
							typeFace.drawStringCenter(s3, k2
								+ child.width / 2, j7, j4, child.typeFaceShadowed);
						}
						else
						{
							typeFace.drawShadowedString(s3, k2, j7, child.typeFaceShadowed, j4);
						}
					}

				}
				else if (child.type == 5)
				{
					ImageRGB imageRGB;
					if (componentEnabled(child))
					{
						imageRGB = child.enabledImage;
					}
					else
					{
						imageRGB = child.disabledImage;
					}
					if (Configuration.FREE_TELEPORTS)
					{

						switch (child.id)
						{
							case 1164:
							case 1167:
							case 1170:
							case 1174:
							case 1540:
							case 1541:
							case 7455:
								imageRGB = child.enabledImage;
								break;
							default:
								break;
						}
					}

					if (imageRGB != null)
					{
						imageRGB.drawImage(k2, l2);
					}
				}
				else if (child.type == 6)
				{
					int k3 = Rasterizer3D.center_x;
					int k4 = Rasterizer3D.center_y;
					Rasterizer3D.center_x = k2 + child.width / 2;
					Rasterizer3D.center_y = l2 + child.height / 2;
					int k5 = Rasterizer3D.SINE[child.rotationX] * child.zoom >> 16;
					int j6 = Rasterizer3D.COSINE[child.rotationX] * child.zoom >> 16;
					boolean flag2 = componentEnabled(child);
					int k7;
					if (flag2)
					{
						k7 = child.enabledAnimation;
					}
					else
					{
						k7 = child.disabledAnimation;
					}
					Model model;
					if (k7 == -1)
					{
						model = child.getAnimatedModel(-1, -1, flag2);
					}
					else
					{
						AnimationSequence class14 = AnimationSequence.animations[k7];
						model = child.getAnimatedModel(class14.frame1Ids[child.animationFrame], class14.getPrimaryFrame[child.animationFrame],
							flag2);
					}
					if (model != null)
					{
						model.render(0, child.rotationY, 0, child.rotationX, 0, k5, j6);
					}
					Rasterizer3D.center_x = k3;
					Rasterizer3D.center_y = k4;
				}
				else
				{
					if (child.type == 7)
					{
						TypeFace typeFace = child.typeFaces;
						int l4 = 0;
						for (int l5 = 0; l5 < child.height; l5++)
						{
							for (int k6 = 0; k6 < child.width; k6++)
							{
								if (child.items[l4] > 0)
								{
									ItemDefinition class16 = ItemDefinition.lookup(child.items[l4] - 1);
									String s6 = String.valueOf(class16.name);
									if (class16.stackable || child.itemAmounts[l4] != 1)
									{
										s6 = s6 + " x" + getFullAmountText(child.itemAmounts[l4]);
									}
									int i10 = k2 + k6 * (115 + child.itemSpritePadsX);
									int i11 = l2 + l5 * (12 + child.itemSpritePadsY);
									if (child.typeFaceCentered)
									{
										typeFace.drawStringCenter(s6, i10 + child.width / 2, i11, child.disabledColor, child.typeFaceShadowed
										);
									}
									else
									{
										typeFace.drawShadowedString(s6, i10, i11, child.typeFaceShadowed, child.disabledColor
										);
									}
								}
								l4++;
							}

						}

					}
					if (child.type == 8
						&& (anInt1284 == child.id || anInt1044 == child.id || anInt1129 == child.id)
						&& anInt893 == 100)
					{
						int l3 = 0;
						int i5 = 0;
						TypeFace typeFace = fontNormal;
						for (String s1 = child.disabledText; s1.length() > 0; )
						{
							int l7 = s1.indexOf("\\n");
							String s4;
							if (l7 != -1)
							{
								s4 = s1.substring(0, l7);
								s1 = s1.substring(l7 + 2);
							}
							else
							{
								s4 = s1;
								s1 = "";
							}
							int j10 = typeFace.getStringEffectWidth(s4);
							if (j10 > l3)
							{
								l3 = j10;
							}
							i5 += typeFace.characterDefaultHeight + 1;
						}

						l3 += 6;
						i5 += 7;
						int i8 = (k2 + child.width) - 5 - l3;
						int k10 = l2 + child.height + 5;
						if (i8 < k2 + 5)
						{
							i8 = k2 + 5;
						}
						if (i8 + l3 > j + class13.width)
						{
							i8 = (j + class13.width) - l3;
						}
						if (k10 + i5 > i + class13.height)
						{
							k10 = (i + class13.height) - i5;
						}
						Rasterizer.drawFilledRectangle(i8, k10, l3, i5, 0xffffa0);
						Rasterizer.drawUnfilledRectangle(i8, k10, l3, i5, 0);
						String s2 = child.disabledText;
						for (int j11 = k10 + typeFace.characterDefaultHeight + 2; s2.length() > 0; j11 += typeFace.characterDefaultHeight + 1)
						{
							int l11 = s2.indexOf("\\n");
							String s5;
							if (l11 != -1)
							{
								s5 = s2.substring(0, l11);
								s2 = s2.substring(l11 + 2);
							}
							else
							{
								s5 = s2;
								s2 = "";
							}
							typeFace.drawShadowedString(s5, i8 + 3, j11, false, 0);
						}

					}
				}
			}
		}

		Rasterizer.setCoordinates(j1, i1, l1, k1);
	}

	private void loadingStages()
	{
		if (lowMemory && loadingStage == 2 && MapRegion.onBuildTimePlane != plane)
		{
			method125(null, "Loading - please wait.");
			loadingStage = 1;
			loadRegionTime = System.currentTimeMillis();
		}
		if (loadingStage == 1)
		{
			int loadingStages = initialiseRegionLoading();
			if (loadingStages != 0 && System.currentTimeMillis() - loadRegionTime > 0x57e40L)
			{
				SignLink.reportError(username + " glcfb " + serverSeed + "," + loadingStages + "," + lowMemory + ","
					+ stores[0] + "," + onDemandRequester.method333() + "," + plane + ","
					+ chunkX + "," + chunkY);
				loadRegionTime = System.currentTimeMillis();
			}
		}
		if (loadingStage == 2 && plane != lastRegionId)
		{
			lastRegionId = plane;
			renderViewport(plane);
		}
	}

	private int initialiseRegionLoading()
	{
		for (int t = 0; t < terrainData.length; t++)
		{
			if (terrainData[t] == null && terrainDataIds[t] != -1)
			{
				return -1;
			}
			if (objectData[t] == null && objectDataIds[t] != -1)
			{
				return -2;
			}
		}

		boolean regionsCached = true;
		for (int region = 0; region < terrainData.length; region++)
		{
			byte[] objects = objectData[region];
			if (objects != null)
			{
				int blockX = (mapCoordinates[region] >> 8) * 64 - nextTopLeftTileX;
				int blockY = (mapCoordinates[region] & 0xff) * 64 - nextTopRightTileY;
				if (loadGeneratedMap)
				{
					blockX = 10;
					blockY = 10;
				}
				regionsCached &= MapRegion.regionCached(blockX, blockY, objects);
			}
		}

		if (!regionsCached)
		{
			return -3;
		}
		if (loadingMap)
		{
			return -4;
		}
		else
		{
			loadingStage = 2;
			MapRegion.onBuildTimePlane = plane;
			loadRegion();
			outBuffer.putOpcode(6);
			return 0;
		}
	}

	private void createObjectSpawnRequest(int plane, int x, int orientation, int duration, int objectType, int objectId, int startDelay, int type, int y)
	{
		SpawnObjectNode request = null;
		for (SpawnObjectNode request2 = (SpawnObjectNode) spawnObjectList.first(); request2 != null; request2 = (SpawnObjectNode) spawnObjectList
			.next())
		{
			if (request2.plane != plane || request2.x != x || request2.y != y
				|| request2.classType != type)
			{
				continue;
			}
			request = request2;
			break;
		}

		if (request == null)
		{
			request = new SpawnObjectNode();
			request.plane = plane;
			request.classType = type;
			request.x = x;
			request.y = y;
			method140((byte) -61, request);
			spawnObjectList.pushBack(request);
		}
		request.locationIndex = objectId;
		request.locationType = objectType;
		request.locationRotation = orientation;
		request.spawnCycle = startDelay;
		request.cycle = duration;
	}

	private void method146(byte byte0)
	{
		if (byte0 != 4)
		{
			return;
		}
		if (minimapState != 0)
		{
			return;
		}
		if (super.clickType == 1)
		{
			int i = super.clickX - 25 - 550;
			int j = super.clickY - 5 - 4;
			if (i >= 0 && j >= 0 && i < 146 && j < 151)
			{
				i -= 73;
				j -= 75;
				int k = cameraHorizontal + cameraYawOffset & 0x7ff;
				int l = Rasterizer3D.SINE[k];
				int i1 = Rasterizer3D.COSINE[k];
				l = l * (mapZoomOffset + 256) >> 8;
				i1 = i1 * (mapZoomOffset + 256) >> 8;
				int j1 = j * l + i * i1 >> 11;
				int k1 = j * i1 - i * l >> 11;
				int l1 = localPlayer.worldX + j1 >> 7;
				int i2 = localPlayer.worldY - k1 >> 7;
				boolean flag = walk(true, false, i2, localPlayer.pathY[0], 0, 0, 1, 0, l1,
					0, 0, localPlayer.pathX[0]);
				if (flag)
				{
					outBuffer.putByte(i);
					outBuffer.putByte(j);
					outBuffer.putShortBE(cameraHorizontal);
					outBuffer.putByte(57);
					outBuffer.putByte(cameraYawOffset);
					outBuffer.putByte(mapZoomOffset);
					outBuffer.putByte(89);
					outBuffer.putShortBE(localPlayer.worldX);
					outBuffer.putShortBE(localPlayer.worldY);
					outBuffer.putByte(anInt1126);
					outBuffer.putByte(63);
				}
			}
		}
	}

	private void resetAllImageProducers()
	{
		if (super.imageProducer != null)
		{
			return;
		}
		method141();
		aClass18_1198 = null;
		aClass18_1199 = null;
		aClass18_1200 = null;
		flameLeftBackground = null;
		flameRightBackground = null;
		aClass18_1203 = null;
		aClass18_1204 = null;
		aClass18_1205 = null;
		aClass18_1206 = null;
		chatboxProducingGraphicsBuffer = null;
		aClass18_1157 = null;
		tabImageProducer = null;
		gameScreenImageProducer = null;
		aClass18_1108 = null;
		aClass18_1109 = null;
		aClass18_1110 = null;
		super.imageProducer = new ProducingGraphicsBuffer(765, 503, getParentComponent());
		welcomeScreenRaised = true;
	}

	private boolean hasFriend(String s)
	{
		if (s == null)
		{
			return false;
		}
		for (int j = 0; j < friendsCount; j++)
		{
			if (s.equalsIgnoreCase(friendUsernames[j]))
			{
				return true;
			}
		}

		return s.equalsIgnoreCase(localPlayer.playerName);
	}

	private void updateLogin()
	{
		if (loginScreenState == 0)
		{
			int x = super.width / 2 - 80;
			int y = super.height / 2 + 20;
			y += 20;
			if (super.clickType == 1 && super.clickX >= x - 75 && super.clickX <= x + 75 && super.clickY >= y - 20
				&& super.clickY <= y + 20)
			{
				loginScreenState = 3;
				loginScreenFocus = 0;
			}
			x = super.width / 2 + 80;
			if (super.clickType == 1 && super.clickX >= x - 75 && super.clickX <= x + 75 && super.clickY >= y - 20
				&& super.clickY <= y + 20)
			{
				statusLineOne = "";
				statusLineTwo = "Enter your username & password.";
				loginScreenState = 2;
				loginScreenFocus = 0;
			}
		}
		else
		{
			if (loginScreenState == 2)
			{
				int y = super.height / 2 - 40;
				y += 30;
				y += 25;
				if (super.clickType == 1 && super.clickY >= y - 15 && super.clickY < y)
				{
					loginScreenFocus = 0;
				}
				y += 15;
				if (super.clickType == 1 && super.clickY >= y - 15 && super.clickY < y)
				{
					loginScreenFocus = 1;
				}
				y += 15;
				int j1 = super.width / 2 - 80;
				int l1 = super.height / 2 + 50;
				l1 += 20;
				if (super.clickType == 1 && super.clickX >= j1 - 75 && super.clickX <= j1 + 75
					&& super.clickY >= l1 - 20 && super.clickY <= l1 + 20)
				{
					reconnectionAttempts = 0;
					login(username, password, false);
					if (loggedIn)
					{
						return;
					}
				}
				j1 = super.width / 2 + 80;
				if (super.clickType == 1 && super.clickX >= j1 - 75 && super.clickX <= j1 + 75
					&& super.clickY >= l1 - 20 && super.clickY <= l1 + 20)
				{
					loginScreenState = 0;
					if (USE_STATIC_DETAILS)
					{
						username = USERNAME;
						password = PASSWORD;
					}
					else
					{
						username = "";
						password = "";
					}
				}
				do
				{
					int character = readCharacter();
					if (character == -1)
					{
						break;
					}
					boolean validCharacter = false;
					for (int c = 0; c < VALID_CHARACTERS.length(); c++)
					{
						if (character != VALID_CHARACTERS.charAt(c))
						{
							continue;
						}
						validCharacter = true;
						break;
					}

					if (loginScreenFocus == 0)
					{
						if (character == 8 && username.length() > 0)
						{
							username = username.substring(0, username.length() - 1);
						}
						if (character == 9 || character == 10 || character == 13)
						{
							loginScreenFocus = 1;
						}
						if (validCharacter)
						{
							username += (char) character;
						}
						if (username.length() > 12)
						{
							username = username.substring(0, 12);
						}
					}
					else if (loginScreenFocus == 1)
					{
						if (character == 8 && password.length() > 0)
						{
							password = password.substring(0, password.length() - 1);
						}
						if (character == 9 || character == 10 || character == 13)
						{
							loginScreenFocus = 0;
						}
						if (validCharacter)
						{
							password += (char) character;
						}
						if (password.length() > 20)
						{
							password = password.substring(0, 20);
						}
					}
				} while (true);
				return;
			}
			if (loginScreenState == 3)
			{
				int x = super.width / 2;
				int y = super.height / 2 + 50;
				y += 20;
				if (super.clickType == 1 && super.clickX >= x - 75 && super.clickX <= x + 75
					&& super.clickY >= y - 20 && super.clickY <= y + 20)
				{
					loginScreenState = 0;
				}
			}
		}
	}

	private void method150(int i, int j, int k, int l, int i1, int j1)
	{
		int k1 = currentScene.getWallObjectHash(k, i, j);
		i1 = 62 / i1;
		if (k1 != 0)
		{
			int l1 = currentScene.getArrangement(j, k, i, k1);
			int k2 = l1 >> 6 & 3;
			int i3 = l1 & 0x1f;
			int k3 = j1;
			if (k1 > 0)
			{
				k3 = l;
			}
			int[] ai = minimapImage.pixels;
			int k4 = 24624 + k * 4 + (103 - i) * 512 * 4;
			int i5 = k1 >> 14 & 0x7fff;
			GameObjectDefinition gameObjectDefinition = GameObjectDefinition.getDefinition(i5);
			if (gameObjectDefinition.anInt795 != -1)
			{
				IndexedImage indexedImage = mapIcons[gameObjectDefinition.anInt795];
				if (indexedImage != null)
				{
					int i6 = (gameObjectDefinition.sizeX * 4 - indexedImage.imgWidth) / 2;
					int j6 = (gameObjectDefinition.sizeY * 4 - indexedImage.height) / 2;
					indexedImage.drawImage(48 + k * 4 + i6, 48 + (104 - i - gameObjectDefinition.sizeY) * 4 + j6
					);
				}
			}
			else
			{
				if (i3 == 0 || i3 == 2)
				{
					if (k2 == 0)
					{
						ai[k4] = k3;
						ai[k4 + 512] = k3;
						ai[k4 + 1024] = k3;
						ai[k4 + 1536] = k3;
					}
					else if (k2 == 1)
					{
						ai[k4] = k3;
						ai[k4 + 1] = k3;
						ai[k4 + 2] = k3;
						ai[k4 + 3] = k3;
					}
					else if (k2 == 2)
					{
						ai[k4 + 3] = k3;
						ai[k4 + 3 + 512] = k3;
						ai[k4 + 3 + 1024] = k3;
						ai[k4 + 3 + 1536] = k3;
					}
					else if (k2 == 3)
					{
						ai[k4 + 1536] = k3;
						ai[k4 + 1536 + 1] = k3;
						ai[k4 + 1536 + 2] = k3;
						ai[k4 + 1536 + 3] = k3;
					}
				}
				if (i3 == 3)
				{
					if (k2 == 0)
					{
						ai[k4] = k3;
					}
					else if (k2 == 1)
					{
						ai[k4 + 3] = k3;
					}
					else if (k2 == 2)
					{
						ai[k4 + 3 + 1536] = k3;
					}
					else if (k2 == 3)
					{
						ai[k4 + 1536] = k3;
					}
				}
				if (i3 == 2)
				{
					if (k2 == 3)
					{
						ai[k4] = k3;
						ai[k4 + 512] = k3;
						ai[k4 + 1024] = k3;
						ai[k4 + 1536] = k3;
					}
					else if (k2 == 0)
					{
						ai[k4] = k3;
						ai[k4 + 1] = k3;
						ai[k4 + 2] = k3;
						ai[k4 + 3] = k3;
					}
					else if (k2 == 1)
					{
						ai[k4 + 3] = k3;
						ai[k4 + 3 + 512] = k3;
						ai[k4 + 3 + 1024] = k3;
						ai[k4 + 3 + 1536] = k3;
					}
					else if (k2 == 2)
					{
						ai[k4 + 1536] = k3;
						ai[k4 + 1536 + 1] = k3;
						ai[k4 + 1536 + 2] = k3;
						ai[k4 + 1536 + 3] = k3;
					}
				}
			}
		}
		k1 = currentScene.getLocationHash(j, k, i);
		if (k1 != 0)
		{
			int i2 = currentScene.getArrangement(j, k, i, k1);
			int l2 = i2 >> 6 & 3;
			int j3 = i2 & 0x1f;
			int l3 = k1 >> 14 & 0x7fff;
			GameObjectDefinition gameObjectDefinition = GameObjectDefinition.getDefinition(l3);
			if (gameObjectDefinition.anInt795 != -1)
			{
				IndexedImage indexedImage = mapIcons[gameObjectDefinition.anInt795];
				if (indexedImage != null)
				{
					int j5 = (gameObjectDefinition.sizeX * 4 - indexedImage.imgWidth) / 2;
					int k5 = (gameObjectDefinition.sizeY * 4 - indexedImage.height) / 2;
					indexedImage.drawImage(48 + k * 4 + j5, 48 + (104 - i - gameObjectDefinition.sizeY) * 4 + k5
					);
				}
			}
			else if (j3 == 9)
			{
				int l4 = 0xeeeeee;
				if (k1 > 0)
				{
					l4 = 0xee0000;
				}
				int[] ai1 = minimapImage.pixels;
				int l5 = 24624 + k * 4 + (103 - i) * 512 * 4;
				if (l2 == 0 || l2 == 2)
				{
					ai1[l5 + 1536] = l4;
					ai1[l5 + 1024 + 1] = l4;
					ai1[l5 + 512 + 2] = l4;
					ai1[l5 + 3] = l4;
				}
				else
				{
					ai1[l5] = l4;
					ai1[l5 + 512 + 1] = l4;
					ai1[l5 + 1024 + 2] = l4;
					ai1[l5 + 1536 + 3] = l4;
				}
			}
		}
		k1 = currentScene.getFloorDecorationHash(j, k, i);
		if (k1 != 0)
		{
			int j2 = k1 >> 14 & 0x7fff;
			GameObjectDefinition class47 = GameObjectDefinition.getDefinition(j2);
			if (class47.anInt795 != -1)
			{
				IndexedImage indexedImage = mapIcons[class47.anInt795];
				if (indexedImage != null)
				{
					int i4 = (class47.sizeX * 4 - indexedImage.imgWidth) / 2;
					int j4 = (class47.sizeY * 4 - indexedImage.height) / 2;
					indexedImage.drawImage(48 + k * 4 + i4, 48 + (104 - i - class47.sizeY) * 4 + j4);
				}
			}
		}
	}

	private void renderGameView()
	{
		this.renderCount++;
		processPlayerAdditions(true);
		renderNPCs(true);
		processPlayerAdditions(false);
		renderNPCs(false);
		renderProjectiles();
		renderStationaryGraphics();
		if (!cutsceneActive)
		{
			int vertical = cameraVertical;
			if (secondaryCameraVertical / 256 > vertical)
			{
				vertical = secondaryCameraVertical / 256;
			}
			if (customCameraActive[4] && cameraAmplitude[4] + 128 > vertical)
			{
				vertical = cameraAmplitude[4] + 128;
			}
			int horizontal = cameraHorizontal + cameraRandomisationA & 0x7ff;
			setCameraPosition(currentCameraPositionH, currentCameraPositionV, getFloorDrawHeight(plane, localPlayer.worldX, localPlayer.worldY
			) - 50, vertical, horizontal);
		}
		int cameraPlane;
		if (!cutsceneActive)
		{
			cameraPlane = getWorldDrawPlane();
		}
		else
		{
			cameraPlane = getCameraPlaneCutscene();
		}
		int x = cameraX;
		int y = cameraY;
		int z = cameraZ;
		int l1 = cameraVerticalRotation;
		int i2 = cameraHorizontalRotation;
		for (int i = 0; i < 5; i++)
		{
			if (customCameraActive[i])
			{
				int randomisation = (int) ((Math.random() * (double) (cameraJitter[i] * 2 + 1) - (double) cameraJitter[i]) + Math
					.sin((double) quakeTimes[i] * ((double) cameraFrequency[i] / 100D))
					* (double) cameraAmplitude[i]);
				if (i == 0)
				{
					cameraX += randomisation;
				}
				if (i == 1)
				{
					cameraZ += randomisation;
				}
				if (i == 2)
				{
					cameraY += randomisation;
				}
				if (i == 3)
				{
					cameraHorizontalRotation = cameraHorizontalRotation + randomisation & 0x7ff;
				}
				if (i == 4)
				{
					cameraVerticalRotation += randomisation;
					if (cameraVerticalRotation < 128)
					{
						cameraVerticalRotation = 128;
					}
					if (cameraVerticalRotation > 383)
					{
						cameraVerticalRotation = 383;
					}
				}
			}
		}

		int textureId = Rasterizer3D.textureGetCount;
		Model.gameScreenClickable = true;
		Model.resourceCount = 0;
		Model.cursorX = super.mouseX - 4;
		Model.cursorY = super.mouseY - 4;
		Rasterizer.resetPixels();
		currentScene.render(cameraX, cameraPlane, 0, cameraZ, cameraY, cameraHorizontalRotation, cameraVerticalRotation);
		currentScene.clearInteractiveObjectCache();
		drawScene2d(false);
		drawMarker();
		animateTexture(textureId);
		draw3dScreen();
		gameScreenImageProducer.drawGraphics(4, 4, super.gameGraphics);
		cameraX = x;
		cameraZ = z;
		cameraY = y;
		cameraVerticalRotation = l1;
		cameraHorizontalRotation = i2;
	}

	private void processAudio()
	{
		for (int index = 0; index < currentSound; index++)
		{
			//if (soundDelay[index] <= 0) {
			boolean played = false;
			try
			{
				Buffer stream = SoundTrack.data(sound[index], soundType[index]);
				new SoundPlayer(new ByteArrayInputStream(stream.buffer, 0, stream.currentPosition), soundType[index], soundDelay[index]);
				if (System.currentTimeMillis() + (long) (stream.currentPosition / 22) > lastSoundTime
					+ (long) (lastSoundPosition / 22))
				{
					lastSoundPosition = stream.currentPosition;
					lastSoundTime = System.currentTimeMillis();
					if (method116(stream.currentPosition, stream.buffer))
					{
						lastSound = sound[index];
						lastSoundType = soundType[index];
					}
					else
					{
						played = true;
					}

				}
			}
			catch (Exception exception)
			{
				if (SignLink.reportError)
				{
					outBuffer.putOpcode(80);
					outBuffer.putShortBE(sound[index] & 0x7fff);
				}
				else
				{
					outBuffer.putOpcode(80);
					outBuffer.putShortBE(-1);
				}
			}
			if (!played || soundDelay[index] == -5)
			{
				currentSound--;
				for (int j = index; j < currentSound; j++)
				{
					sound[j] = sound[j + 1];
					soundType[j] = soundType[j + 1];
					soundDelay[j] = soundDelay[j + 1];
					soundVolume[j] = soundVolume[j + 1];
				}

				index--;
			}
			else
			{
				soundDelay[index] = -5;
			}
			/*} else {
				soundDelay[index]--;
			}*/
		}
		if (previousSong > 0)
		{
			previousSong -= 20;
			if (previousSong < 0)
			{
				previousSong = 0;
			}
			if (previousSong == 0 && musicEnabled && !lowMemory)
			{
				nextSong = currentSong;
				songChanging = true;
				onDemandRequester.request(2, nextSong);
			}
		}
	}

}
