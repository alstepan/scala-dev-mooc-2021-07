package module4

import zio.Has
import com.dimafeng.testcontainers.PostgreSQLContainer
import zio.ZLayer
import zio.blocking.{effectBlocking, Blocking}
import zio.ZManaged
import org.testcontainers.utility.DockerImageName


object TestContainer {
  type Postgres = Has[PostgreSQLContainer]
  
  def postgres(): ZLayer[Blocking, Nothing, Postgres] =
    ZManaged.make {
      effectBlocking {
        val container = new PostgreSQLContainer()
        container.start()
        container
      }.orDie
    }(container => effectBlocking(container.stop()).orDie).toLayer
}