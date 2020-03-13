package net.runelite.client.plugins.pktools;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PkToolsAutoPrayerModes
{
	OFF(0),
	HOTKEY(1),
	AUTO(2);

	private final int mode;
}
