package com.bigcommerce.twitterhooks.stripe.api

import zangelo.spray.json.annotation._

/**
 * Created by zack.angelo on 6/28/15.
 */
@JsonPropertyCase(JsonPropertyCases.Snakize)
case class Address(line1:String,
                   line2:Option[String],
                   city:String,
                   state:Option[String],
                   postalCode:String,
                   country:String) extends Entity
