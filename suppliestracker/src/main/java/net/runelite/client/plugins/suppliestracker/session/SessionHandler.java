package net.runelite.client.plugins.suppliestracker.session;

import com.google.inject.Inject;
import net.runelite.api.Client;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import static net.runelite.client.RuneLite.RUNELITE_DIR;

public class SessionHandler
{
	private static final File SESSION_DIR = new File(RUNELITE_DIR, "supplies");

	private final File playerFolder = SESSION_DIR;

	private final Client client;

	private final HashMap<Integer, Integer> supplies = new HashMap<>();
	private final HashMap<Integer, Integer> charges = new HashMap<>();

	@Inject
	public SessionHandler(Client client)
	{
		this.client = client;
		SESSION_DIR.mkdir();
	}

	public void setupMaps(int itemId, int quantity, String type)
	{
		if (type.equals("c"))
		{
			this.charges.put(itemId, this.charges.getOrDefault(itemId, 0) + quantity);
		}
		else
		{
			this.supplies.put(itemId, this.supplies.getOrDefault(itemId, 0) + quantity);
		}
	}

	public void clearItem(int itemId)
	{
		this.supplies.remove(itemId);
		this.charges.remove(itemId);
		buildSessionFile(this.charges, this.supplies);
	}

	public void clearSupplies()
	{
		this.supplies.clear();
		this.charges.clear();
		buildSessionFile(this.charges, this.supplies);
	}

	public void addtoSession(int itemId, int quantity, String type)
	{
		if (type.equals("c"))
		{
			this.charges.put(itemId, this.charges.getOrDefault(itemId, 0) + quantity);
		}
		else
		{
			this.supplies.put(itemId, this.supplies.getOrDefault(itemId, 0) + quantity);
		}
		buildSessionFile(this.charges, this.supplies);
	}

	private void buildSessionFile(HashMap<Integer, Integer> c, HashMap<Integer, Integer> s)
	{
		try
		{
			File sessionFile = new File(RUNELITE_DIR + "/supplies/" + client.getUsername() + ".txt");

			if (!sessionFile.createNewFile())
			{

				sessionFile.delete();
				sessionFile.createNewFile();
			}

			try (FileWriter f = new FileWriter(sessionFile, Charset.defaultCharset(), true); BufferedWriter b = new BufferedWriter(f); PrintWriter p = new PrintWriter(b);)
			{
				for (int id:c.keySet())
				{
					p.println("c" + id + ":" + c.get(id));
				}
				for (int id:s.keySet())
				{
					p.println(id + ":" + s.get(id));
				}
			}
			catch (IOException i)
			{
				i.printStackTrace();
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void clearSession()
	{
		supplies.clear();
		charges.clear();
	}
}
