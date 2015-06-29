package com.bigcommerce.twitterhooks.stripe.api.util

import spray.http.FormData
import spray.json._

/**
 * Created by zack.angelo on 6/28/15.
 */
object JsonToFormDataConverter {
  private def fieldName(fs:List[String]) = {
    val rfs = fs.reverse
    rfs.head + rfs.tail.map(f=> s"[$f]").mkString("")
  }

  def apply(json:JsObject):FormData = {
    def convertObj(o:JsObject, fs:List[String]):List[(List[String],String)] = {
      o.fields.flatMap {
        case (f,JsString(string)) => List((fs :+ f, string))
        case (f,JsNumber(num))    => List((fs :+ f, num.toString))
        case (f,JsBoolean(bool))  => List((fs :+ f, bool.toString))
        case (f,obj:JsObject)     => convertObj(obj, fs :+ f)
        case (f,arr:JsArray)      => List.empty
      }.toList
    }

    val formFields = convertObj(json,List.empty).map { x =>
      fieldName(x._1) -> x._2
    }

    FormData(formFields)
  }
}