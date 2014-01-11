package com.onlinebroker.models

import com.onlinebroker.models.tables._

case class Currency(
  id: Option[Long],
  acronym: String,
  name: String
)

object Currency {
  def insert(currency: Currency) =
    DBAccess.db withSession { implicit session: Session =>
      Currencies.insert(currency)
    }
}