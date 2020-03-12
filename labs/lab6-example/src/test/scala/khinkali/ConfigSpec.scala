package khinkali

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.error.ConfigReaderFailures

class ConfigSpec extends AnyFlatSpec  with Matchers {
  import ConfigSpec._
  "Config" should "be correctly parsed from a string representation" in {
    val config = ConfigSource.string(configString).load[Config]
    config shouldBe a [Right[ConfigReaderFailures, Config]]
  }
}

object ConfigSpec {
  val configString = "number-of-chefs = 10\nnumber-of-customers = 2\nseed = 209384\nchef-config = { type = \"chef-config\",  cooking-time-boundaries = { type = \"boundaries\", min = 1, max = 2 }}\ncustomer-config = { type = \"customer-config\",\n  order-decision-time-boundaries = { type = \"boundaries\", min = 1, max = 2 },\n  eating-time-boundaries = { type = \"boundaries\", min = 1, max = 2 },\n  order-size-boundaries: { type = \"boundaries\", min = 1, max = 2 },\n  khinkali-amount-boundaries = { type = \"boundaries\", min = 1, max = 2 }\n}"
}
