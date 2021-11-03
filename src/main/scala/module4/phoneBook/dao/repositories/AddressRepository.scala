package module4.phoneBook.dao.repositories

import zio.Has
import doobie.quill.DoobieContext
import io.getquill.CompositeNamingStrategy2
import io.getquill.Escape
import io.getquill.Literal
import module4.phoneBook.db.DBTransactor
import module4.phoneBook.dao.entities.Address
import zio.ZLayer
import zio.ULayer


object AddressRepository {
  type AddressRepository = Has[Service]

  val dc: DoobieContext.Postgres[CompositeNamingStrategy2[Escape.type, Literal.type]] = DBTransactor.doobieContext
  import dc._

  trait Service{
      def findBy(id: String): Result[Option[Address]]
      def insert(phoneRecord: Address): Result[Unit]
      def update(phoneRecord: Address): Result[Unit]
      def delete(id: String): Result[Unit]
  }

  class ServiceImpl extends Service{

      val addressSchema = quote{
        querySchema[Address](""""Address"""")
    }
      def findBy(id: String): Result[Option[Address]] = 
         dc.run(addressSchema.filter(_.id == lift(id))).map(_.headOption)
      
      def insert(address: Address): Result[Unit] = dc.run(addressSchema.insert(lift(address))).map(_ => ())
      
      def update(address: Address): Result[Unit] = dc.run(addressSchema.update(lift(address))).map(_ => ())
      
      def delete(id: String): Result[Unit] = dc.run(addressSchema.filter(_.id == lift(id)).delete).map(_ => ())
      
  }

  val live: ULayer[AddressRepository] = ZLayer.succeed(new ServiceImpl)
}
