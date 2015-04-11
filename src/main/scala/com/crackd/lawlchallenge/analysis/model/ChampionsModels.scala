package com.crackd.lawlchallenge.analysis.model

import com.crackd.lawlchallenge.analysis.model.StatModels.{StatsMonoid, Stats}
import com.crackd.lawlchallenge.gametypes.ChampionId
import play.api.libs.json._

import scala.language.postfixOps
import scalaz.Monoid

/**
 * Created by trent ahrens on 4/11/15.
 */
object ChampionsModels {
  case class Champions(m: Map[ChampionId,Champion]) {
    def +(id: ChampionId, c: Champion): Champions = Champions(m + (id -> (m.getOrElse(id, Champion(0, StatsMonoid.zero)) + c)))
    def +(o: Champions): Champions = m.foldRight(o) { case ((id, c), a) => a + (id,c) }
  }

  case class Champion(timesPicked: Long, stat: Stats) {
    def +(o: Champion): Champion = Champion(o.timesPicked + timesPicked, o.stat + stat)
  }

  implicit object ChampionsMonoid extends Monoid[Champions] {
    override def zero: Champions = Champions(Map.empty)

    override def append(f1: Champions, f2: => Champions): Champions = f1 + f2
  }

  implicit val championFormat = Json.format[Champion]

  implicit val championsPairFormat = new Format[(ChampionId,Champion)] {
    override def reads(json: JsValue): JsResult[(ChampionId, Champion)] = JsSuccess((
      (json \ "championId").as[Int],
      (json \ "data").as[Champion]
    ))

    override def writes(o: (ChampionId, Champion)): JsValue = Json.obj(
      "championId" -> o._1,
      "data" -> Json.toJson(o._2)
    )
  }

  implicit val championsFormat = new Format[Champions] {
    override def reads(json: JsValue): JsResult[Champions] = JsSuccess(Champions(json.as[Seq[(ChampionId,Champion)]].toMap))

    override def writes(o: Champions): JsValue = Json.toJson(o.m.toSeq.sortBy(t => t._1))
  }
}
