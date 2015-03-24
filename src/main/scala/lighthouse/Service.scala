package lighthouse

import akka.actor.{ActorRef, Props, Actor}
import lighthouse.utils.Loggable
import spray.can.Http
import spray.http.HttpRequest

import model._

class Service(
  resourceMap: Map[LightHouse#Resource, LightHouse#Path]) extends Actor
with Loggable {

  log.info(s"Resource map : $resourceMap")

  val resourceActors: Map[LightHouse#Path, ActorRef] =
  resourceMap.map {
    case (resource, path) =>
      s"$path$resource" -> context.actorOf(Stateful[ResourceValue](resource, path))
  }

  log.info(s"Resource actors : $resourceActors")

  override def receive = {

    case conn: Http.Connected =>
      // when a new connection comes in we register ourselves as the connection handler
      log.info(s"Incomming connection " +
      s"[From] ${conn.remoteAddress} " +
      s"[To] ${conn.localAddress}")
      sender ! Http.Register(self)

    case req: HttpRequest =>
      val target = resourceActors.get(req.uri.toRelative.toString())
      log.info(s"""Forwarding $req to [${target.getOrElse("")}]""")
      target.foreach(_ forward req)
  }

  override def unhandled(obj: Any): Unit = {
    log.info(s"Unhandled message : $obj")
    super.unhandled(obj)
  }

}

object Service {
  def apply(resourceMap: Map[LightHouse#Resource, LightHouse#Path]): Props =
    Props(new Service(resourceMap))
}
