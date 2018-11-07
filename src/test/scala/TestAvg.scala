package movingavg

import org.specs2._

class TestAvg extends Specification {
  def is = s2"""

  AvgState should

    allow insertion          $e1
    allow deletion           $e2
    indicate correct average $e3
    increment start position $e4
    decrement start position $e5
  """

  val testNum = 1000000
  val testVal = 1.0
  var avg = AvgState()

  val e1 = {
    val startTime = System.currentTimeMillis
    for( index <- 1 to testNum) {
      avg = avg.addVal(testVal)
    }
    val endTime = System.currentTimeMillis
    val diffTime = endTime - startTime
    println(s"Updated state $testNum times in $diffTime ms.")

    println(avg.toString)
    avg.num must_== testNum
  }

  val e2 = {
    avg = avg.subVal(testNum/2*testVal, testNum/2)
    avg.num must_== testNum/2
  }

  val e3 = {
    avg.avg must_== testVal
  }

  val e4 = {
    for( index <- 1 to testNum) {
      avg = avg.incStart()
    }
    avg.start must_== testNum
  }

  val e5 = {
    for( index <- 1 to testNum) {
      avg = avg.decStart()
    }
    avg.start must_== 0
  }
}
