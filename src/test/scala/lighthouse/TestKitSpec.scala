package lighthouse

import akka.actor.ActorSystem
import akka.testkit._
import org.scalatest.{Matchers, BeforeAndAfter, FlatSpecLike}

abstract class TestKitSpec extends TestKit(ActorSystem())
with Matchers
with ImplicitSender
with FlatSpecLike
with BeforeAndAfter{

  override protected def after(fun: => Any): Unit = {
    system.shutdown()
    super.after(fun)
  }

}