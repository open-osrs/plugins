package renderer.cache;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.runelite.client.RuneLite;

public final class CacheFiles
{
	public static byte[] read(int archive, int group)
	{
		try
		{
			Path path = RuneLite.RUNELITE_DIR.toPath().resolve("better-renderer/cache/" + archive + "/" + group);

			if (!Files.exists(path))
			{
				return null;
			}

			return Files.readAllBytes(path);
		}
		catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public static void write(int archive, int group, byte[] data)
	{
		try
		{
			Path path = RuneLite.RUNELITE_DIR.toPath().resolve("better-renderer/cache/" + archive + "/" + group);
			Files.createDirectories(path.getParent());
			Files.write(path, data);
		}
		catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}
}
