package renderer.plugin;

import java.awt.Component;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFWNativeWin32.glfwAttachWin32Window;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.jawt.JAWTDrawingSurfaceInfo;
import static org.lwjgl.system.jawt.JAWTFunctions.JAWT_DrawingSurface_GetDrawingSurfaceInfo;
import org.lwjgl.system.jawt.JAWTWin32DrawingSurfaceInfo;

public class JawtContextWindows extends JawtContext
{
	private final long window;

	protected JawtContextWindows(Component component)
	{
		super(component);

		try (Lock ignored = lock())
		{
			JAWTDrawingSurfaceInfo info = JAWT_DrawingSurface_GetDrawingSurfaceInfo(drawingSurface, drawingSurface.GetDrawingSurfaceInfo());
			JAWTWin32DrawingSurfaceInfo winInfo = JAWTWin32DrawingSurfaceInfo.create(info.platformInfo());
			window = glfwAttachWin32Window(winInfo.hwnd(), 0);
			glfwMakeContextCurrent(window);
			GL.createCapabilities();
		}
	}

	@Override
	public void update()
	{
		try (Lock ignored = lock())
		{
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	@Override
	public void attach()
	{
		glfwMakeContextCurrent(window);
	}

	@Override
	public void detach()
	{
		glfwMakeContextCurrent(0);
	}

	@Override
	public int width()
	{
		int[] framebufferWidth = new int[1];
		int[] framebufferHeight = new int[1];
		glfwGetFramebufferSize(window, framebufferWidth, framebufferHeight);
		return framebufferWidth[0];
	}

	@Override
	public int height()
	{
		int[] framebufferWidth = new int[1];
		int[] framebufferHeight = new int[1];
		glfwGetFramebufferSize(window, framebufferWidth, framebufferHeight);
		return framebufferHeight[0];
	}
}
