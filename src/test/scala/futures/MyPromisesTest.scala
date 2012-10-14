package futures

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import work._
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.util.Duration
import scala.util.Random

@RunWith(classOf[JUnitRunner])
class MyPromisesTest extends Specification {
  val PROMISE_TIME_LIMIT: Long = 101

  def time[R](block: => R): R = {
    val t0 = System.currentTimeMillis()
    val result = block
    val t1 = System.currentTimeMillis()
    println("Time: " + (t1 - t0));
    if (t1 - t0 > PROMISE_TIME_LIMIT)
      failure("You are too slow to promise")
    result
  }
  
  def busyWork() = Thread.sleep(PROMISE_TIME_LIMIT + 1)

  "MyPromises" should {

    "compute square" in {
      val promise = time { MyPromises.computeSquare(2) }
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(4)
    }

    "compute square of future value" in {
      val futureValue = future {
        busyWork()
        2
      }
      val promise = time { MyPromises.computeSquare(futureValue) }
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(4)
    }

    "find max factor" in {
      val work = new FactorNumber(4723755L)
      val promise = time { MyPromises.findMaxFactor(work) }
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(1574585L)
    }

    "find max factor of future factors" in {
      val futureFactors = future {
        busyWork()
        new FactorNumber(4723755L)
      }
      val promise = time { MyPromises.findMaxFactor(futureFactors) }
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(1574585L)
    }

    "do risky work or fallback on safe work" in {
      // All work will exceed the time limit
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

    "find sum of max factors" in {
      val work = Seq(new FactorNumber(472375L), new FactorNumber(4872335L), new FactorNumber(7172225L))
      val promise = time { MyPromises.findSumOfMaxFactors(work) }
      val result = Await.result(promise.future, Duration.Inf)
      println(result)
      result must beEqualTo(2503387L)
    }
    
    "findMaxFactorOfAllMaxFactorsInParallel" in {
      // Each work will take at least 100 milliseconds
      def wrap(n: Long): FactorNumber = new FactorNumber(n, PROMISE_TIME_LIMIT + 1)
      val work = Seq(wrap(49), wrap(12L), wrap(21L), wrap(54L))
      
      val promise = time { MyPromises.findMaxFactorOfAllMaxFactorsInParallel(work) }
      val t1 = System.currentTimeMillis()
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(27)
      val totalExecutionTime = System.currentTimeMillis() - t1
      // Requires multiple cores to pass
      totalExecutionTime must beLessThan(PROMISE_TIME_LIMIT * 4)
      println("Parallel execution time: " + totalExecutionTime)
    }
  }
}