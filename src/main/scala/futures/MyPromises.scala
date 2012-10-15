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

  def computeSquare(n: Int): Promise[Int] = ???

  def computeSquare(f: Future[Int]): Promise[Int] = ???

  def findMaxFactor(work: FactorNumber): Promise[Long] = ???

  def findMaxFactor(work: Future[FactorNumber]): Promise[Long] = ???

  def computeRiskySumFallbackOnSafeSum(riskyWork: SumSequence, safeWork: SumSequence): Promise[Int] = ???

  def findSumOfAllMaxFactors(work: Seq[FactorNumber]): Promise[Long] = ???

  def findMaxFactorOfAllMaxFactorsInParallel(work: Seq[FactorNumber]): Promise[Long] = ???

}

object Examples extends App {

  def futureHelloWorld() = {
    println("Test print before future")
    val s = "hello"
    val f = future {
      Thread.sleep(10)
      s + " future!"
    }
    println("Test print after future")
    f onSuccess { case s => println(s) } //Completely asynchronous
    Await.ready(f, Duration.Inf) //Blocks until the future is ready
  }

  def promiseHelloWorld() = {
    val f = future {
      Thread.sleep(10)
      println("Computing sum...")
      new SumSequence(0, 2).perform
    }
    val p = promise[Int]()
    p completeWith f
    val result = Await.result(p.future, Duration.Inf)
    println("Sum = " + result)
  }

  def simpleTransformation() = {
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

  futureHelloWorld
  //  promiseHelloWorld
  //  simpleTransformation
}



