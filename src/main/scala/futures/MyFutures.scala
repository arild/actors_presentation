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
        println("Starting executing future")
        Thread.sleep(3000)
        n * n
      }
      println("computeSquare done");
      p
  }
}