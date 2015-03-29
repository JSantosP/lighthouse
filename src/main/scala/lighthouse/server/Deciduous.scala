package lighthouse.server

import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

trait Deciduous[S] extends Stateful[S] {
  import Deciduous._
  import context._

  val ttl = ConfigFactory.load("server.conf").getInt("app.server.stateful.ttl")

  val scheduler = context.system.scheduler.schedule(ttl.seconds, ttl.seconds, self, CheckTTL)

  override def receive = {
    case CheckTTL => state.fold(()) {
      case (s, lastUpdate) if now - lastUpdate > (ttl * 1000) => state = None
      case _ =>
    }
    case msg => super.receive(msg)
  }

  override def postStop(): Unit = {
    scheduler.cancel()
    super.postStop()
  }

}

object Deciduous {

  def now = System.currentTimeMillis()

  case object CheckTTL

}