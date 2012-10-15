package futures

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable._
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.util.Duration
import work._

@RunWith(classOf[JUnitRunner])
class MyPromisesTest extends Specification {
  val PROMISE_TIME_LIMIT: Long = 100

  def time[R](block: => R): R = {
    val t0 = System.currentTimeMillis()
    val result = block
    val t1 = System.currentTimeMillis()
    if (t1 - t0 > PROMISE_TIME_LIMIT)
      failure("You are too slow to promise")
    result
  }
  
  def delayFactorNumber(n: Long): FactorNumber = new FactorNumber(n, PROMISE_TIME_LIMIT * 2)
  
  "MyPromises" should {

    "compute square" in {
      val promise = time { MyPromises.computeSquare(2) }
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(4)
    }

    "compute square of future value" in {
      val futureValue = future {
        Thread.sleep(PROMISE_TIME_LIMIT * 2)
        2
      }
      val promise = time { MyPromises.computeSquare(futureValue) }
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(4)
    }

    "find max factor" in {
      val work = delayFactorNumber(49L)
      val promise = time { MyPromises.findMaxFactor(work) }
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(7L)
    }

    "find max factor of future factors" in {
      val futureFactors = future {
        delayFactorNumber(49L)
      }
      val promise = time { MyPromises.findMaxFactor(futureFactors) }
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(7L)
    }

    "do risky work or fallback on safe work" in {
      // Each work will exceed the time limit
      val shouldNotDoWork = new SumSequence(0, 4, PROMISE_TIME_LIMIT + 1)
      val safeWork = new SumSequence(0, 5, PROMISE_TIME_LIMIT + 1)
      val riskyWork = new SumSequence(-1, 6, PROMISE_TIME_LIMIT + 1)
      val promise = time { MyPromises.computeRiskySumFallbackOnSafeSum(safeWork, shouldNotDoWork) }
      val promise2 = time { MyPromises.computeRiskySumFallbackOnSafeSum(riskyWork, safeWork) }

      val result = Await.result(promise.future, Duration.Inf)
      val result2 = Await.result(promise2.future, Duration.Inf)
      result must beEqualTo(15)
      result2 must beEqualTo(15)
    }

    "find sum of all max factors" in {
      val work = Seq(delayFactorNumber(21L), delayFactorNumber(49L), delayFactorNumber(12L))
      val promise = time { MyPromises.findSumOfAllMaxFactors(work) }
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(20L)
    }
    
    "find max factor of all max factors in parallel" in {
      // Each work will take at least 100 milliseconds
      val work = Seq(delayFactorNumber(49L), delayFactorNumber(12L), delayFactorNumber(21L), delayFactorNumber(54L))
      
      val promise = time { MyPromises.findMaxFactorOfAllMaxFactorsInParallel(work) }
      val t1 = System.currentTimeMillis()
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(27)
      val totalExecutionTime = System.currentTimeMillis() - t1
      totalExecutionTime must beLessThan(PROMISE_TIME_LIMIT * 7)
      println("Parallel execution time: " + totalExecutionTime)
    }
  }
}