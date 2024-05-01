import components.ExampleScript
import components.AnotherExampleScript
import glm_.vec3.Vec3
import org.openartifact.artifact.core.ApplicationProfile
import org.openartifact.artifact.core.GameContext
import org.openartifact.artifact.core.graphics.window.WindowProfile
import org.openartifact.artifact.game.nodes.CameraNode
import org.openartifact.artifact.game.scene.Scene
import org.openartifact.artifact.game.scene.SceneProfile
import org.openartifact.artifact.game.scene.writeNodes
import org.openartifact.debug.components.CameraController
import org.openartifact.debug.nodes.BunnyNode


fun main() {

    constructTestScene()

    GameContext.createContext {
        configureApplicationProfile(
            ApplicationProfile(
                "ArtifactTestProject",
                "TestScene"
            )
        )

        configureWindowProfile(
            WindowProfile(
                "TestGame",
                0,
                0,
                854,
                480
            )
        )
    }
        .set()
        .registerComponent(CameraController::class)
        .registerComponent(ExampleScript::class)
        .registerComponent(AnotherExampleScript::class)
        .registerNode(BunnyNode::class)
        .run()

    println(GameContext.current().sceneManager.activeScene?.nodes)

}

private fun constructTestScene() : Scene {
    val scene = Scene(SceneProfile("TestScene"))

    val camera = CameraNode(90.0f, Vec3(3, 3, 3), Vec3(0, 0,0))
    val cube = BunnyNode(Vec3(0, 0, 0), Vec3(0, 0, 0), Vec3(1, 1, 1))

    scene.nodes.add(camera)
    scene.nodes.add(cube)

    println(writeNodes(scene))

    return scene
}