package actors

import scala.actors.Actor

abstract class Work {
  def perform(): Any
}

class SumSequence(start: Int, stop: Int) extends Work {
  def perform(): Int = {
    require(start >= 0)
    (start to stop).sum
  }
}

class ComputeActor extends Actor {
  def act() {
    receive {
      case s: String => sender ! s.length
      case n: Int => sender ! n * n
      case w: Work => sender ! w.perform()
    }
  }
}