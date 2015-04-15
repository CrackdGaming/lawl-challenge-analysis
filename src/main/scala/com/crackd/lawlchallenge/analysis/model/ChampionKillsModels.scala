package com.crackd.lawlchallenge.analysis.model

import com.crackd.lawlchallenge.MutableMonoid
import com.crackd.lawlchallenge.gametypes._
import play.api.libs.json._

import scala.collection.mutable

/**
 * Created by trent ahrens on 4/10/15.
 */
object ChampionKillsModels {
  type Killer = ChampionId
  type Victim = ChampionId
  type Kills = Long
  type Assists = Long

  case class ChampionKills(m: mutable.Map[(Killer,Victim),(Kills,Assists)]){
    def +(kr: Killer, v: Victim, ks: Kills, a: Assists) =
      ChampionKills(m + ((kr, v) -> (m.getOrElse((kr, v), (0L, 0L)) match { case (oks, oa) => (oks + ks, oa + a) })))
    def +(o: ChampionKills): ChampionKills = {
      var foldOnto = o
      var map = m
      if (m.size > foldOnto.m.size) {
        foldOnto = this
        map = o.m
      }
      map.foldRight(foldOnto) {
        case (((kr, v), (ks, a)), ck) => ck + (kr, v, ks, a)
      }
    }
    def +=(kr: Killer, v: Victim, ks: Kills, a: Assists): ChampionKills = {
      m += ((kr, v) -> (m.getOrElse((kr, v), (0L, 0L)) match {
        case (oks, oa) => (oks + ks, oa + a)
      }))
      this
    }
    def +=(o: ChampionKills): ChampionKills = o.m.foldRight(this) { case (((kr, v), (ks, a)), ck) => ck += (kr, v, ks, a) }
  }

  implicit object ChampionKillsMonoid extends MutableMonoid[ChampionKills] {
    override def zero: ChampionKills = new ChampionKills(mutable.Map.empty)

    override def append(f1: ChampionKills, f2: => ChampionKills): ChampionKills = f1 + f2

    override def +=(left: ChampionKills, right: ChampionKills): ChampionKills = left += right
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
      JsSuccess(ChampionKills(mutable.Map(json.as[Seq[((Killer, Victim), (Kills, Assists))]]: _*)))
  }
}
