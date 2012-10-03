package actors

import scala.actors.Actor
import scala.actors.Futures

abstract class Work {
  def perform(): Any
}

class SumSequence(start: Int, stop: Int) extends Work {
  def perform(): Int = {
    require(start >= 0)
    (start to stop).sum
  }
}

case class HeavyWork

//class HeavyWork(x: Int) extends Work {
//  def perform(): Int = {
//    Thread.sleep(10000)
//    x
//  }
//}

class ComputeActor extends Actor {
  def act() {
    receive {
      case s: String => sender ! s.length
      case n: Int => sender ! n * n
      case w: Work => sender ! w.perform()
      case h: HeavyWork => sender ! Futures.future {
        //      Thread.sleep(5000)
        println("omg, the future arrived")
        42
      }
    }
  }

  def doSomeHeavyLifting() = Futures.future {
    Futures.future {
      //      Thread.sleep(5000)
      println("omg, the future arrived")
      42
    }
  }
}

object Asdf {
  def main(args: Array[String]) {
    val f = Futures.future {
      //      Thread.sleep(5000)
      println("omg, the future arrived")
      42
    }
    println(f())
  }
}