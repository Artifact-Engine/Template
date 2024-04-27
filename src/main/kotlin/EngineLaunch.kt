import org.openartifact.artifact.core.ApplicationProfile
import org.openartifact.artifact.core.GameContext
import org.openartifact.artifact.utils.getDefaultProjectDir

fun main() {

    val context = GameContext.createContext {
        configureApplicationProfile(
            ApplicationProfile(
                "ArtifactTestProject",
                "Example",
                "TestScene"
            )
        )
    }.setCurrent()
        .launch()

    println(GameContext.current())
    println(context)

    println(getDefaultProjectDir())

    println(
        GameContext.current().application()
            .profile.displayTitle
    )

}