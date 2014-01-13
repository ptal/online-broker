package com.onlinebroker.controllers.api

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scalaz.{\/, -\/, \/-}

import com.onlinebroker.models._

object Player extends Controller with securesocial.core.SecureSocial {

  def listAccounts() = SecuredAction { request =>
    val id = request.user.identityId
    Ok(Json.obj(
        "status" -> "OK",
        "accounts" -> JsArray(
          User.listAccounts(AuthenticationUserInfo(id.providerId, id.userId))
              .map(c => Json.obj(
                "currency" -> c.currencyAcronym, 
                "amount" -> JsString(c.amount.toString())))
              .toSeq)
      ))
  }

  def userInfo() = SecuredAction { request =>
    val id = request.user.identityId
    implicit val writer = Json.writes[User]
    val user = AuthentificationUtils.identityToUser(request.user)
    Ok(Json.obj(
      "status" -> "OK",
      "userInfo" -> Json.toJson(user)
    ))
  }



  def openAccount() = SecuredAction(parse.json) { request =>
    val id = request.user.identityId
    implicit val openAccountReads = (
      (__ \ "account-to-open").read[String] and
      (__ \ "pay-with-account").read[String]
      tupled
    )
    request.body.validate[(String, String)].fold(
      valid = {
        case (accountCurrencyToOpen, payWithAccount) => {
          OpenAccountEvent.open(accountCurrencyToOpen, payWithAccount, 
            AuthenticationUserInfo(id.providerId, id.userId)) match 
          {
            case -\/(error) => BadRequest(GenericError.makeErrorResponse(error))
            case \/-(accounts) => Ok(Json.obj(
              "status" -> "OK",
              "payWithAccount" -> Json.obj(
                "currency" -> payWithAccount,
                "amount" -> accounts.payWithAccount.toString()),
              "accountOpened" -> Json.obj(
                "currency" -> accountCurrencyToOpen,
                "amount" -> accounts.accountOpened.toString())
            ))
          }
        }
      },
      invalid = { 
        error => BadRequest(GenericError.makeErrorResponse(MalformedRequest(error.toString())))
      }
    )
  }
}