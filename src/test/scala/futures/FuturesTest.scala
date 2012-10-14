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
class ComputeActorTest extends Specification {

  def time[R](block: => R): R = {
    val t0 = System.currentTimeMillis()
    val result = block
    val t1 = System.currentTimeMillis()
    if (t1 - t0 > 100)
      failure("You are too slow to promise")
    result
  }

  "MyFutures" should {
    "compute square" in {
      val promise = time { MyFutures.computeSquare(2) }
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(4)
    }
    "compute square of future value" in {
      val futureValue = future {
        Thread.sleep(101);
        2
      }
      val promise = time { MyFutures.computeSquare(futureValue) }
      val result = Await.result(promise.future, Duration.Inf)
      result must beEqualTo(4)
    }
    "find max factor" in {
      val work = new FactorNumber(4723755L)
      val promise = time { MyFutures.findMaxFactor(work) }
      val result = Await.result(promise.future, Duration.Inf)
      println(result)
      result must beEqualTo(1574585L)
    }
    "find sum of max factors" in {
      val work = Seq(new FactorNumber(472375L), new FactorNumber(4872335L), new FactorNumber(7172225L))
      val promise = time { MyFutures.findSumOfMaxFactors(work) }
      val result = Await.result(promise.future, Duration.Inf)
      println(result)
      result must beEqualTo(2503387L)
    }
    "finds risky sum fallbacks on safe" in {
      val mightBeNegative = Random.nextInt(2) - 1
      val riskyWork = new SumSequence(mightBeNegative, 6)
      val safeWork = new SumSequence(0, 5)
      val promise = time { MyFutures.findRiskySumFallbackOnSafeSum(riskyWork, safeWork) }
      if (mightBeNegative < 0) {
        val result = Await.result(promise.future, Duration.Inf)
        result must beEqualTo(15)
      } else {
        val result = Await.result(promise.future, Duration.Inf)
        result must beEqualTo(21)
      }

    }
  }
}