package movingavg

class CompAvg {
/** After benchmarking against SortedMap, and since data arrives
  * implicitly in-order (no timestamp, i.e. current time only),
  * I decided to use a Vector to store the state. It holds tuples
  * (currTime, Sum, Num) of sums of values that were inserted
  * at the same millisecond. Due to the millisecond granularity,
  * it holds at most 60,000 entries for the whole minute.
  * 10^6 (.tail :+ record) operations maintaining only the last 100
  * entries on a 1.8 GHz Core Duo laptop take:
  * Vector: ~1900ms
  * SortedMap: ~2250ms
  * If data arrived out-of-order with a timestamp, a SortedMap
  * would be a good alternative to quickly look up existing values.
  */

  // Type timedAvg holds time stamp, sum of doubles, and
  // number of doubles to evaluate average as sum/number
  type timedAvg = (Long, Double, Int)

  // _dataState holds structure for evicting obsolete data
  protected var _dataState: Vector[timedAvg] = Vector.empty

  // AvgState objects for the three averages required.
  protected var _avg = Array(AvgState(10*1000), AvgState(30*1000), AvgState(60*1000))
  protected val _avgNum = _avg.length

  // adds value from new record to all averages
  protected def addToAvg(record: timedAvg) {
    for(index <- 0 until _avgNum) {
      _avg(index) = _avg(index).addVal(record._2)
    }
  }

  // subtracts values from expired records from all averages,
  // according to new time stamp in record
  protected def subFromAvg(record: timedAvg) {
    val size = _dataState.size
    for(index <- 0 until _avgNum) {
      var start = _avg(index).start
      // time stamp may be newer by one minute, so make sure
      // that start index is smaller than vector size
      while(start < size && record._1 - _avg(index).interval >= _dataState(start)._1 ) {
        _avg(index) = _avg(index)
          .subVal(_dataState(start)._2, _dataState(start)._3)
          .incStart()
        start += 1
      }
    }
  }

  // decrement all start indices when dropping from front of vector
  protected def decAvgStart(by: Int) {
    for(index <- 0 until _avgNum) {
      _avg(index) = _avg(index).decStart(by)
    }
  }

  // Insert Tuple (currTime, currVal, currNum) into _dataState.
  def insertRecord(record: timedAvg) = {
    _dataState = insertRecordToState(_dataState, record)
  }

  // Insert Tuple (currTime, currVal, currNum) into passed state.
  protected def insertRecordToState(state: Vector[timedAvg], record: timedAvg): Vector[timedAvg] = {
    if(state.isEmpty) {
      addToAvg(record)
      Vector(record)
    } else {
      val last = state.last
      if(last._1 == record._1) {
        // We have seen this time before, add to existing record,
        // no need to evict any data.
        addToAvg(record)
        // record._3 == 1 always
        val newRecord = (last._1, last._2 + record._2, last._3 + 1)
        // Remove last and replace with newRecord
        return state.init :+ newRecord
      } else {
        // We have a new time, evict old data, then add record
        subFromAvg(record)
        addToAvg(record)
        // query longest average about how many records we can drop
        val dropBy = _avg.last.start
        decAvgStart(dropBy)
        return state.drop(dropBy) :+ record
      }
    }
  }

  def size = { _dataState.size }

  def dataState = { _dataState }

  def avg = { _avg }

  def avgNum = { _avgNum }

  def averages = {
    new MovingAverages(_avg(0).avg, _avg(1).avg, _avg(2).avg)
  }

  // Consume new value and update moving averages
  def consume(value: Double): MovingAverages = {
    val currTime: Long = System.currentTimeMillis
    insertRecord((currTime, value, 1))
    averages
  }
}
