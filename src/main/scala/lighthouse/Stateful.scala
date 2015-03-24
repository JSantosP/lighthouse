package lighthouse

import akka.actor.{Props, Actor}
import lighthouse.utils.Loggable
import spray.can.Http
import spray.http.ContentTypes._
import spray.http.HttpMethods._
import spray.http._
import spray.json._
import StatusCode.int2StatusCode

import scala.reflect.ClassTag
import scala.util.Try

class Stateful[T: ClassTag : JsonFormat](
  id: String,
  parentUri: String,
  initialState: Option[T]) extends Actor with Loggable{

  private var state: Option[T] = initialState

  val path = s"$parentUri$id"

  override def receive = {

    case conn: Http.Connected =>
      // when a new connection comes in we register ourselves as the connection handler
      log.info(s"Incomming connection " +
        s"[From] ${conn.remoteAddress} " +
        s"[To] ${conn.localAddress}")
      sender ! Http.Register(self)

    case req @ HttpRequest(GET, uri, _, _, _) if uri.toRelative.toString() == path =>
      log.info(s"Received : $req")
      sender() ! state.fold(ifEmpty = HttpResponse(status = 204))(state =>
        HttpResponse(entity = implicitly[JsonFormat[T]].write(state).toString()))

    case req @ HttpRequest(PUT, uri, headers, HttpEntity.NonEmpty(contentType,entity), _) if uri.toRelative.toString() == path && contentType==`application/json` =>
      log.info(s"Received : $req")
      sender() ! Try{
        state = Some(implicitly[JsonFormat[T]].read(entity.asString.parseJson))
        HttpResponse(200)
      }.recover{
        case _:Throwable => HttpResponse(500)
      }.get

  }

  override def unhandled(obj: Any): Unit = {
    log.info(s"Unhandled message : $obj")
    super.unhandled(obj)
  }

}

object Stateful {

  def apply[T: ClassTag : JsonFormat](
    id: String,
    parentUri: String = "/",
    initialState: Option[T] = None): Props = Props(
    new Stateful[T](id, parentUri, initialState))

}
