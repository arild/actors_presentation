package futures

import scala.concurrent.Future
import scala.concurrent.future
import scala.concurrent.Await
import scala.concurrent.util._
import scala.concurrent.promise
import scala.concurrent.Promise
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random
import work._
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleExtension
import scala.util.Try

object MyFutures {
  def computeSquare(n: Int): Promise[Int] = {
    val p = promise[Int]()
    p completeWith future {
      n * n
    }
  }
  
  def findMaxFactor(work: FactorNumber): Promise[Long] = {
	val p = promise[Long]()
    p completeWith future {
	  val factors = work.perform()
	  factors.max
	}
  }
  
  def findSumOfMaxFactors(work: Seq[FactorNumber]) : Promise[Long] = {
    val p = promise[Long]()
    p completeWith future {
      val res = work.map(w => w.perform().max)
      res.sum
    }
  }
  
  def findRiskySumFallbackOnSafeSum(risky: SumSequence, safe: SumSequence) : Promise[Int] = {
    val p = promise[Int]()
    val riskyRes = future {
      risky.perform()
    }
    val safeRes = future {
      safe.perform()
    }
    p completeWith {
    	riskyRes recoverWith { case e: IllegalArgumentException => safeRes }
    }
  }
}