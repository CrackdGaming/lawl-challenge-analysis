package com.crackd.lawlchallenge.analysis.model

import play.api.libs.json.Json

import scalaz.Monoid

/**
 * Created by trent ahrens on 4/10/15.
 */
object GameModels {
  case class Game(numGames: Long, numMinutesPlayed: Long) {
    def +(o: Game) = Game(o.numGames + numGames, o.numMinutesPlayed + numMinutesPlayed)
  }

  implicit object GameMonoid extends Monoid[Game] {
    override def zero: Game = new Game(0L, 0L)

    override def append(f1: Game, f2: => Game): Game = f1 + f2
  }

  implicit val gameFormat = Json.format[Game]
}
