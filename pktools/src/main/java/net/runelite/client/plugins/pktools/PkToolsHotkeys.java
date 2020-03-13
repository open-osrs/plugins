package net.runelite.client.plugins.pktools;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.event.KeyEvent;

@Getter
@RequiredArgsConstructor
public enum PkToolsHotkeys
{
	F1(KeyEvent.VK_F1),
	F2(KeyEvent.VK_F2),
	F3(KeyEvent.VK_F3),
	F4(KeyEvent.VK_F4),
	F5(KeyEvent.VK_F5),
	F6(KeyEvent.VK_F6),
	F7(KeyEvent.VK_F7),
	F8(KeyEvent.VK_F8),
	F9(KeyEvent.VK_F9),
	F10(KeyEvent.VK_F10),
	F11(KeyEvent.VK_F11),
	F12(KeyEvent.VK_F12),
	ESC(KeyEvent.VK_ESCAPE);

	private final int key;
}
