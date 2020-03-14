package maxim.salo

object UserBalance {

  def apply(rub: Rational, dollar: Rational, euro: Rational): UserBalance = new UserBalance(rub, dollar, euro)
}

case class UserBalance private (rub: Rational, dollar: Rational, euro: Rational) {

  def +(ub: UserBalance): UserBalance = UserBalance(this.rub + ub.rub, this.dollar + ub.dollar, this.euro + ub.euro)

  def -(ub: UserBalance): UserBalance = UserBalance(this.rub - ub.rub, this.dollar - ub.dollar, this.euro - ub.euro)

  def unary_-(): UserBalance = UserBalance(-this.rub, -this.dollar, -this.euro)
}
