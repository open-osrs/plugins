package renderer.world;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import renderer.model.AnimationDefinition;
import renderer.model.ModelDefinition;

public class ObjectDefinition
{
	public int id;
	public String name = null;
	public String[] actions = new String[5];
	public int sizeX = 1;
	public int sizeY = 1;
	public int decorationOffset = 16;

	/////////////////////////////////////////////
	//            Model Properties             //
	/////////////////////////////////////////////
	public EnumMap<LocationType, ModelDefinition> typeModels;
	public List<ModelDefinition> models;
	public int offsetX = 0;
	public int offsetY = 0;
	public int offsetZ = 0;
	public int scaleX = 128;
	public int scaleY = 128;
	public int scaleZ = 128;
	public boolean mirror = false;
	public boolean mergeNormals = false; // TODO: this field seems to be named wrong
	public Int2IntMap colorSubstitutions = new Int2IntArrayMap();
	public Int2IntMap textureSubstitutions = new Int2IntArrayMap();
	public int ambient = 0;
	public int contrast = 0;
	public boolean shadow = true;
	public AnimationDefinition animation = null;

	public boolean hollow = false;

	public int mapAreaId = -1;
	public int mapSceneID = -1;

	public int wall = -1;

	public int ambientSound = -1;
	public List<Integer> ambientSounds;
	public int ambientSoundRadius = 0;
	public int ambientSoundMinLoopTime = 0;
	public int ambientSoundMaxLoopTime = 0;

	public int interactType = 2;
	public int blockingMask = 0;
	public boolean obstructsGround = false;
	public int contouredGround = -1;
	public int supportsItems = -1;
	public int[] configChangeDest;
	public boolean blocksProjectile = true;
	public Map<Integer, Object> params = null;

	public int varbit = -1;
	public int varp = -1;

	public boolean unknown1 = false;
}
