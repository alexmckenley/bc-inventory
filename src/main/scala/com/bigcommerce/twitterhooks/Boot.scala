package com.bigcommerce.twitterhooks

import java.util.UUID

import com.bigcommerce.api._
import akka.actor._
import com.bigcommerce.api.json.BigcommerceJsonProtocol
import com.bigcommerce.twitterhooks.stripe.api.OrderItemTypes
import com.typesafe.config.ConfigFactory
import spray.http._
import spray.httpx.SprayJsonSupport
import spray.routing._
import spray.json._
import spray.httpx.marshalling._
import scala.concurrent._

/**
 * Created by zack.angelo on 6/28/15.
 */
object Boot
  extends App
  with SimpleRoutingApp
  with SprayJsonSupport {

  implicit val sys = ActorSystem("twitterhooks")

  def log = sys.log

  import stripe.api.JsonProtocol._
  import sys.dispatcher

  val bigcommerce = new BigcommerceClient(
    baseUrl = Uri(s"https://${HooksConfig.BigcommerceApi.host}/api/v2"),
    creds = BigcommerceClient.BasicCredentials(HooksConfig.BigcommerceApi.user, HooksConfig.BigcommerceApi.key))

  def createBcOrderFromStripeOrder(so:stripe.api.Order):Future[Order] = {
      val bcOrder = Order(
        products = Some(List(
          OrderProduct(
            productId = Some(77),
            quantity  = Some(1)
          )
        )),
        billingAddress = com.bigcommerce.api.Address(
          firstName = "Zack",
          lastName = "Angelo",
          street1 = "2900 Hudson Place",
          city = "New Orleans",
          state = "LA",
          zip = "70131",
          country = "United States",
          phone = "713-555-1212"
        ),
        externalSource = Some("Twitter")
      )

    bigcommerce.orders.save(bcOrder)
  }

  startServer(interface = HooksConfig.ApiServer.interface, port = HooksConfig.ApiServer.port) {
    post {
      path("reserve") { ctx =>
        log.info(s"[/reserve] <- ${ctx.request}")

        //manually marshal into an object because
        // twitter doesn't send correct Content-Type header
        val reqJson = ctx.request.entity.asString.parseJson
        val stripeOrder = reqJson.convertTo[stripe.api.Order]
        val bcOrder = createBcOrderFromStripeOrder(stripeOrder)

        ctx.complete {
          bcOrder.map { order =>
//            o.toJson(BigcommerceJsonProtocol.OrderJsonFormat).asJsObject
            val taxItem = stripe.api.OrderItem(
              parent = None,
              itemType = OrderItemTypes.Tax,
              amount = ((order.totalIncTax.get - order.totalExTax.get) * 100).toInt,
              description = "Taxes",
              quantity = None
            )

            val shippingItem = stripe.api.OrderItem(
              parent = None,
              itemType = OrderItemTypes.Shipping,
              amount = (order.shippingCostExTax.get * 100).toInt,
              description = "Shipping",
              quantity = None
            )

            List(shippingItem,taxItem)
          }
        }
      } ~
      path("pay") { ctx =>
        log.info(s"[/pay] <- ${ctx.request}")
        ctx.complete(JsObject("id" -> JsString(UUID.randomUUID().toString)))
      }
    }
  }
}

object HooksConfig {
  private val cfg = ConfigFactory.load().getConfig("twitter-hooks")

  object ApiServer {
    private val c = cfg.getConfig("server")
    val interface = c.getString("interface")
    val port = c.getInt("port")
  }

  object BigcommerceApi {
    private val c = cfg.getConfig("bc-target-api")
    val host = c.getString("host")
    val user = c.getString("user")
    val key = c.getString("key")
  }
}
