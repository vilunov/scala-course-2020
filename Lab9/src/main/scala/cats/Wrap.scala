package cats

import scala.concurrent.Future

case class Wrap[I, O](value: I => Future[O])