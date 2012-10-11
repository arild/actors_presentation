package actors

import scala.actors.Actor
import scala.concurrent.Future
import scala.concurrent.future
import scala.concurrent.Await
import scala.concurrent.util._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

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

object Futures extends App {
  def lightWork(): Future[Int] = {
    val f = future {
      new SumSequence(0, 1).perform
    }
    f.mapTo[Int]
  }

  def heavyWork(): Future[Int] = {
    val f = future {
      Thread.sleep(1000)
      new SumSequence(0, 5).perform
    }
    f.mapTo[Int]
  }

  def riskyWork(): Future[Int] = {
    val f = future {
      val mightBeNegative = Random.nextInt(2) - 1
      new SumSequence(mightBeNegative, 5).perform
    }
    f.mapTo[Int]
  }

  def helloWorld() = {
    println("Test print before future")
    val s = "Hello"
    val f = future { s + " future!" }
    f onSuccess { case v => println(v) }
    println("Test print after future")
    Await.ready(f, Duration.Inf) // Blocks until future is ready
  }

  def simpleTransformations() = {
    val f = future {
      Thread.sleep(1000)
      1 + 1
    }.map(_ + 1) // Completely asynchronously
    f onSuccess { case v => println(v) }
    Await.ready(f, Duration.Inf)
  }

  def safeTypecast() = {
    val f: Future[Any] = future {
      new SumSequence(0, 2000).perform()
    }
    val f2 = f
    f2 onSuccess { case v => println(v) }
    Await.ready(f2, Duration.Inf)
  }

  def selectFutureFinishedFirst() = {
    val result = lightWork() either heavyWork() // Light work (should) always win
    result onSuccess { case v => println(v) }
    Await.ready(result, Duration.Inf)

    // Alternative way
    val sequenceOfFutures = Seq(lightWork(), heavyWork())
    val result2 = Future.firstCompletedOf(sequenceOfFutures)
    result2 onSuccess { case v => println(v) }
    Await.ready(result2, Duration.Inf)
  }

  def sumOfMultipleResults() = {
    val sequenceOfFutures = Seq(lightWork(), heavyWork())
    val futureOfSequence: Future[Seq[Int]] = Future.sequence(sequenceOfFutures)
    futureOfSequence onSuccess { case v => println("Sum: " + v.sum) }
    Await.ready(futureOfSequence, Duration.Inf)
  }

  def sumOfMultipleResultsWithPowerTools() = {
    val sequenceOfFutures: Seq[Future[Int]] = Seq(lightWork(), heavyWork())
    val sum: Future[Int] = Future.reduce(sequenceOfFutures)(_ + _) // Asynchronous
    sum onSuccess { case v => println(v) }
    println("Waiting for reduce to complete")
    Await.ready(sum, Duration.Inf)
  }

  def failureRecovery() = {
    val sum = riskyWork() recoverWith { case e: IllegalArgumentException => lightWork() }
    sum onSuccess { case v => println("Success: " + v) } // onFailure will not trigger with recoverWith set
    Await.ready(sum, Duration.Inf)
  }

  def forComprehension() = {
    val f = for {
      v1 <- lightWork()
      v2 <- heavyWork()
    } yield v1 + v2 // Yields a new future

    f onSuccess { case v => println(v) }
    Await.ready(f, Duration.Inf)
  }

  //  helloWorld
  //  simpleTransformations
  //  selectFutureFinishedFirst
  //  sumOfMultipleResultsWithPowerTools
  //  forComprehension
  failureRecovery

}