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
import org.openartifact.artifact.core.ApplicationEntry
import org.openartifact.artifact.core.event.events.FPSUpdateEvent
import org.openartifact.artifact.core.event.subscribe
import org.openartifact.artifact.extensions.reset
import org.openartifact.artifact.graphics.*
import org.openartifact.artifact.graphics.cameras.PerspectiveCamera
import org.openartifact.artifact.graphics.interfaces.*
import org.openartifact.artifact.graphics.platform.opengl.OpenGLShader
import org.openartifact.artifact.graphics.platform.opengl.ShaderType
import org.openartifact.artifact.graphics.window.WindowConfig
import org.openartifact.artifact.input.KeyConstants.KEY_A
import org.openartifact.artifact.input.KeyConstants.KEY_D
import org.openartifact.artifact.input.KeyConstants.KEY_E
import org.openartifact.artifact.input.KeyConstants.KEY_LEFT_CONTROL
import org.openartifact.artifact.input.KeyConstants.KEY_LEFT_SHIFT
import org.openartifact.artifact.input.KeyConstants.KEY_Q
import org.openartifact.artifact.input.KeyConstants.KEY_S
import org.openartifact.artifact.input.KeyConstants.KEY_W
import org.openartifact.artifact.input.KeyConstants.MOUSE_BUTTON_1
import org.openartifact.artifact.input.MouseInput
import org.openartifact.artifact.input.MouseInput.hold
import org.openartifact.artifact.input.MouseInput.move
import org.openartifact.artifact.input.createKeyInputMap
import org.openartifact.artifact.input.getKeyDown
import org.openartifact.artifact.input.with
import org.openartifact.artifact.resource.resource

@ApplicationEntry
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

    private val keyInputMap = createKeyInputMap {
        KEY_LEFT_CONTROL with KEY_Q to { requestShutdown() }
    }

    inner class Cube(val pos : Vec3) {
        var shader : IShader

        var vertexArray : IVertexArray
        var vertexBuffer : IVertexBuffer
        var indexBuffer : IIndexBuffer


        val vertices = floatArrayOf(
            // Positions
            // Front face
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,

            // Back face
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,

            // Top face
            -0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f,

            // Bottom face
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, -0.5f,

            // Left face
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,

            // Right face
            0.5f, 0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
        )

        val indices = intArrayOf(
            // Front face
            0, 1, 2, 2, 3, 0,
            // Back face
            4, 5, 6, 6, 7, 4,
            // Top face
            8, 9, 10, 10, 11, 8,
            // Bottom face
            12, 13, 14, 14, 15, 12,
            // Left face
            16, 17, 18, 18, 19, 16,
            // Right face
            20, 21, 22, 22, 23, 20
        )

        init {
            vertexArray = renderer.choose<IVertexArray>().create()

            val bufferLayout = renderer.choose<IBufferLayout>().create(
                mapOf(
                    DataType.Vec3 to "a_Position"
                )
            )

            vertexBuffer = renderer.choose<IVertexBuffer>().create(vertices, bufferLayout)

            indexBuffer = renderer.choose<IIndexBuffer>().create(indices)

            vertexArray.addVertexBuffer(vertexBuffer)
            vertexArray.defineIndexBuffer(indexBuffer)

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
    private lateinit var cube2 : Cube

    override fun init() {
        logger.info("Sandbox init")

        renderer = createRenderer()

        cube1 = Cube(Vec3(1, 0, 0))
        cube2 = Cube(Vec3(-1, 0, 0))

        camera = PerspectiveCamera(90f, Vec3(4, 4, 4), Vec3(22.5f, -45, 0))

        subscribe(FPSUpdateEvent::class) { event ->
            logger.info("FPS: ${event.fps}")
        }
    }

    override fun update(deltaTime : Double) {
        keyInputMap.process()
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

        renderer.frame {
            renderFlow {
                cube1.apply {
                    val mvp = camera.calculateMVPMatrix(calculateModelMatrix(pos, Vec3(10, 54, 0)))

                    directCommit(shader) {
                        parameterMat4("u_MVP", mvp)
                        parameterVec3("u_Color", Vec3(1, 1, 1))
                        parameterVec3("u_LightColor", Vec3(0.5, 0.5, 0.5))
                    }

                    commit(vertexArray)

                    push()
                }

                cube2.apply {
                    val mvp = camera.calculateMVPMatrix(calculateModelMatrix(pos, Vec3(0, 0, 0)))

                    directCommit(shader) {
                        parameterMat4("u_MVP", mvp)
                        parameterVec3("u_Color", Vec3(1, 1, 1))
                        parameterVec3("u_LightColor", Vec3(1, 0.25, 0.645))
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