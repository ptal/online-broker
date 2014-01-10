package com.onlinebroker.models

case class Account(
  id: Option[Long],
  owner: Long,
  currency: Long,
  amount: Double
)
