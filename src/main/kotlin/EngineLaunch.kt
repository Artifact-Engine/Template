import org.openartifact.artifact.core.ApplicationProfile
import org.openartifact.artifact.core.GameContext

fun main() {

    GameContext.createContext {
        configureApplicationProfile(
            ApplicationProfile(
                "ArtifactTestProject",
                "Example",
                "TestScene"
            )
        )
    }.setCurrent().launch()

    println(
        GameContext.current().application()
            .profile.displayTitle
    )

}