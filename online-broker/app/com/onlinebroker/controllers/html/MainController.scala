package com.onlinebroker.controllers.html

import play.api.mvc._

import com.onlinebroker.views

object MainController extends Controller with securesocial.core.SecureSocial {

  def userAccounts = SecuredAction { implicit request =>
    Ok(views.html.main.render(request.user.identityId.userId, request.user.identityId.providerId))
  }

}

