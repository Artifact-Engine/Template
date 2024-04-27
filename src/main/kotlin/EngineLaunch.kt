import components.TestComponentScript
import org.openartifact.artifact.core.ApplicationProfile
import org.openartifact.artifact.core.GameContext
import org.openartifact.artifact.core.event.events.KeyPressEvent
import org.openartifact.artifact.core.event.handler
import org.openartifact.artifact.core.graphics.RenderAPI
import org.openartifact.artifact.core.graphics.window.WindowProfile
import org.lwjgl.glfw.GLFW.*
import org.openartifact.artifact.core.graphics.EngineProfile


fun main() {

    val context = GameContext.createContext {

        configureEngineProfile(
            EngineProfile(
                RenderAPI.OpenGL
            )
        )

        configureApplicationProfile(
            ApplicationProfile(
                "ArtifactTestProject",
                "TestScene"
            )
        )

        configureWindowProfile(
            WindowProfile(
                "TestGame",
                60,
                800,
                600
            )
        )
    }.setCurrent()
        .registerComponent(TestComponentScript::class)
        .run()


    val keyInputHandler = handler<KeyPressEvent>({ event ->
        if (event.key == GLFW_KEY_ESCAPE) {
            glfwSetWindowShouldClose(GameContext.current().windowProfile().windowId, true)
        }
    })

}