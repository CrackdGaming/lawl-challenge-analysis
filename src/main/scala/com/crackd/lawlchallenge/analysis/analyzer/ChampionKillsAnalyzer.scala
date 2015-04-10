package com.crackd.lawlchallenge.analysis.analyzer

import com.crackd.lawlchallenge.analysis.model.ChampionKillsModels._
import com.crackd.lawlchallenge.gameconstants.championKill
import com.crackd.lawlchallenge.gametypes._
import com.crackd.lawlchallenge.helper.GameDataFinders.richGameData
import play.api.libs.json._

/**
*  Created by trent ahrens on 4/9/15.
*/
object ChampionKillsAnalyzer {
  def apply() = new ChampionKillsAnalyzer()
}

class ChampionKillsAnalyzer extends Analyzer[ChampionKills] {
  def apply(json: JsValue): ChampionKills = ChampionKillsMonoid.append(kills(json)(events(json)), assists(json)(events(json)))

  def events(json: JsValue): Seq[JsValue] = json.events.withType(championKill)

  def kills(json: JsValue)(e: Seq[JsValue]): ChampionKills = e.foldRight(ChampionKills.empty)((j,a) =>
    a + (killerChampion(json)(j), victimChampion(json)(j), 1, 0)
  )

  def assists(json: JsValue)(e: Seq[JsValue]): ChampionKills = e.foldRight(ChampionKills.empty)((j,a) => {
    ChampionKillsMonoid.append(a, assistChampions(json)(j)
      .foldRight(ChampionKills.empty)((assist,aa) => aa + (assist, victimChampion(json)(j), 0, 1)))
  })

  def killerChampion(json: JsValue): JsValue => ChampionId =
    j => json.championIdForParticipantId((j \ "killerId").as[ParticipantId])

  def victimChampion(json: JsValue): JsValue => ChampionId =
    j => json.championIdForParticipantId((j \ "victimId").as[ParticipantId])

  def assistChampions(json: JsValue): JsValue => Seq[ChampionId] = j =>
    (j \ "assistingParticipantIds").asOpt[JsArray]
      .map(_.value.map(_.as[ParticipantId]).map(ai => json.championIdForParticipantId(ai)))
      .getOrElse(Seq.empty)
}
