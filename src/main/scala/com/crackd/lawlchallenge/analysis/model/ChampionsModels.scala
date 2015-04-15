package com.crackd.lawlchallenge.analysis.model

import com.crackd.lawlchallenge.MutableMonoid
import com.crackd.lawlchallenge.analysis.model.StatModels.{Stats, StatsMonoid}
import com.crackd.lawlchallenge.gametypes.ChampionId
import play.api.libs.json._

import scala.collection.mutable
import scala.language.postfixOps

/**
 * Created by trent ahrens on 4/11/15.
 */
object ChampionsModels {
  case class Champions(m: mutable.Map[ChampionId,Champion]) {
    def +(id: ChampionId, c: Champion): Champions = Champions(m + (id -> (m.getOrElse(id, Champion(0, 0, StatsMonoid.zero)) + c)))
    def +(o: Champions): Champions = m.foldLeft(o) { case (a,(id, c)) => a + (id,c) }
    def +=(id: ChampionId, c: Champion): Champions =  {
      m += (id -> (m.getOrElse(id, Champion(0, 0, StatsMonoid.zero)) + c))
      this
    }
    def +=(o: Champions): Champions = o.m.foldLeft(this) { case (a,(id, c)) => a += (id,c) }
  }

  case class Champion(totalWins: Long, timesPicked: Long, stat: Stats) {
    def +(o: Champion): Champion = Champion(o.totalWins + totalWins, o.timesPicked + timesPicked, o.stat + stat)
  }

  implicit object ChampionsMonoid extends MutableMonoid[Champions] {
    override def zero: Champions = Champions(mutable.Map.empty)

    override def append(f1: Champions, f2: => Champions): Champions = f1 + f2

    override def +=(left: Champions, right: Champions): Champions = left += right
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
    override def reads(json: JsValue): JsResult[Champions] = JsSuccess(Champions(mutable.Map(json.as[Seq[(ChampionId,Champion)]]: _*)))

    override def writes(o: Champions): JsValue = Json.toJson(o.m.toSeq.sortBy(t => t._1))
  }
}
