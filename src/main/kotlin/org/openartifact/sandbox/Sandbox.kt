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

import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.openartifact.artifact.core.Application
import org.openartifact.artifact.core.ApplicationEntry
import org.openartifact.artifact.core.event.events.FPSUpdateEvent
import org.openartifact.artifact.core.event.subscribe
import org.openartifact.artifact.core.event.unsubscribe
import org.openartifact.artifact.globalext.reset
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
import org.openartifact.artifact.input.KeyConstants.KEY_Q
import org.openartifact.artifact.input.KeyConstants.KEY_S
import org.openartifact.artifact.input.KeyConstants.KEY_W
import org.openartifact.artifact.input.KeyConstants.MOUSE_BUTTON_1
import org.openartifact.artifact.input.MouseInput
import org.openartifact.artifact.input.MouseInput.hold
import org.openartifact.artifact.input.MouseInput.move
import org.openartifact.artifact.input.createKeyInputMap
import org.openartifact.artifact.input.with
import org.openartifact.artifact.resource.getResource
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

    private lateinit var rectShader : IShader

    private lateinit var rectVertexArray : IVertexArray
    private lateinit var rectVertexBuffer : IVertexBuffer
    private lateinit var rectIndexBuffer : IIndexBuffer

    override fun init() {
        logger.info("Sandbox init")

        renderer = createRenderer()

        // Rectangle
        rectVertexArray = renderer.choose<IVertexArray>().create()

        val vertices = floatArrayOf(
            -1.0f, -1.0f, -1.0f, // 0
            -1.0f, -1.0f, 1.0f, // 1
            -1.0f, 1.0f, 1.0f, // 2
            1.0f, 1.0f, -1.0f, // 3
            -1.0f, -1.0f, -1.0f, // 4
            -1.0f, 1.0f, -1.0f, // 5
            1.0f, -1.0f, 1.0f, // 6
            -1.0f, -1.0f, -1.0f, // 7
            1.0f, -1.0f, -1.0f, // 8
            1.0f, 1.0f, -1.0f, // 9
            1.0f, -1.0f, -1.0f, // 10
            -1.0f, -1.0f, -1.0f, // 11
            -1.0f, -1.0f, -1.0f, // 12
            -1.0f, 1.0f, 1.0f, // 13
            -1.0f, 1.0f, -1.0f, // 14
            1.0f, -1.0f, 1.0f, // 15
            -1.0f, -1.0f, 1.0f, // 16
            -1.0f, -1.0f, -1.0f, // 17
            -1.0f, 1.0f, 1.0f, // 18
            -1.0f, -1.0f, 1.0f, // 19
            1.0f, -1.0f, 1.0f, // 20
            1.0f, 1.0f, 1.0f, // 21
            1.0f, -1.0f, -1.0f, // 22
            1.0f, 1.0f, -1.0f, // 23
            1.0f, -1.0f, -1.0f, // 24
            1.0f, 1.0f, 1.0f, // 25
            1.0f, -1.0f, 1.0f, // 26
            1.0f, 1.0f, 1.0f, // 27
            1.0f, 1.0f, -1.0f, // 28
            -1.0f, 1.0f, -1.0f, // 29
            1.0f, 1.0f, 1.0f, // 30
            -1.0f, 1.0f, -1.0f, // 31
            -1.0f, 1.0f, 1.0f, // 32
            1.0f, 1.0f, 1.0f, // 33
            -1.0f, 1.0f, 1.0f, // 34
            1.0f, -1.0f, 1.0f  // 35
        )

        val indices = intArrayOf(
            0, 1, 2, // Triangle 1
            3, 4, 5, // Triangle 2
            6, 7, 8, // Triangle 3
            9, 10, 11, // Triangle 4
            12, 13, 14, // Triangle 5
            15, 16, 17, // Triangle 6
            18, 19, 20, // Triangle 7
            21, 22, 23, // Triangle 8
            24, 25, 26, // Triangle 9
            27, 28, 29, // Triangle 10
            30, 31, 32, // Triangle 11
            33, 34, 35  // Triangle 12
        )

        val rectLayout = renderer.choose<IBufferLayout>().create(
            mapOf(
                DataType.Vec3 to "a_Position"
            )
        )

        rectVertexBuffer = renderer.choose<IVertexBuffer>().create(vertices, rectLayout)

        rectIndexBuffer = renderer.choose<IIndexBuffer>().create(indices)

        rectVertexArray.addVertexBuffer(rectVertexBuffer)
        rectVertexArray.defineIndexBuffer(rectIndexBuffer)

        resource("vertex", "shaders/vertex.glsl").cache()
        resource("fragment", "shaders/fragment.glsl").cache()

        rectShader = renderer.choose<IShader>()
            .create(
                listOf(
                    OpenGLShader.ShaderModule(getResource("vertex").asText(), ShaderType.VERTEX),
                    OpenGLShader.ShaderModule(getResource("fragment").asText(), ShaderType.FRAGMENT),
                )
            )

        camera = PerspectiveCamera(90f, Vec3(4, 4, 4), Vec3(22.5f, -45, 0))

        subscribe(FPSUpdateEvent::class) { event ->
            logger.info("FPS: ${event.fps}")
        }
    }

    override fun update(deltaTime : Double) {
        keyInputMap.process()
        cameraInputMap.process()
            .run { camera.move(movement, 0.1f) }

        MouseInput.process {
            move { pos : Vec2 ->
                hold(MOUSE_BUTTON_1) {
                    camera.rotate(pos.y, pos.x, 0f)

                    if (camera.rotation.x > 45) {
                        camera.rotate(-1f, 0f, 0f)
                    }
                }
            }
        }

        movement.reset()

        renderer.frame {
            renderFlow {
                val model = Mat4().identity()

                val mvp = camera.calculateMVPMatrix(model)

                directCommit(rectShader) {
                    parameterMat4("u_MVP", mvp)
                    parameterVec4("u_Color", Vec4(1f, 1f, 1f, 1f))
                }

                commit(rectVertexArray)

                push()
            }
        }
    }

    override fun shutdown() {
        logger.info("Sandbox shutdown")
    }

}