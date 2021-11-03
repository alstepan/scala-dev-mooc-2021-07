package module4.phoneBook

import cats.effect.{ExitCode => CatsExitCode}
import cats.syntax.all._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz._
import zio.{RIO, ZIO}
import zio.{ZLayer, Layer}
import zio.config.ReadError
import configuration._
import org.http4s.HttpRoutes
import configuration._
import api.PhoneBookAPI
import module4.phoneBook.services.PhoneBookService
import module4.phoneBook.db._
import zio.random.Random
import module4.phoneBook.dao.repositories.{PhoneRecordRepository, AddressRepository}
import zio.Has


object Server {

    type AppEnvironment = PhoneBookService.PhoneBookService with PhoneRecordRepository.PhoneRecordRepository with AddressRepository.AddressRepository with Configuration with 
    Clock with Blocking with Liqui with LiquibaseService with Random with DBTransactor


    val appEnvironment = Configuration.live >+> Blocking.live >+> DBTransactor.live >+> LiquibaseService.liquibaseLayer ++ 
    PhoneRecordRepository.live >+> AddressRepository.live >+> PhoneBookService.live ++ LiquibaseService.live

    type AppTask[A] = RIO[AppEnvironment, A]

    val httApp = Router[AppTask]("/phoneBook" -> new PhoneBookAPI().route).orNotFound

    val server = for{
      config <- zio.config.getConfig[Config]
       _ <- LiquibaseService.performMigration
      server <- ZIO.runtime[AppEnvironment].flatMap{ implicit rts =>
          val ec = rts.platform.executor.asEC
          BlazeServerBuilder[AppTask](ec)
          .bindHttp(config.api.port, config.api.host)
          .withHttpApp(httApp)
          .serve
          .compile[AppTask, AppTask, CatsExitCode]
          .drain
      }

    } yield server
}
