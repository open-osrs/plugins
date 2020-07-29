package com.jagex.runescape377.util;

import com.jagex.runescape377.config.Configuration;
import static com.jagex.runescape377.config.Configuration.CACHE_NAME;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class SignLink implements Runnable
{

	public static final int CLIENT_REVISION = 377;
	public static final RandomAccessFile[] cacheIndex = new RandomAccessFile[5];
	public static int uid;
	public static int storeId = 32;
	public static RandomAccessFile cacheData = null;
	public static String dns = null;
	public static String midi = null;
	public static int midiVolume;
	public static int fadeMidi;
	public static int waveVolume;
	public static boolean reportError = true;
	public static String errorName = "";
	public static Sequencer music = null;
	private static boolean active;
	private static int threadLiveId;
	private static InetAddress inetAddress;
	private static int socketRequest;
	private static Socket socket = null;
	private static int threadRequestPriority = 1;
	private static Runnable threadRequest = null;
	private static String dnsRequest = null;
	private static String urlRequest = null;
	private static DataInputStream urlStream = null;
	private static int saveLength;
	private static String saveRequest = null;
	private static byte[] saveBuffer = null;
	private static boolean play;
	private static int midiPosition;
	private static boolean midiPlay;
	private static int wavePosition;
	private static Synthesizer synthesizer = null;
	private Position curPosition;

	public static void initialize(InetAddress address)
	{
		threadLiveId = (int) (Math.random() * 99999999D);

		if (active)
		{
			try
			{
				Thread.sleep(500L);
			}
			catch (Exception ignored)
			{
			}

			active = false;
		}

		socketRequest = 0;
		threadRequest = null;
		dnsRequest = null;
		saveRequest = null;
		urlRequest = null;
		inetAddress = address;
		Thread thread = new Thread(new SignLink());

		thread.setDaemon(true);
		thread.start();

		while (!active)
		{
			try
			{
				Thread.sleep(50L);
			}
			catch (Exception ignored)
			{
			}
		}
	}

	/**
	 * Sets the volume for the midi synthesizer.
	 *
	 * @param value
	 */
	public static void setVolume(int value)
	{
		final int CHANGE_VOLUME = 7;
		midiVolume = value;

		if (synthesizer.getDefaultSoundbank() == null)
		{
			try
			{
				ShortMessage volumeMessage = new ShortMessage();

				for (int i = 0; i < 16; i++)
				{
					volumeMessage.setMessage(ShortMessage.CONTROL_CHANGE, i, CHANGE_VOLUME, midiVolume);
					volumeMessage.setMessage(ShortMessage.CONTROL_CHANGE, i, 39, midiVolume);
					MidiSystem.getReceiver().send(volumeMessage, -1);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			MidiChannel[] channels = synthesizer.getChannels();

			for (int c = 0; channels != null && c < channels.length; c++)
			{
				channels[c].controlChange(CHANGE_VOLUME, midiVolume);
				channels[c].controlChange(39, midiVolume);
			}
		}
	}

	public static String cacheLocation()
	{
		File file = new File(System.getProperty("user.home") + System.getProperty("file.separator") + CACHE_NAME + System.getProperty("file.separator"));
		if (!file.exists())
		{
			if (!file.mkdir())
			{
				return secondaryLocation();
			}
		}
		return System.getProperty("user.home") + System.getProperty("file.separator") + CACHE_NAME + System.getProperty("file.separator");
	}

	public static String secondaryLocation()
	{
		File file = new File("c:/.377cache/");
		if (!file.exists())
		{
			file.mkdir();
		}
		return file.toString();
	}

	private static int getUID(String location)
	{
		try
		{
			File uid = new File(location + "uid.dat");

			if (!uid.exists() || uid.length() < 4L)
			{
				DataOutputStream output = new DataOutputStream(new FileOutputStream(location + "uid.dat"));

				output.writeInt((int) (Math.random() * 99999999D));
				output.close();
			}
		}
		catch (Exception ignored)
		{
		}

		try
		{
			DataInputStream input = new DataInputStream(new FileInputStream(location + "uid.dat"));
			int value = input.readInt();

			input.close();

			return value + 1;
		}
		catch (Exception ex)
		{
			return 0;
		}
	}

	public static synchronized Socket openSocket(int port) throws IOException
	{
		for (socketRequest = port; socketRequest != 0; )
		{
			try
			{
				Thread.sleep(50L);
			}
			catch (Exception ignored)
			{
			}
		}

		if (socket == null)
		{
			throw new IOException("could not open socket");
		}
		else
		{
			return socket;
		}
	}

	public static synchronized DataInputStream openURL(String url) throws IOException
	{
		for (urlRequest = url; urlRequest != null; )
		{
			try
			{
				Thread.sleep(50L);
			}
			catch (Exception ignored)
			{
			}
		}

		if (urlStream == null)
		{
			throw new IOException("could not open: " + url);
		}
		else
		{
			return urlStream;
		}
	}

	public static synchronized void dnsLookup(String host)
	{
		dns = host;
		dnsRequest = host;
	}

	public static synchronized void startThread(Runnable runnable, int priority)
	{
		threadRequestPriority = priority;
		threadRequest = runnable;
	}

	public static synchronized boolean saveWave(byte[] data, int length)
	{
		if (length > 2000000 || saveRequest != null)
		{
			return false;
		}

		wavePosition = (wavePosition + 1) % 5;
		saveLength = length;
		saveBuffer = data;
		midiPlay = true;
		saveRequest = "sound" + wavePosition + ".wav";

		return true;
	}

	public static synchronized boolean replayWave()
	{
		if (saveRequest != null)
		{
			return false;
		}

		saveBuffer = null;
		midiPlay = true;
		saveRequest = "sound" + wavePosition + ".wav";

		return true;
	}

	public static synchronized void saveMidi(byte[] data, int length)
	{
		if (length > 2000000 || saveRequest != null)
		{
			return;
		}

		midiPosition = (midiPosition + 1) % 5;
		saveLength = length;
		saveBuffer = data;
		play = true;
		saveRequest = "jingle" + midiPosition + ".mid";
	}

	public static void reportError(String error)
	{
		System.out.println("Error: " + error);
	}

	public void run()
	{
		active = true;
		String directory = cacheLocation();
		uid = getUID(directory);

		try
		{
			cacheData = new RandomAccessFile(directory + "main_file_cache.dat", "rw");

			for (int idx = 0; idx < 5; idx++)
			{
				cacheIndex[idx] = new RandomAccessFile(directory + "main_file_cache.idx" + idx, "rw");
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}

		for (int i = threadLiveId; threadLiveId == i; )
		{
			if (socketRequest != 0)
			{
				try
				{
					socket = new Socket(inetAddress, socketRequest);
				}
				catch (Exception _ex)
				{
					socket = null;
				}

				socketRequest = 0;
			}
			else if (threadRequest != null)
			{
				Thread thread = new Thread(threadRequest);

				thread.setDaemon(true);
				thread.start();
				thread.setPriority(threadRequestPriority);

				threadRequest = null;
			}
			else if (dnsRequest != null)
			{
				try
				{
					dns = InetAddress.getByName(dnsRequest).getHostName();
				}
				catch (Exception _ex)
				{
					dns = "unknown";
				}

				dnsRequest = null;
			}
			else if (saveRequest != null)
			{
				if (saveBuffer != null)
				{
					try
					{
						FileOutputStream fileoutputstream = new FileOutputStream(directory + saveRequest);

						fileoutputstream.write(saveBuffer, 0, saveLength);
						fileoutputstream.close();
					}
					catch (Exception ignored)
					{
					}
				}

				if (midiPlay)
				{
					String wave = directory + saveRequest;
					midiPlay = false;
					AudioInputStream audioInputStream;

					try
					{
						audioInputStream = AudioSystem.getAudioInputStream(new File(wave));
					}
					catch (UnsupportedAudioFileException | IOException e)
					{
						e.printStackTrace();
						return;
					}

					AudioFormat format = audioInputStream.getFormat();
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
					SourceDataLine audioLine;

					try
					{
						audioLine = (SourceDataLine) AudioSystem.getLine(info);
						audioLine.open(format);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return;
					}

					if (audioLine.isControlSupported(FloatControl.Type.PAN))
					{
						FloatControl pan = (FloatControl) audioLine.getControl(FloatControl.Type.PAN);

						if (curPosition == Position.RIGHT)
						{
							pan.setValue(1.0f);
						}
						else if (curPosition == Position.LEFT)
						{
							pan.setValue(-1.0f);
						}
					}

					audioLine.start();

					int nBytesRead = 0;
					byte[] abData = new byte[524288];

					try
					{
						while (nBytesRead != -1)
						{
							nBytesRead = audioInputStream.read(abData, 0, abData.length);

							if (nBytesRead >= 0)
							{
								audioLine.write(abData, 0, nBytesRead);
							}
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
						return;
					}
					finally
					{
						audioLine.drain();
						audioLine.close();
					}
				}

				if (play)
				{
					midi = directory + saveRequest;

					try
					{
						if (music != null)
						{
							music.stop();
							music.close();
						}

						playMidi(midi);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}

					play = false;
				}

				saveRequest = null;
			}
			else if (urlRequest != null)
			{
				try
				{
					System.out.println("urlStream");

					urlStream = new DataInputStream((new URL(new URL(Configuration.CODEBASE), urlRequest)).openStream());
				}
				catch (Exception _ex)
				{
					urlStream = null;
				}

				urlRequest = null;
			}

			try
			{
				Thread.sleep(50L);
			}
			catch (Exception ignored)
			{
			}
		}
	}

	/**
	 * Plays the specified midi sequence.
	 *
	 * @param location
	 */
	private void playMidi(String location)
	{
		Sequence sequence;
		music = null;
		synthesizer = null;
		File midiFile = new File(location);

		try
		{
			sequence = MidiSystem.getSequence(midiFile);
			music = MidiSystem.getSequencer();

			music.open();
			music.setSequence(sequence);
		}
		catch (Exception e)
		{
			System.err.println("Problem loading MIDI file.");
			e.printStackTrace();
			return;
		}

		if (music instanceof Synthesizer)
		{
			synthesizer = (Synthesizer) music;
		}
		else
		{
			try
			{
				synthesizer = MidiSystem.getSynthesizer();

				synthesizer.open();

				if (synthesizer.getDefaultSoundbank() == null)
				{
					music.getTransmitter().setReceiver(MidiSystem.getReceiver());
				}
				else
				{
					music.getTransmitter().setReceiver(synthesizer.getReceiver());
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
		}

		music.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
		music.start();
	}

	enum Position
	{
		LEFT, RIGHT
	}

}
