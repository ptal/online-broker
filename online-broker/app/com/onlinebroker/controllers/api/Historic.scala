package com.onlinebroker.controllers.api

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.iteratee.Concurrent

import scalaz.{\/, -\/, \/-}

import com.onlinebroker.models._


object Historic extends Controller with securesocial.core.SecureSocial {

  def rateHistoric(currency: String) = SecuredAction {
    ExchangeRatesEvent.rateHistoric(currency).fold(
      error => BadRequest(GenericError.makeErrorResponse(error)),
      rates => Ok(Json.obj(
        "historic" -> JsArray(
          rates.map(r => Json.obj(
            "timestamp" -> JsNumber(r.timestamp),
            "rate" -> JsString(r.rate.toString())
          ))
        )
      ))
    )
  }
}