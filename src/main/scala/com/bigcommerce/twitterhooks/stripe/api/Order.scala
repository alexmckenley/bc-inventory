package com.bigcommerce.twitterhooks.stripe.api

import OrderItemTypes.OrderItemType
import OrderStatuses.OrderStatus
import zangelo.spray.json.annotation._

@JsonPropertyCase(JsonPropertyCases.Snakize)
case class Order(id:Option[String],
                 created:Option[Long],
                 modified:Option[Long],
                 livemode: Option[Boolean],
                 status:Option[OrderStatus],
//                metadata:Map[String,String], //TODO
                 shipping:Option[ShippingInfo],
                 items:List[OrderItem],
                 shippingMethods:Option[List[ShippingMethod]])
  extends Entity

case class OrderItem(parent:Option[String],
                     @JsonProperty("type") itemType:OrderItemType,
                     description:String,
                     amount:Int,
                     currency:String = "usd",
                     quantity:Option[Int])
  extends Entity

@JsonPropertyCase(JsonPropertyCases.Snakize)
case class ShippingInfo(name:String,
                        address:Address,
                        phone:Option[String],
                        trackingNumber:Option[String],
                        carrier:Option[String])
  extends Entity

case class ShippingMethod(id:String,
                          amount:Int,
                          currency:String,
                          description:String)
  extends Entity


object OrderItemTypes extends Enumeration {
  type OrderItemType = Value

  val Sku = Value("sku")
  val Tax = Value("tax")
  val Shipping = Value("shipping")
  val Discount = Value("discount")
}

object OrderStatuses extends Enumeration {
  type OrderStatus = Value

  val Created = Value("created")
  val Paid = Value("paid")
  val Canceled = Value("canceled")
  val Fulfilled = Value("fulfilled")
  val Returned = Value("returned")
}