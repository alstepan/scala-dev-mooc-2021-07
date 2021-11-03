package module4.phoneBook

import io.circe.generic.semiauto.deriveCodec
import io.circe.Codec
import scala.util.Success
import scala.util.Failure
import org.http4s.dsl.impl.PathVar
import scala.util.Try
import module4.phoneBook.dao.entities.PhoneRecord

package object dto {

    case class PhoneRecordDTO(phone: String, fio: String, zipCode: String, address: String)

    object PhoneRecordDTO{
        def from(phoneRecord: PhoneRecord): PhoneRecordDTO = PhoneRecordDTO(
            phoneRecord.phone,
            phoneRecord.fio,
            "",
            ""
        ) 
    }

    case class RecordId(id: Int)

    object IdVar{
        def unapply(str: String): Option[RecordId] = {
            Try(str.toInt) match {
                case Failure(exception) => None
                case Success(value) if value < 0 => None
            }
        }
    }
}
