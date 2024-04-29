import components.ExampleScript
import components.AnotherExampleScript
import glm_.vec3.Vec3
import org.openartifact.artifact.core.ApplicationProfile
import org.openartifact.artifact.core.GameContext
import org.openartifact.artifact.core.event.events.KeyPressEvent
import org.openartifact.artifact.core.event.handler
import org.openartifact.artifact.core.graphics.window.WindowProfile
import org.lwjgl.glfw.GLFW.*
import org.openartifact.artifact.core.graphics.window.AspectRatio
import org.openartifact.artifact.game.nodes.CameraNode
import org.openartifact.artifact.game.nodes.CubeNode
import org.openartifact.artifact.game.scene.Scene
import org.openartifact.artifact.game.scene.SceneProfile
import org.openartifact.artifact.game.scene.writeNodes


fun main() {

    constructTestScene()

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
                854,
                480,
                AspectRatio.RATIO_16_9
            )
        )
    }.setCurrent()
        .registerComponent(ExampleScript::class)
        .registerComponent(AnotherExampleScript::class)
        .run()
}


val keyInputHandler = handler<KeyPressEvent>({ event ->
    if (event.key == GLFW_KEY_ESCAPE) {
        glfwSetWindowShouldClose(GameContext.current().windowProfile().windowId, true)
    }
})

private fun constructTestScene() : Scene {
    val scene = Scene(SceneProfile("TestScene"))

    val camera = CameraNode(60.0f, Vec3(4, 3, 3), Vec3(0, 0,0), Vec3(0, 1, 0))
    val cube = CubeNode()

    scene.nodes.add(camera)
    scene.nodes.add(cube)

    println(writeNodes(scene))

    return scene
}