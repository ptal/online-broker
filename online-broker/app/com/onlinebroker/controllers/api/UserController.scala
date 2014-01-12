package com.onlinebroker.controllers.api

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.onlinebroker.models._
import com.onlinebroker.models.tables.{Accounts, Users}


case class UserWithAccounts(user: User, accounts: List[Account])

object UserController extends Controller with securesocial.core.SecureSocial {

  def listAccounts() = SecuredAction { request =>
    val id = request.user.identityId
    Ok(Json.obj(
        "status" -> "OK",
        "accounts" -> JsObject(
          User.listAccounts(AuthenticationUserInfo(id.providerId, id.userId))
              .map(c => (c.currencyAcronym, JsString(c.amount.toString())))
              .toSeq)
      ))
  }
}