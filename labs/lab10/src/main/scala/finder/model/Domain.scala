package finder.model

import cats.Show
import tofu.logging.Loggable

case class Keyword(value: String) extends AnyVal

object Keyword {
  implicit val showKeyword: Show[Keyword] = _.value
  implicit val loggableKeyword: Loggable[Keyword] = Loggable.show
}

case class Url(value: String) extends AnyVal

object Url {
  implicit val showUrl: Show[Url] = _.value
  implicit val loggableUrl: Loggable[Url] = Loggable.show
}

case class KeywordCount(keyword: Keyword, count: Int)

object KeywordCount {
  implicit val showKeywordCount: Show[KeywordCount] = c => s"(${c.keyword.value}, ${c.count})"
  implicit val loggableKeywordCount: Loggable[KeywordCount] = Loggable.show
}
