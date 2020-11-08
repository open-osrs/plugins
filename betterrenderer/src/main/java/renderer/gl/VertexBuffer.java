package renderer.gl;

import java.nio.ByteBuffer;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL15C.glGenBuffers;

public class VertexBuffer implements AutoCloseable
{
	public final int id;
	private int vertexCount;
	private boolean closed = false;

	public VertexBuffer()
	{
		id = glGenBuffers();
	}

	public void set(int vertexCount, ByteBuffer buffer)
	{
		if (closed)
		{
			throw new IllegalStateException("closed");
		}

		this.vertexCount = vertexCount;
		glBindBuffer(GL_ARRAY_BUFFER, id);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}

	@Override
	public void close()
	{
		if (!closed)
		{
			closed = true;
			glDeleteBuffers(id);
		}
	}

	public int getVertexCount()
	{
		return vertexCount;
	}
}
