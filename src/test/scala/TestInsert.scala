package movingavg

import org.specs2._

class TestInsert extends Specification {
  def is = s2"""

  State should satisfy

    avg state array       $e1
    increasing intervals  $e2
    basic insertion       $e3
    insertion & eviction  $e4
    tenSecond average     $e5
    thirtySecond average  $e6
    oneMinute average     $e7
    evict all             $e8
  """

  val mainObj = new CompAvg

  val e1 = {
    val avg = mainObj.avg
    var avgArray = true
    for(index <- 0 until mainObj.avgNum) {
      avg(index) match {
        case AvgState(_, _, _, _) =>
        case _ => avgArray = false
      }
    }
    avgArray must_== true
  }

  val e2  = {
    val avg = mainObj.avg
    var increasingIntervals = true
    for(index <- 0 until mainObj.avgNum - 1) {
      if( !(avg(index).interval < avg(index + 1).interval) ) {
        increasingIntervals = false
      }
    }
    increasingIntervals must_== true
  }

  // Testing with descending series
  def sumTo(max: Int): Long = {
    (max.toLong*(max.toLong + 1))/2
  }

  // Average of descending series
  def avgExpect(max: Int): Double = {
    sumTo(max).toDouble/max
  }

  val testNum = 1000000
  val multiple = 4

  val e3 = {
    val myTestNum = 20
    val myObj = new CompAvg
    for( index <- 0 until myTestNum) {
      // Multiple values per time index, inserts downto 1
      val record = (index.toLong/multiple, (myTestNum - index).toDouble, 1)
      myObj.insertRecord(record)
    }

    myObj.avg.last.sum must_== sumTo(myTestNum)
  }

  val e4 = {
    val startTime = System.currentTimeMillis
    for( index <- 0 until testNum) {
      // Multiple values per time index, inserts downto 1
      val record = (index.toLong/multiple, (testNum - index).toDouble, 1)
      mainObj.insertRecord(record)
    }
    val endTime = System.currentTimeMillis
    val diffTime = endTime - startTime
    println(s"Updated state $testNum times in $diffTime ms.")

    println(mainObj.avg(0).toString)
    println(mainObj.avg(1).toString)
    println(mainObj.avg.last.toString)
    println(s"data head: ${mainObj.dataState.head}")

    mainObj.size must_== mainObj.avg.last.interval
  }

  val e5 = {
    val avg = mainObj.averages
    avg.tenSecondAvg must_== avgExpect(multiple*10000)
  }

  val e6 = {
    val avg = mainObj.averages
    avg.thirtySecondAvg must_== avgExpect(multiple*30000)
  }

  val e7 = {
    val avg = mainObj.averages
    avg.oneMinuteAvg must_== avgExpect(multiple*60000)
  }

  val e8 = {
    val record = (testNum.toLong/multiple + mainObj.avg.last.interval, 1.0.toDouble, 1)
    mainObj.insertRecord(record)
    mainObj.avg.last.avg must_== 1.0
  }
}
