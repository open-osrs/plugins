package renderer.world;

public final class Location
{
	public final ObjectDefinition object;
	public final LocationType type;
	public final int rotation;
	public final Position position;

	public Location(ObjectDefinition object, LocationType type, int rotation, Position position)
	{
		this.object = object;
		this.type = type;
		this.rotation = rotation;
		this.position = position;
	}
}
