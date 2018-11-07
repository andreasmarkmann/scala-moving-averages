package movingavg

/**
  * AvgState class stores current state of a single average,
  * including the start position for this average in data state
  * var start: Int = 0 // start index in data
  * var sum: Double = 0.0 // sum of values
  * var num: Int = 0 // number of value
  * Case class used below to create toString and equals, same performance
  * as ordinary class with companion object.
  * There is probably a clever scala-like way of getting case class
  * in testing and ordinary class with companion object in production. :)
  */

case class AvgState(val interval: Int = 60*1000, val start: Int = 0, val sum: Double = 0.0, val num: Int = 0) {

  // Add to sum of values and increment number
  def addVal(value: Double): AvgState = {
    new AvgState(interval, start, sum + value, num + 1)
  }

  // Remove value and number from average
  def subVal(value: Double, numVal: Int): AvgState = {
    new AvgState(interval, start, sum - value, num - numVal)
  }

  def incStart(): AvgState = {
    new AvgState(interval, start + 1, sum, num)
  }

  def incStart(by: Int): AvgState = {
    new AvgState(interval, start + by, sum, num)
  }

  def decStart(): AvgState = {
    new AvgState(interval, start - 1, sum, num)
  }

  def decStart(by: Int): AvgState = {
    new AvgState(interval, start - by, sum, num)
  }

  // Called only after inserting values, which increments num
  def avg(): Double = {
    return sum/num
  }
}
