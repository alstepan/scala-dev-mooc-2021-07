package module4.phoneBook.dao.repositories

import module4.phoneBook.db.DBTransactor
import zio.Has
import module4.phoneBook.dao.entities._
import doobie.quill.DoobieContext
import io.getquill.CompositeNamingStrategy2
import io.getquill.Escape
import io.getquill.Literal
import zio.ZLayer
import zio.ULayer

object PhoneRecordRepository {
  val dc: DoobieContext.Postgres[CompositeNamingStrategy2[Escape.type, Literal.type]] = DBTransactor.doobieContext
  import dc._

  type PhoneRecordRepository = Has[Service]

  trait Service{
      def find(phone: String): Result[Option[PhoneRecord]]
      def list(): Result[List[PhoneRecord]]
      def insert(phoneRecord: PhoneRecord): Result[Unit]
      def update(phoneRecord: PhoneRecord): Result[Unit]
      def delete(id: String): Result[Unit]
  }

  class Impl extends Service{

    val phoneRecordSchema = quote{
      querySchema[PhoneRecord](""""PhoneRecord"""")
    }

    val addressSchema = quote{
      querySchema[Address](""""Address"""")
    }

    def find(phone: String): Result[Option[PhoneRecord]] = dc.run(
      phoneRecordSchema.filter(r => r.phone == lift(phone))
    ).map(_.headOption) // SELECT "r"."id", "r"."phone", "r"."fio", "r"."addressId" FROM "PhoneRecord" "r" WHERE "r"."phone" = ?
    
    def list(): Result[List[PhoneRecord]] = dc.run(phoneRecordSchema) // SELECT "x"."id", "x"."phone", "x"."fio", "x"."addressId" FROM "PhoneRecord" "x"
    
    def insert(phoneRecord: PhoneRecord): Result[Unit] = 
      dc.run(phoneRecordSchema.insert(lift(phoneRecord))).map(_ => ())
    
    def update(phoneRecord: PhoneRecord): Result[Unit] = 
      dc.run(phoneRecordSchema.filter(_.id == lift(phoneRecord.id)).update(lift(phoneRecord))).map(_ => ()) // UPDATE "PhoneRecord" SET "id" = ?, "phone" = ?, "fio" = ?, "addressId" = ? WHERE "id" = ?
    
    def delete(id: String): Result[Unit] = 
      dc.run(phoneRecordSchema.filter(_.id == lift(id)).delete).map(_ => ()) // DELETE FROM "PhoneRecord" WHERE "id" = ?

    // implicit join
    def listWithAddress() = dc.run(
      for{
        phoneRecord <- phoneRecordSchema
        address <- addressSchema if(phoneRecord.addressId == address.id)
      } yield (phoneRecord, address)
    ) // SELECT "phoneRecord"."id", "phoneRecord"."phone", "phoneRecord"."fio", "phoneRecord"."addressId", "address"."id", "address"."zipCode", "address"."streetAddress" FROM "PhoneRecord" "phoneRecord", "Address" "address" WHERE "phoneRecord"."addressId" = "address"."id"

    // applicative join
    def listWithAddress2() = dc.run(
      phoneRecordSchema.join(addressSchema).on(_.addressId == _.id).filter( r => r._1.phone == lift("123456"))
    ) // SELECT "x7"."id", "x7"."phone", "x7"."fio", "x7"."addressId", "x8"."id", "x8"."zipCode", "x8"."streetAddress" FROM "PhoneRecord" "x7" INNER JOIN "Address" "x8" ON "x7"."addressId" = "x8"."id" 

    
    // flat join
    def listWithAddress3() = dc.run(
      for{
        phoneRecord <- phoneRecordSchema
        address <- addressSchema.join(_.id == phoneRecord.addressId)
      } yield (phoneRecord, address)
    ) // SELECT "phoneRecord"."id", "phoneRecord"."phone", "phoneRecord"."fio", "phoneRecord"."addressId", "x9"."id", "x9"."zipCode", "x9"."streetAddress" FROM "PhoneRecord" "phoneRecord" INNER JOIN "Address" "x9" ON "x9"."id" = "phoneRecord"."addressId"

    // pagination
    private val query = quote(
      for{
        phoneRecord <- phoneRecordSchema
        address <- addressSchema.join(_.id == phoneRecord.addressId)
      } yield (phoneRecord, address)
    )

    def listPaged(pageSize: Int, pageNumber: Int) = dc.run(query.drop(lift(pageSize)).take(lift(pageSize * (pageNumber - 1))))
    // SELECT "x10"."_1id", "x10"."_1phone", "x10"."_1fio", "x10"."_1addressId", "x10"."_2id", "x10"."_2zipCode", "x10"."_2streetAddress" FROM (SELECT "phoneRecord"."id" AS _1"id", "phoneRecord"."phone" AS _1"phone", "phoneRecord"."fio" AS _1"fio", "phoneRecord"."addressId" AS _1"addressId", "x10"."id" AS _2"id", "x10"."zipCode" AS _2"zipCode", "x10"."streetAddress" AS _2"streetAddress" FROM "PhoneRecord" "phoneRecord" INNER JOIN "Address" "x10" ON "x10"."id" = "phoneRecord"."addressId") AS "x10" LIMIT ? OFFSET ?

    def count() = dc.run(query.size)

    def sorted() = dc.run(query.sortBy(_._1.fio))

  }

  val live: ULayer[PhoneRecordRepository] = ZLayer.succeed(new Impl())
}
