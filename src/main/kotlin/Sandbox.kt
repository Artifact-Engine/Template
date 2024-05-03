import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL46.*
import org.openartifact.artifact.Entry
import org.openartifact.artifact.core.Application
import org.openartifact.artifact.core.Artifact
import org.openartifact.artifact.input.*
import org.openartifact.artifact.input.KeyConstants.KEY_LEFT_CONTROL
import org.openartifact.artifact.input.KeyConstants.KEY_Q

@Entry
class Sandbox : Application() {

    private val keyInputMap = createKeyInputMap {
        KEY_LEFT_CONTROL with KEY_Q to { GLFW.glfwSetWindowShouldClose(Artifact.instance.window.handle, true) }
    }

    private var vertexArray = 0
    private var vertexBuffer = 0
    private var indexBuffer = 0

    override fun init() {
        logger.info("Sandbox init")

        vertexArray = glGenVertexArrays()
        glBindVertexArray(vertexArray)

        vertexBuffer = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer)

        val vertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f,
             0.5f, -0.5f, 0.0f,
             0.0f,  0.5f, 0.0f
        )

        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

        indexBuffer = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer)

        val indices = intArrayOf(0, 1, 2)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)


    }

    override fun update() {
        keyInputMap.process()

        glClearColor(0.1f, 0.1f, 0.1f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glBindVertexArray(vertexArray)
        glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0)
    }

    override fun shutdown() {
        logger.info("Sandbox shutdown")
    }

}