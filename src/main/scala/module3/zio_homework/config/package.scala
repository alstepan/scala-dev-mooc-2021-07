package module3.zio_homework

import zio.Task
import pureconfig.ConfigSource
import java.nio.file.Paths
import pureconfig.generic.auto._

package object config {
  case class AppConfig(appName: String, appUrl: String)

  val load: Task[AppConfig] =
    Task.effect(ConfigSource.file(Paths.get("src/main/resources/application.conf")).loadOrThrow[AppConfig])
}
