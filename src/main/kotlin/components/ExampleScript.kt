package components

import org.openartifact.artifact.game.Component

class ExampleScript : Component() {

    override fun awake() {
        println("awaken")
    }

    override fun update() {

    }

    override fun rest() {
        println("resting")
    }

}