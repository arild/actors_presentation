package actors

import scala.actors.Actor

class KomputeActor extends Actor {

  def act() {
   while(true) {
    receive{
	  case s: String => 
	    println("received : " + s) 
	    sender ! s.length
	  case _ =>
	    println("unknown stuff..")
	    sender ! -1
	}
   }
  }
  
}