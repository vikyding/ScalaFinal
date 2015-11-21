/**
  * Created by mengchending on 11/21/15.
  */

import org.scalatest.{ FlatSpec, Matchers, Inside }
import scala.util._




class UnitTest extends FlatSpec with Inside with Matchers {

  "Parser" should "RT @_janaebaeee_: Someone take me shopping" in {
    Parser.SplitAndFilter("RT @_janaebaeee_: Someone take me shopping") should matchPattern { case Success(h) => }
  }
}
