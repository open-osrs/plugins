package com.jagex.runescape377.media.renderable;

import com.jagex.runescape377.collection.CacheableNode;
import com.jagex.runescape377.media.VertexNormal;

public class Renderable extends CacheableNode
{

	public VertexNormal[] verticesNormal;
	public int modelHeight = 1000;

	public void renderAtPoint(int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2)
	{
		Model model = getRotatedModel();
		if (model != null)
		{
			modelHeight = model.modelHeight;
			model.renderAtPoint(i, j, k, l, i1, j1, k1, l1, i2);
		}
	}

	public Model getRotatedModel()
	{
		return null;
	}


}
