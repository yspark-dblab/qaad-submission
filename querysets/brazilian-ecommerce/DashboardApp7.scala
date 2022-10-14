// Get top 5 best selling categories during the selected range for the seller
class DashboardApp7(paramMap: Map[String, Any]) extends Serializable {
  import QaaD._

	type OrderRow = (String, String, String, String, String, String, String, String) // order_purchase_timestamp, order_approved_at, order_delivered_carrier_date, order_delivered_customer_date, order_estimated_delivery_date, order_id, customer_id, order_status
	type OrderItemRow = (String, String, String, String, String, String, String) // shipping_limit_date, orer_id, order_item_id, product_id, seller_id, price, freight_value
	type ProductRow = (String, String, String, String, String, String, String, String, String) // product_id, product_category_name, product_name_length, product_description_length, product_photos_qty, product_weight_g, product_length_cm, product_height_cm, product_width_cm
  type CustomerRow = (String, String, String, String, String) // customer_id, customer_unique_id, geolocation_zip_code_prefix, customer_city, customer_state
  type SellerRow = (String, String, String, String) // seller_id, geolocation_zip_code_prefix, seller_city, seller_state
  type GeolocationRow = (String, String, String) // geolocation_zip_code_prefix, geolocation_lat, geolocation_lng
  type OrderPaymentRow = (String, String, String, String, String) // order_id, payment_sequential, payment_type, payment_installments, payment_value
  type OrderReviewRow = (String, String, String, String, String) // review_id, order_id, review_score, review_creation_date, review_answer_timestamp

	val startTime = paramMap("startTime").asInstanceOf[Long]
	val endTime = paramMap("endTime").asInstanceOf[Long]
	val sellerId = paramMap("sellerId").asInstanceOf[String]


	def f1(data: Any): Boolean = { // filter
    val row = data.asInstanceOf[OrderItemRow]
		row._4 == sellerId
	}

	def f2(data: Any): (String, String) = { // map
    val row = data.asInstanceOf[OrderItemRow]
		(row._3, row._1)
	}

	def f3(data: Any): (String, String) = { // map
    val row = data.asInstanceOf[ProductRow]
		(row._1, row._2)
	}

	def f4(data: Any): (String, Int) = {
    val row = data.asInstanceOf[(String, (String, String))]
    (row._2._2, 1)
	}

	def f5(d1: Any, d2: Any): (Int, Int) = {
    val row1 = d1.asInstanceOf[(Int, Int)]
    val row2 = d2.asInstanceOf[(Int, Int)]
    (row1._1, row1._2 + row2._2)
	}

  def f6(data: Any): (Int, Seq[(String, Int)]) = {
    (0, Seq(data.asInstanceOf[(String, Int)]))
  }

  def f7(d1: Any, d2: Any): (Int, Seq[(String, Int)]) = {
    val row1 = d1.asInstanceOf[(Int, Seq[(String, Int)])]
    val row2 = d2.asInstanceOf[(Int, Seq[(String, Int)])]
    (row1._1, row1._2 ++ row2._2)
  }

  def f8(data: Any): Array[Any] = {
    val row = data.asInstanceOf[(Int, Seq[(String, Int)])]
    row._2.sortWith(_._2 > _._2).map(_._1).slice(0, scala.math.min(row._2.size, 5)).toArray.asInstanceOf[Array[Any]]
  }

  def run(): Unit = {
    val rddOrderItems = qTextFile(s"/root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${numRows}/order_items.csv")
    val rddProducts = qTextFile(s"/root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${numRows}/products.csv")
		rddOrderItems.qFilter(x => f1(x))
			.qMap(x => f2(x))
			.qJoin(rddProducts.qMap(x => f3(x)))
			.qMap(x => f4(x))
			.qReduceByKey((x, y) => f5(x, y))
		  .qMap(x => f6(x))
			.qReduceByKey((x, y) => f7(x, y))
      .qFlatMap(x => f8(x))
  }

  def seqRun(rddOrders: RDD[Any],
             rddOrderItems: RDD[Any],
             rddProducts: RDD[Any],
             rddCustomers: RDD[Any],
             rddSellers: RDD[Any],
             rddGeolocation: RDD[Any],
             rddOrderPayments: RDD[Any],
             rddOrderReviews: RDD[Any]): RDD[Any] = {
		rddOrderItems.filter(x => f1(x))
			.map(x => f2(x))
			.join(rddProducts.map(x => f3(x)))
			.map(x => f4(x))
			.reduceByKey((x, y) => x + y)
			.map(x => f6(x))
			.reduceByKey((x, y) => x ++ y)
      .flatMap(x => f8(x))
      .asInstanceOf[RDD[Any]]
  }
}

