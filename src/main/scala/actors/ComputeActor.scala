package actors

import scala.actors.Actor
import scala.actors.Futures._

abstract class Work {
  def perform(): Any
}

class SumSequence(start: Int, stop: Int) extends Work {
  def perform(): Int = {
    require(start >= 0)
    (start to stop).sum
  }
}

case object HeavyWork {
  
}

//class HeavyWork(x: Int) extends Work {
//  def perform(): Int = {
//    Thread.sleep(10000)
//    x
//  }
//}

class ComputeActor extends Actor {
  def computeSquare(i: Int) = i * i
  def computeLength(s: String) = s.length()

  def act() {
    receive {
      case i: Int => sender ! computeSquare(i)
      case s: String => sender ! computeLength(s);
      case w: Work => sender ! w.perform()
    }
  }

  def doSomeHeavyLifting() = future {
    future {
      //      Thread.sleep(5000)
      println("omg, the future arrived")
      42
    }
  }
}

object Asdf {
  def main(args: Array[String]) {
    val f = future {
      //      Thread.sleep(5000)
      println("omg, the future arrived")
      42
    }
    println(f())
  }
}