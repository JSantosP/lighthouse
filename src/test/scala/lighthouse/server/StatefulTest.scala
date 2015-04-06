package lighthouse.server

import spray.http._
import spray.json._
import spray.http.HttpMethods._
import spray.http.ContentTypes._

class StatefulTest extends lighthouse.TestKitSpec {

  import serializers._
  import SampleValues._

  behavior of "Stateful"

  it should "return a 204 error if entity is not previously set" in {
    val stateful = system.actorOf(Stateful[Foo]("1"))
    stateful ! HttpRequest(GET, Uri("/1"), null, null, null)
    val Seq(HttpResponse(code,_,_,_)) = receiveN(1)
    code should equal(StatusCode.int2StatusCode(204))
  }

  it should "return the initial value if entity " +
    "is not modified after initialization" in {
    val stateful = system.actorOf(Stateful[Foo]("1","/",Some(foo1)))
    stateful ! HttpRequest(
      GET,
      Uri("/1"),
      List[HttpHeader](),
      "",
      HttpProtocols.`HTTP/1.1`)
    val Seq(resp @ HttpResponse(code,HttpEntity.NonEmpty(`application/json`,data),_,_)) = receiveN(1)
    val response = new String(data.toByteArray).parseJson
    code should equal(StatusCode.int2StatusCode(200))
    fooSer.read(response) should equal(foo1)
  }

  it should "modify containing value" in {
    val stateful = system.actorOf(Stateful[Foo]("1","/",Some(foo1)))
    val put = {
      stateful ! HttpRequest(
        PUT,
        Uri("/1"),
        List[HttpHeader](),
        HttpEntity(`application/json`,fooSer.write(foo2).toString()),
        HttpProtocols.`HTTP/1.1`)
      val Seq(resp @ HttpResponse(code,entity,_,_)) = receiveN(1)
      code should equal(StatusCode.int2StatusCode(200))
      entity should equal(HttpEntity.Empty)
    }
    val get = {
      stateful ! HttpRequest(
        GET,
        Uri("/1"),
        List[HttpHeader](),
        "",
        HttpProtocols.`HTTP/1.1`)
      val Seq(resp @ HttpResponse(code,HttpEntity.NonEmpty(_,data),_,_)) = receiveN(1)
      val response = new String(data.toByteArray).parseJson
      code should equal(StatusCode.int2StatusCode(200))
      fooSer.read(response) should equal(foo2)
    }

  }

}

object SampleValues {
  val foo1 = Foo(3,"hi",List(1,2,3))
  val foo2 = Foo(4,"ey",List(4,5,6))
}

case class Foo(a1: Int, a2: String, a3: List[Int])

object serializers extends DefaultJsonProtocol {
 implicit val fooSer: JsonFormat[Foo] = jsonFormat3(Foo)
}
