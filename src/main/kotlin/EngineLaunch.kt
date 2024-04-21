import org.openartifact.artifact.core.Engine
import org.openartifact.artifact.game.components.TransformComponent

fun main(args : Array<String>) {
    Engine

    Engine.application.scenes.forEach { (_, scene) ->
        scene.nodes.forEach { node ->
            println(node)
            node.components.forEach { component ->
                component as TransformComponent
                println(component.scale)
            }
        }
    }

    println(Engine.application.getCurrentScene())
}