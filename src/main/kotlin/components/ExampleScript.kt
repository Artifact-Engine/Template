package components

import org.openartifact.artifact.game.Component

class ExampleScript : Component() {

    override fun awake() {

    }

    override fun render(deltaTime : Double) {
        println("RENDER $deltaTime")
    }

    override fun update(physicsDeltaTime: Double) {
        println("UPDATE $physicsDeltaTime")
    }

    override fun rest() {

    }

}