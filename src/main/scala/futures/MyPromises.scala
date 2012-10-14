package futures

import scala.concurrent.Future
import scala.concurrent.future
import scala.concurrent.promise
import scala.concurrent.Promise
import scala.concurrent.Await
import scala.concurrent.util.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import work._

object MyPromises {

  def computeSquare(n: Int): Promise[Int] = {
    val p = promise[Int]()
    p completeWith future {
      n * n
    }
  }

  def computeSquare(f: Future[Int]): Promise[Int] = {
    val newFuture = f.map(n => n * n)
    val p = promise[Int]()
    p completeWith newFuture
  }

  def findMaxFactor(work: FactorNumber): Promise[Long] = {
    val p = promise[Long]()
    p completeWith future {
      val factors = work.perform()
      factors.max
    }
  }

  def findMaxFactor(work: Future[FactorNumber]): Promise[Long] = {
    val p = promise[Long]()
    val newFuture = work.map(w => w.perform.max)
    p completeWith newFuture
  }

  def computeRiskySumFallbackOnSafeSum(riskyWork: SumSequence, safeWork: SumSequence): Promise[Int] = {
    val p = promise[Int]()
    val riskyRes = future { riskyWork.perform }
    val safeRes = future { safeWork.perform }
    p completeWith {
      riskyRes recoverWith {
        case e: IllegalArgumentException => safeRes
      }
    }
  }

  def findSumOfAllMaxFactors(work: Seq[FactorNumber]): Promise[Long] = {
    val p = promise[Long]()
    p completeWith future {
      work.map(w => w.perform.max).sum
    }
  }

  def findMaxFactorOfAllMaxFactorsInParallel(work: Seq[FactorNumber]): Promise[Long] = {
    val p = promise[Long]()
    val futureFactors: Seq[Future[Seq[Long]]] = work.map(w => future { w.perform })
    p completeWith Future.fold(futureFactors)(0L)((r, c) => Math.max(r, c.max))
  }
}

object Examples extends App {
  
  def helloWorld() = {
    println("Test print before future")
    val s = "Hello"
    val f = future {
      Thread.sleep(1000);
      s + " future!"
    }
    f onSuccess { case v => println(v) }
    println("Test print after future")
    Await.ready(f, Duration.Inf) // Blocks until future is ready
  }

  def simpleTransformations() = {
    val f1 = future {
      Thread.sleep(1000)
      println("Original future done")
      1 + 1
    }

    val f2 = f1.map(x => { // Completely asynchronously
      Thread.sleep(1000)
      println("Transformation future done")
      x + 1
    })

    f2 onSuccess { case v => println("Result: " + v) }
    Await.ready(f2, Duration.Inf)
  }

  helloWorld
  //simpleTransformations
}



