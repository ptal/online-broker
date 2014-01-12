package com.onlinebroker.controllers.api

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.iteratee.Concurrent

import scalaz.{\/, -\/, \/-}

import com.onlinebroker.models._
import com.onlinebroker.models.tables._


object Money extends Controller with securesocial.core.SecureSocial {

  val (out, channel) = Concurrent.broadcast[String]

  def listCurrenciesNames = SecuredAction {
    val currencies = Currencies.nameOfAllCurrencies()
    Ok(Json.obj(
      "status" -> "OK",
      "currencies" -> JsObject(
        currencies.map(c => ("currency" -> Json.obj(
          "acronym" -> c.acronym, 
          "fullName" -> JsString(c.name))))
        .toSeq)
    ))
  }

  def listCurrencies = SecuredAction {
    //FIXME: Find the way to serialize the errors as json
    implicit val writer = Json.writes[CurrencyInfo]
    ExchangeRatesEvent.findAllLastExchangeRates.fold(
      error => InternalServerError(error.toString),
      rates => Ok(Json.obj(
        "status" -> "OK",
        "currencies" -> JsObject(
          rates.map(c => ("currency" -> Json.obj(
            "acronym" -> c.acronym, 
            "rate" -> JsString(c.exchangeRate.toString()))))
          .toSeq)
      ))
    )
  }

  def transfer = SecuredAction(parse.json) { request =>
    val id = request.user.identityId
    implicit val transferReads = (
      (__ \ "amount").read[Double] and
      (__ \ "from").read[String] and
      (__ \ "to").read[String]
      tupled
    )
    request.body.validate[(Double, String, String)].fold(
      valid = { case (amount, fromCurrencyAcronym, toCurrencyAcronym) =>
        TransferGameEvent.transfer(fromCurrencyAcronym, toCurrencyAcronym, amount, 
          AuthenticationUserInfo(id.providerId, id.userId)) match 
        {
          case -\/(error) => BadRequest(GenericError.makeErrorResponse(error))
          case \/-(transferInfo) => Ok(Json.obj(
            "status" -> "OK",
            "from" -> Json.obj(
              "currency" -> fromCurrencyAcronym,
              "amount" -> transferInfo.from.toString()),
            "to" -> Json.obj(
              "currency" -> toCurrencyAcronym,
              "amount" -> transferInfo.to.toString())
          ))
        }
      },
      invalid = { error => BadRequest(Json.toJson(Map(
        "status" -> "KO", 
        "error" -> s"The request is invalid. Error: ${error.toString}"
      )))}
    )
  }

  def updateCurrencies = Action { request =>
    implicit val writer = Json.writes[CurrencyInfo]
    ExchangeRatesEvent.findAllLastExchangeRates.fold(
      error => play.api.mvc.Results.InternalServerError(error.toString),
      rates => {
          channel.push(Json.toJson(rates).toString())
          Ok(Json.obj(
            "status" -> "OK",
            "currencies" -> Json.toJson(rates)
          ))
      }
    )
  }

  def listCurrenciesWebSocket = WebSocket.using[String] { request =>
    // Log events to the console
    val in = Iteratee.foreach[String](println).map { _ =>
      println("Disconnected")
    }
    (in, out)
  }
}