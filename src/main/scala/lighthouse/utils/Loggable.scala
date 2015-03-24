package lighthouse.utils

import org.slf4j.LoggerFactory

trait Loggable {

  val log = LoggerFactory.getLogger(getClass.getName)

}
