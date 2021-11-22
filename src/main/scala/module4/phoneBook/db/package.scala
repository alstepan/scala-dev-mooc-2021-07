package module4.phoneBook

import zio.Has
import doobie.util.transactor.Transactor
import liquibase.Liquibase
import zio.Task
import zio.RIO
import zio.ZManaged
import module4.phoneBook.configuration.Config
import zio.ZLayer
import module4.phoneBook.configuration.Configuration
import zio.URIO
import zio.ZIO
import zio.interop.catz._
import liquibase.resource.FileSystemResourceAccessor
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.resource.CompositeResourceAccessor
import liquibase.database.jvm.JdbcConnection
import doobie.quill.DoobieContext
import io.getquill.NamingStrategy
import io.getquill.Escape
import io.getquill.Literal
import scala.concurrent.ExecutionContext
import zio._
import doobie.hikari.HikariTransactor
import cats.effect.Blocker
import zio.blocking.Blocking
import zio.macros.accessible
import module4.phoneBook.configuration.DbConfig

package object db {
  type DBTransactor = Has[Transactor[Task]]

  type LiquibaseService = Has[LiquibaseService.Service]

  type Liqui = Has[Liquibase]

  @accessible
  object LiquibaseService {

    trait Service {
      def performMigration: RIO[Liqui, Unit]
    }

    class Impl extends Service {

      override def performMigration: RIO[Liqui, Unit] = liquibase.map(_.update("dev"))
    }

    def mkLiquibase(config: Config, transactor: Transactor[Task]): ZManaged[Any, Throwable, Liquibase] = for {
      connection <- transactor.connect(transactor.kernel).toManagedZIO
      fileAccessor <-  ZIO.effect(new FileSystemResourceAccessor()).toManaged_
      classLoader <- ZIO.effect(classOf[LiquibaseService].getClassLoader).toManaged_
      classLoaderAccessor <- ZIO.effect(new ClassLoaderResourceAccessor(classLoader)).toManaged_
      fileOpener <- ZIO.effect(new CompositeResourceAccessor(fileAccessor, classLoaderAccessor)).toManaged_
      jdbcConn <- ZManaged.makeEffect(new JdbcConnection(connection))(c => c.close())
      liqui <- ZIO.effect(new Liquibase(config.liquibase.changeLog, fileOpener, jdbcConn)).toManaged_
    } yield liqui


    val liquibaseLayer: ZLayer[DBTransactor with Configuration, Throwable, Liqui] = ZLayer.fromManaged(
      for {
        config <- zio.config.getConfig[Config].toManaged_
        transactor <- DBTransactor.dbTransactor.toManaged_
        liquibase <- mkLiquibase(config, transactor)
      } yield (liquibase)
    )


    def liquibase: URIO[Liqui, Liquibase] = ZIO.service[Liquibase]

    val live: ULayer[LiquibaseService] = ZLayer.succeed(new Impl)

  }

  object DBTransactor {

    val doobieContext = new DoobieContext.Postgres(NamingStrategy(Escape, Literal)) // Literal naming scheme

    def mkTransactor(conf: configuration.DbConfig, connectEC: ExecutionContext, transactEC: ExecutionContext): Managed[Throwable, Transactor[Task]] =
      HikariTransactor.newHikariTransactor[Task](
        conf.driver,
        conf.url,
        conf.user,
        conf.password,
        connectEC,
        Blocker.liftExecutionContext(transactEC)
      ).toManagedZIO

    val live: ZLayer[Configuration with Blocking, Throwable, DBTransactor] = ZLayer.fromManaged(
      (for {
        config <- zio.config.getConfig[Config].toManaged_
        ec <- ZIO.descriptor.map(_.executor.asEC).toManaged_
        blocingEC <- zio.blocking.blockingExecutor.map(_.asEC).toManaged_
        transactor <- DBTransactor.mkTransactor(config.db, ec, blocingEC)
      } yield transactor)
    )

    def dbTransactor: URIO[DBTransactor, Transactor[Task]] = ZIO.service[Transactor[Task]]
  }
}
