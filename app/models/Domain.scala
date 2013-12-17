package models

import play.api.libs.json._

case class Currency(name: String, currentRateAgainstDollar: Double) {
  def convert(to: Currency, amount: Double) = amount / currentRateAgainstDollar * to.currentRateAgainstDollar
}

object Currency {

  val writeCurrency = Json.writes[Currency]

}


case class User(id: Long, name: String)

case class Account(currency: Currency, amount: Double, owner: Long)

case class Transfer(currencyFrom: Currency, currencyTo: Currency, amount: Double, owner: Long)

object Account {


}

case class UserAggregatedView(id: Long, name: String, accounts: Set[Account])

object UserAggregatedView {

  def create(user: User, accounts: Set[Account]) : UserAggregatedView =
        UserAggregatedView(id = user.id, name = user.name, accounts = accounts)

}


