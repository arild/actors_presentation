package futures

import scala.concurrent.Future
import scala.concurrent.future
import scala.concurrent.promise
import scala.concurrent.Promise
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.util.Duration
import scala.util.Random

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random
import work._
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleExtension
import scala.util.Try

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
    val newFuture = work.map(w => w.perform().max)
    val p = promise[Long]()
    p completeWith newFuture
  }

  def computeRiskySumFallbackOnSafeSum(riskyWork: SumSequence, safeWork: SumSequence): Promise[Int] = {
    val p = promise[Int]()
    val riskyRes = future { riskyWork.perform() }
    val safeRes = future { safeWork.perform() }
    p completeWith {
      riskyRes recoverWith { case e: IllegalArgumentException => safeRes }
    }
  }

  def findSumOfMaxFactors(work: Seq[FactorNumber]): Promise[Long] = {
    val p = promise[Long]()
    p completeWith future {
      val res = work.map(w => w.perform().max)
      res.sum
    }
  }
  
  def findMaxFactorOfAllMaxFactorsInParallel(work: Seq[FactorNumber]): Promise[Long] = {
    val p = promise[Long]()
    val futureFactors: Seq[Future[Seq[Long]]] = work.map(w => future { w.perform } )
    p completeWith Future.fold(futureFactors)(0L)((res, factors) => Math.max(res, factors.max))
    
    // Sequential
//    val p = promise[Long]()
//    p completeWith future {
//      val maxFactors = work.map(w => w.perform.max)
//      println(maxFactors)
//      maxFactors.max
//    }
  }
}