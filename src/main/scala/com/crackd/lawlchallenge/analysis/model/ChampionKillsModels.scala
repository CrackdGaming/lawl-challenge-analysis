package com.crackd.lawlchallenge.analysis.model

import com.crackd.lawlchallenge.gametypes._
import play.api.libs.json._

import scalaz.Monoid

/**
 * Created by trent ahrens on 4/10/15.
 */
object ChampionKillsModels {
  type Killer = ChampionId
  type Victim = ChampionId
  type Kills = Long
  type Assists = Long

  object ChampionKills {
    val empty = new ChampionKills(Map.empty)
  }

  case class ChampionKills(m: Map[(Killer,Victim),(Kills,Assists)]){
    def +(kr: Killer, v: Victim, ks: Kills, a: Assists) =
      ChampionKills(m + ((kr, v) -> (m.getOrElse((kr, v), (0L, 0L)) match { case (oks, oa) => (oks + ks, oa + a) })))
    def +(o: ChampionKills): ChampionKills = m.foldRight(o) {
      case (((kr,v),(ks,a)),ck) => ck + (kr, v, ks, a)
    }
  }

  implicit object ChampionKillsMonoid extends Monoid[ChampionKills] {
    override def zero: ChampionKills = new ChampionKills(Map.empty)

    override def append(f1: ChampionKills, f2: => ChampionKills): ChampionKills = f1 + f2
  }

  implicit val championKillsPairFormat = new Format[((Killer,Victim),(Kills,Assists))] {
    override def writes(o: ((Killer, Victim), (Kills, Assists))): JsValue = Json.obj(
      "killer" -> o._1._1,
      "victim" -> o._1._2,
      "kills" -> o._2._1,
      "assists" -> o._2._2
    )

    override def reads(json: JsValue): JsResult[((Killer, Victim), (Kills, Assists))] = JsSuccess((
      ((json \ "killer").as[Killer],(json \ "victim").as[Victim]),
      ((json \ "kills").as[Kills],(json \ "assists").as[Assists])
      ))
  }

  implicit val championKillsFormat = new Format[ChampionKills] {
    override def writes(o: ChampionKills): JsValue = Json.toJson(o.m.toSeq.sortBy(p => (p._1._1,p._1._2)))

    override def reads(json: JsValue): JsResult[ChampionKills] =
      JsSuccess(ChampionKills(json.as[Seq[((Killer, Victim), (Kills, Assists))]].toMap))
  }
}
