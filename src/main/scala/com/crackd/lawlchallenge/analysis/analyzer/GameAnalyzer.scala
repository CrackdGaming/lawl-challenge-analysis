package com.crackd.lawlchallenge.analysis.analyzer

import com.crackd.lawlchallenge.analysis.model.GameModels.Game
import play.api.libs.json._

/**
 * Created by trent ahrens on 4/9/15.
 */
class GameAnalyzer extends Analyzer[Game] {
  override def apply(json: JsValue): Game = Game(1, (json \ "matchDuration").as[Long], (json \ "matchDuration").as[Long])
}
