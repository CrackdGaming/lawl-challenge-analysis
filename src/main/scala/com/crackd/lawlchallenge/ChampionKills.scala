package com.crackd.lawlchallenge

import com.crackd.lawlchallenge.gameconstants.championKill
import com.crackd.lawlchallenge.gametypes._
import com.crackd.lawlchallenge.helpers.GameDataFinders.richGameData
import play.api.libs.json._

import scala.collection.mutable.ListBuffer

/**
*  Created by trent ahrens on 4/9/15.
*/
object ChampionKills {
  def apply() = new ChampionKills()
}

class ChampionKills {
  type Killer = ChampionId
  type Victim = ChampionId
  type Kills = Long
  type Assists = Long

  case class CKills(kr: Killer, v: Victim, ks: Kills = 0, a: Assists = 0)

  def apply(json: JsValue): JsValue = Json.toJson(reducer(json.events.withType(championKill).map(toCKillsSeq(json)).flatten).sortBy(ck => (ck.kr, ck.v)))

  def toCKillsSeq(json: JsValue): JsValue => Seq[CKills] = j => {
    val s = ListBuffer.empty[CKills]
    val victim = json.championIdForParticipantId((j \ "victimId").as[ParticipantId])
    s += CKills(json.championIdForParticipantId((j \ "killerId").as[ParticipantId]), victim, 1, 0)
    s ++= (j \ "assistingParticipantIds").asOpt[JsArray]
      .map(_.value.map(_.as[ParticipantId]).map(ai => json.championIdForParticipantId(ai)).map(CKills(_, victim, 0, 1)))
      .getOrElse(Seq.empty)
  }

  def reducer: Seq[CKills] => Seq[CKills] = s => s.groupBy(ck => (ck.kr, ck.v)).foldRight(Seq.empty[CKills]) {
    case (((kr, v), ckills), a) => a :+ ckills.foldLeft(CKills(kr, v, 0, 0))((k, a) => CKills(kr, v, k.ks + a.ks, k.a + a.a))
  }

  implicit val cKillsWrites = new Writes[CKills] {
    override def writes(o: CKills): JsValue = Json.obj(
      "killer" -> o.kr,
      "victim" -> o.v,
      "kills" -> o.ks,
      "assists" -> o.a
    )
  }
}
