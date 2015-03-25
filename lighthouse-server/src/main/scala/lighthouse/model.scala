package lighthouse

import spray.json.{JsonFormat, DefaultJsonProtocol}


object model extends DefaultJsonProtocol {

  case class ResourceValue(value: String)

  implicit val resourceValueFormat: JsonFormat[ResourceValue] =
    jsonFormat1(ResourceValue)

}