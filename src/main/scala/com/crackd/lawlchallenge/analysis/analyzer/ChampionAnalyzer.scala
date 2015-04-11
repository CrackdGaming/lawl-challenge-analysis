package com.crackd.lawlchallenge.analysis.analyzer

import com.crackd.lawlchallenge.analysis.model.ChampionsModels.{Champion, Champions, ChampionsMonoid}
import com.crackd.lawlchallenge.gametypes.ParticipantId
import com.crackd.lawlchallenge.helper.GameDataFinders.richGameData
import play.api.libs.json.{JsValue, Json}

/**
 * Created by trent ahrens on 4/11/15.
 */
class ChampionAnalyzer extends Analyzer[Champions] {
  override def apply(json: JsValue): Champions =
    json.participants.value
      .map(j => ((j \ "participantId").as[Int], (j \ "championId").as[Int]))
      .map(ids => (ids._2, Champion(1, participantStatAnalyzer(ids._1).apply(json))))
      .foldRight(ChampionsMonoid.zero) { case ((id, c), a) => a + (id, c) }
  
  def participantStatAnalyzer(id: ParticipantId) = new StatsAnalyzer(json => Json.arr(json.participants.value.filter(j => (j \ "participantId").as[ParticipantId] == id)))
}
