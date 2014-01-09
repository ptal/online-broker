package com.onlinebroker.plugins

import play.api.mvc._

import play.api.templates.{Html, Txt}
import play.api.{Logger, Plugin, Application}

import securesocial.core.{Identity, SecuredRequest, SocialUser}
import play.api.data.Form
import securesocial.controllers.Registration.RegistrationInfo
import securesocial.controllers.PasswordChange.ChangeInfo
import securesocial.controllers.TemplatesPlugin
import securesocial.controllers.DefaultTemplatesPlugin

import com.onlinebroker.views

class MyViews(application: play.Application) extends TemplatesPlugin
{

  private val default = new DefaultTemplatesPlugin(play.api.Play.current)

  /**
   * Returns the html for the login page
   * @param request
   * @tparam A
   * @return
   */
  override def getLoginPage[A](implicit request: Request[A], form: Form[(String, String)],
                               msg: Option[String] = None): Html =
  {
    views.html.login(form, msg)
  }

  /**
   * Returns the html for the signup page
   *
   * @param request
   * @tparam A
   * @return
   */
  override def getSignUpPage[A](implicit request: Request[A], form: Form[RegistrationInfo], token: String): Html = {
    default.getSignUpPage
  }

  /**
   * Returns the html for the start signup page
   *
   * @param request
   * @tparam A
   * @return
   */
  override def getStartSignUpPage[A](implicit request: Request[A], form: Form[String]): Html = {
    default.getStartSignUpPage
  }

  /**
   * Returns the html for the reset password page
   *
   * @param request
   * @tparam A
   * @return
   */
  override def getStartResetPasswordPage[A](implicit request: Request[A], form: Form[String]): Html = {
    default.getStartResetPasswordPage
  }

  /**
   * Returns the html for the start reset page
   *
   * @param request
   * @tparam A
   * @return
   */
  def getResetPasswordPage[A](implicit request: Request[A], form: Form[(String, String)], token: String): Html = {
    default.getResetPasswordPage
  }

  /**
   * Returns the html for the change password page
   *
   * @param request
   * @param form
   * @tparam A
   * @return
   */
  def getPasswordChangePage[A](implicit request: SecuredRequest[A], form: Form[ChangeInfo]): Html = {
    default.getPasswordChangePage
  }


  /**
   * Returns the email sent when a user starts the sign up process
   *
   * @param token the token used to identify the request
   * @param request the current http request
   * @return a String with the text and/or html body for the email
   */
  def getSignUpEmail(token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    default.getSignUpEmail(token)
  }

  /**
   * Returns the email sent when the user is already registered
   *
   * @param user the user
   * @param request the current request
   * @return a String with the text and/or html body for the email
   */
  def getAlreadyRegisteredEmail(user: SocialUser)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    default.getAlreadyRegisteredEmail(user)
  }

  /**
   * Returns the welcome email sent when the user finished the sign up process
   *
   * @param user the user
   * @param request the current request
   * @return a String with the text and/or html body for the email
   */
  def getWelcomeEmail(user: SocialUser)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    default.getWelcomeEmail(user)
  }

  /**
   * Returns the email sent when a user tries to reset the password but there is no account for
   * that email address in the system
   *
   * @param request the current request
   * @return a String with the text and/or html body for the email
   */
  def getUnknownEmailNotice()(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    default.getUnknownEmailNotice()
  }

  /**
   * Returns the email sent to the user to reset the password
   *
   * @param user the user
   * @param token the token used to identify the request
   * @param request the current http request
   * @return a String with the text and/or html body for the email
   */
  def getSendPasswordResetEmail(user: SocialUser, token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    default.getSendPasswordResetEmail(user, token)
  }

  /**
   * Returns the email sent as a confirmation of a password change
   *
   * @param user the user
   * @param request the current http request
   * @return a String with the text and/or html body for the email
   */
  def getPasswordChangedNoticeEmail(user: SocialUser)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    default.getPasswordChangedNoticeEmail(user)
  }


  /**
   * Returns the html of the Not Authorized page
   *
   * @param request the current http request
   * @return a String with the text and/or html body for the email
   */
  def getNotAuthorizedPage[A](implicit request: Request[A]): Html = {
    default.getNotAuthorizedPage
  }

  def getAlreadyRegisteredEmail(user: securesocial.core.Identity)(implicit request: play.api.mvc.RequestHeader): (Option[play.api.templates.Txt], Option[play.api.templates.Html]) = ???
  def getPasswordChangedNoticeEmail(user: securesocial.core.Identity)(implicit request: play.api.mvc.RequestHeader): (Option[play.api.templates.Txt], Option[play.api.templates.Html]) = ???
  def getSendPasswordResetEmail(user: securesocial.core.Identity,token: String)(implicit request: play.api.mvc.RequestHeader): (Option[play.api.templates.Txt], Option[play.api.templates.Html]) = ???
  def getWelcomeEmail(user: securesocial.core.Identity)(implicit request: play.api.mvc.RequestHeader): (Option[play.api.templates.Txt], Option[play.api.templates.Html]) = ???

}
