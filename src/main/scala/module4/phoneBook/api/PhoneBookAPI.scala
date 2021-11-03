package module4.phoneBook.api



import zio.RIO
import io.circe.Decoder
import io.circe.Encoder
import org.http4s.EntityEncoder
import org.http4s.EntityDecoder
import io.circe.generic.auto._
import org.http4s.circe._
import zio.interop.catz._
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import module4.phoneBook.dto._
import module4.phoneBook.services.PhoneBookService
import module4.phoneBook.db.DBTransactor
import zio.random.Random



class PhoneBookAPI[R <: PhoneBookService.PhoneBookService with DBTransactor with Random] {

    type PhoneBookTask[A] =  RIO[R, A]

    val dsl = Http4sDsl[PhoneBookTask]
    import dsl._


    implicit def jsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[PhoneBookTask, A] = jsonOf[PhoneBookTask, A]
    implicit def jsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[PhoneBookTask, A] = jsonEncoderOf[PhoneBookTask, A]
  


    def route = HttpRoutes.of[PhoneBookTask]{
      case GET -> Root / phone => PhoneBookService.find(phone).foldM(
        err => NotFound(),
        result => Ok(result)
      )
      case req @ POST -> Root => (for{
        record <- req.as[PhoneRecordDTO]
        result <- PhoneBookService.insert(record)
      } yield result).foldM(
        err => BadRequest(err.getMessage()),
        result => Ok(result)
      )
      case req @ PUT -> Root / id / addressId => (for{
        record <- req.as[PhoneRecordDTO]
        _ <- PhoneBookService.update(id, addressId, record)
      } yield ()).foldM(
        err => BadRequest(err.getMessage()),
        result => Ok(result)
      )
      case DELETE -> Root / id => PhoneBookService.delete(id).foldM(
        err => BadRequest("Not found"),
        result => Ok(result)
      )
    }
}