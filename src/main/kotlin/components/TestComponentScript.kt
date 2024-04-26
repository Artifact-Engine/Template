package components

import org.openartifact.artifact.game.Component

class TestComponentScript : Component() {

    override fun awake() {
        println("awaken")
    }

    override fun update() {
        println("updated")
    }

    override fun rest() {
        println("resting")
    }

}