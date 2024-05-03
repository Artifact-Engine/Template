import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import org.openartifact.artifact.core.Application
import org.openartifact.artifact.core.Artifact
import org.openartifact.artifact.input.*
import org.openartifact.artifact.input.KeyConstants.KEY_LEFT_CONTROL
import org.openartifact.artifact.input.KeyConstants.KEY_Q

class Sandbox : Application() {

    private val keyInputMap = createKeyInputMap {
        KEY_LEFT_CONTROL with KEY_Q to { GLFW.glfwSetWindowShouldClose(Artifact.instance.window.handle, true) }
    }

    override fun init() {
        logger.info("Sandbox init")
    }

    override fun update() {
        keyInputMap.process()

        glBegin(GL_TRIANGLES)
        glColor3f(1.0f, 1.0f, 1.0f)

        glVertex2f(- 0.5f, - 0.5f)
        glVertex2f(0.5f, - 0.5f)
        glVertex2f(0.0f, 0.5f)

        glEnd()
    }

    override fun shutdown() {
        logger.info("Sandbox shutdown")
    }

}