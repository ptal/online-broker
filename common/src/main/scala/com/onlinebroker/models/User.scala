package com.onlinebroker.models

case class User(
  id: Option[Long],
  providerId: Long,
  providerUserId: String,
  email: Option[String],
  firstName: String,
  lastName: String
)
