// Get the sales per day during the selected range for the seller
class DashboardApp3(paramMap: Map[String, Any]) extends Serializable {
  import QaaD._

	type RowType = (String, String, String, String, String, String, String, String, String)

	val startTime = paramMap("startTime").asInstanceOf[Float]
	val endTime = paramMap("endTime").asInstanceOf[Float]
	val sellerId = paramMap("sellerId").asInstanceOf[String]
  val reviewScore = paramMap("reviewScore").asInstanceOf[Float]

	def f1(data: Any): Boolean = {
    val row = data.asInstanceOf[RowType]
		startTime <= row._3.toFloat && row._3.toFloat < endTime
	}

	def f2(data: Any): Boolean = {
    val row = data.asInstanceOf[RowType]
		row._9 == sellerId
	}

	def f3(data: Any): (Long, Float) = {
    val row = data.asInstanceOf[RowType]
		((row._3.toFloat * 24 * 60 * 60 / (60 * 60 * 24)).toLong, row._7.toFloat)
	}

	def f4(d1: Any, d2: Any): (Int, Float) = {
    val row1 = d1.asInstanceOf[(Int, Float)]
    val row2 = d2.asInstanceOf[(Int, Float)]
    (row1._1, row1._2 + row2._2)
	}

  def run(): Unit = {
    val rdd = qTextFile(s"/root/QaaD/datasets/synthetic-ebay/num-rows-${numRows}/ebay.csv")
    rdd.qFilter(x => f1(x))
      .qFilter(x => f2(x))
      .qMap(x => f3(x))
      .qReduceByKey((x, y) => f4(x, y))
  }

  def seqRun(rdd: RDD[Any]): RDD[Any] = {
    rdd.filter(x => f1(x))
      .filter(x => f2(x))
      .map(x => f3(x))
      .reduceByKey((x, y) => x + y)
      .asInstanceOf[RDD[Any]]
  }
}

