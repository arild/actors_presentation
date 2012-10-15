package actors

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable._
import scala.actors._
import scala.actors.Futures._
import work._

@RunWith(classOf[JUnitRunner])
class ComputeActorTest extends Specification {

  "ComputeActor" should {
    "compute length of string" in {}
    "multiply numbers" in {}
    "compute arbitrary work synchronously" in {}
    "compute arbitrary work in the future" in {}
  }
}