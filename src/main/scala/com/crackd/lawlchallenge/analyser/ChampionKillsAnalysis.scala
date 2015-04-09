package com.crackd.lawlchallenge.analyser

import com.crackd.lawlchallenge.Statistic
import com.crackd.lawlchallenge.analyser.ChampionKillsAnalysis._
import com.crackd.lawlchallenge.gameconstants.championKill
import com.crackd.lawlchallenge.gametypes._
import com.crackd.lawlchallenge.helpers.GameDataFinders.richGameData
import play.api.libs.json._

/**
*  Created by trent ahrens on 4/9/15.
*/
object ChampionKillsAnalysis {
  def apply() = new ChampionKillsAnalysis()

  type Killer = ChampionId
  type Victim = ChampionId
  type Kills = Long
  type Assists = Long

  object ChampionKills {
    def empty = new ChampionKills(Map.empty)
  }

  case class ChampionKills(m: Map[(Killer,Victim),(Kills,Assists)]) extends Statistic[ChampionKills] {
    def +(kr: Killer, v: Victim, ks: Kills, a: Assists) =
      ChampionKills(m + ((kr, v) -> (m.getOrElse((kr, v), (0L, 0L)) match { case (oks, oa) => (oks + ks, oa + a) })))

    override def +(o: ChampionKills): ChampionKills = m.foldRight(o) {
      case (((kr,v),(ks,a)),ck) => ck + (kr, v, ks, a)
    }
  }

  implicit val championKillsPairWrites = new Writes[((Killer,Victim),(Kills,Assists))] {
    override def writes(o: ((Killer, Victim), (Kills, Assists))): JsValue = Json.obj(
      "killer" -> o._1._1,
      "victim" -> o._1._2,
      "kills" -> o._2._1,
      "assists" -> o._2._2
    )
  }

  implicit val championKillsWrites = new Writes[ChampionKills] {
    override def writes(o: ChampionKills): JsValue = Json.toJson(o.m.toSeq.sortBy(p => (p._1._1,p._1._2)))
  }
}

class ChampionKillsAnalysis extends Analyser[ChampionKills] {
  def apply(json: JsValue): ChampionKills = kills(json)(events(json)) + assists(json)(events(json))

  def events(json: JsValue): Seq[JsValue] = json.events.withType(championKill)

  def kills(json: JsValue)(e: Seq[JsValue]): ChampionKills = e.foldRight(ChampionKills.empty)((j,a) =>
    a + (killerChampion(json)(j), victimChampion(json)(j), 1, 0)
  )

  def assists(json: JsValue)(e: Seq[JsValue]): ChampionKills = e.foldRight(ChampionKills.empty)((j,a) => {
    a + assistChampions(json)(j)
      .foldRight(ChampionKills.empty)((assist,aa) => aa + (assist, victimChampion(json)(j), 0, 1))
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
