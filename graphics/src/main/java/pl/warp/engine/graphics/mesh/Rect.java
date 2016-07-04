package pl.warp.engine.graphics.mesh;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import pl.warp.engine.graphics.utility.BufferTools;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

/**
 * @author Jaca777
 *         Created 2015-06-25 at 14
 */
public class Rect {

    private static final float[] VERTICES = new float[]{
            -1.0f, -1.0f, 0.0f, 1.0f,
            1.0f, -1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            -1.0f, 1.0f, 0.0f, 1.0f
    };

    private static final float[] TEX_COORDS = new float[]{
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
    };

    private static final int[] INDICES = new int[]{
            0, 1, 2, 0, 2, 3
    };

    public static final int INDICES_AMOUNT = INDICES.length;

    private int vao;
    private int vertexBuffer;
    private int texCoordBuffer;
    private int indexBuffer;

    public Rect(int vertexAttr, int texCoordAttr) {
        this.vao = GL30.glGenVertexArrays();

        this.vertexBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferTools.toDirectBuffer(VERTICES), GL15.GL_STATIC_DRAW);

        this.texCoordBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texCoordBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferTools.toDirectBuffer(TEX_COORDS), GL15.GL_STATIC_DRAW);

        this.indexBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, BufferTools.toDirectBuffer(INDICES), GL15.GL_STATIC_DRAW);

        GL30.glBindVertexArray(vao);
        GL20.glEnableVertexAttribArray(vertexAttr);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
        GL20.glVertexAttribPointer(vertexAttr, 4, GL_FLOAT, false, 0, 0);

        GL20.glEnableVertexAttribArray(texCoordAttr);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texCoordBuffer);
        GL20.glVertexAttribPointer(texCoordAttr, 2, GL_FLOAT, false, 0, 0);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        GL30.glBindVertexArray(0);
    }

    public void bind() {
        GL30.glBindVertexArray(vao);
    }

    public void destroy() {
        GL30.glDeleteVertexArrays(vao);
        GL15.glDeleteBuffers(new int[]{vertexBuffer, texCoordBuffer, indexBuffer});
    }

    public void unbind() {
        GL30.glBindVertexArray(0);
    }
}
