import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL46.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL46.GL_VERTEX_SHADER
import org.openartifact.artifact.core.ApplicationEntry
import org.openartifact.artifact.core.Application
import org.openartifact.artifact.core.Artifact
import org.openartifact.artifact.graphics.RenderAPI
import org.openartifact.artifact.graphics.choose
import org.openartifact.artifact.graphics.flow.renderFlow
import org.openartifact.artifact.graphics.interfaces.*
import org.openartifact.artifact.graphics.platform.opengl.OpenGLRenderer
import org.openartifact.artifact.graphics.platform.opengl.OpenGLShader
import org.openartifact.artifact.input.KeyConstants.KEY_LEFT_CONTROL
import org.openartifact.artifact.input.KeyConstants.KEY_Q
import org.openartifact.artifact.input.createKeyInputMap
import org.openartifact.artifact.input.with

@ApplicationEntry
@Suppress("unused")
class Sandbox : Application(RenderAPI.OpenGL) {

    private val keyInputMap = createKeyInputMap {
        KEY_LEFT_CONTROL with KEY_Q to { GLFW.glfwSetWindowShouldClose(Artifact.instance.window.handle, true) }
    }

    private lateinit var triangleShader : IShader
    private lateinit var rectShader : IShader

    private lateinit var triangleVertexArray : IVertexArray
    private lateinit var triangleVertexBuffer : IVertexBuffer
    private lateinit var triangleIndexBuffer : IIndexBuffer

    private lateinit var rectVertexArray : IVertexArray
    private lateinit var rectVertexBuffer : IVertexBuffer
    private lateinit var rectIndexBuffer : IIndexBuffer

    override fun init() {
        logger.info("Sandbox init")

        renderer = createRenderer()

        // Triangle
        triangleVertexArray = renderer.choose<IVertexArray>().create()

        val triangleVertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f
        )

        val triangleIndices = intArrayOf(0, 1, 2)

        val triangleLayout = renderer.choose<IBufferLayout>().create(
            mapOf(
                DataType.Vec3 to "a_Position",
                DataType.Vec4 to "a_Color"
            )
        )

        triangleVertexBuffer = renderer.choose<IVertexBuffer>().create(triangleVertices, triangleLayout)

        triangleIndexBuffer = renderer.choose<IIndexBuffer>().create(triangleIndices)

        triangleVertexArray.addVertexBuffer(triangleVertexBuffer)
        triangleVertexArray.defineIndexBuffer(triangleIndexBuffer)

        // Rectangle
        rectVertexArray = renderer.choose<IVertexArray>().create()

        val rectVertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f
        )

        val rectIndices = intArrayOf(0, 1, 2, 2, 3, 0)

        val rectLayout = renderer.choose<IBufferLayout>().create(
            mapOf(
                DataType.Vec3 to "a_Position"
            )
        )

        rectVertexBuffer = renderer.choose<IVertexBuffer>().create(rectVertices, rectLayout)

        rectIndexBuffer = renderer.choose<IIndexBuffer>().create(rectIndices)

        rectVertexArray.addVertexBuffer(rectVertexBuffer)
        rectVertexArray.defineIndexBuffer(rectIndexBuffer)

        // Shader
        val triangleVertexSource = """
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

        val triangleFragmentSource = """
            #version 330 core
            
            layout(location = 0) out vec4 color;
            
            in vec3 v_Position;
            in vec4 v_Color;
            
            void main() {
                color = v_Color;
            }
        """.trimIndent()

        triangleShader = renderer.choose<IShader>(
            listOf(
                OpenGLShader.ShaderModule(triangleVertexSource, GL_VERTEX_SHADER),
                OpenGLShader.ShaderModule(triangleFragmentSource, GL_FRAGMENT_SHADER)
            )
        ).create()

        // Shader2
        val rectangleVertexSource = """
            #version 330 core
            
            layout(location = 0) in vec3 a_Position;
            
            out vec3 v_Position;
            
            void main() {
                v_Position = a_Position;
                gl_Position = vec4(a_Position, 1.0);
            }
            
        """.trimIndent()

        val rectangleFragmentSource = """
            #version 330 core
            
            layout(location = 0) out vec4 color;
            
            in vec3 v_Position;
            
            void main() {
                color = vec4(0.0, 0.0, 0.2, 1.0);
            }
        """.trimIndent()

        rectShader = renderer.choose<IShader>(
            listOf(
                OpenGLShader.ShaderModule(rectangleVertexSource, GL_VERTEX_SHADER),
                OpenGLShader.ShaderModule(rectangleFragmentSource, GL_FRAGMENT_SHADER)
            )
        ).create()
    }

    override fun update() {
        keyInputMap.process()

        (renderer as OpenGLRenderer).clearScreenBuffers()

        renderFlow {
            commit(rectShader)
            commit(rectVertexArray)

            commit(triangleShader)
            commit(triangleVertexArray)
        }
    }

    override fun shutdown() {
        logger.info("Sandbox shutdown")
    }

}
