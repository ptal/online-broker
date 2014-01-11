package com.onlinebroker.controllers.api

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.onlinebroker.models._
import com.onlinebroker.models.tables.Users


object UserController extends Controller {

  def index(providerId: String, providerUserId: String) = Action {

    implicit val writeAccount : Writes[Account] = Json.writes[Account]
    implicit val writeUser : Writes[User] = Json.writes[User]

    // FIXME: that's not the format expected by the js client
    Users.findByInfo(AuthenticationUserInfo(providerUserId = providerUserId, providerName = providerId)) fold (
       error => NotFound("The user does not exist"),
       user => Ok(Json.toJson(user))
    )

  }
}