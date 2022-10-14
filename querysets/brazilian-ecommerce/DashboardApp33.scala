// Get product, price and review score given a peroid for a seller (product catalog)

class DashboardApp33(paramMap: Map[String, Any]) extends Serializable {
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
  val customerZipCodePrefix = paramMap("customerZipCodePrefix").asInstanceOf[String]
  val sellerZipCodePrefix = paramMap("sellerZipCodePrefix").asInstanceOf[String]
  val reviewScore = paramMap("reviewScore").asInstanceOf[Float]
  val distance = paramMap("distance").asInstanceOf[Float]
  val deliveredTime = paramMap("deliveredTime").asInstanceOf[Long]
  val reviewAnswerTime = paramMap("reviewAnswerTime").asInstanceOf[Long]
  val volume = paramMap("volume").asInstanceOf[Float]
  val orderStatus = paramMap("orderStatus").asInstanceOf[String]
  val numRows = paramMap("numRows").asInstanceOf[Int]

  def f1(data: Any): (String, Float) = {
    val row = data.asInstanceOf[OrderReviewRow]
    (row._2, row._3.toFloat)
  }

  def f2(data: Any): (String, String) = {
    val row = data.asInstanceOf[OrderRow]
    (row._1, row._2)
  }

  def f3(data: Any): (String, Float) = {
    val row = data.asInstanceOf[(String, (Float, String))]
    (row._1, row._2._1)
  }

  def f4(data: Any): Boolean = {
    val row = data.asInstanceOf[OrderItemRow]
    row._4 == sellerId
  }

  def f5(data: Any): (String, (String, Float)) = {
    val row = data.asInstanceOf[OrderItemRow]
    (row._1, (row._3, row._6.toFloat))
  }

  def f6(data: Any): ((String, Float), (Float, Int)) = {
    val row = data.asInstanceOf[(String, (Float, (String, Float)))]
    (row._2._2, (row._2._1, 1))
  }

	def f7(d1: Any, d2: Any): (Int, (Float, Int)) = {
    val row1 = d1.asInstanceOf[(Int, (Float, Int))]
    val row2 = d2.asInstanceOf[(Int, (Float, Int))]
    (row1._1, (row1._2._1 + row2._2._1, row1._2._2 + row2._2._2))
	}

  def f8(data: Any): (String, (Float, Float)) = {
    val row = data.asInstanceOf[((String, Float), (Float, Int))]
    (row._1._1, (row._1._2, row._2._1 / row._2._2))
  }

  def f9(data: Any): (String, String) = {
    val row = data.asInstanceOf[ProductRow]
    (row._1, row._2)
  }

  def f10(data: Any): (String, Float, Float, String) = {
    val row = data.asInstanceOf[(String, ((Float, Float), String))]
    (row._1, row._2._1._1, row._2._1._2, row._2._2)
  }

  def run(): Unit = {
    val rddOrders = qTextFile(s"/root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${numRows}/orders.csv")
    val rddOrderItems = qTextFile(s"/root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${numRows}/order_items.csv")
    val rddOrderReviews = qTextFile(s"/root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${numRows}/order_reviews.csv")
    val rddProducts = qTextFile(s"/root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${numRows}/products.csv")

    rddOrderReviews.qMap(x => f1(x))
      .qJoin(rddOrders.qMap(x => f2(x)))
      .qMap(x => f3(x))
      .qJoin(rddOrderItems.qFilter(x => f4(x))
        .qMap(x => f5(x)))
      .qMap(x => f6(x))
      .qReduceByKey((x, y) => f7(x, y))
      .qMap(x => f8(x))
      .qJoin(rddProducts.qMap(x => f9(x)))
      .qMap(x => f10(x))
  }

  def seqRun(rddOrders: RDD[Any],
             rddOrderItems: RDD[Any],
             rddProducts: RDD[Any],
             rddCustomers: RDD[Any],
             rddSellers: RDD[Any],
             rddGeolocation: RDD[Any],
             rddOrderPayments: RDD[Any],
             rddOrderReviews: RDD[Any]): RDD[Any] = {
    rddOrderReviews.map(x => f1(x))
      .join(rddOrders.map(x => f2(x)))
      .map(x => f3(x))
      .join(rddOrderItems.filter(x => f4(x))
        .map(x => f5(x)))
      .map(x => f6(x))
      .reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2))
      .map(x => f8(x))
      .join(rddProducts.map(x => f9(x)))
      .map(x => f10(x))
      .asInstanceOf[RDD[Any]]
  }
}

