package com.crackd.lawlchallenge.analysis.analyzer

import com.crackd.lawlchallenge.analysis.model.ChampionsModels.{Champion, Champions, ChampionsMonoid}
import com.crackd.lawlchallenge.gametypes.ParticipantId
import com.crackd.lawlchallenge.helper.GameDataFinders.richGameData
import play.api.libs.json.{JsArray, JsValue, Json}

/**
 * Created by trent ahrens on 4/11/15.
 */
class ChampionsAnalyzer extends Analyzer[Champions] {
  override def apply(json: JsValue): Champions =
    json.participants.value
      .map(j => ((j \ "participantId").as[Int], (j \ "championId").as[Int]))
      .map(ids => (ids._2, Champion(participantWinCount(ids._1, json), 1, participantStatAnalyzer(ids._1).apply(json))))
      .foldRight(ChampionsMonoid.zero) { case ((id, c), a) => a += (id, c) }

  def participantWinCount(id: ParticipantId, json: JsValue) =
    (json \ "teams").as[JsArray].value.find(j => (j \ "teamId").as[Int] == json.participantTeamId(id))
      .map(j => if ((j \ "winner").as[Boolean]) 1 else 0).getOrElse(0)

  def participantStatAnalyzer(id: ParticipantId) = new StatsAnalyzer(json => Json.arr(json.participants.value.filter(j => (j \ "participantId").as[ParticipantId] == id)))
}
