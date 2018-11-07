package movingavg

object MovingAvg {
	def main(args: Array[String]) = {
		new ReportMovingAverages(new CompAvg, 90*1000L).run()
	}
}
