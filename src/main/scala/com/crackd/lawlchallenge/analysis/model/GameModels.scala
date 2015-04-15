package com.crackd.lawlchallenge.analysis.model

import com.crackd.lawlchallenge.MutableMonoid
import play.api.libs.json.Json

/**
 * Created by trent ahrens on 4/10/15.
 */
object GameModels {
  case class Game(numGames: Long, mostMinutesPlayed: Long, totalMinutesPlayed: Long) {
    def +(o: Game) = Game(
      o.numGames + numGames,
      Math.max(o.mostMinutesPlayed, mostMinutesPlayed),
      o.totalMinutesPlayed + totalMinutesPlayed)
  }

  implicit object GameMonoid extends MutableMonoid[Game] {
    override def zero: Game = new Game(0L, 0L, 0L)

    override def append(f1: Game, f2: => Game): Game = f1 + f2

    override def +=(left: Game, right: Game): Game = left + right
  }

  implicit val gameFormat = Json.format[Game]
}
