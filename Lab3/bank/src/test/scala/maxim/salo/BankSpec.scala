package maxim.salo

import maxim.salo.Currency.RUB
import maxim.salo.Transaction.{CreateAccount, Deposit, Transfer, Withdraw}
import org.scalatest.{FlatSpec, Matchers}

class BankSpec extends FlatSpec with Matchers {

  "Create account" should "be correct for RUB" in {
    val bankState = BankState()
    val transaction = CreateAccount(RUB)
    val res = bankState.applyTransaction(transaction) match {
      case Right(state) => state
    }
    res.accounts(0) shouldEqual BankAccount(RUB, 0, Vector(transaction))
  }

  "Deposit" should "be correct for id 0 and 200 RUB" in {
    val bankState = BankState(Vector(BankAccount(RUB, 0, Vector())))
    val transaction = Deposit(0, RUB, 200)
    val res = bankState.applyTransaction(transaction) match {
      case Right(state) => state
      case Left(_) => BankState()
    }
    res.accounts(0) shouldEqual BankAccount(RUB, 200, Vector(transaction))
  }

  "Deposit" should "be correct for id 999 and 200 RUB" in {
    val bankState = BankState(Vector(BankAccount(RUB, 0, Vector())))
    val transaction = Deposit(999, RUB, 200)
    val res = bankState.applyTransaction(transaction) match {
      case Right(_) => false
      case Left(_) => true
    }
    res shouldBe true
  }

  "Withdraw" should "be correct for id 0 and 200 RUB" in {
    val bankState = BankState(Vector(BankAccount(RUB, 400, Vector())))
    val transaction = Withdraw(0, RUB, 200)
    val res = bankState.applyTransaction(transaction) match {
      case Right(state) => state
      case Left(_) => BankState()
    }
    res.accounts(0) shouldEqual BankAccount(RUB, 200, Vector(transaction))
  }

  "Withdraw" should "be correct for id 0 and 2000000 RUB" in {
    val bankState = BankState(Vector(BankAccount(RUB, 400, Vector())))
    val transaction = Withdraw(0, RUB, 2000000)
    val res = bankState.applyTransaction(transaction) match {
      case Right(_) => false
      case Left(_) => true
    }
    res shouldBe true
  }

  "Transfer" should "be correct for id 0 and id 1 and 200 RUB" in {
    val bankState = BankState(Vector(BankAccount(RUB, 400, Vector()), BankAccount(RUB, 0, Vector())))
    val transaction = Transfer(0, 1, RUB, 200)
    val res = bankState.applyTransaction(transaction) match {
      case Right(state) => state
      case Left(_) => BankState()
    }
    res.accounts(0) shouldEqual BankAccount(RUB, 200, Vector(transaction))
    res.accounts(1) shouldEqual BankAccount(RUB, 200, Vector(transaction))
  }

  "Transfer" should "be correct for id 0 and id 1 and 2000000 RUB" in {
    val bankState = BankState(Vector(BankAccount(RUB, 400, Vector()), BankAccount(RUB, 0, Vector())))
    val transaction = Transfer(0, 1, RUB, 2000000)
    val res = bankState.applyTransaction(transaction) match {
      case Right(_) => false
      case Left(_) => true
    }
    res shouldBe true
  }
}
