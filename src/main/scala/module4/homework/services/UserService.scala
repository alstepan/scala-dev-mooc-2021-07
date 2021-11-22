package module4.homework.services

import zio.Has
import zio.Task
import module4.homework.dao.entity.User
import module4.homework.dao.entity.Role
import module4.homework.dao.repository.UserRepository
import module4.phoneBook.db.DBTransactor
import doobie.quill.DoobieContext
import zio.interop.catz._
import io.getquill.CompositeNamingStrategy2
import io.getquill.Escape
import io.getquill.Literal
import zio.ZIO
import zio.RIO
import module4.homework.dao.entity.UserToRole
import zio.ZLayer
import zio.macros.accessible
import module4.homework.dao.entity.RoleCode

@accessible
object UserService{
    type UserService = Has[Service]

    trait Service{
        def listUsers(): RIO[DBTransactor, List[User]]
        def listUsersDTO(): RIO[DBTransactor, List[UserDTO]]
        def addUserWithRole(user: User, roleCode: RoleCode): RIO[DBTransactor, UserDTO]
        def listUsersWithRole(roleCode: RoleCode): RIO[DBTransactor, List[UserDTO]]
    }

    class Impl(userRepo: UserRepository.Service) extends Service{
        import doobie.implicits._
        val dc: DoobieContext.Postgres[CompositeNamingStrategy2[Escape.type,Literal.type]] = DBTransactor.doobieContext
        import dc._

        def listUsers(): RIO[DBTransactor, List[User]] = for{
             transactor <- DBTransactor.dbTransactor
             users <- userRepo.list().transact(transactor)
        } yield users

        def listUsersDTO(): RIO[DBTransactor,List[UserDTO]] = ???
        
        def addUserWithRole(user: User, roleCode: RoleCode): RIO[DBTransactor,UserDTO] = ???
        
        def listUsersWithRole(roleCode: RoleCode): RIO[DBTransactor,List[UserDTO]] = ???
        
        
    }

    val live: ZLayer[UserRepository.UserRepository, Nothing, UserService] = ???
}

case class UserDTO(user: User, roles: Set[Role])