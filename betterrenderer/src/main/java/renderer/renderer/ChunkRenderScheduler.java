package renderer.renderer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.joml.Vector2i;
import renderer.cache.CacheSystem;
import renderer.world.Position;
import renderer.world.World;

public class ChunkRenderScheduler
{
	private final List<WorldRenderer> renderersToClose = new ArrayList<>();
	private final ExecutorService buildExecutor;
	private final Cache<Integer, WorldRenderer> chunks = CacheBuilder
		.newBuilder()
		.expireAfterAccess(300, TimeUnit.SECONDS)
		.weigher((Weigher<Integer, WorldRenderer>) (key, value) -> value.opaqueBuffer.memoryUsage() + value.translucentBuffer.memoryUsage())
		.maximumWeight(32 * 1024 * 1024L * 1024L) // todo: set this based on max graphics memory
		.removalListener(n -> renderersToClose.add(n.getValue()))
		.concurrencyLevel(Runtime.getRuntime().availableProcessors() + 2)
		.build();
	private final Set<Integer> scheduled = ConcurrentHashMap.newKeySet();
	private final World world;
	private HashSet<Position> roofsRemoved = new HashSet<>();
	private int roofRemovalPlane;

	public ChunkRenderScheduler(World world)
	{
		this.world = world;
		buildExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}

	public WorldRenderer get(int x, int y)
	{
		closedUncachedRenderers();

		int key = (x << 16) + y;
		WorldRenderer chunk = chunks.getIfPresent(key);

		if (chunk != null)
		{
			return chunk;
		}

		if (scheduled.add(key))
		{
			buildExecutor.submit(() -> render(x, y));
		}

		return null;
	}

	private void closedUncachedRenderers()
	{
		for (WorldRenderer renderer : new ArrayList<>(renderersToClose))
		{
			if (renderer != null)
			{
				renderer.opaqueBuffer.close();
				renderer.translucentBuffer.close();
			}
		}

		renderersToClose.clear();
	}

	public void render(int x, int y)
	{
		try
		{
			if (CacheSystem.region(x * Renderer.CHUNK_SIZE / 64, y * Renderer.CHUNK_SIZE / 64) == null)
			{
				return;
			}

			WorldRenderer renderer = new WorldRenderer(world);
			renderer.chunk(x, y);

			int key = (x << 16) + y;
			chunks.put(key, renderer);
			scheduled.remove(key);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			System.exit(0);
		}
	}

	public void setRoofsRemoved(HashSet<Position> newRoofsRemoved, int roofRemovalPlane)
	{
		Set<Vector2i> toUpdate = new HashSet<>();

		for (Position p : roofsRemoved)
		{
			if (!newRoofsRemoved.contains(p))
			{
				toUpdate.add(new Vector2i(p.x / 8, p.y / 8));
			}
		}

		for (Position p : newRoofsRemoved)
		{
			if (roofRemovalPlane != this.roofRemovalPlane || !roofsRemoved.contains(p))
			{
				toUpdate.add(new Vector2i(p.x / 8, p.y / 8));
			}
		}

		for (Vector2i chunkPos : toUpdate)
		{
			render(chunkPos.x, chunkPos.y);
		}

		roofsRemoved = new HashSet<>(newRoofsRemoved);
		this.roofRemovalPlane = roofRemovalPlane;
	}

	public void clear()
	{
		chunks.invalidateAll();
		scheduled.clear();
	}

	public void stopAllThreads()
	{
		buildExecutor.shutdown();
	}
}
