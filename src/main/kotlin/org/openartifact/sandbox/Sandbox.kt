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
import org.openartifact.artifact.input.KeyConstants.KEY_Q
import org.openartifact.artifact.input.KeyConstants.KEY_S
import org.openartifact.artifact.input.KeyConstants.KEY_W
import org.openartifact.artifact.input.KeyConstants.MOUSE_BUTTON_1
import org.openartifact.artifact.input.MouseInput
import org.openartifact.artifact.input.MouseInput.hold
import org.openartifact.artifact.input.MouseInput.move
import org.openartifact.artifact.input.createKeyInputMap
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

    private lateinit var texture : ITexture

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
            // Positions         // Texture Coords
            // Front face
            -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,  // V0
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,  // V1
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,  // V2
            0.5f,  0.5f,  0.5f,  1.0f, 1.0f,  // V3

            // Back face
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,  // V4
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,  // V5
            0.5f, -0.5f, -0.5f,  1.0f, 0.0f,  // V6
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,  // V7

            // Top face
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,  // V8 (same as V4)
            -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,  // V9 (same as V0)
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,  // V10 (same as V3)
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,  // V11 (same as V7)

            // Bottom face
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,  // V12 (same as V5)
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,  // V13 (same as V1)
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,  // V14 (same as V2)
            0.5f, -0.5f, -0.5f,  1.0f, 1.0f,  // V15 (same as V6)

            // Left face
            -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,  // V16 (same as V4)
            -0.5f, -0.5f, -0.5f,  1.0f, 0.0f,  // V17 (same as V5)
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,  // V18 (same as V1)
            -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,  // V19 (same as V0)

            // Right face
            0.5f,  0.5f,  0.5f,  1.0f, 1.0f,  // V20 (same as V3)
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,  // V21 (same as V2)
            0.5f, -0.5f, -0.5f,  0.0f, 0.0f,  // V22 (same as V6)
            0.5f,  0.5f, -0.5f,  0.0f, 1.0f   // V23 (same as V7)
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


        val rectLayout = renderer.choose<IBufferLayout>().create(
            mapOf(
                DataType.Vec3 to "a_Position",
                DataType.Vec2 to "a_TexCoord",
            )
        )

        rectVertexBuffer = renderer.choose<IVertexBuffer>().create(vertices, rectLayout)

        rectIndexBuffer = renderer.choose<IIndexBuffer>().create(indices)

        rectVertexArray.addVertexBuffer(rectVertexBuffer)
        rectVertexArray.defineIndexBuffer(rectIndexBuffer)

        val vertex = resource("shaders/vertex.glsl")
        val fragment = resource("shaders/fragment.glsl")

        rectShader = renderer.choose<IShader>()
            .create(
                listOf(
                    OpenGLShader.ShaderModule(vertex.asText(), ShaderType.VERTEX),
                    OpenGLShader.ShaderModule(fragment.asText(), ShaderType.FRAGMENT),
                )
            )

        camera = PerspectiveCamera(90f, Vec3(4, 4, 4), Vec3(22.5f, -45, 0))

        val texResource = resource("tex.png")
        println(texResource.extract())

        println(resource("tex.png").extract())

        println(texResource.path)
        println(texResource.extract().path)

        texture = renderer.choose<ITexture>().create(
            texResource
        )

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
                }

                commit(rectVertexArray)
                commit(texture)

                push()
            }
        }
    }

    override fun shutdown() {
        logger.info("Sandbox shutdown")
    }

}