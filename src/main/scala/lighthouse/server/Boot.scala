package lighthouse.server

import akka.actor.ActorSystem
import akka.io.IO
import com.typesafe.config.ConfigFactory
import spray.can.Http
import util.Properties

object Boot extends App
  with LightHouse
  with Configuration {

  implicit val system = ActorSystem("lighthouse")

  lazy val service = system.actorOf(Service(resources))

  IO(Http) ! Http.Bind(service, interface = bindingIp, port = bindingPort)

}

trait LightHouse {
  _: Configuration =>

  type Resource = String
  type Path = String

  lazy val resources: Map[Resource, Path] = resourceList.map(_ -> "/").toMap

}

trait Configuration {

  import scala.collection.JavaConversions._

  val config = ConfigFactory.load("server.conf")

  val bindingIp = config.getString("app.server.bind.ip")
  val bindingPort = Properties.envOrElse(
    "PORT",
    config.getString("app.server.bind.port")).toInt
  val resourceList = config.getStringList("app.server.resources").toList

}

