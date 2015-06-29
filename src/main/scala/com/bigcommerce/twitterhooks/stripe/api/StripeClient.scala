package com.bigcommerce.twitterhooks.stripe.api

import java.nio.charset.Charset

import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import spray.client.UnsuccessfulResponseException
import spray.http
import spray.http.HttpHeaders.{Authorization, Accept}

import scala.concurrent._
import scala.concurrent.duration._

import spray.json._
import spray.http._
import spray.httpx.marshalling._

import akka.actor._
import akka.pattern.ask

/**
 * Created by zack.angelo on 6/28/15.
 */
class StripeClient(val apiUrl:Uri, apiKey:String)
                  (implicit sys:ActorSystem, ec:ExecutionContext) {
  private val baseUri = apiUrl.copy(path = http.Uri.Path("/v1"))

  import JsonProtocol._

  implicit val requestTimeout:Timeout = 10 seconds

  import spray.httpx.marshalling.BasicMarshallers.FormDataMarshaller

  private val headers = List(
    Accept(MediaTypes.`application/json`),
    Authorization(BasicHttpCredentials(apiKey,""))
  )

  private def getObject[T : JsonFormat](uri:Uri):Future[Option[T]] = {
    val req = HttpRequest(HttpMethods.GET, uri, headers)
    println(s"-> $req")
    (IO(Http) ? req).mapTo[HttpResponse] map { resp =>
      println(s"<- $resp")

      resp match {
        case HttpResponse(StatusCodes.OK, entity, _, _) =>
          val entityJson = entity.asString.parseJson
          Some(entityJson.convertTo[T])
        case HttpResponse(StatusCodes.NotFound, _, _, _) =>
          None
        case HttpResponse(code, _,_,_) if code.isFailure =>
          throw new UnsuccessfulResponseException(code) //TODO better error exception
      }
    }
  }

  private def getList[T <: Entity : JsonFormat](uri:Uri):Future[List[T]] = {
    val req = HttpRequest(HttpMethods.GET, uri, headers)
    println(s"-> $req")
    (IO(Http) ? req).mapTo[HttpResponse] map { resp =>
      println(s"<- $resp")

      resp match {
        case HttpResponse(StatusCodes.OK, entity, _, _) =>
          val entityJson = entity.asString.parseJson
          val listResp = entityJson.convertTo[ListResponse[T]]
          listResp.data
        case HttpResponse(code, _,_,_) if code.isFailure =>
          throw new UnsuccessfulResponseException(code) //TODO better error exception
      }
    }
  }

  private def post[T : JsonFormat](uri:Uri, o:T):Future[T] = {
    val formData = util.JsonToFormDataConverter(o.toJson.asJsObject)
    val formDataWithoutId = FormData(formData.fields.filterNot(_._1 == "id"))
    val encoded = http.Uri.Query(formDataWithoutId.fields: _*).render(new StringRendering, Charset.defaultCharset()).get
    val entity = HttpEntity(ContentType(MediaTypes.`application/x-www-form-urlencoded`), encoded)
    val req = HttpRequest(HttpMethods.POST, uri,headers, entity)

    println(s"-> $req")

    (IO(Http) ? req).mapTo[HttpResponse] map { resp =>
      println(s"<- $resp")

      resp match {
        case HttpResponse(StatusCodes.OK, entity, _, _) =>
          val entityJson = entity.asString.parseJson
          entityJson.convertTo[T]
        case HttpResponse(code, _, _, _) if code.isFailure =>
          throw new UnsuccessfulResponseException(code) //TODO better error exception
      }
    }
  }

  object products {
    private val uri = baseUri + "/products"
    def fetch(id:String):Future[Option[Product]] = getObject[Product](uri + s"/$id")
    def list():Future[List[Product]] = getList[Product](uri)
    def save(obj:Product):Future[Product] = post(uri, obj)
    def update(obj:Product):Future[Product] = post(uri + s"/${obj.id.get}", obj)
  }

  object skus {
    private val uri = baseUri + "/skus"
    def fetch(id:String):Future[Option[ProductSku]] = getObject[ProductSku](uri + s"/$id")
    def list():Future[List[ProductSku]] = getList[ProductSku](uri)
    def save(obj:ProductSku):Future[ProductSku] = post(uri, obj)
    def update(obj:ProductSku):Future[ProductSku] = post(uri + s"/${obj.id.get}", obj)
  }

  object orders {
    private val uri = baseUri + "/orders"
    def fetch(id:String):Future[Option[Order]] = getObject[Order](uri + s"/$id")
    def list():Future[List[Order]] = getList[Order](uri)
    def save(obj:Order):Future[Order] = post(uri, obj)
    def update(obj:Order):Future[Order] = post(uri + s"/${obj.id.get}", obj)
  }
}

object StripeClient {
  def apply(apiKey:String)(implicit sys:ActorSystem, ec:ExecutionContext) = new StripeClient("https://api.stripe.com", apiKey)
}