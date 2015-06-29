package com.bigcommerce.twitterhooks.stripe.api

import zangelo.spray.json.annotation._

/**
 * A Stripe catalog product
 */
@JsonPropertyCase(JsonPropertyCases.Snakize)
case class Product(id:Option[String] = None,
                  created:Option[Long] = None,    //TODO datetime
                  updated:Option[Long] = None,    //TODO datetime
                  livemode:Option[Boolean] = None, //TODO enum,
                  name:Option[String] = None,
                  caption:Option[String] = None,
                  description:Option[String] = None,
                  active:Option[Boolean] = None,
                  attributes:Option[List[String]] = None,
                  images:Option[List[String]] = None,
                  packageDimensions:Option[ProductDimensions] = None,
                  shippable:Option[Boolean] = None,
//                  skus:Option[ListResponse[ProductSku]] = None,
                  url:Option[String] = None)
  extends Entity

case class ProductDimensions(height:Double,
                             length:Double,
                             weight:Double,
                             width:Double)
  extends Entity

