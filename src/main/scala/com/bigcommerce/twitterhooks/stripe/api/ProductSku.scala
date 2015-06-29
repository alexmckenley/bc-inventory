package com.bigcommerce.twitterhooks.stripe.api

import SkuInventoryStocks.SkuInventoryStock
import SkuInventoryTypes.SkuInventoryType
import zangelo.spray.json.annotation.{JsonProperty, JsonPropertyCases, JsonPropertyCase}

/**
 * Created by zack.angelo on 6/28/15.
 */
case class ProductSku(id:Option[String],
                     created:Option[Long],
                     updated:Option[Long],
                     livemode:Option[Boolean],
                     product:String,
                     name:Option[String],
                     description:Option[String],
                     image:Option[String],
                     active:Option[Boolean],
                     price:Option[Int],
                     currency:Option[String],
                     inventory:Option[ProductSkuInventory])
  extends Entity

object SkuInventoryTypes extends Enumeration {
  type SkuInventoryType = Value

  val Finite    = Value("finite")
  val Bucket    = Value("bucket")
  val Infinite  = Value("infinite")
  val Untracked = Value("untracked")
}

object SkuInventoryStocks extends Enumeration {
  type SkuInventoryStock = Value

  val InStock     = Value("in_stock")
  val Limited     = Value("limited")
  val Scant       = Value("scant")
  val OutOfStock  = Value("out_of_stock")
}

@JsonPropertyCase(JsonPropertyCases.Snakize)
case class ProductSkuInventory(@JsonProperty("type")
                               inventoryType:Option[SkuInventoryType],
                               quantity:Int,
                               value:Option[SkuInventoryStock])
  extends Entity