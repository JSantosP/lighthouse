package lighthouse

import scala.language.implicitConversions
import spray.json._

object model extends DefaultJsonProtocol {

  case class ResourceValue(value: String)

  implicit val resourceValueFormat: RootJsonFormat[ResourceValue] =
    jsonFormat1(ResourceValue)

  implicit def stringToJsValue(json: String):JsValue =
  	json.parseJson

}