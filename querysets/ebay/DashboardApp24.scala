// Get the most recent 5 product review scores for a specific seller
class DashboardApp24(paramMap: Map[String, Any]) extends Serializable {
  import QaaD._

	type RowType = (String, String, String, String, String, String, String, String, String)

	val startTime = paramMap("startTime").asInstanceOf[Float]
	val endTime = paramMap("endTime").asInstanceOf[Float]
	val sellerId = paramMap("sellerId").asInstanceOf[String]
  val reviewScore = paramMap("reviewScore").asInstanceOf[Float]

	def f1(data: Any): Boolean = {
    val row = data.asInstanceOf[RowType]
		row._9 == sellerId
	}

	def f2(data: Any): (Int, Seq[(Float, Float)]) = {
		val row = data.asInstanceOf[RowType]
		(0, Seq((row._5.toFloat, row._3.toFloat)))
	}

	def f3(data1: Any, data2: Any): (Int, Seq[(Float, Float)]) = {
		val row1 = data1.asInstanceOf[(Int, Seq[(Float, Float)])]
		val row2 = data2.asInstanceOf[(Int, Seq[(Float, Float)])]
		(row1._1, row1._2 ++ row2._2)
	}

  def f4(data: Any): Seq[Float] = {
    val row = data.asInstanceOf[(Int, Seq[(Float, Float)])]
    row._2.sortBy(-_._2).slice(0, scala.math.min(5, row._2.size)).map(_._1)
  }

  def run(): Unit = {
    val rdd = qTextFile(s"/root/QaaD/datasets/synthetic-ebay/num-rows-${numRows}/ebay.csv")
    rdd.qFilter(x => f1(x))
      .qMap(x => f2(x))
      .qReduceByKey((x, y) => f3(x, y))
      .qMap(x => f4(x))
  }

  def seqRun(rdd: RDD[Any]): RDD[Any] = {
    rdd.filter(x => f1(x))
      .map(x => f2(x))
      .reduceByKey((x, y) => x ++ y)
      .map(x => f4(x))
      .asInstanceOf[RDD[Any]]
  }
}

