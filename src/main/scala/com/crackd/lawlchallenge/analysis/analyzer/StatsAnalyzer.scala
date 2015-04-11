package com.crackd.lawlchallenge.analysis.analyzer

import com.crackd.lawlchallenge.analysis.model.StatModels._
import play.api.libs.json._

/**
 * Created by trent ahrens on 4/11/15.
 */
class StatsAnalyzer(participantSelector: JsValue => JsArray) {
  def apply(json: JsValue): Stats =
    Stats(
      statSelector("champLevel", json).sum,
      statSelector("totalTimeCrowdControlDealt", json).max, statSelector("totalTimeCrowdControlDealt", json).sum,
      goldAnalysis(json),
      killsAnalysis(json),
      damageAnalysis(json))

  def goldAnalysis(json: JsValue): Gold =
    Gold(
      statSelector("goldEarned", json).max, statSelector("goldEarned", json).sum,
      statSelector("goldSpent", json).max, statSelector("goldSpent", json).sum)

  def killsAnalysis(json: JsValue): Kills =
    Kills(
      statSelector("deaths", json).max, statSelector("deaths", json).sum,
      statSelector("kills", json).max, statSelector("kills", json).sum,
      statSelector("assists", json).max, statSelector("assists", json).sum,
      statSelector("doubleKills", json).max, statSelector("doubleKills", json).sum,
      statSelector("inhibitorKills", json).max, statSelector("inhibitorKills", json).sum,
      statSelector("killingSprees", json).max, statSelector("killingSprees", json).sum,
      statSelector("largestKillingSpree", json).max, statSelector("largestKillingSpree", json).sum,
      statSelector("largestMultiKill", json).max, statSelector("largestMultiKill", json).sum,
      statSelector("minionsKilled", json).max, statSelector("minionsKilled", json).sum,
      statSelector("towerKills", json).max, statSelector("towerKills", json).sum,
      statSelector("tripleKills", json).max, statSelector("tripleKills", json).sum)

  def damageAnalysis(json: JsValue): Damage =
    Damage(
      statSelector("largestCriticalStrike", json).max, statSelector("largestCriticalStrike", json).sum,
      statSelector("totalDamageDealt", json).max, statSelector("totalDamageDealt", json).sum,
      statSelector("totalDamageDealtToChampions", json).max, statSelector("totalDamageDealtToChampions", json).sum,
      statSelector("totalDamageTaken", json).max, statSelector("totalDamageTaken", json).sum,
      magicAnalysis(json), physicalAnalysis(json), healAnalysis(json)
    )

  def magicAnalysis(json: JsValue): Magic =
    Magic(
      statSelector("magicDamageDealt", json).max, statSelector("magicDamageDealt", json).sum,
      statSelector("magicDamageDealtToChampions", json).max, statSelector("magicDamageDealtToChampions", json).sum,
      statSelector("magicDamageTaken", json).max, statSelector("magicDamageTaken", json).sum)

  def physicalAnalysis(json: JsValue): Physical =
    Physical(
      statSelector("physicalDamageDealt", json).max, statSelector("physicalDamageDealt", json).sum,
      statSelector("physicalDamageDealtToChampions", json).max, statSelector("physicalDamageDealtToChampions", json).sum,
      statSelector("physicalDamageTaken", json).max, statSelector("physicalDamageTaken", json).sum)

  def healAnalysis(json: JsValue): Heal =
    Heal(
      statSelector("totalHeal", json).max, statSelector("totalHeal", json).sum,
      statSelector("totalUnitsHealed", json).max, statSelector("totalUnitsHealed", json).sum)

  def statSelector(stat: String, json: JsValue): Seq[Long] = (participantSelector(json) \\ stat).map(_.as[Long])
}
