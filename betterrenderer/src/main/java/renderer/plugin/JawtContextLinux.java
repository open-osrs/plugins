package renderer.plugin;

import java.awt.Component;
import java.nio.IntBuffer;
import java.util.Objects;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GLX13.GLX_RGBA_TYPE;
import static org.lwjgl.opengl.GLX13.glXChooseFBConfig;
import static org.lwjgl.opengl.GLX13.glXCreateNewContext;
import static org.lwjgl.opengl.GLX13.glXGetVisualFromFBConfig;
import static org.lwjgl.opengl.GLX13.glXMakeCurrent;
import static org.lwjgl.opengl.GLX13.glXSwapBuffers;
import org.lwjgl.system.jawt.JAWTDrawingSurfaceInfo;
import static org.lwjgl.system.jawt.JAWTFunctions.JAWT_DrawingSurface_GetDrawingSurfaceInfo;
import org.lwjgl.system.jawt.JAWTX11DrawingSurfaceInfo;
import org.lwjgl.system.linux.XVisualInfo;

public class JawtContextLinux extends JawtContext
{
	private final long context;
	private final long display;
	private final long drawable;
	private final Component component;

	protected JawtContextLinux(Component component)
	{
		super(component);
		this.component = component;

		try (Lock ignored = lock())
		{
			JAWTDrawingSurfaceInfo info = JAWT_DrawingSurface_GetDrawingSurfaceInfo(drawingSurface, drawingSurface.GetDrawingSurfaceInfo());
			JAWTX11DrawingSurfaceInfo linuxInfo = JAWTX11DrawingSurfaceInfo.create(info.platformInfo());


			display = linuxInfo.display();
			drawable = linuxInfo.drawable();

			PointerBuffer configs = Objects.requireNonNull(glXChooseFBConfig(display, 0, (IntBuffer) null));

			long config = 0;

			for (int i = 0; i < configs.remaining(); i++)
			{
				XVisualInfo vi = Objects.requireNonNull(glXGetVisualFromFBConfig(display, configs.get(i)));
				if (vi.visualid() == linuxInfo.visualID())
				{
					config = configs.get(i);
					break;
				}
			}

			context = glXCreateNewContext(display, config, GLX_RGBA_TYPE, 0, true);
			glXMakeCurrent(display, drawable, context);
			GL.createCapabilities();
		}
	}

	@Override
	public void update()
	{
		try (Lock ignored = lock())
		{
			glXSwapBuffers(display, drawable);
		}
	}

	@Override
	public void attach()
	{
		glXMakeCurrent(display, drawable, context);
	}

	@Override
	public void detach()
	{
		glXMakeCurrent(display, drawable, 0);
	}

	@Override
	public int width()
	{
		return component.getWidth();
	}

	@Override
	public int height()
	{
		return component.getHeight();
	}
}
