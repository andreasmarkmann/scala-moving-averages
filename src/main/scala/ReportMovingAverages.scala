package movingavg

class ReportMovingAverages(compAvg: CompAvg, private val maximumRunTime: Long = 180*1000L) {
  private val rnd = new scala.util.Random(System.currentTimeMillis)
  def run() {
    val StopTime = System.currentTimeMillis + maximumRunTime
    var lastLog = System.currentTimeMillis
    while (System.currentTimeMillis <= StopTime) {
      val result = compAvg.consume(rnd.nextDouble)
      val remaining = StopTime - System.currentTimeMillis
      val elapsed = System.currentTimeMillis - lastLog
	  if (elapsed > 500) {
        println(s"time remaining: ${remaining} ms; 10s avg: ${result.tenSecondAvg}, 30s avg: ${result.thirtySecondAvg}, 1m avg: ${result.oneMinuteAvg}")
	    // Reset timer to display message again in 500 ms
        lastLog = System.currentTimeMillis
      }
    }
  }
}
