package work

abstract class Work {
  def perform(): Any
}

class SumSequence(start: Int, stop: Int) extends Work {
  def perform(): Int = {
    require(start >= 0)
    (start to stop).sum
  }
}

class FactorNumber(n: Long) extends Work {
  def perform(): List[Long] = {
    faktor(n)
  }

  /**
   * Faktoriserer et tall, på naivt vis
   */
  def faktor(n: Long): List[Long] = {

    def faktor0(a: Long, acc: List[Long]): List[Long] = {
      def isFaktor(f: Long, p: Long): Boolean = p % f == 0
      if (a > 1) {
        if (isFaktor(a, n))
          faktor0(a - 1, List(a) ::: acc)
        else
          faktor0(a - 1, acc)
      } else {
        acc
      }
    }
    faktor0(n - 1, List())
  }
}