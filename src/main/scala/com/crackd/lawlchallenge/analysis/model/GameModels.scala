package com.crackd.lawlchallenge.analysis.model

import play.api.libs.json.Json

import scalaz.Monoid

/**
 * Created by trent ahrens on 4/10/15.
 */
object GameModels {
  case class Game(totalGames: Int)

  implicit object GameMonoid extends Monoid[Game] {
    override def zero: Game = new Game(0)

    override def append(f1: Game, f2: => Game): Game = Game(f1.totalGames + f2.totalGames)
  }

  implicit val gameFormatter = Json.format[Game]
}
