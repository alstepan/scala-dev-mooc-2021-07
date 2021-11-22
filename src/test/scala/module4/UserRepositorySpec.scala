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
import module4.homework.dao.entity.UserId


object UserRepositorySpec extends DefaultRunnableSpec{

    import MigrationAspects._
    import doobie.implicits._
    val dc: DoobieContext.Postgres[CompositeNamingStrategy2[Escape.type,Literal.type]] = DBTransactor.doobieContext
    import dc._

    type Env = Blocking with TestContainer.Postgres with DBTransactor.DBTransactor with 
        UserRepository.UserRepository with LiquibaseService.Liqui with  LiquibaseService.LiquibaseService
    
    val layer: ZLayer[Any, Throwable, Env] = 
        Blocking.live >+> TestContainer.postgres() >+> DBTransactor.test >+> LiquibaseService.liquibaseLayer ++ 
        UserRepository.live ++ LiquibaseService.live



    val genName: Gen[Random with Sized, String] = Gen.anyASCIIString
    val genAge: Gen[Random,Int] = Gen.int(18, 120)
    val genUuid: Gen[Random, UUID] = Gen.anyUUID
    
    val genUser = for {
        uuid <- genUuid
        firstName <- genName
        lastName <- genName
        age <- genAge
    } yield User(uuid.toString(), firstName, lastName, age)


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



    def spec = suite("UserRepositorySpec")(
            testM("метод list возвращает пустую коллекцию, на пустой базе")(
                for{
                        transactor <- DBTransactor.dbTransactor
                        userRepo <- ZIO.environment[UserRepository.UserRepository].map(_.get)
                        result <- userRepo.list().transact(transactor)
                    } yield assert(1)(equalTo(1)) &&
                        assert(result.isEmpty)(equalTo(true))
            )  @@ migrate(),
            testM("методы create а затем findBy по созданному пользователю")(
                checkAllM(usersGen){ user => 
                    for{
                        transactor <- DBTransactor.dbTransactor
                        userRepo <- ZIO.environment[UserRepository.UserRepository].map(_.get)
                        user <- userRepo.createUser(user).transact(transactor)
                        result <- userRepo.findUser(user.typedId).transact(transactor).some.mapError(_ => new Exception("fetch failed"))
                    } yield assert(user.id)(equalTo(result.id)) &&
                        assert(result.firstName)(equalTo(user.firstName))
                }

            ) @@ migrate(),
            testM("метод findBy по случайному id")(
                checkAllM(usersGen, Gen.anyUUID){ (user, id) => 
                    for{
                        transactor <- DBTransactor.dbTransactor
                        userRepo <- ZIO.environment[UserRepository.UserRepository].map(_.get)
                        user <- userRepo.createUser(user).transact(transactor)
                        result <- userRepo.findUser(UserId(id.toString())).transact(transactor)
                    } yield assert(result)(isNone) 
                }

            ) @@ migrate(),
            testM("метод update должен обновлять только целевого пользователя")(
                for{
                    transactor <- DBTransactor.dbTransactor
                    userRepo <- ZIO.environment[UserRepository.UserRepository].map(_.get)
                    users <- userRepo.createUsers(users).transact(transactor)
                    user = users.head
                    newFirstName = "Petr"
                    _ <- userRepo.updateUser(user.copy(firstName = newFirstName)).transact(transactor)
                    updated <- userRepo.findUser(user.typedId).transact(transactor).some.mapError(_ => new Exception("fetch failed"))
                    all <- userRepo.list().transact(transactor)

                } yield assert(updated.firstName)(equalTo(newFirstName)) && 
                    assert(all.filter(_.id != user.id).toSet)(equalTo(users.filter(_.id != user.id).toSet))

            ) @@ migrate(),
            testM("метод delete должен удалять только целевого пользователя")(
                for{
                    transactor <- DBTransactor.dbTransactor
                    userRepo <- ZIO.environment[UserRepository.UserRepository].map(_.get)
                    users <- userRepo.createUsers(users).transact(transactor)
                    user = users.last
                    _ <- userRepo.deleteUser(user).transact(transactor)
                    all <- userRepo.list().transact(transactor)

                } yield assert(all.length)(equalTo(9)) && 
                    assert(all.toSet)(equalTo(users.filter(_.id != user.id).toSet))

            ) @@ migrate(),
            testM("метод findByLastName должен находить пользователя")(
                    for{
                        transactor <- DBTransactor.dbTransactor
                        userRepo <- ZIO.environment[UserRepository.UserRepository].map(_.get)
                        users <- userRepo.createUsers(users).transact(transactor)
                        user = users(5)
                        result <- userRepo.findByLastName(user.lastName).transact(transactor)
                    } yield assert(result.length)(equalTo(1)) &&
                        assert(result.head.lastName)(equalTo(user.lastName))

            ) @@ migrate(),

        ).provideCustomLayer(layer.orDie)  
}

// 7c038f1d-4e8c-4c8e-a8ba-dd58b49b62af
// 7c038f1d-4e8c-4c8e-a8ba-dd58b49b62af