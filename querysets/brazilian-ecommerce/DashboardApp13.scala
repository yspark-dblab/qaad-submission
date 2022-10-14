// Get average sales per month in each year during the selected range for the seller
class DashboardApp13(paramMap: Map[String, Any]) extends Serializable {
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

	def f1(data: Any): (String, Long) = {
    val row = data.asInstanceOf[OrderRow]
    (row._1, row._4.toLong)
	}

	def f2(data: Any): Boolean = {
    val row = data.asInstanceOf[(String, Long)]
    startTime <= row._2.toLong && row._2.toLong < endTime
	}

	def f3(data: Any): Boolean = { // filter
    val row = data.asInstanceOf[OrderItemRow]
		row._4 == sellerId
	}

	def f4(data: Any): (String, Float) = { // map
    val row = data.asInstanceOf[OrderItemRow]
		(row._1, row._6.toFloat)
	}

	def f5(data: Any): (Long, Float) = {
    val row = data.asInstanceOf[(String, (Long, Float))]
    (row._2._1 / (60 * 60 * 24 * 365), row._2._2)
	}

	def f6(d1: Any, d2: Any): (Int, Float) = {
    val row1 = d1.asInstanceOf[(Int, Float)]
    val row2 = d2.asInstanceOf[(Int, Float)]
    (row1._1, row1._2 + row2._2)
	}

  def f7(data: Any): (Long, Float) = {
    val row = data.asInstanceOf[(Long, Float)]
    (row._1, row._2 / (60 * 60 * 24))
  }

  def run(): Unit = {
    val rddOrders = qTextFile(s"/root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${numRows}/orders.csv")
    val rddOrderItems = qTextFile(s"/root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${numRows}/order_items.csv")
		rddOrders.qMap(x => f1(x))
			.qFilter(x => f2(x))
			.qJoin(rddOrderItems.qFilter(x => f3(x))
				.qMap(x => f4(x)))
			.qMap(x => f5(x))
			.qReduceByKey((x, y) => f6(x, y))
      .qMap(x => f7(x))
  }

  def seqRun(rddOrders: RDD[Any],
             rddOrderItems: RDD[Any],
             rddProducts: RDD[Any],
             rddCustomers: RDD[Any],
             rddSellers: RDD[Any],
             rddGeolocation: RDD[Any],
             rddOrderPayments: RDD[Any],
             rddOrderReviews: RDD[Any]): RDD[Any] = {
		rddOrders.map(x => f1(x))
			.filter(x => f2(x))
			.join(rddOrderItems.filter(x => f3(x))
				.map(x => f4(x)))
			.map(x => f5(x))
			.reduceByKey((x: Float, y: Float) => x + y)
      .map(x => f7(x))
      .asInstanceOf[RDD[Any]]
  }
}

