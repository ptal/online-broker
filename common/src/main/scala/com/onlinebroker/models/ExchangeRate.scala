package com.onlinebroker.models

case class ExchangeRate(
  id: Option[Long],
  rate: Double,
  currency: Long,
  event: Long
)
