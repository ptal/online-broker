package fr.jussieu.models

case class User(id: Long, name: String)

case class Account(currencyAcronym: String, amount: Double, owner: Long, currencyName:String, currencyExchangeRate: Double)

case class Transfer(currencyFrom: String, currencyTo: String, amount: Double, owner: Long)

case class UserAggregatedView(id: Long, name: String, accounts: Set[Account])

object UserAggregatedView {

  def create(user: User, accounts: Set[Account]) : UserAggregatedView =
        UserAggregatedView(id = user.id, name = user.name, accounts = accounts)

}


