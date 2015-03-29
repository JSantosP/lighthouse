package lighthouse.server

import scala.reflect.ClassTag
import scala.util.Try

import akka.actor.{ Props, Actor }
import lighthouse.utils.Loggable
import spray.can.Http
import spray.http.ContentTypes._
import spray.http.HttpMethods._
import spray.http._
import spray.json._
import StatusCode.int2StatusCode
import Stateful._

trait Stateful[S] extends Actor with Loggable {

  implicit val ctag: ClassTag[S]
  implicit val jsonFormat: JsonFormat[S]

  type LastUpdate = Long
  type State = (S, LastUpdate)

  val id: String

  val parentUri: String = "/"

  val initialState: Option[S] = None

  var state: Option[State] = initialState.map(_ -> Never)

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
      sender() ! state.fold(ifEmpty = HttpResponse(status = 204)) {
        case (s, _) =>
          HttpResponse(entity = implicitly[JsonFormat[S]].write(s).toString())
      }

    case req @ HttpRequest(PUT, uri, headers, HttpEntity.NonEmpty(contentType, entity), _) if uri.toRelative.toString() == path && contentType == `application/json` =>
      log.info(s"Received : $req")
      sender() ! Try {
        state = Some(implicitly[JsonFormat[S]].read(entity.asString.parseJson) -> System.currentTimeMillis())
        HttpResponse(200)
      }.recover {
        case _: Throwable => HttpResponse(500)
      }.get

  }

  override def unhandled(obj: Any): Unit = {
    log.info(s"Unhandled message : $obj")
    super.unhandled(obj)
  }

}

object Stateful {

  def apply[S](
    _id: String,
    _parentUri: String = "/",
    _initialState: Option[S] = None,
    deciduous: Boolean = false)(implicit ev1:ClassTag[S],ev2: JsonFormat[S]): Props = Props({
    if (!deciduous)
      new {
        override implicit val ctag: ClassTag[S] = ev1
        override implicit val jsonFormat: JsonFormat[S] = ev2
        val id = _id
        override val parentUri = _parentUri
        override val initialState = _initialState
      } with Stateful[S]
    else new  {
      override implicit val ctag: ClassTag[S] = ev1
      override implicit val jsonFormat: JsonFormat[S] = ev2
      val id = _id
      override val parentUri = _parentUri
      override val initialState = _initialState
    } with Deciduous[S]
  })

  val Never = 0L

}
