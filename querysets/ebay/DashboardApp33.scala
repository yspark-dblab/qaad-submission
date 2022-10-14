// Get the product, price and review score given a period for a seller
class DashboardApp33(paramMap: Map[String, Any]) extends Serializable {
  import QaaD._

	type RowType = (String, String, String, String, String, String, String, String, String)

	val startTime = paramMap("startTime").asInstanceOf[Float]
	val endTime = paramMap("endTime").asInstanceOf[Float]
	val sellerId = paramMap("sellerId").asInstanceOf[String]
  val reviewScore = paramMap("reviewScore").asInstanceOf[Float]

	def f1(data: Any): Boolean = {
    val row = data.asInstanceOf[RowType]
		startTime <= row._3.toFloat && row._3.toFloat < endTime && row._9 == sellerId
	}

	def f2(data: Any): (String, String, String) = {
		val row = data.asInstanceOf[RowType]
		(row._9, row._7, row._5)
	}

  def run(): Unit = {
    val rdd = qTextFile(s"/root/QaaD/datasets/synthetic-ebay/num-rows-${numRows}/ebay.csv")
    rdd.qFilter(x => f1(x))
      .qMap(x => f2(x))
  }

  def seqRun(rdd: RDD[Any]): RDD[Any] = {
    rdd.filter(x => f1(x))
      .map(x => f2(x))
      .asInstanceOf[RDD[Any]]
  }
}

