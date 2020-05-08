package net.runelite.client.plugins.pktools;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PkToolsPrayerSwaps
{
	NONE("Off"),
	PIETY("Piety"),
	RIGOUR("Rigour"),
	AUGURY("Augury");

	private final String name;

	@Override
	public String toString()
	{
		return this.name;
	}
}