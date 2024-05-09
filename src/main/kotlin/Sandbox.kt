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

    private lateinit var testArray : IVertexArray

    override fun init() {
        logger.info("Sandbox init")

        renderer = OpenGLRenderer()

        testArray = renderer.choose<IVertexArray>().create()

        val vertices = floatArrayOf(
            // Square vertices
            -0.5f, -0.5f, 0.0f, // bottom-left
            0.5f, -0.5f, 0.0f,  // bottom-right
            0.5f, 0.5f, 0.0f,   // top-right
            -0.5f, 0.5f, 0.0f,  // top-left

            // Triangle vertices
            -0.5f, -0.5f, 0.0f, // bottom-left
            0.5f, -0.5f, 0.0f,  // bottom-right
            0.0f, 0.5f, 0.0f    // top-center
        )

        val indices = intArrayOf(
            // Square indices
            0, 1, 2, // first triangle
            2, 3, 0, // second triangle

            // Triangle indices
            4, 5, 6  // single triangle
        )

        val layout = renderer.choose<IBufferLayout>().create(
            mapOf(
                DataType.Vec3 to "a_Position",
            )
        )

        val vb = renderer.choose<IVertexBuffer>().create(vertices, layout)
        val idx = renderer.choose<IIndexBuffer>().create(indices)

        testArray = renderer.choose<IVertexArray>().create()
        testArray.addVertexBuffer(vb)
        testArray.defineIndexBuffer(idx)

        val vertexSource = """
            #version 330 core
            
            layout(location = 0) in vec3 a_Position;
            
            out vec3 v_Position;
            
            void main() {
                v_Position = a_Position;
                gl_Position = vec4(a_Position, 1.0);
            }
            
        """.trimIndent()

        val fragmentSource = """
            #version 330 core
            
            layout(location = 0) out vec4 color;
            
            in vec3 v_Position;
            
            void main() {
                color = vec4(v_Position * 0.5 + 0.5, 1.0);
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

        testArray.bind()
        glDrawElements(GL_TRIANGLES, testArray.indexBuffer.count, GL_UNSIGNED_INT, 0)
    }

    override fun shutdown() {
        logger.info("Sandbox shutdown")
    }

}