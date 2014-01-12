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

case class NotYetSuchEvent(eventName: String) extends OnlineBrokerError {
  def name = "not-yet-such-event"
  def description = "Event " + eventName + " has not yet occur."
}

case class BadCurrencyAcronym(acronym: String) extends OnlineBrokerError {
  def name = "bad-currency-acronym"
  def description = "Currency acronym " + acronym + " does not exist."
}

case class TransferWithClosedAccount() extends OnlineBrokerError {
  def name = "transfer-with-closed-account"
  def description = "You tried to transfer money on an account that you didn't open."
}

case class NegativeAccountNotAllowed() extends OnlineBrokerError {
  def name = "negative-account-not-allowed"
  def description = "You tried to transfer money without having the money to do so."
}

case class AccountAlreadyOpened() extends OnlineBrokerError {
  def name = "account-already-opened"
  def description = "You tried to open an account that is already opened."
}

case class NotEnoughMoneyToOpenAccount() extends OnlineBrokerError {
  def name = "not-enough-money-to-open-account"
  def description = "You tried to open an account but you haven't enough money to do so."
}
