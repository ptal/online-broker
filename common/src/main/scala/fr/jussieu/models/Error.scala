package models

sealed trait OnlineBrokerError {
  def name: String
  def description: String
}

case class InternalServerError() extends OnlineBrokerError {
  def name = "internal-server-error"
  def description = "Server encountered a internal error."
}

case class MalformedRequest() extends OnlineBrokerError {
  def name = "malformed-request"
  def description = "Last request you sent was malformed."
}

case class EmailAlreadyUsed(email: String) extends OnlineBrokerError {
  def name = "email-already-used"
  def description = "Email already used, did you already registered for an account?"
}
