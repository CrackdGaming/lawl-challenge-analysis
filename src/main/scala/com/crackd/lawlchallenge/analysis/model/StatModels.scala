package com.crackd.lawlchallenge.analysis.model

import java.lang.Math.max

import play.api.libs.json.Json

import scalaz.Monoid

/**
 * Created by trent ahrens on 4/11/15.
 */

object StatModels {
  case class Stats(totalChampLevels: Long,
                         mostTimeCrowdControlDealt: Long, totalTimeCrowdControlDealt: Long,
                         gold: Gold, kills: Kills, damage: Damage) {
    def +(o: Stats): Stats =
      Stats(
        o.totalChampLevels + totalChampLevels,
        max(o.mostTimeCrowdControlDealt, mostTimeCrowdControlDealt), o.totalTimeCrowdControlDealt + totalTimeCrowdControlDealt,
        o.gold + gold, o.kills + kills, o.damage + damage)
  }

  case class Gold(mostGoldAccumulated: Long, totalGoldAccumulated: Long, mostGoldSpent: Long, totalGoldSpent: Long) {
    def +(o: Gold): Gold =
      Gold(
        max(o.mostGoldAccumulated, mostGoldAccumulated), o.totalGoldAccumulated + totalGoldAccumulated,
        max(o.mostGoldSpent, mostGoldSpent), o.totalGoldSpent + totalGoldSpent)
  }

  case class Kills(mostDeaths: Long, totalDeaths: Long,
                   mostKills: Long, totalKills: Long,
                   mostAssists: Long, totalAssists: Long,
                   mostDoubleKills: Long, totalDoubleKills: Long,
                   mostInhibitorKills: Long, totalInhibitorKills: Long,
                   mostKillingSprees: Long, totalKillingSprees: Long,
                   mostKillingSpree: Long, totalKillingSpree: Long,
                   mostMultiKill: Long, totalMultiKills: Long,
                   mostMinionsKilled: Long, totalMinionsKilled: Long,
                   mostTowerKills: Long, totalTowerKills: Long,
                   mostTripleKills: Long, totalTripleKills: Long) {
    def +(o: Kills): Kills =
      Kills(
        max(o.mostDeaths, mostDeaths), o.totalDeaths + totalDeaths,
        max(o.mostKills, mostKills), o.totalKills + totalKills,
        max(o.mostAssists, mostAssists), o.totalAssists + totalAssists,
        max(o.mostDoubleKills, mostDoubleKills), o.totalDoubleKills + totalDoubleKills,
        max(o.mostInhibitorKills, mostInhibitorKills), o.totalInhibitorKills + totalInhibitorKills,
        max(o.mostKillingSprees, mostKillingSprees), o.totalKillingSprees + totalKillingSprees,
        max(o.mostKillingSpree, mostKillingSpree), o.totalKillingSpree + totalKillingSpree,
        max(o.mostMultiKill, mostMultiKill), o.totalMultiKills + totalMultiKills,
        max(o.mostMinionsKilled, mostMinionsKilled), o.totalMinionsKilled + totalMinionsKilled,
        max(o.mostTowerKills, mostTowerKills), o.totalTowerKills + totalTowerKills,
        max(o.mostTripleKills, mostTripleKills), o.totalTripleKills + totalTripleKills)
  }

  case class Damage(mostCriticalStrikeDamage: Long, totalCriticalStrikeDamage: Long,
                    mostDamageDealt: Long, totalDamageDealt: Long,
                    mostDamageDealtToChampions: Long, totalDamageDealtToChampions: Long,
                    mostDamageTaken: Long, totalDamageTaken: Long,
                    magic: Magic, physical: Physical, heal: Heal) {
    def +(o: Damage): Damage =
      Damage(
        max(o.mostCriticalStrikeDamage, mostCriticalStrikeDamage), o.totalCriticalStrikeDamage + totalCriticalStrikeDamage,
        max(o.mostDamageDealt, mostDamageDealt), o.totalDamageDealt + totalDamageDealt,
        max(o.mostDamageDealtToChampions, mostDamageDealtToChampions), o.totalDamageDealtToChampions + totalDamageDealtToChampions,
        max(o.mostDamageTaken, mostDamageTaken), o.totalDamageTaken + totalDamageTaken,
        o.magic + magic, o.physical + physical, o.heal + heal)
  }

  case class Magic(mostMagicDamageDealt: Long, totalMagicDamageDealt: Long,
                   mostMagicDamageDealtToChampions: Long, totalMagicDamageDealtToChampions: Long,
                   mostMagicDamageTaken: Long, totalMagicDamageTaken: Long) {
    def +(o :Magic): Magic =
      Magic(
        max(o.mostMagicDamageDealt, mostMagicDamageDealt), o.totalMagicDamageDealt + totalMagicDamageDealt,
        max(o.mostMagicDamageDealtToChampions, mostMagicDamageDealtToChampions), o.totalMagicDamageDealtToChampions + totalMagicDamageDealtToChampions,
        max(o.mostMagicDamageTaken, mostMagicDamageTaken), o.totalMagicDamageTaken + totalMagicDamageTaken)
  }

  case class Physical(mostPhysicalDamageDealt: Long, totalPhysicalDamageDealt: Long,
                      mostPhysicalDamageDealtToChampions: Long, totalPhysicalDamageDealtToChampions: Long,
                      mostphysicalDamageTaken: Long, totalphysicalDamageTaken: Long) {
    def +(o: Physical): Physical =
      Physical(
        max(o.mostPhysicalDamageDealt, mostPhysicalDamageDealt), o.totalPhysicalDamageDealt + totalPhysicalDamageDealt,
        max(o.mostPhysicalDamageDealtToChampions, mostPhysicalDamageDealtToChampions), o.totalPhysicalDamageDealtToChampions + totalPhysicalDamageDealtToChampions,
        max(o.mostphysicalDamageTaken, mostphysicalDamageTaken), o.totalphysicalDamageTaken + totalphysicalDamageTaken)
  }

  case class Heal(mostHealing: Long, totalHealing: Long, mostUnitsHealed: Long, totalUnitsHealed: Long) {
    def +(o: Heal): Heal =
      Heal(
        max(o.mostHealing, mostHealing), o.totalHealing + totalHealing,
        max(o.mostUnitsHealed, mostUnitsHealed), o.totalUnitsHealed + totalUnitsHealed)
  }
  
  implicit object StatsMonoid extends Monoid[Stats] {
    override def zero: Stats = Stats(
      0L, 0L, 0L,
      Gold(0L, 0L, 0L, 0L),
      Kills(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
      Damage(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L,
        Magic(0L, 0L, 0L, 0L, 0L, 0L),
        Physical(0L, 0L, 0L, 0L, 0L, 0L),
        Heal(0L, 0L, 0L, 0L)))

    override def append(f1: Stats, f2: => Stats): Stats = f1 + f2
  }

  implicit val healFormat = Json.format[Heal]

  implicit val physicalFormat = Json.format[Physical]

  implicit val magicFormat = Json.format[Magic]

  implicit val damageFormat = Json.format[Damage]

  implicit val killsFormat = Json.format[Kills]

  implicit val goldFormat = Json.format[Gold]

  implicit val statFormat = Json.format[Stats]
}