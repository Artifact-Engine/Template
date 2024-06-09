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
import org.openartifact.artifact.utils.calculateModelMatrix
import org.openartifact.artifact.core.Application
import org.openartifact.artifact.core.event.events.FPSUpdateEvent
import org.openartifact.artifact.core.event.events.ResizeEvent
import org.openartifact.artifact.core.event.subscribe
import org.openartifact.artifact.extensions.multiValuedMapOf
import org.openartifact.artifact.extensions.reset
import org.openartifact.artifact.graphics.*
import org.openartifact.artifact.graphics.cameras.PerspectiveCamera
import org.openartifact.artifact.graphics.interfaces.*
import org.openartifact.artifact.graphics.platform.opengl.OpenGLShader
import org.openartifact.artifact.graphics.platform.opengl.ShaderType
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

    private lateinit var camera : PerspectiveCamera
    private val movement = Vec3()

    private val cameraInputMap = createKeyInputMap {
        KEY_W to { movement.z += -1f }
        KEY_A to { movement.x += -1f }
        KEY_S to { movement.z += 1f }
        KEY_D to { movement.x += 1f }

        KEY_E to { movement.y += 1f }
        KEY_Q to { movement.y += -1f }
    }

    inner class Cube(val pos : Vec3) {
        var shader : IShader

        var vertexArray : IVertexArray
        var vertexBuffer : IVertexBuffer
        var indexBuffer : IIndexBuffer


        val vertices = floatArrayOf(
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,

            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f
        )

        val indices = intArrayOf()

        init {
            val bufferLayout = renderer.choose<IBufferLayout>().create(
                multiValuedMapOf(
                    DataType.Vec3 to "a_Position",
                    DataType.Vec3 to "a_Normal"
                )
            )

            vertexBuffer = renderer.choose<IVertexBuffer>().create(vertices, bufferLayout)

            indexBuffer = renderer.choose<IIndexBuffer>().create(indices)

            vertexArray = renderer.choose<IVertexArray>().create(
                vertexBuffer,
                indexBuffer
            )

            val vertex = resource("shaders/vertex.glsl")
            val fragment = resource("shaders/fragment.glsl")

            shader = renderer.choose<IShader>()
                .create(
                    listOf(
                        OpenGLShader.ShaderModule(vertex.asText(), ShaderType.VERTEX),
                        OpenGLShader.ShaderModule(fragment.asText(), ShaderType.FRAGMENT),
                    )
                )
        }
    }

    private lateinit var cube1 : Cube

    override fun init() {
        logger.info("Sandbox init")

        renderer = createRenderer()

        cube1 = Cube(Vec3(1, 0, 0))

        camera = PerspectiveCamera(90f, Vec3(4, 4, 4), Vec3(22.5f, -45, 0))

        subscribe(FPSUpdateEvent::class) { event ->
            logger.info("FPS: ${event.fps}")
        }

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
            lightPosDirection = -lightPosDirection
        }
        lightPosY += lightPosDirection

        renderer.frame {
            renderFlow {
                cube1.apply {
                    commit(shader) {
                        parameterMat4("u_Projection", camera.calculateProjectionMatrix())
                        parameterMat4("u_View", camera.calculateViewMatrix())
                        parameterMat4("u_Model", calculateModelMatrix(pos, Vec3(0, 0, 0)))
                        parameterVec3("u_Color", Vec3(.6, .6, .6))
                        parameterVec3("u_Light_Pos", Vec3(2, lightPosY, 0))
                        parameterVec3("u_Light_Color", Vec3(.8, .8, .8))
                    }

                    commit(vertexArray)

                    push()
                }
            }
        }
    }

    override fun shutdown() {
        logger.info("Sandbox shutdown")
    }

}