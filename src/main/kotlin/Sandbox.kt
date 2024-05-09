import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL20.GL_VERTEX_SHADER
import org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL46.*
import org.openartifact.artifact.ApplicationEntry
import org.openartifact.artifact.core.Application
import org.openartifact.artifact.core.Artifact
import org.openartifact.artifact.graphics.choose
import org.openartifact.artifact.graphics.interfaces.*
import org.openartifact.artifact.graphics.platform.opengl.OpenGLRenderer
import org.openartifact.artifact.graphics.platform.opengl.OpenGLShader
import org.openartifact.artifact.input.KeyConstants.KEY_LEFT_CONTROL
import org.openartifact.artifact.input.KeyConstants.KEY_Q
import org.openartifact.artifact.input.createKeyInputMap
import org.openartifact.artifact.input.with

@ApplicationEntry
@Suppress("unused")
class Sandbox : Application() {

    /**
     * Creates a keyInputMap.
     * @see update for processing.
     */
    private val keyInputMap = createKeyInputMap {
        KEY_LEFT_CONTROL with KEY_Q to { GLFW.glfwSetWindowShouldClose(Artifact.instance.window.handle, true) }
    }

    override fun init() {
        logger.info("Sandbox init")

        renderer = OpenGLRenderer()

        vertexArray = renderer.choose<IVertexArray>().create()

        val vertices = floatArrayOf(
            // Vertices-------  Color-----------------  Test------
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
        )

        val indices = intArrayOf(0, 1, 2)

        val bufferLayout = renderer.choose<IBufferLayout>().create(
            mapOf(
                DataType.Vec3 to "a_Position",
                DataType.Vec4 to "a_Color",
                DataType.Vec2 to "a_Test",
            )
        )

        vertexBuffer = renderer.choose<IVertexBuffer>().create(vertices, bufferLayout)

        indexBuffer = renderer.choose<IIndexBuffer>().create(indices)

        vertexArray.addVertexBuffer(vertexBuffer)
        vertexArray.defineIndexBuffer(indexBuffer)

        val vertexSource = """
            #version 330 core
            
            layout(location = 0) in vec3 a_Position;
            layout(location = 1) in vec4 a_Color;
            
            out vec3 v_Position;
            out vec4 v_Color;
            
            void main() {
                v_Position = a_Position;
                gl_Position = vec4(a_Position, 1.0);
                v_Color = a_Color;
            }
            
        """.trimIndent()

        val fragmentSource = """
            #version 330 core
            
            layout(location = 0) out vec4 color;
            
            in vec3 v_Position;
            in vec4 v_Color;
            
            void main() {
                color = v_Color;
            }
        """.trimIndent()

        @Deprecated("Only supports OpenGL.")
        shader = renderer.choose<IShader>(
            listOf(
                OpenGLShader.ShaderModule(vertexSource, GL_VERTEX_SHADER),
                OpenGLShader.ShaderModule(fragmentSource, GL_FRAGMENT_SHADER),
            )
        ).create()
    }

    override fun update() {
        keyInputMap.process()

        shader!!.bind()

        (renderer as OpenGLRenderer).clearScreenBuffers()

        vertexArray.bind()
        glDrawElements(GL_TRIANGLES, indexBuffer.count, GL_UNSIGNED_INT, 0)
    }

    override fun shutdown() {
        logger.info("Sandbox shutdown")
    }

}