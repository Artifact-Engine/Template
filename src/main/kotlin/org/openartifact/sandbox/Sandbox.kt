/*
 * Copyright Artifact-Engine (c) 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.openartifact.sandbox

import glm_.vec2.Vec2
import glm_.vec3.Vec3
import org.openartifact.artifact.core.application.Application
import org.openartifact.artifact.core.event.events.FPSUpdateEvent
import org.openartifact.artifact.core.event.events.ResizeEvent
import org.openartifact.artifact.core.event.subscribe
import org.openartifact.artifact.extensions.reset
import org.openartifact.artifact.graphics.*
import org.openartifact.artifact.graphics.cameras.PerspectiveCamera
import org.openartifact.artifact.graphics.mesh.Mesh
import org.openartifact.artifact.graphics.window.WindowConfig
import org.openartifact.artifact.input.*
import org.openartifact.artifact.input.MouseInput.hold
import org.openartifact.artifact.input.MouseInput.move
import org.openartifact.artifact.resource.resource

@Suppress("unused")
class Sandbox : Application(
    RenderAPI.OpenGL,
    WindowConfig(
        854f, 480f, "Sandbox"
    )
) {

    private val movement = Vec3()

    private val cameraInputMap = createKeyInputMap {
        KEY_W to { movement.z += - 1f }
        KEY_A to { movement.x += - 1f }
        KEY_S to { movement.z += 1f }
        KEY_D to { movement.x += 1f }

        KEY_E to { movement.y += 1f }
        KEY_Q to { movement.y += - 1f }
    }

    private lateinit var mesh : Mesh

    override fun init() {
        logger.info("Sandbox init")

        renderer = createRenderer()

        mesh = Mesh(resource("meshes/airboat.obj"))

        camera = PerspectiveCamera(90f, Vec3(4, 4, 4), Vec3(22.5f, - 45, 0))

        subscribe(ResizeEvent::class) { event ->
            logger.info("Resized to ${event.x} ${event.y}")
        }
    }

    private var lightPosY = 0.0f
    private var lightPosDirection = 0.05f

    override fun update(deltaTime : Double) {
        cameraInputMap.process()
            .run {
                camera.move(
                    movement,
                    if (getKeyDown(KEY_LEFT_SHIFT)) 8f * deltaTime.toFloat() else 2f * deltaTime.toFloat()
                )
            }

        MouseInput.process {
            move { pos : Vec2 ->
                hold(MOUSE_BUTTON_1) {
                    camera.rotate(pos.y, pos.x, 0f)
                }
            }
        }

        movement.reset()

        if (lightPosY > 5.0f || lightPosY < 0f) {
            lightPosDirection = - lightPosDirection
        }
        lightPosY += lightPosDirection

        renderer.frame {
            commit(mesh)
        }
    }

    override fun shutdown() {
        logger.info("Sandbox shutdown")
    }

}