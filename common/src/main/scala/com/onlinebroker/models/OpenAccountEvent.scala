package com.onlinebroker.models

case class OpenAccountEvent(
  id: Option[Long],
  owner: Long,
  whichAccount: Long,
  paymentMethod: String
)
