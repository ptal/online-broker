package com.onlinebroker.controllers.html

import play.api.mvc._
import play.api.libs.json.Json

import com.onlinebroker.views
import com.onlinebroker.models.User
import com.onlinebroker.controllers.api.AuthentificationUtils


object MainController extends Controller with securesocial.core.SecureSocial {

  case class UserWithAvatar(user: User, avatar: String)
  def userAccounts = SecuredAction { implicit request =>
    implicit val writes = Json.writes[User]
    implicit val writesAvatar = Json.writes[UserWithAvatar]
    val user = AuthentificationUtils.identityToUser(request.user)
    val avatar = user.avatar.getOrElse("/assets/images/gravatar-140.png")
    Ok(views.html.main.render(Json.toJson(UserWithAvatar(user, avatar)).toString()))
  }

}

