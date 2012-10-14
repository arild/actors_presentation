package futures

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import work._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.util.Duration

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
      val promise = MyFutures.computeSquare(2)
      val result = Await.result(promise.future, Duration.Inf)
      result must time { beEqualTo(4) }
    }
  }
}