// Get the customer and date during a particular period for a specific seller
class DashboardApp32(paramMap: Map[String, Any]) extends Serializable {
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

	def f3(data: Any): (String, String) = {
		val row = data.asInstanceOf[RowType]
		(row._4, row._3)
	}

  def run(): Unit = {
    val rdd = qTextFile(s"/root/QaaD/datasets/synthetic-ebay/num-rows-${numRows}/ebay.csv")
    rdd.qFilter(x => f1(x))
      .qFilter(x => f2(x))
      .qMap(x => f3(x))
  }

  def seqRun(rdd: RDD[Any]): RDD[Any] = {
    rdd.filter(x => f1(x))
      .filter(x => f2(x))
      .map(x => f3(x))
      .asInstanceOf[RDD[Any]]
  }
}

