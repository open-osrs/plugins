package renderer.plugin;

import java.util.LinkedList;
import java.util.List;

public class FramerateTracker
{
	private final int averageOver;
	private final List<Integer> frameTimes = new LinkedList<>();
	private long lastTime = -1;
	private double fps;

	public FramerateTracker(int averageOver)
	{
		this.averageOver = averageOver;
	}

	public void nextFrame()
	{
		long time = System.nanoTime();

		if (lastTime != -1)
		{
			frameTimes.add((int) (time - lastTime));
		}

		while (frameTimes.size() > averageOver)
		{
			frameTimes.remove(0);
		}

		lastTime = time;

		fps = 1 / (frameTimes.stream().mapToInt(x -> x).average().orElse(1) / 1000000000);
	}

	public double fps()
	{
		return fps;
	}
}
