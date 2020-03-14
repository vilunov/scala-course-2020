package maxim.salo

import maxim.salo.Command.{Exit, PrintHistory, TransactionCommand}
import maxim.salo.Transaction.{CreateAccount, Deposit, Transfer, Withdraw}

import scala.annotation.tailrec
import scala.io.StdIn

sealed trait Currency

object Currency {

  val currencyList: List[Currency] = List(USD, RUB)

  object USD extends Currency {
    override def toString: String = "USD"
  }

  object RUB extends Currency {
    override def toString: String = "RUB"
  }

}

sealed trait Command

object Command {

  object Exit extends Command

  final case class PrintHistory(id: Int) extends Command

  final case class TransactionCommand(t: Transaction) extends Command

}

sealed trait Transaction

object Transaction {

  final case class CreateAccount(currency: Currency) extends Transaction

  final case class Deposit(id: Int, currency: Currency, amount: BigDecimal) extends Transaction

  final case class Withdraw(id: Int, currency: Currency, amount: BigDecimal) extends Transaction

  final case class Transfer(idSrc: Int, idDst: Int, currency: Currency, amount: BigDecimal) extends Transaction

}

final case class ParseError(message: String)

final case class TransactionError(message: String)

final case class BankAccount(currency: Currency, balance: BigDecimal, transactionHistory: Vector[Transaction])

object BankState {
  def apply(): BankState = new BankState(Vector())
}

final case class BankState private(accounts: Vector[BankAccount]) {

  def applyTransaction(transaction: Transaction): Either[TransactionError, BankState] = {
    transaction match {
      case CreateAccount(currency) =>
        Right(
          BankState(this.accounts
            .appended(
              BankAccount(
                currency,
                0,
                Vector(transaction)
              )
            )
          )
        )
      case Deposit(id, currency, amount) =>
        validateDeposit(id, currency) match {
          case Some(error) => Left(error)
          case None =>
            val account = this.accounts(id)
            Right(
              BankState(this.accounts
                .updated(
                  id,
                  BankAccount(
                    account.currency,
                    account.balance + amount,
                    account.transactionHistory.appended(transaction)
                  )
                )
              )
            )
        }
      case Withdraw(id, currency, amount) =>
        validateWithdraw(id, currency, amount) match {
          case Some(error) => Left(error)
          case None =>
            val account = this.accounts(id)
            Right(
              BankState(this.accounts
                .updated(id,
                  BankAccount(
                    account.currency,
                    account.balance - amount,
                    account.transactionHistory.appended(transaction)
                  )
                )
              )
            )
        }
      case Transfer(idSrc, idDst, currency, amount) =>
        validateTransfer(idSrc, idDst, currency, amount) match {
          case Some(error) => Left(error)
          case None =>
            val accountSrc = this.accounts(idSrc)
            val accountDst = this.accounts(idDst)
            Right(
              BankState(this.accounts
                .updated(
                  idSrc,
                  BankAccount(
                    accountSrc.currency,
                    accountSrc.balance - amount,
                    accountSrc.transactionHistory.appended(transaction)
                  )
                )
                .updated(
                  idDst,
                  BankAccount(
                    accountDst.currency,
                    accountDst.balance + amount,
                    accountDst.transactionHistory.appended(transaction)
                  )
                )
              )
            )
        }
      case _ => Left(TransactionError(s"Unknown transaction $transaction"))
    }
  }

  def validateId(id: Int): Option[TransactionError] = {
    if (id >= this.accounts.length)
      Some(TransactionError(s"No account with ID $id"))
    else
      None
  }

  def validateCurrency(account: BankAccount, currency: Currency): Option[TransactionError] = {
    if (currency != account.currency)
      Some(TransactionError(s"Currency $currency is unavailable for account with currency ${account.currency}"))
    else
      None
  }

  def validateAmount(account: BankAccount, amount: BigDecimal): Option[TransactionError] = {
    if (amount > account.balance)
      Some(TransactionError(s"Not enough amount of money. Balance is ${account.balance} ${account.currency}"))
    else
      None
  }

  def validateSameId(idSrc: Int, idDst: Int): Option[TransactionError] = {
    if (idSrc == idDst)
      Some(TransactionError(s"Transaction between the same account is unavailable"))
    else
      None
  }

  def validateDeposit(id: Int, currency: Currency): Option[TransactionError] = {
    validateId(id) orElse {
      val account = this.accounts(id)
      validateCurrency(account, currency)
    }
  }

  def validateWithdraw(id: Int, currency: Currency, amount: BigDecimal): Option[TransactionError] = {
    validateId(id) orElse {
      val account = this.accounts(id)
      validateCurrency(account, currency) orElse validateAmount(account, amount)
    }
  }

  def validateTransfer(idSrc: Int,
                       idDst: Int,
                       currency: Currency,
                       amount: BigDecimal): Option[TransactionError] = {
    validateId(idSrc) orElse validateId(idDst) orElse {
      val accountSrc = this.accounts(idSrc)
      val accountDst = this.accounts(idDst)
      validateCurrency(accountSrc, currency) orElse validateCurrency(accountDst, currency) orElse
        validateAmount(accountSrc, amount) orElse validateSameId(idSrc, idDst)
    }
  }
}

object Main extends App {

  def parseCommand(input: String): Either[ParseError, Command] = {
    val parsed = input match {
      case "exit" => Some(Exit)
      case s"history of account $id" => for (id <- id.toIntOption) yield PrintHistory(id)
      case s"open account in $currency" => for (currency <- parseCurrency(currency))
        yield TransactionCommand(CreateAccount(currency))
      case s"deposit $amount $currency to account $id" =>
        for (amount <- parseBigDec(amount);
             currency <- parseCurrency(currency);
             id <- id.toIntOption)
          yield TransactionCommand(Deposit(id, currency, amount))
      case s"withdraw $amount $currency from account $id" =>
        for (amount <- parseBigDec(amount);
             currency <- parseCurrency(currency);
             id <- id.toIntOption)
          yield TransactionCommand(Withdraw(id, currency, amount))
      case s"transfer $amount $currency from account $idSrc to account $idDst" =>
        for (amount <- parseBigDec(amount);
             currency <- parseCurrency(currency);
             idSrc <- idSrc.toIntOption;
             idDst <- idDst.toIntOption)
          yield TransactionCommand(Transfer(idSrc, idDst, currency, amount))
      case _ => None
    }
    parsed match {
      case Some(cmd) => Right(cmd)
      case _ => Left(ParseError(s"Unknown input: $input"))
    }
  }

  def parseCurrency(stringCurrency: String): Option[Currency] = Currency.currencyList.find(_.toString == stringCurrency)

  def parseBigDec(string: String): Option[BigDecimal] = {
    try {
      Some(BigDecimal(string))
    } catch {
      case _: NumberFormatException => None
    }
  }

  @tailrec def mainLoop(state: BankState): Unit = {
    print("> ")
    val input = StdIn.readLine()
    val newState = parseCommand(input) match {
      case Left(ParseError(message)) =>
        println(message)
        state
      case Right(Command.Exit) =>
        return
      case Right(Command.PrintHistory(accountId)) =>
        state.validateId(accountId) match {
          case Some(TransactionError(message)) =>
            println(message)
          case None =>
            val account = state.accounts(accountId)
            println(s"Operations of account $accountId (${account.currency}):")
            account.transactionHistory.foreach {
              case CreateAccount(_) =>
                println(s"- Creation")
              case Deposit(_, currency, amount) =>
                println(s"- Deposition of $amount $currency")
              case Withdraw(_, currency, amount) =>
                println(s"- Withdrawal of $amount $currency")
              case Transfer(idSrc, idDst, currency, amount) =>
                println(s"- Transfer of $amount $currency from account $idSrc to account $idDst")
              case x =>
                println(s"- Unknown operation: $x")
            }
        }
        state
      case Right(Command.TransactionCommand(transaction)) =>
        state.applyTransaction(transaction) match {
          case Left(TransactionError(message)) =>
            println(message)
            state
          case Right(value) =>
            transaction match {
              case CreateAccount(currency) =>
                println(s"New account in $currency, ID ${value.accounts.length - 1}")
              case Deposit(id, currency, amount) =>
                println(s"Deposited $amount $currency to account $id, new balance: ${value.accounts(id).balance} $currency")
              case Withdraw(id, currency, amount) =>
                println(s"Withdrew $amount $currency from account $id, new balance: ${value.accounts(id).balance} $currency")
              case Transfer(idSrc, idDst, currency, amount) =>
                println(s"Transferred $amount $currency to account $idDst, new account $idSrc balance: ${value.accounts(idSrc).balance} $currency")
              case x =>
                println(s"Unknown operation: $x")
            }
            value
        }
    }
    mainLoop(newState)
  }

  mainLoop(BankState())
}