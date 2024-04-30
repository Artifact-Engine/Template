package components

import org.openartifact.artifact.game.Component

class ExampleScript : Component() {

    override fun awake() {

    }

    var i1 = 0
    var i2 = 0

    override fun render(deltaTime : Double) {
        i2++
    }

    override fun update(physicsDeltaTime: Double) {

    }

    override fun rest() {

    }

}