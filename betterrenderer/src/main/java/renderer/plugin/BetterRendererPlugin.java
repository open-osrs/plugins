package renderer.plugin;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.BufferProvider;
import net.runelite.api.Client;
import net.runelite.api.Entity;
import net.runelite.api.GameState;
import net.runelite.api.Model;
import net.runelite.api.Texture;
import net.runelite.api.Tile;
import net.runelite.api.TileModel;
import net.runelite.api.TilePaint;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector4i;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11C.glGetError;
import static org.lwjgl.opengl.GL32.GL_BGRA;
import static org.lwjgl.opengl.GL32.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL32.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL32.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL32.GL_CULL_FACE;
import static org.lwjgl.opengl.GL32.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL32.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL32.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL32.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL32.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL32.GL_FRONT;
import static org.lwjgl.opengl.GL32.GL_LINEAR;
import static org.lwjgl.opengl.GL32.GL_MAX_SAMPLES;
import static org.lwjgl.opengl.GL32.GL_NEAREST;
import static org.lwjgl.opengl.GL32.GL_QUADS;
import static org.lwjgl.opengl.GL32.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL32.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL32.GL_RGBA;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL32.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL32.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.opengl.GL32.glBegin;
import static org.lwjgl.opengl.GL32.glBindFramebuffer;
import static org.lwjgl.opengl.GL32.glBindRenderbuffer;
import static org.lwjgl.opengl.GL32.glBindTexture;
import static org.lwjgl.opengl.GL32.glBlitFramebuffer;
import static org.lwjgl.opengl.GL32.glColor4d;
import static org.lwjgl.opengl.GL32.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL32.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL32.glDeleteTextures;
import static org.lwjgl.opengl.GL32.glDisable;
import static org.lwjgl.opengl.GL32.glEnable;
import static org.lwjgl.opengl.GL32.glEnd;
import static org.lwjgl.opengl.GL32.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL32.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL32.glGenFramebuffers;
import static org.lwjgl.opengl.GL32.glGenRenderbuffers;
import static org.lwjgl.opengl.GL32.glGenTextures;
import static org.lwjgl.opengl.GL32.glGetInteger;
import static org.lwjgl.opengl.GL32.glReadBuffer;
import static org.lwjgl.opengl.GL32.glReadPixels;
import static org.lwjgl.opengl.GL32.glRenderbufferStorageMultisample;
import static org.lwjgl.opengl.GL32.glTexCoord2d;
import static org.lwjgl.opengl.GL32.glTexImage2D;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;
import static org.lwjgl.opengl.GL32.glTexParameteri;
import static org.lwjgl.opengl.GL32.glTexSubImage2D;
import static org.lwjgl.opengl.GL32.glVertex2d;
import org.lwjgl.system.MemoryUtil;
import org.pf4j.Extension;
import renderer.cache.CacheSystem;
import renderer.renderer.BufferBuilder;
import renderer.renderer.Renderer;
import renderer.renderer.WorldRenderer;
import renderer.util.Colors;
import renderer.util.Util;

@Extension
@PluginDescriptor(
	name = "Better Renderer",
	description = "Optimized renderer providing nearly infinite view distance and minor graphical improvements",
	type = PluginType.UTILITY)
public class BetterRendererPlugin extends Plugin implements DrawCallbacks
{
	private static final String XTEA_LOCATION = "https://gist.githubusercontent.com/Runemoro/d68a388aeb35ad432adf8af027eae832/raw/xtea.json";
	@Inject
	public Client client;
	@Inject
	public BetterRendererConfig config;
	@Inject
	private DrawManager drawManager;
	@Inject
	private ClientThread clientThread;
	@Inject
	private PluginManager pluginManager;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private LoadingCacheOverlay loadingCacheOverlay;

	private JawtContext context;
	public Renderer renderer;
	private WorldRenderer dynamicBuffer;
	private boolean hasFrame = false;
	private final FramerateTracker framerateTracker = new FramerateTracker(10);

	private int interfaceTexture = -1;
	private int lastCanvasWidth = -1;
	private int lastCanvasHeight = -1;
	private int lastWidth = -1;
	private int lastHeight = -1;
	private int lastSamples = -1;
	private int framebuffer = -1;
	private int colorRenderbuffer = -1;
	private int framebufferTexture = -1;
	private int depthRenderbuffer = -1;
	private int width = -1;
	private int height = -1;

	private boolean executorInitialized = false;
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private Future<?> frameFuture;
	private Thread initThread;

	@Override
	protected void startUp()
	{
		// Remove off-heap memory limit (default is equal to Xmx)
		try
		{
			ByteBuffer.allocateDirect(0);
			Class<?> bitsClass = Class.forName("java.nio.Bits");
			Field maxMemoryField;

			try
			{
				maxMemoryField = bitsClass.getDeclaredField("MAX_MEMORY");
			}
			catch (NoSuchFieldException e)
			{
				maxMemoryField = bitsClass.getDeclaredField("maxMemory"); // Java 8
			}

			maxMemoryField.setAccessible(true);
			maxMemoryField.set(null, Long.MAX_VALUE);
		}
		catch (ReflectiveOperationException e)
		{
			throw new AssertionError(e);
		}

		// Download the cache

		initThread = new Thread(() -> {
			overlayManager.add(loadingCacheOverlay);

			try
			{
				Path xteaPath = RuneLite.RUNELITE_DIR.toPath().resolve("better-renderer/xtea.json");
				Files.createDirectories(xteaPath.getParent());
				Files.write(xteaPath, Util.readAllBytes(new URL(XTEA_LOCATION).openStream()));
				CacheSystem.CACHE.init(client.getWorld(), client.getRevision());
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}

			overlayManager.remove(loadingCacheOverlay);

			clientThread.invoke(this::init);
		});

		initThread.start();
	}

	private void init()
	{
		client.setDrawCallbacks(this);
		client.setGpu(true);
		client.resizeCanvas();

		glfwInit();
		glfwSetErrorCallback((id, description) -> {
			throw new RuntimeException(id + ": " + MemoryUtil.memUTF8(description));
		});

		context = JawtContext.create(client.getCanvas());
		createInterfaceTexture();

		renderer = new Renderer();
		dynamicBuffer = new WorldRenderer(renderer.world);
		renderer.init();
	}

	@Override
	protected void shutDown()
	{
		clientThread.invoke(() -> {
			try
			{
				overlayManager.remove(loadingCacheOverlay);
				initThread.stop();
				renderer.chunkScheduler.stopAllThreads();
			}
			catch (Throwable ignored)
			{

			}

			client.setDrawCallbacks(null);
			client.setGpu(false);

			try
			{
				glfwTerminate();
				context.close();
			}
			catch (Throwable ignored)
			{

			}

			context = null;
			hasFrame = false;
			interfaceTexture = -1;
			lastCanvasWidth = -1;
			lastCanvasHeight = -1;
			lastWidth = -1;
			lastHeight = -1;
			lastSamples = -1;
			framebuffer = -1;
			colorRenderbuffer = -1;
			framebufferTexture = -1;
			depthRenderbuffer = -1;
			width = -1;
			height = -1;
			executorInitialized = false;
			frameFuture = null;
			renderer = null;
			dynamicBuffer = null;

			client.resizeCanvas();
		});
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOADING)
		{
			// Stop the GPU plugin before starting up
			for (Plugin plugin : pluginManager.getPlugins())
			{
				if (plugin.getName().equals("GPU") && pluginManager.isPluginEnabled(plugin))
				{
					try
					{
						System.out.println("Stopping GPU plugin");
						pluginManager.stopPlugin(plugin);
					}
					catch (PluginInstantiationException e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	@Provides
	public BetterRendererConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BetterRendererConfig.class);
	}

	@Override
	public void draw(Entity entity, int orientation, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z, long hash)
	{
		Model model = entity instanceof Model ? (Model) entity : entity.getModel();

		if (model == null)
		{
			return;
		}

		model.calculateBoundsCylinder();
		model.calculateExtreme(orientation);
		client.checkClickbox(model, orientation, pitchSin, pitchCos, yawSin, yawCos, x, y, z, hash);

		if (!(entity instanceof Model))
		{
			Vector3d pos = new Vector3d(
				client.getBaseX() + (x + client.getCameraX2()) / 128.,
				client.getBaseY() + (z + client.getCameraZ2()) / 128.,
				-(y + client.getCameraY2()) / 128.
			);

			for (int faceIndex = 0; faceIndex < model.getTrianglesCount(); faceIndex++)
			{
				int alpha = model.getTriangleTransparencies() == null ? 0xff : 0xff - model.getTriangleTransparencies()[faceIndex];
				BufferBuilder buffer = alpha == 0xff ? dynamicBuffer.opaqueBuffer : dynamicBuffer.translucentBuffer;
				byte priority = model.getFaceRenderPriorities() == null ? 0 : model.getFaceRenderPriorities()[faceIndex];

				int i = model.getTrianglesX()[faceIndex];
				int j = model.getTrianglesY()[faceIndex];
				int k = model.getTrianglesZ()[faceIndex];

				Vector3d a = new Vector3d(model.getVerticesX()[i] * WorldRenderer.SCALE, model.getVerticesZ()[i] * WorldRenderer.SCALE, -model.getVerticesY()[i] * WorldRenderer.SCALE).rotateZ(-(Math.PI * orientation / 1024.)).add(pos);
				Vector3d b = new Vector3d(model.getVerticesX()[j] * WorldRenderer.SCALE, model.getVerticesZ()[j] * WorldRenderer.SCALE, -model.getVerticesY()[j] * WorldRenderer.SCALE).rotateZ(-(Math.PI * orientation / 1024.)).add(pos);
				Vector3d c = new Vector3d(model.getVerticesX()[k] * WorldRenderer.SCALE, model.getVerticesZ()[k] * WorldRenderer.SCALE, -model.getVerticesY()[k] * WorldRenderer.SCALE).rotateZ(-(Math.PI * orientation / 1024.)).add(pos);

				int color1 = model.getFaceColors1()[faceIndex];
				int color2 = model.getFaceColors2()[faceIndex];
				int color3 = model.getFaceColors3()[faceIndex];

				if (color3 == -1)
				{
					color2 = color3 = color1;
				}
				else if (color3 == -2)
				{
					continue; // hidden
				}

				buffer.vertex(a, alpha << 24 | (Colors.hsl(color1) & 0xffffff), 20 + priority);
				buffer.vertex(b, alpha << 24 | (Colors.hsl(color2) & 0xffffff), 20 + priority);
				buffer.vertex(c, alpha << 24 | (Colors.hsl(color3) & 0xffffff), 20 + priority);
			}
		}
	}

	@Override
	public void drawScenePaint(int orientation, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z, TilePaint paint, int tileZ, int tileX, int tileY, int zoom, int centerX, int centerY)
	{

	}

	@Override
	public void drawSceneModel(int orientation, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z, TileModel model, int tileZ, int tileX, int tileY, int zoom, int centerX, int centerY)
	{

	}

	@Override
	public void draw()
	{
		try
		{
			if (hasFrame)
			{
				finishFrame();
			}

			startFrame();
			hasFrame = true;
		}
		catch (Throwable t)
		{
			handleCrash(t);
		}
	}

	private void finishFrame()
	{
		if (config.offThreadRendering())
		{
			if (frameFuture != null)
			{
				try
				{
					frameFuture.get();
				}
				catch (InterruptedException | ExecutionException e)
				{
					throw new UncheckedExecutionException(e);
				}

				context.attach();
			}
			else
			{
				context.attach();
				renderWorld(width, height, dynamicBuffer);
			}
		}
		else
		{
			context.attach();
			renderWorld(width, height, dynamicBuffer);
		}

		glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);

		glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);

		drawInterface();
		updateWindow();
		drawManager.processDrawComplete(this::screenshot);
		framerateTracker.nextFrame();


		int err = glGetError();

		if (err != 0)
		{
			throw new IllegalStateException("0x" + Integer.toHexString(err));
		}
	}

	private void startFrame()
	{
		width = context.width();
		height = context.height();

		if (client.getLocalPlayer() != null && Math.abs(client.getBaseX() + client.getCameraX() / 128.) > 1)
		{ // ???
			// Update renderer settings
			double cameraX = client.getBaseX() + client.getCameraX() / 128.;
			double cameraY = client.getBaseY() + client.getCameraY() / 128.;
			double cameraZ = -client.getCameraZ() / 128.;
			double cameraPitch = -Math.PI * client.getCameraPitch() / 1024.;
			double cameraYaw = -Math.PI * client.getCameraYaw() / 1024.;
			double zoom = 2. * client.getScale() / client.getCanvasHeight();

			Vector3d cameraPosition = new Vector3d(cameraX, cameraY, cameraZ);
			renderer.rotation.set(0, 0, 0, 1);
			renderer.rotation.rotateX(-Math.PI / 2);
			renderer.rotation.rotateX(-cameraPitch);
			renderer.rotation.rotateZ(cameraYaw);

			if (config.improvedZoom() /*&& client.getOculusOrbFocalPointX() != client.getLocalPlayer().getLocalLocation().getX()*/)
			{
				renderer.scale = 1;
				double amount = (1 - 1 / zoom) * getActorPosition(client.getLocalPlayer()).distance(cameraPosition);
				cameraPosition.add(renderer.rotation.transformInverse(new Vector3d(0, 0, -amount)));
			}
			else
			{
				renderer.scale = zoom;
			}

			renderer.position.set(cameraPosition);
			renderer.viewDistance = config.viewDistance();
			renderer.gamma = 1 - 0.1 * client.getVarpValue(166);
			renderer.fogColor = Colors.unpack(client.getSkyboxColor());

			// Instance chunks
			if (client.isInInstancedRegion())
			{
				Map<Vector3i, Vector4i> chunks = new HashMap<>();

				for (int x1 = 0; x1 < 13; x1++)
				{
					for (int y1 = 0; y1 < 13; y1++)
					{
						for (int z1 = 0; z1 < 4; z1++)
						{
							int chunkData = client.getInstanceTemplateChunks()[z1][x1][y1];
							int rotation = chunkData >> 1 & 0x3;
							int x2 = (chunkData >> 14 & 0x3FF);
							int y2 = (chunkData >> 3 & 0x7FF);
							int z2 = chunkData >> 24 & 0x3;

							Vector3i pos1 = new Vector3i(client.getBaseX() / 8 + x1, client.getBaseY() / 8 + y1, z1);
							Vector4i pos2 = new Vector4i(x2, y2, z2, rotation);

							chunks.put(pos1, pos2);
						}
					}
				}

				if (renderer.setInstanceChunks(chunks))
				{
					for (Map.Entry<Vector3i, Vector4i> chunk : chunks.entrySet())
					{
						renderer.world.copyInstanceChunk(chunk.getKey(), new Vector3i(chunk.getValue().x, chunk.getValue().y, chunk.getValue().z), chunk.getValue().w);
						renderer.chunkScheduler.render(chunk.getKey().x, chunk.getKey().y);
					}
				}
			}
			else
			{
				renderer.setInstanceChunks(null);
				renderer.world.instanceRegions = null;
			}

			// Roofs
			WorldPoint p = client.getLocalPlayer().getWorldLocation();
			renderer.world.updateRoofs(p.getX(), p.getY(), p.getPlane(), config.roofRemovalRadius());
			renderer.chunkScheduler.setRoofsRemoved(renderer.world.roofsRemoved, renderer.world.roofRemovalPlane);
		}

		// Create or update the FBO
		updateFramebuffer();

		// Submit frame render task to the executor
		context.detach();

		WorldRenderer extra = dynamicBuffer;
		dynamicBuffer = new WorldRenderer(renderer.world);

		if (config.offThreadRendering())
		{
			frameFuture = executor.submit(() -> {
				context.attach();

				if (!executorInitialized)
				{
					GL.createCapabilities();
					executorInitialized = true;
				}

				renderWorld(width, height, extra);
				context.detach();
			});
		}
	}

	private void updateWindow()
	{
		context.update();
	}

	private void renderWorld(int width, int height, WorldRenderer dynamic)
	{
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

		renderer.draw(width, height, dynamic);
	}

	private Vector3d getActorPosition(Actor player)
	{
		WorldPoint pos = WorldPoint.fromLocal(client, player.getLocalLocation());
		Tile tile = client.getScene().getTiles()[pos.getPlane()][pos.getX() - client.getBaseX()][pos.getY() - client.getBaseY()];

		if (tile == null)
		{ // ??
			return new Vector3d(0, 0, 0);
		}

		return convert((tile.getBridge() == null ? tile.getPlane() : tile.getPlane() + 1), player.getLocalLocation());
	}

	private Vector3d convert(int plane, LocalPoint local)
	{
		return renderer.world.position(
			client.getBaseX() + local.getX() / 128., client.getBaseY() + local.getY() / 128., plane
		);
	}

	private void drawInterface()
	{
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);
		int canvasWidth = client.getCanvasWidth();
		int canvasHeight = client.getCanvasHeight();

		glBindTexture(GL_TEXTURE_2D, interfaceTexture);

		if (canvasWidth != lastCanvasWidth || canvasHeight != lastCanvasHeight)
		{
			lastCanvasWidth = canvasWidth;
			lastCanvasHeight = canvasHeight;
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, canvasWidth, canvasHeight, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, 0);
		}

		final BufferProvider b = client.getBufferProvider();
		glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, b.getWidth(), b.getHeight(), GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, b.getPixels());

		glColor4d(1, 1, 1, 1);
		double i = 1;
		glBegin(GL_QUADS);
		glTexCoord2d(0, 0);
		glVertex2d(-i, i);
		glTexCoord2d(0, 1);
		glVertex2d(-i, -i);
		glTexCoord2d(1, 1);
		glVertex2d(i, -i);
		glTexCoord2d(1, 0);
		glVertex2d(i, i);
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glDisable(GL_TEXTURE_2D);
	}

	@Override
	public boolean drawFace(Model model, int face)
	{
		return false;
	}

	@Override
	public void drawScene(int cameraX, int cameraY, int cameraZ, int cameraPitch, int cameraYaw, int plane)
	{
		client.getScene().setDrawDistance(90);
	}

	@Override
	public void animate(Texture texture, int diff)
	{
		// ignored
	}

	private BufferedImage screenshot()
	{
		int width = client.getCanvasWidth();
		int height = client.getCanvasWidth();

		ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4 * 10)
			.order(ByteOrder.nativeOrder());

		glReadBuffer(GL_FRONT);
		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		for (int y = 0; y < height; ++y)
		{
			for (int x = 0; x < width; ++x)
			{
				int r = buffer.get() & 0xff;
				int g = buffer.get() & 0xff;
				int b = buffer.get() & 0xff;
				buffer.get(); // alpha

				pixels[(height - y - 1) * width + x] = (r << 16) | (g << 8) | b;
			}
		}

		return image;
	}

	private void createInterfaceTexture()
	{
		interfaceTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, interfaceTexture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	private void updateFramebuffer()
	{
		if (lastWidth == width && lastHeight == height && lastSamples == config.samples().getSamples())
		{
			return;
		}

		if (framebufferTexture != -1)
		{
			glDeleteTextures(framebufferTexture);
			framebufferTexture = -1;
		}

		if (framebuffer != -1)
		{
			glDeleteFramebuffers(framebuffer);
			framebuffer = -1;
		}

		if (colorRenderbuffer != -1)
		{
			glDeleteRenderbuffers(colorRenderbuffer);
			colorRenderbuffer = -1;
		}

		if (depthRenderbuffer != -1)
		{
			glDeleteRenderbuffers(depthRenderbuffer);
			colorRenderbuffer = -1;
		}

		int samples = Math.max(1, Math.min(glGetInteger(GL_MAX_SAMPLES), config.samples().getSamples()));
		framebuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

		// Create color render buffer
		colorRenderbuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, colorRenderbuffer);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_RGBA, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, colorRenderbuffer);

		// Create depth render buffer
		depthRenderbuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthRenderbuffer);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH_COMPONENT, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderbuffer);

		// Create texture
		framebufferTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, framebufferTexture);
		glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGBA, width, height, true);

		// Bind texture
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, framebufferTexture, 0);

		// Reset
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glBindRenderbuffer(GL_RENDERBUFFER, 0);

		lastWidth = width;
		lastHeight = height;
		lastSamples = config.samples().getSamples();
	}

	private void handleCrash(Throwable t)
	{
		t.printStackTrace();
		try
		{
			pluginManager.stopPlugin(this);
		}
		catch (PluginInstantiationException e)
		{
			RuntimeException e2 = new RuntimeException(e);
			e2.addSuppressed(t);
			throw e2;
		}
	}
}
