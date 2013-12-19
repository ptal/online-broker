package models

import play.api.libs.json._

case class User(id: Long, name: String)

case class Account(currency: String, amount: Double, owner: Long)

case class Transfer(currencyFrom: String, currencyTo: String, amount: Double, owner: Long)

case class UserAggregatedView(id: Long, name: String, accounts: Set[Account])

object UserAggregatedView {

  def create(user: User, accounts: Set[Account]) : UserAggregatedView =
        UserAggregatedView(id = user.id, name = user.name, accounts = accounts)

}


