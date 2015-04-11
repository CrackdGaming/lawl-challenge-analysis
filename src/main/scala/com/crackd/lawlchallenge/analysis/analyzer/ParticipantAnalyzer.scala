package com.crackd.lawlchallenge.analysis.analyzer

import com.crackd.lawlchallenge.analysis.model.StatModels._
import com.crackd.lawlchallenge.helper.GameDataFinders.richGameData
import play.api.libs.json._

/**
 * Created by trent ahrens on 4/11/15.
 */
class ParticipantAnalyzer extends Analyzer[Stats] {

  override def apply(json: JsValue): Stats = new StatsAnalyzer(json => json.participants).apply(json)
}
