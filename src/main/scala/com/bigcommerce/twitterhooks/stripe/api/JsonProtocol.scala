package com.bigcommerce.twitterhooks.stripe.api

import spray.json._
import zangelo.spray.json.{AutoEnumFormats, AutoProductFormats}

/**
 * Created by zack.angelo on 6/28/15.
 */
trait JsonProtocol
  extends DefaultJsonProtocol
  with AutoEnumFormats {

  implicit def listResponseFormat[T <: Entity : JsonFormat] = new JsonFormat[ListResponse[T]] {
    override def read(jsval: JsValue): ListResponse[T] = {
      val json = jsval.asJsObject()

      ListResponse(
        hasMore = json.fields("has_more").convertTo[Boolean],
        totalCount = json.fields.get("total_count").map(_.convertTo[Int]),
        data = json.fields("data").convertTo[List[T]]
      )
    }

    override def write(obj: ListResponse[T]): JsValue = ???
  }

  implicit val AddressFormat = EntityFormats.autoProductFormat[Address]
  implicit val ShippingInfoFormat = EntityFormats.autoProductFormat[ShippingInfo]
  implicit val ShippingMethodFormat = EntityFormats.autoProductFormat[ShippingMethod]
  implicit val OrderItemFormat = EntityFormats.autoProductFormat[OrderItem]
  implicit val OrderFormat = EntityFormats.autoProductFormat[Order]
  implicit val ProductSkuInventoryFormat = EntityFormats.autoProductFormat[ProductSkuInventory]
  implicit val ProductSkuFormat = EntityFormats.autoProductFormat[ProductSku]
  implicit val ProductDimFormat = EntityFormats.autoProductFormat[ProductDimensions]
  implicit val ProductFormat = EntityFormats.autoProductFormat[Product]
}

object JsonProtocol extends JsonProtocol

object EntityFormats extends AutoProductFormats[Entity]
