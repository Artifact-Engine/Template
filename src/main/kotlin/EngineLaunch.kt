import components.ExampleScript
import components.AnotherExampleScript
import org.openartifact.artifact.core.ApplicationProfile
import org.openartifact.artifact.core.GameContext
import org.openartifact.artifact.core.event.events.KeyPressEvent
import org.openartifact.artifact.core.event.handler
import org.openartifact.artifact.core.graphics.window.WindowProfile
import org.lwjgl.glfw.GLFW.*


fun main() {

    val context = GameContext.createContext {
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
                60,
                800,
                600
            )
        )
    }.setCurrent()
        .registerComponent(ExampleScript::class)
        .registerComponent(AnotherExampleScript::class)
        .run()


    val keyInputHandler = handler<KeyPressEvent>({ event ->
        if (event.key == GLFW_KEY_ESCAPE) {
            glfwSetWindowShouldClose(GameContext.current().windowProfile().windowId, true)
        }
    })


}