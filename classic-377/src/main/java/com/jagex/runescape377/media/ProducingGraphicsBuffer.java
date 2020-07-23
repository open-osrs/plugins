package com.jagex.runescape377.media;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public class ProducingGraphicsBuffer implements ImageProducer, ImageObserver
{

	public int pixels[];
	public int width;
	public int height;
	public ColorModel colorModel;
	public ImageConsumer imageConsumer;
	public Image image;

	public ProducingGraphicsBuffer(int width, int height, Component component)
	{
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
		colorModel = new DirectColorModel(32, 0xff0000, 65280, 255);
		image = component.createImage(this);
		drawPixels();
		component.prepareImage(image, this);
		drawPixels();
		component.prepareImage(image, this);
		drawPixels();
		component.prepareImage(image, this);
		createRasterizer();
	}

	public void createRasterizer()
	{
		Rasterizer.createRasterizer(pixels, width, height);
	}

	public void drawGraphics(int x, int y, Graphics graphics)
	{
		drawPixels();
		graphics.drawImage(image, x, y, this);
	}

	public synchronized void addConsumer(ImageConsumer imageConsumer)
	{
		this.imageConsumer = imageConsumer;
		imageConsumer.setDimensions(width, height);
		imageConsumer.setProperties(null);
		imageConsumer.setColorModel(colorModel);
		imageConsumer.setHints(14);
	}

	public synchronized boolean isConsumer(ImageConsumer imageConsumer)
	{
		return this.imageConsumer == imageConsumer;
	}

	public synchronized void removeConsumer(ImageConsumer imageConsumer)
	{
		if (this.imageConsumer == imageConsumer)
		{
			this.imageConsumer = null;
		}
	}

	public void startProduction(ImageConsumer imageConsumer)
	{
		addConsumer(imageConsumer);
	}

	public void requestTopDownLeftRightResend(ImageConsumer imageConsumer)
	{
		System.out.println("TDLR");
	}

	public synchronized void drawPixels()
	{
		if (imageConsumer == null)
		{
			return;
		}
		else
		{
			imageConsumer.setPixels(0, 0, width, height, colorModel, pixels, 0, width);
			imageConsumer.imageComplete(2);
			return;
		}
	}

	public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
	{
		return true;
	}


}
