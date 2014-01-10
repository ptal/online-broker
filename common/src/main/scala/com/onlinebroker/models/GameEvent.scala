package com.onlinebroker.models

import java.sql.Date

case class GameEvent(
  id: Option[Long],
  owner: Long,
  creationDate: Date,
  eventType: Long,
  event: Long
)
