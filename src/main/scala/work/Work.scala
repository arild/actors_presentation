package work

abstract class Work {
  def perform(): Any
}

class SumSequence(start: Int, stop: Int, delay: Long = 0) extends Work {
  def perform(): Int = {
    require(start >= 0)
    Thread.sleep(delay)
    (start to stop).sum
  }
}

class MaxFactor(n: Long) extends Work {
  def perform(): Long = {
    val factors = new FactorNumber(n).perform
    factors.max
  }
}

class FactorNumber(n: Long, delay: Long = 0) extends Work {
  def perform(): List[Long] = {
    Thread.sleep(delay)
    factor(n)
  }

  /**
   * Faktoriserer et tall, på naivt vis
   */
  def factor(n: Long): List[Long] = {

    def factor0(a: Long, acc: List[Long]): List[Long] = {
      def isFaktor(f: Long, p: Long): Boolean = p % f == 0
      if (a > 1) {
        if (isFaktor(a, n))
          factor0(a - 1, List(a) ::: acc)
        else
          factor0(a - 1, acc)
      } else {
        acc
      }
    }
    factor0(n - 1, List())
  }
}
