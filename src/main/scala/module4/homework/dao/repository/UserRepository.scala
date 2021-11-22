package module4.homework.dao.repository

import zio.Has
import doobie.quill.DoobieContext
import io.getquill.CompositeNamingStrategy2
import io.getquill.Escape
import io.getquill.Literal
import module4.phoneBook.db.DBTransactor
import zio.Task
import module4.homework.dao.entity.User
import zio.macros.accessible
import zio.{ZLayer, ULayer}
import module4.homework.dao.entity.{Role, UserToRole}
import module4.homework.dao.entity.UserId
import module4.homework.dao.entity.RoleCode


object UserRepository{


    val dc: DoobieContext.Postgres[CompositeNamingStrategy2[Escape.type, Literal.type]] = DBTransactor.doobieContext
    import dc._

    type UserRepository = Has[Service]

    trait Service{
        def findUser(userId: UserId): Result[Option[User]]
        def createUser(user: User): Result[User]
        def createUsers(users: List[User]): Result[List[User]]
        def updateUser(user: User): Result[Unit]
        def deleteUser(user: User): Result[Unit]
        def findByLastName(lastName: String): Result[List[User]]
        def list(): Result[List[User]]
        def userRoles(userId: UserId): Result[List[Role]]
        def insertRoleToUser(roleCode: RoleCode, userId: UserId): Result[Unit]
        def listUsersWithRole(roleCode: RoleCode): Result[List[User]]
        def findRoleByCode(roleCode: RoleCode): Result[Option[Role]]
    }

    class ServiceImpl extends Service{

        val userSchema = ???

        val roleSchema = ???

        val userToRoleSchema = ???
        def findUser(userId: UserId): Result[Option[User]] = ???
        
        def createUser(user: User): Result[User] = ???
        
        def createUsers(users: List[User]): Result[List[User]] = ???
        
        def updateUser(user: User): Result[Unit] = ???
        
        def deleteUser(user: User): Result[Unit] = ???
        
        def findByLastName(lastName: String): Result[List[User]] = ???
        
        def list(): Result[List[User]] = ???
        
        def userRoles(userId: UserId): Result[List[Role]] = ???
        
        def insertRoleToUser(roleCode: RoleCode, userId: UserId): Result[Unit] = ???
        
        def listUsersWithRole(roleCode: RoleCode): Result[List[User]] = ???
        
        def findRoleByCode(roleCode: RoleCode): Result[Option[Role]] = ???
                
    }

    val live: ULayer[UserRepository] = ???
}