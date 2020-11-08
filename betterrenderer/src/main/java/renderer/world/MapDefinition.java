package renderer.world;

public class MapDefinition
{
	public int regionX;
	public int regionY;
	public Tile[][][] tiles = new Tile[4][64][64];

	public static class Tile
	{
		public Integer height;
		public byte settings;
		public OverlayDefinition overlay;
		public OverlayShape overlayShape;
		public byte overlayRotation;
		public UnderlayDefinition underlay;
	}
}
