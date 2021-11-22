package module4

import doobie.quill.DoobieContext
import io.getquill.NamingStrategy
import io.getquill.Escape
import io.getquill.Literal
import module4.phoneBook.configuration.DbConfig
import scala.concurrent.ExecutionContext
import zio.Managed
import doobie.hikari.HikariTransactor
import zio.Task
import cats.effect.Blocker
import doobie.util.transactor
import zio.interop.catz._
import zio.ZLayer
import zio.ZIO
import zio.URIO
import zio.Has
import zio.blocking.Blocking

object DBTransactor {

    type DBTransactor = Has[transactor.Transactor[Task]]


    val doobieContext = new DoobieContext.Postgres(NamingStrategy(Escape, Literal)) // Literal naming scheme

    def mkTransactor(conf: DbConfig, connectEC: ExecutionContext, transactEC: ExecutionContext): Managed[Throwable, transactor.Transactor[Task]] =
      HikariTransactor.newHikariTransactor[Task](
        conf.driver,
        conf.url,
        conf.user,
        conf.password,
        connectEC,
        Blocker.liftExecutionContext(transactEC)
      ).toManagedZIO

    import com.dimafeng.testcontainers.PostgreSQLContainer
    
    def test: ZLayer[TestContainer.Postgres with Blocking, Throwable, DBTransactor] = ZLayer.fromManaged(
        (for {
        pg <- ZIO.service[PostgreSQLContainer].toManaged_ 
        ec <- ZIO.descriptor.map(_.executor.asEC).toManaged_
        blocingEC <- zio.blocking.blockingExecutor.map(_.asEC).toManaged_
        transactor <- DBTransactor.mkTransactor(DbConfig("org.postgresql.Driver" , pg.jdbcUrl, pg.username, pg.password), ec, blocingEC)
      } yield transactor)
    )

    def dbTransactor: URIO[DBTransactor, transactor.Transactor[Task]] = ZIO.service[transactor.Transactor[Task]]
  }