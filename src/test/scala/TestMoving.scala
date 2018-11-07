package movingavg

import org.specs2._

class TestMoving extends Specification {
  def is = s2"""
  solution $e1
"""

  val e1 = new ComputeMovingAverages(new CompAvg, 10*1000L).run() must_== ()
}
