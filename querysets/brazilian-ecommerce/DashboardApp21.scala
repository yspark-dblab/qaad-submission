// Get sales per category in a particular period

class DashboardApp21(paramMap: Map[String, Any]) extends Serializable {
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
  val weight = paramMap("weight").asInstanceOf[Float]
  val customerCity = paramMap("customerCity").asInstanceOf[String]
  val sellerCity = paramMap("sellerCity").asInstanceOf[String]
  val customerState = paramMap("customerState").asInstanceOf[String]
  val sellerState = paramMap("sellerState").asInstanceOf[String]
  val reviewScore = paramMap("reviewScore").asInstanceOf[Float]
  val distance = paramMap("distance").asInstanceOf[Float]
  val deliveredTime = paramMap("deliveredTime").asInstanceOf[Long]
  val numRows = paramMap("numRows").asInstanceOf[Int]

	def f0(data: Any): Boolean = {
    val row = data.asInstanceOf[OrderRow]
    startTime <= row._4.toLong && row._4.toLong < endTime
	}

	def f1(data: Any): (String, String) = {
    val row = data.asInstanceOf[OrderRow]
    (row._1, row._4)
	}

  def f2(data: Any): (String, (String, Float)) = {
    val row = data.asInstanceOf[OrderItemRow]
    (row._1, (row._3, row._6.toFloat))
  }

  def f3(data: Any): (String, Float) = {
    val row = data.asInstanceOf[(String, (String, (String, Float)))]
    (row._2._2._1, row._2._2._2)
  }

  def f4(data: Any): (String, String) = {
    val row = data.asInstanceOf[ProductRow]
    (row._1, row._2)
  }

  def f5(data: Any): (String, Float) = {
    val row = data.asInstanceOf[(String, (Float, String))]
    (row._2._2, row._2._1)
  }

  def f6(data1: Any, data2: Any): (Int, Float) = {
    val row1 = data1.asInstanceOf[(Int, Float)]
    val row2 = data2.asInstanceOf[(Int, Float)]
    (row1._1, (row1._2 + row2._2))
  }

  def run(): Unit = {
    val rddOrders = qTextFile(s"/root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${numRows}/orders.csv")
    val rddOrderItems = qTextFile(s"/root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${numRows}/order_items.csv")
    val rddProducts = qTextFile(s"/root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${numRows}/products.csv")

    rddOrders.qFilter(x => f0(x))
      .qMap(x => f1(x))
      .qJoin(rddOrderItems.qMap(x => f2(x)))
      .qMap(x => f3(x))
      .qJoin(rddProducts.qMap(x => f4(x)))
      .qMap(x => f5(x))
      .qReduceByKey((x, y) => f6(x, y))
  }

  def seqRun(rddOrders: RDD[Any],
             rddOrderItems: RDD[Any],
             rddProducts: RDD[Any],
             rddCustomers: RDD[Any],
             rddSellers: RDD[Any],
             rddGeolocation: RDD[Any],
             rddOrderPayments: RDD[Any],
             rddOrderReviews: RDD[Any]): RDD[Any] = {

    rddOrders.filter(x => f0(x))
      .map(x => f1(x))
      .join(rddOrderItems.map(x => f2(x)))
      .map(x => f3(x))
      .join(rddProducts.map(x => f4(x)))
      .map(x => f5(x))
      .reduceByKey((x, y) => x + y)
      .asInstanceOf[RDD[Any]]
  }
}

