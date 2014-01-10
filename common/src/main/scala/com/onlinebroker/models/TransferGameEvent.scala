package com.onlinebroker.models

case class TransferGameEvent(
  id: Option[Long],
  owner: Long,
  fromAccount: Long,
  toAccount: Long,
  amount: Double
)
