package actors

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.actors._

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
    "compute heavy work that cannot wait!" in {
      val actor = new ComputeActor
      actor.start
      val f = actor !? new HeavyWork()
      f match {
		  case x: Int => println("int!" + x)
		  case z: Any => println("any .." )
		  case _ => println("none of em")
		}
      
//      println("calling returned future?!")
//      while (!f().isSet) {
//        Thread.sleep(10)
//        println("waiting for a better future")
//      }
//      println("finally a bright new day : " + f + " or " + f())
    }
  }
}