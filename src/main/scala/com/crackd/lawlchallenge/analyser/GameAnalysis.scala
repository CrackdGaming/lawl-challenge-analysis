package com.crackd.lawlchallenge.analyser

import com.crackd.lawlchallenge.Statistic
import com.crackd.lawlchallenge.analyser.GameAnalysis._
import play.api.libs.json._

/**
 * Created by trent ahrens on 4/9/15.
 */

object GameAnalysis {
  def apply() = new GameAnalysis()

  object Game {
    val empty = new Game(0)
  }

  case class Game(totalGames: Int) extends Statistic[Game] {
    override def +(o: Game): Game = Game(o.totalGames + totalGames)
  }

  implicit val gameWrites = new Writes[Game] {
    override def writes(o: Game): JsValue = Json.obj(
      "totalGames" -> o.totalGames
    )
  }
}

class GameAnalysis extends Analyser [Game]{
  override def apply(json: JsValue): Game = Game(1)
}
