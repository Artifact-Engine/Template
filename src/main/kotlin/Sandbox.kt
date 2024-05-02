import org.lwjgl.opengl.GL11.*
import org.openartifact.artifact.core.Application

class Sandbox : Application() {

    override fun init() {

    }

    override fun update() {
        glBegin(GL_TRIANGLES)

        glVertex2f(- 0.5f, - 0.5f)
        glVertex2f(0.5f, - 0.5f)
        glVertex2f(0.0f, 0.5f)

        glEnd();
    }

    override fun shutdown() {

    }

}