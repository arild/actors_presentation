package actors

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.actors.Actor._

@RunWith(classOf[JUnitRunner])
class ComputeActorTest extends Specification {

  "ComputeActor" should {
    "compute length of string" in {
      val actor = new ComputeActor
      actor.start
      val len = actor !? "hello"
      len must beEqualTo(5)
    }
    "multiply numbers" in {
      val actor = new ComputeActor
      actor.start
      val n = actor !? 2
      n must beEqualTo(4)
    }
    "compute arbitrary work" in {
      val actor = new ComputeActor
      actor.start
      val result = actor !? new SumSequence(1, 3)
      result must beEqualTo(6)
    }
  }
}