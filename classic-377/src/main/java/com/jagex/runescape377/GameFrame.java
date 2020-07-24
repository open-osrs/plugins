package com.jagex.runescape377;

import com.jagex.runescape377.config.Configuration;
import java.awt.Graphics;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameFrame extends JFrame
{
	private final GameShell gameStub;

	public GameFrame(GameShell gameStub, int width, int height)
	{
		this.gameStub = gameStub;
		pack();
		setTitle("Classic Rev 377 - " + Configuration.USERNAME);
		setResizable(false);
		gameStub.extraWidth = getInsets().left;
		gameStub.extraHeight = getInsets().top;
		setSize(width + gameStub.extraWidth + getInsets().right, height + gameStub.extraHeight + getInsets().bottom);
		setVisible(true);
		toFront();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	@Override
	public void update(Graphics graphics)
	{
		gameStub.update();
	}

	@Override
	public Graphics getGraphics()
	{
		Graphics graphics = super.getGraphics();
		graphics.translate(gameStub.extraWidth, gameStub.extraHeight);
		return graphics;
	}

	@Override
	public void paint(Graphics graphics)
	{
		gameStub.paint();
	}

}
