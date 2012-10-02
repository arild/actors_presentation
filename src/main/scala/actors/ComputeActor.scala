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
    while (true) {
      receive {
        case s: String =>
          println("received : " + s)
          sender ! s.length
        case n: Int =>
          sender ! n * n
        case f: (Int => Boolean) =>
          sender ! f(2)
      }
    }
  }

}