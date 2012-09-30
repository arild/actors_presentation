package actors

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.actors.Actor._

@RunWith(classOf[JUnitRunner])
class ComputeActorTest extends Specification {
  
  "ComputeActor" should {
    "respond" in {
      println("running test")
      val aktor = new KomputeActor()
      aktor.start()
      val s = aktor !? "hello"
      s match {
        case length : Int => length must beEqualTo(5)
      }
    }
  }
  
}