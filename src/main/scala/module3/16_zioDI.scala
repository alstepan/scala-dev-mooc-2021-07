package module3

import zio.{Has, IO, Task, ZIO, ZLayer}
import zio.clock.Clock
import zio.console.Console
import zio.duration.durationInt
import zio.random.Random

import scala.language.postfixOps

object di {

  type Query[_]
  type DBError
  type QueryResult[_]
  type Email
  type User


  trait DBService{
    def tx[T](query: Query[T]): IO[DBError, QueryResult[T]]
  }

  trait EmailService{
    def makeEmail(email: String, body: String): Task[Email]
    def sendEmail(email: Email): Task[Unit]
  }

  trait LoggingService{
    def log(str: String): Task[Unit]
  }

  trait UserService{
      def getUserBy(id: Int): Task[User]
  }



  type MyEnv = Console with Clock with Random

  /**
   * Написать эффект который напечатет в консоль приветствие, подождет 5 секунд,
   * сгенерит рандомное число, напечатает его в консоль
   *   Console
   *   Clock
   *   Random
   */


  def e1: ZIO[Console with Clock with Random, Nothing, Unit] = for{
    console <- ZIO.environment[Console].map(_.get)
    clock <- ZIO.environment[Clock].map(_.get)
    random <- ZIO.environment[Random].map(_.get)
    _ <- console.putStrLn("Hello")
    _ <- clock.sleep(5 seconds)
    int <- random.nextInt
    _ <- console.putStrLn(int.toString())

  } yield ()


  def e2: ZIO[MyEnv, Nothing, Unit] = for{
    console <- ZIO.environment[Console].map(_.get)
    clock <- ZIO.environment[Clock].map(_.get)
    random <- ZIO.environment[Random].map(_.get)
    _ <- console.putStrLn("Hello")
    _ <- clock.sleep(5 seconds)
    int <- random.nextInt
    _ <- console.putStrLn(int.toString())

  } yield ()


  // lazy val getUser: ZIO[UserService, Nothing, User] = ZIO.environment[UserService].flatMap(_.getUserBy(1))

  lazy val sendMail: ZIO[EmailService, Throwable, Unit] = ???


  /**
   * Эффект, который будет комбинацией двух эффектов выше
   */
  // azy val combined2: ZIO[UserService with EmailService,Throwable,(User, Unit)] = getUser <*> sendMail


  /**
   * Написать ZIO программу которая выполнит запрос и отправит email
   */
  val queryAndNotify: ZIO[UserService with EmailService, Throwable, Unit] = for{
    userService <- ZIO.environment[UserService]
    emailService <- ZIO.environment[EmailService]
    user <- userService.getUserBy(1)
    email <- emailService.makeEmail("", "")
    _ <- emailService.sendEmail(email)
  } yield ()



  lazy val services: DBService with EmailService = ???

  def services(emailService: EmailService): EmailService with DBService = ???

  lazy val dBService: DBService = ???

  lazy val emailService2: EmailService = ???

  // provide
  // lazy val e3: ZIO[Any, Throwable, Unit] = queryAndNotify.provide(services)

  // provide some
  // lazy val e4: ZIO[DBService,Throwable,Unit] = queryAndNotify.provideSome[DBService](emailService2)

  // provide
  // lazy val e5: IO[Throwable, Unit] = e4.provide(dBService) 

  lazy val servicesLayer: ZLayer[Any, Nothing, DBService with EmailService] = ???

  lazy val dbServiceLayer: ZLayer[Any, Nothing, DBService] = ???

  // provide layer
  lazy val e6 = ???

  // provide some layer
  lazy val e7 = ???

}