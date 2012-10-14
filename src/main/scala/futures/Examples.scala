package futures

import scala.concurrent.Future
import scala.concurrent.future
import scala.concurrent.Await
import scala.concurrent.util._
import scala.concurrent.promise
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random
import work._
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleExtension
import scala.util.Try

object SimpleFutureExapmles {
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
    }.map(x => x + 1) // Completely asynchronously
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
}

object AdvancedFutureExamples {
  def factorNumbersInParallel() = {
    // Wraps a number into a future that computes the number's factors
    def toFutureFactors(number: Long): Future[Seq[Long]] = {
      future {
        println("Original future is executing");
        val f = new FactorNumber(number)
        val res = f.perform()
        println("Original future is done");
        res
      }
    }
    val f1 = toFutureFactors(545515193L)
    val f2 = toFutureFactors(958284559L)
    val f3 = toFutureFactors(215487021L)

    // Elegant way
    //    val res1: Seq[Future[Seq[Long]]] = Seq(f1, f2, f3)
    //    val res2 = Future.fold(res1)(0L)((r, c) => {
    //      Math.max(r, c.max)
    //    })
    //    res2 onSuccess { case v => println("Result: " + v) }
    //    println("Waiting for result...")
    //    Await.ready(res2, Duration.Inf)

    // Not so elegant way
    val res1: Seq[Future[Seq[Long]]] = Seq(f1, f2, f3)
    val res2: Future[Seq[Seq[Long]]] = Future.sequence(res1)
    val res3 = res2.map(factorSequences => {
      println("All factors computed. Finding max now.")
      factorSequences.foldLeft(0L)((r, c) => Math.max(r, c.max))
    })
    res3 onSuccess { case v => println("Result: " + v) }
    println("Waiting for result...")
    Await.ready(res3, Duration.Inf)

    // Finer granularity on tasks: Finding max of sub-results are wrapped into new futures
    //    val factorSequences: Seq[Future[Seq[Long]]] = Seq(f1, f2, f3)
    //    val maxFactors: Seq[Future[Long]] = factorSequences.map(f => {
    //      println("Creating new future")
    //      f.map({
    //        factors =>
    //          Thread.sleep(1000 + Random.nextInt(3000))
    //          println("Sub-result done: " + factors)
    //          factors.max
    //      })
    //    })
    //    val res: Future[Seq[Long]] = Future.sequence(maxFactors)
    //    res onSuccess { case v => println("Final result: " + v.max) }
    //    println("Waiting for result...")
    //    Await.ready(res, Duration.Inf)
  }

  factorNumbersInParallel
}

object Promises {
  def lightWork(): Future[Int] = {
    val f = future {
      new SumSequence(0, 1).perform
    }
    f.mapTo[Int]
  }

  //  val p1 = promise[Int]()
  //  println("Completing promise");
  //  p1 complete (Try({ Thread.sleep(1000); new SumSequence(0, 1).perform }))
  //  p1.future onSuccess { case v => println("Result: " + v) }
  //  println("Waiting for result...")
  //  Await.ready(p1.future, Duration.Inf)
  //  println("Done completing")

  val p1 = promise[Int]()
  println("Completing promise");
  p1 completeWith lightWork
  p1.future onSuccess { case v => println("Result: " + v) }
  println("Waiting for result...")
  Await.ready(p1.future, Duration.Inf)

}

object Futures extends App {

  //  helloWorld
  //  simpleTransformations
  //  selectFutureFinishedFirst
  //  sumOfMultipleResultsWithPowerTools
  //  forComprehension
  // failureRecovery
  SimpleFutureExapmles.helloWorld
  //AdvancedFutureExamples
  //Promises
}