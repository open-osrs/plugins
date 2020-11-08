package renderer.model;

import java.util.ArrayList;
import java.util.List;

public class AnimationDefinition
{
	public final int id;

	public List<Frame> frames = new ArrayList<>();
	public List<TransformDefinition> chatFrames = new ArrayList<>();
	public List<FrameSound> soundFrames = new ArrayList<>();

	public int frameStep = -1;
	public int[] interleaveLeave;
	public boolean stretches = false;
	public int forcedPriority = 5;
	public int leftHandItem = -1;
	public int rightHandItem = -1;
	public int maxLoops = 99;
	public int precedenceAnimating = -1;
	public int priority = -1;
	public int replyMode = 2;

	public AnimationDefinition(int id)
	{
		this.id = id;
	}

	public static class Frame
	{
		public final TransformDefinition transfrom;
		public final int length;

		public Frame(TransformDefinition transfrom, int length)
		{
			this.transfrom = transfrom;
			this.length = length;
		}
	}

	public static class FrameSound
	{
		public final int sound;
		public final int loop;
		public final int location;

		public FrameSound(int sound, int loop, int location)
		{
			this.sound = sound;
			this.loop = loop;
			this.location = location;
		}
	}
}
