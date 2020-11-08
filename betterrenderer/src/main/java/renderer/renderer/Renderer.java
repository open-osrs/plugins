package renderer.renderer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector4d;
import org.joml.Vector4i;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import renderer.gl.GlProgram;
import renderer.util.Util;
import renderer.world.World;

public class Renderer
{
	private static final double FOV = 0.5;
	public static final double CROSSHAIR_SIZE = 50;
	public static final double CROSSHAIR_THICKNESS = 5;
	public static final int CHUNK_SIZE = 8;
	public int viewDistance = 150;
	public Vector3d fogColor = new Vector3d(0.8, 0.9, 0.95);
	public double scale;
	public double gamma;
	private Map<Vector3i, Vector4i> instanceChunks = new HashMap<>();

	public final Vector3d position = new Vector3d(3223, 3425, 20);
	public final Quaterniond rotation = new Quaterniond();

	private GlProgram program;
	public final World world = new World();
	public final ChunkRenderScheduler chunkScheduler = new ChunkRenderScheduler(world);
	public Matrix4d projection;
	public Matrix4d transform;

	public void init()
	{
		glEnable(GL_MULTISAMPLE);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		glEnable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		try
		{
			program = new GlProgram(
				new String(Util.readAllBytes(Renderer.class.getResourceAsStream("/shaders/vertex-shader.glsl"))),
				new String(Util.readAllBytes(Renderer.class.getResourceAsStream("/shaders/fragment-shader.glsl")))
			);
		}
		catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public void draw(int width, int height, WorldRenderer extra)
	{
		glViewport(0, 0, width, height);
		glClearColor((float) fogColor.x, (float) fogColor.y, (float) fogColor.z, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		projection = new Matrix4d()
			.scale(scale, scale, 1)
			.perspective(FOV * Math.PI, (double) width / height, 50 / 128., Double.POSITIVE_INFINITY);

		transform = new Matrix4d()
			.rotate(rotation)
			.translate(-position.x, -position.y, -position.z);

		Vector3d light = new Vector3d(position.x - 50, position.y - 50, 200);

		program.enable(
			transform.get(new float[16]),
			projection.get(new float[16]),
			new float[]{(float) light.x, (float) light.y, (float) light.z},
			viewDistance - CHUNK_SIZE,
			fogColor,
			new float[]{(float) position.x, (float) position.y, (float) position.z},
			(float) gamma
		);

		List<WorldRenderer> chunks = new ArrayList<>();

		for (int dx = -viewDistance; dx <= viewDistance; dx += CHUNK_SIZE)
		{
			int w = (int) Math.sqrt(viewDistance * viewDistance - dx * dx);

			for (int dy = -w; dy <= w; dy += CHUNK_SIZE)
			{
				int x = (int) (position.x + dx) / CHUNK_SIZE;
				int y = (int) (position.y + dy) / CHUNK_SIZE;

				if (!visible(x, y))
				{
					continue;
				}

				WorldRenderer chunk = chunkScheduler.get(x, y);

				if (chunk != null)
				{
					chunks.add(chunk);
				}
			}
		}

		for (WorldRenderer chunk : chunks)
		{
			program.render(chunk.opaqueBuffer.buffer());
		}

		for (WorldRenderer chunk : chunks)
		{
			program.render(chunk.translucentBuffer.buffer());
		}

		if (extra != null)
		{
			program.render(extra.opaqueBuffer.buffer());
			program.render(extra.translucentBuffer.buffer());
			extra.opaqueBuffer.close();
			extra.translucentBuffer.close();
		}

		program.disable();
	}

	private boolean visible(int x, int y)
	{
		Vector3d v = new Vector3d(x * CHUNK_SIZE, y * CHUNK_SIZE, 0);
		return visible(new Vector3d(v)) ||
			visible(new Vector3d(v).add(CHUNK_SIZE, 0, 0)) ||
			visible(new Vector3d(v).add(0, CHUNK_SIZE, 0)) ||
			visible(new Vector3d(v).add(CHUNK_SIZE, CHUNK_SIZE, 0));
	}

	private boolean visible(Vector3d v)
	{
		v = world.position(v.x, v.y, 0);
		return pointVisible(new Vector3d(v).add(0, 0, 3)) || pointVisible(new Vector3d(v).add(0, 0, -3));
	}

	private boolean pointVisible(Vector3d v)
	{
		Vector4d v4 = new Vector4d(v.x, v.y, v.z, 1);

		v4.mul(transform);
		v4.mul(projection);
		v4.div(v4.w);

		return v4.x >= -1 && v4.x <= 1 && v4.y >= -1 && v4.y <= 1;
	}

	public Vector2d toScreen(Vector3d pos)
	{
		Vector4d v = new Vector4d(pos.x, pos.y, pos.z, 1);

		v.mul(transform);
		v.mul(projection);
		v.div(v.w);

		return new Vector2d(v.x, v.y);
	}

	public boolean setInstanceChunks(Map<Vector3i, Vector4i> instanceChunks)
	{
		if (!Objects.equals(instanceChunks, this.instanceChunks))
		{
			this.instanceChunks = instanceChunks;
			return true;
		}

		return false;
	}
}
