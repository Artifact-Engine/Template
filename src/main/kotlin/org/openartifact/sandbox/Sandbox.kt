package org.openartifact.sandbox

import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.glfw.GLFW
import org.openartifact.artifact.core.*
import org.openartifact.artifact.graphics.DataType
import org.openartifact.artifact.graphics.RenderAPI
import org.openartifact.artifact.graphics.choose
import org.openartifact.artifact.graphics.flow.renderFlow
import org.openartifact.artifact.graphics.interfaces.*
import org.openartifact.artifact.graphics.platform.opengl.OpenGLRenderer
import org.openartifact.artifact.graphics.window.WindowConfig
import org.openartifact.artifact.input.KeyConstants.KEY_LEFT_CONTROL
import org.openartifact.artifact.input.KeyConstants.KEY_Q
import org.openartifact.artifact.input.createKeyInputMap
import org.openartifact.artifact.input.with

@ApplicationEntry
@Suppress("unused")
class Sandbox : Application(
    RenderAPI.OpenGL,
    WindowConfig(
        640, 360, "Sandbox"
    )
) {

    private val keyInputMap = createKeyInputMap {
        KEY_LEFT_CONTROL with  KEY_Q to { GLFW.glfwSetWindowShouldClose(Artifact.instance.window.handle, true) }
    }

    private lateinit var rectShader : IShader

    private lateinit var rectVertexArray : IVertexArray
    private lateinit var rectVertexBuffer : IVertexBuffer
    private lateinit var rectIndexBuffer : IIndexBuffer

    private lateinit var projectionMatrix : Mat4

    override fun init() {
        logger.info("Sandbox init")

        renderer = createRenderer()

        // Rectangle
        rectVertexArray = renderer.choose<IVertexArray>().create()

        val rectVertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f
        )

        val rectIndices = intArrayOf(
            0, 1, 2, 2, 3, 0
        )

        val rectLayout = renderer.choose<IBufferLayout>().create(
            mapOf(
                DataType.Vec3 to "a_Position"
            )
        )

        rectVertexBuffer = renderer.choose<IVertexBuffer>().create(rectVertices, rectLayout)

        rectIndexBuffer = renderer.choose<IIndexBuffer>().create(rectIndices)

        rectVertexArray.addVertexBuffer(rectVertexBuffer)
        rectVertexArray.defineIndexBuffer(rectIndexBuffer)

        val rectangleVertexSource = """
            #version 330 core
            
            layout(location = 0) in vec3 a_Position;

            uniform vec4 u_Color;
            uniform mat4 u_MVP;
            
            out vec4 v_Color;
            out vec3 v_Position;
            
            void main() {
                gl_Position = u_MVP * vec4(a_Position, 1.0);
                v_Color = u_Color;
                v_Position = a_Position;
            }
            
        """.trimIndent()

        val rectangleFragmentSource = """
            #version 330 core
                        
            layout(location = 0) out vec4 color;
                        
            in vec3 v_Position;
                        
            void main() {
                color = vec4(v_Position * 0.5 + 0.5, 1.0);
            }

        """.trimIndent()

        rectShader = renderer.choose<IShader>(
            rectangleVertexSource,
            rectangleFragmentSource
        ).create()


        projectionMatrix = glm.perspective(glm.radians(45f), (windowConfig.width / windowConfig.height).toFloat(), 0.1f, 100f)
    }

    override fun update() {
        keyInputMap.process()

        (renderer as OpenGLRenderer).clearScreenBuffers()

        renderFlow {

            val view = glm.lookAt(
                Vec3(4, 3, 3),
                Vec3(0, 0, 0),
                Vec3(0, 1, 0)
            )

            val model = Mat4(1f)

            val mvp = projectionMatrix * view * model

            directCommit(rectShader) {
                parameterMat4("u_MVP", mvp)
                parameterVec4("u_Color", Vec4(1f, 1f, 1f, 1f))
            }

            commit(rectVertexArray)

            push()
        }
    }

    override fun shutdown() {
        logger.info("Sandbox shutdown")
    }

}
