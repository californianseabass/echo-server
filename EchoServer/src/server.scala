package EchoServer

import zio.http._
import zio._
import zio.json._
import zio.Console._

object HelloWorld extends ZIOAppDefault {
  case class RequestOne(key1: String, key2: String)

  implicit val encode: JsonEncoder[RequestOne] = DeriveJsonEncoder.gen[RequestOne]
  implicit val decode: JsonDecoder[RequestOne] = DeriveJsonDecoder.gen[RequestOne]

  val port = 5273

  val app: App[Any] = Http.collectZIO[Request] {
    case req@Method.GET -> Root / "test" => ZIO.succeed(
      Response.text("Hoot!")
    )
    case req@Method.POST -> Root / "webhook" => (for {
      u <- req.body.asString
      r <- ZIO.debug(u)
      .as(
        Response.text(u)
      )

    } yield r).orDie
    case req@Method.POST -> Root / "specialRequest" =>
      val parsed =  req.body.asString.map(_.fromJson[RequestOne])
      parsed.map(_ match {
         case Left(e) => Response.text(e).withStatus(Status.BadRequest)
         case Right(u) => Response.json(u.toJson)
      }).orDie
  }

  val program: ZIO[Any, Throwable, ExitCode] = for {
    _ <- Console.printLine(s"Running on port $port")
    _ <- Server.serve(app).provide(Server.defaultWithPort(port))
  } yield ExitCode.success


  override def run = {
    program
  }
}
