package com.bigcommerce.twitterhooks.stripe.api

/**
 * A Stripe list response
 *
 * (e.g., fetch me product SKUs with every product)
 */
case class ListResponse[T <: Entity](hasMore:Boolean,
                                     totalCount:Option[Integer],
                                     data:List[T])
