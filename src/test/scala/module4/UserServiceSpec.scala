package module4

import zio.test.DefaultRunnableSpec
import zio.test.ZSpec
import zio.test._
import module4.homework.dao.repository.UserRepository
import zio.ZIO
import homework.dao.entity.User
import doobie.quill.DoobieContext
import io.getquill.CompositeNamingStrategy2
import io.getquill.Escape
import io.getquill.Literal
import zio.interop.catz._
import zio.test.Assertion._
import module4.homework.dao.entity.Role
import zio.blocking.Blocking
import zio.Layer
import zio.test.environment.TestEnvironment
import zio.random.Random
import zio.{Has, ZLayer}
import zio.Task
import zio.random.Random._
import java.util.UUID
import TestAspect._
import homework.services.UserService
import module4.homework.dao.entity.RoleCode


object UserServiceSpec extends DefaultRunnableSpec{

    import MigrationAspects._
    import doobie.implicits._
    val dc: DoobieContext.Postgres[CompositeNamingStrategy2[Escape.type,Literal.type]] = DBTransactor.doobieContext
    import dc._

    type Env = Blocking with TestContainer.Postgres with DBTransactor.DBTransactor with 
        UserRepository.UserRepository with LiquibaseService.Liqui with  LiquibaseService.LiquibaseService with UserService.UserService
    
    val layer: ZLayer[Any, Throwable, Env] = 
        Blocking.live >+> TestContainer.postgres() >+> DBTransactor.test >+> LiquibaseService.liquibaseLayer ++ 
        UserRepository.live >+> UserService.live ++ LiquibaseService.live



    val zLayer: ZLayer[Any, Nothing, Env] = 
        layer.orDie


    val users = List(
        User(UUID.randomUUID().toString(), scala.util.Random.nextString(15), scala.util.Random.nextString(30), scala.util.Random.nextInt(120)),
        User(UUID.randomUUID().toString(), scala.util.Random.nextString(15), scala.util.Random.nextString(30), scala.util.Random.nextInt(120)),
        User(UUID.randomUUID().toString(), scala.util.Random.nextString(15), scala.util.Random.nextString(30), scala.util.Random.nextInt(120)),
        User(UUID.randomUUID().toString(), scala.util.Random.nextString(15), scala.util.Random.nextString(30), scala.util.Random.nextInt(120)),
        User(UUID.randomUUID().toString(), scala.util.Random.nextString(15), scala.util.Random.nextString(30), scala.util.Random.nextInt(120)),
        User(UUID.randomUUID().toString(), scala.util.Random.nextString(15), scala.util.Random.nextString(30), scala.util.Random.nextInt(120)),
        User(UUID.randomUUID().toString(), scala.util.Random.nextString(15), scala.util.Random.nextString(30), scala.util.Random.nextInt(120)),
        User(UUID.randomUUID().toString(), scala.util.Random.nextString(15), scala.util.Random.nextString(30), scala.util.Random.nextInt(120)),
        User(UUID.randomUUID().toString(), scala.util.Random.nextString(15), scala.util.Random.nextString(30), scala.util.Random.nextInt(120)),
        User(UUID.randomUUID().toString(), scala.util.Random.nextString(15), scala.util.Random.nextString(30), scala.util.Random.nextInt(120))
    )
    val usersGen = Gen.fromIterable(users)

    val Manager = RoleCode("manager")

    def spec = suite("UserServiceSpec")(
            testM("add user with role")(
                for{
                        transactor <- DBTransactor.dbTransactor
                        userService <- ZIO.environment[UserService.UserService].map(_.get)
                        _ <- userService.addUserWithRole(users.head, Manager)
                        result <- userService.listUsersDTO()
                    } yield assert(result.length)(equalTo(1)) &&
                        assert(result.head.user)(equalTo(users.head)) && assert(result.head.roles)(equalTo(Set(Role(Manager.code, "Manager"))))
            )  @@ migrate(),
            testM("list user with role Manager should return empty List")(
                for{
                    transactor <- DBTransactor.dbTransactor
                    userRepo <- ZIO.environment[UserRepository.UserRepository].map(_.get)
                    userService <- ZIO.environment[UserService.UserService].map(_.get)
                    _ <- userRepo.createUsers(users).transact(transactor)
                    result <- userService.listUsersWithRole(Manager)
                } yield assert(result)(isEmpty)
            ) @@ migrate(),
            testM("list user with role Manager should return one Entry")(
                for{
                    transactor <- DBTransactor.dbTransactor
                    userRepo <- ZIO.environment[UserRepository.UserRepository].map(_.get)
                    userService <- ZIO.environment[UserService.UserService].map(_.get)
                    _ <- userRepo.createUsers(users.tail).transact(transactor)
                    _ <- userService.addUserWithRole(users.head, Manager)
                    result <- userService.listUsersWithRole(Manager)
                } yield assert(result.length)(equalTo(1)) && assert(result.head.user)(equalTo(users.head)) && 
                    assert(result.head.roles)(equalTo(Set(Role(Manager.code, "Manager"))))
            ) @@ migrate()
        ).provideCustomLayer(zLayer)  
}
