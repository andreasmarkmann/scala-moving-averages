package movingavg

private object HeapUsed {
  def apply() = {
    val rt = Runtime.getRuntime
    (rt.totalMemory - rt.freeMemory).toDouble / rt.totalMemory
  }
}

class ComputeMovingAverages(compAvg: CompAvg, private val maximumRunTime: Long = 180*1000L) {
  private val rnd = new scala.util.Random(System.currentTimeMillis)
  def run() {
    val StopTime = System.currentTimeMillis + maximumRunTime
    var lastLog = System.currentTimeMillis
    while (System.currentTimeMillis <= StopTime) {
      val result = compAvg.consume(rnd.nextDouble)
      val remaining = StopTime - System.currentTimeMillis
      val elapsed = System.currentTimeMillis - lastLog
      if (elapsed > 1000) {
        sys.error(s"Pause detected! Either memory low, or too slow processing.")
      } else if (elapsed > 500) {
        println(s"time remaining: ${remaining} ms; elapsed ${elapsed} ms; heap usage: ${HeapUsed()}")
        if (HeapUsed() > 0.95) sys.error("Too much heap usage.")
	    // Reset timer to display message again in 500 ms
        lastLog = System.currentTimeMillis
      }
    }
  }
}
