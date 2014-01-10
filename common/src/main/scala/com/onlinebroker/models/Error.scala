package com.onlinebroker.models

sealed trait OnlineBrokerError {
  def name: String
  def description: String
}

case class InternalServerError(debugInfo: String) extends OnlineBrokerError {
  def name = "internal-server-error"
  def description = "Server encountered a internal error."
}

case class MalformedRequest(debugInfo: String) extends OnlineBrokerError {
  def name = "malformed-request"
  def description = "Last request you sent was malformed."
}

case class EmailAlreadyUsed(email: String) extends OnlineBrokerError {
  def name = "email-already-used"
  def description = "Email already used, did you already registered for an account?"
}

case class UserNotRegistered(providerID: String) extends OnlineBrokerError {
  def name = "user-not-registered"
  def description = "You can't sign in if you are not registered."
}
