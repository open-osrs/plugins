package renderer.cache;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.ref.SoftReference;
import java.util.Optional;
import java.util.function.Supplier;
import renderer.loader.ModelLoader;
import renderer.loader.ObjectLoader;
import renderer.loader.OverlayLoader;
import renderer.loader.RegionLoader;
import renderer.loader.SequenceLoader;
import renderer.loader.TextureLoader;
import renderer.loader.TransformLoader;
import renderer.loader.UnderlayLoader;
import renderer.model.AnimationDefinition;
import renderer.model.ModelDefinition;
import renderer.model.TextureDefinition;
import renderer.model.TransformDefinition;
import renderer.util.FastIntMap;
import renderer.world.ObjectDefinition;
import renderer.world.OverlayDefinition;
import renderer.world.Region;
import renderer.world.UnderlayDefinition;

public class CacheSystem
{
	public static final GameCache CACHE = new GameCache();
	private static final Int2ObjectMap<SoftReference<ObjectDefinition>> objectDefinitions = new FastIntMap<>();
	private static final Int2ObjectMap<SoftReference<UnderlayDefinition>> underlayDefinitions = new FastIntMap<>();
	private static final Int2ObjectMap<SoftReference<OverlayDefinition>> overlayDefinitions = new FastIntMap<>();
	private static final Int2ObjectMap<SoftReference<TextureDefinition>> textureDefinitions = new FastIntMap<>();
	private static final Int2ObjectMap<SoftReference<ModelDefinition>> modelDefinitions = new FastIntMap<>();
	private static final Int2ObjectMap<SoftReference<AnimationDefinition>> animationDefinitions = new FastIntMap<>();
	private static final Int2ObjectMap<SoftReference<TransformDefinition>> transformDefinitions = new Int2ObjectOpenHashMap<>();
	private static final Int2ObjectMap<SoftReference<TransformLoader.SkeletonDefinition>> skeletonDefinitions = new FastIntMap<>();
	private static final SoftReference<Optional<Region>>[] regions = new SoftReference[256 * 256];

	public static ObjectDefinition getObjectDefinition(int id)
	{
		return get(objectDefinitions, id, () -> ObjectLoader.load(id, CACHE.get(2, 6, id)));
	}

	public static UnderlayDefinition getUnderlayDefinition(int id)
	{
		return get(underlayDefinitions, id, () -> UnderlayLoader.load(id, CACHE.get(2, 1, id)));
	}

	public static ModelDefinition getModelDefinition(int id)
	{
		return get(modelDefinitions, id, () -> ModelLoader.load(id, CACHE.get(7, id, 0)));
	}

	public static OverlayDefinition getOverlayDefinition(int id)
	{
		return get(overlayDefinitions, id, () -> OverlayLoader.load(id, CACHE.get(2, 4, id)));
	}

	public static TextureDefinition getTextureDefinition(int id)
	{
		return get(textureDefinitions, id, () -> TextureLoader.load(id, CACHE.get(9, 0, id)));
	}

	public static AnimationDefinition getAnimationDefinition(int id)
	{
		return get(animationDefinitions, id, () -> SequenceLoader.load(id, CACHE.get(2, 12, id)));
	}

	public static TransformDefinition getTransformDefiniton(int id)
	{
		return get(transformDefinitions, id, () -> TransformLoader.loadTransform(id, CACHE.get(0, id >> 16, id & 0xffff)));
	}

	public static TransformLoader.SkeletonDefinition getSkeletonDefinition(int id)
	{
		return get(skeletonDefinitions, id, () -> TransformLoader.loadSkeleton(id, CACHE.get(1, id, 0)));
	}

	public static Region loadRegion(int x, int y)
	{
		Region region = new Region(x, y);

		byte[] terrain = null;
		byte[] locations = null;

		try
		{
			terrain = CACHE.archive(5).group("m" + x + "_" + y).file(0);
			locations = CACHE.archive(5).group("l" + x + "_" + y).file(0);
		}
		catch (Exception e)
		{
			System.err.println("Couldn't load region (" + x + ", " + y + ")");
		}

		if (terrain == null)
		{
			return null;
		}

		region.loadTerrain(RegionLoader.readTerrain(terrain));

		if (locations != null)
		{
			region.loadLocations(RegionLoader.loadLocations(locations));
		}

		return region;
	}

	public static Region region(int regionX, int regionY)
	{
		SoftReference<Optional<Region>> ref = regions[regionX * 256 + regionY];
		Optional<Region> region = ref == null ? null : ref.get();

		if (region == null)
		{
			synchronized (CacheSystem.class)
			{
				ref = regions[regionX * 256 + regionY];
				region = ref == null ? null : ref.get();

				if (region == null)
				{
					region = Optional.ofNullable(loadRegion(regionX, regionY));
					regions[regionX * 256 + regionY] = new SoftReference<>(region);
				}
			}
		}

		return region.orElse(null);
	}

	private static <T> T get(Int2ObjectMap<SoftReference<T>> cache, int id, Supplier<T> supplier)
	{
		SoftReference<T> ref = cache.get(id);

		if (ref == null)
		{
			T t = supplier.get();
			cache.put(id, new SoftReference<>(t));
			return t;
		}

		T t = ref.get();

		if (t == null)
		{
			t = supplier.get();
			ref = new SoftReference<>(t);
			cache.put(id, ref);
			return t;
		}

		return t;
	}
}
