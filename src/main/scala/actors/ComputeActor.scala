package actors

import scala.actors.Actor
import work._

class ComputeActor extends Actor {
  def computeSquare(i: Int) = i * i
  def computeLength(s: String) = s.length()

  def act() {
    while (true) {
      receive {
        case i: Int => sender ! computeSquare(i)
        case s: String => sender ! computeLength(s);
        case w: Work => sender ! w.perform()
      }
    }
  }
}