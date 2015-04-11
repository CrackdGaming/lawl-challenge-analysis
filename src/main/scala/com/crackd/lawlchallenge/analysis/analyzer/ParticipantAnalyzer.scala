package com.crackd.lawlchallenge.analysis.analyzer

import com.crackd.lawlchallenge.analysis.model.ParticipantModels._
import com.crackd.lawlchallenge.helper.GameDataFinders.richGameData
import play.api.libs.json._

/**
 * Created by trent ahrens on 4/11/15.
 */
class ParticipantAnalyzer extends Analyzer[Participant] {

  override def apply(json: JsValue): Participant =
    Participant(
      stats("champLevel", json).sum,
      stats("assists", json).max, stats("assists", json).sum,
      stats("totalTimeCrowdControlDealt", json).max, stats("totalTimeCrowdControlDealt", json).sum,
      goldAnalysis(json),
      killsAnalysis(json),
      damageAnalysis(json))

  def goldAnalysis(json: JsValue): Gold =
    Gold(
      stats("goldEarned", json).max, stats("goldEarned", json).sum,
      stats("goldSpent", json).max, stats("goldSpent", json).sum)

  def killsAnalysis(json: JsValue): Kills =
    Kills(
      stats("deaths", json).max, stats("deaths", json).sum,
      stats("kills", json).max, stats("kills", json).sum,
      stats("doubleKills", json).max, stats("doubleKills", json).sum,
      stats("inhibitorKills", json).max, stats("inhibitorKills", json).sum,
      stats("killingSprees", json).max, stats("killingSprees", json).sum,
      stats("largestKillingSpree", json).max, stats("largestKillingSpree", json).sum,
      stats("largestMultiKill", json).max, stats("largestMultiKill", json).sum,
      stats("minionsKilled", json).max, stats("minionsKilled", json).sum,
      stats("towerKills", json).max, stats("towerKills", json).sum,
      stats("tripleKills", json).max, stats("tripleKills", json).sum)

  def damageAnalysis(json: JsValue): Damage =
    Damage(
      stats("largestCriticalStrike", json).max, stats("largestCriticalStrike", json).sum,
      stats("totalDamageDealt", json).max, stats("totalDamageDealt", json).sum,
      stats("totalDamageDealtToChampions", json).max, stats("totalDamageDealtToChampions", json).sum,
      stats("totalDamageTaken", json).max, stats("totalDamageTaken", json).sum,
      magicAnalysis(json), physicalAnalysis(json), healAnalysis(json)
    )

  def magicAnalysis(json: JsValue): Magic =
    Magic(
      stats("magicDamageDealt", json).max, stats("magicDamageDealt", json).sum,
      stats("magicDamageDealtToChampions", json).max, stats("magicDamageDealtToChampions", json).sum,
      stats("magicDamageTaken", json).max, stats("magicDamageTaken", json).sum)

  def physicalAnalysis(json: JsValue): Physical =
    Physical(
      stats("physicalDamageDealt", json).max, stats("physicalDamageDealt", json).sum,
      stats("physicalDamageDealtToChampions", json).max, stats("physicalDamageDealtToChampions", json).sum,
      stats("physicalDamageTaken", json).max, stats("physicalDamageTaken", json).sum)

  def healAnalysis(json: JsValue): Heal =
    Heal(
      stats("totalHeal", json).max, stats("totalHeal", json).sum,
      stats("totalUnitsHealed", json).max, stats("totalUnitsHealed", json).sum)

  def stats(stat: String, json: JsValue): Seq[Long] = (json.participants \\ stat).map(_.as[Long])
}
