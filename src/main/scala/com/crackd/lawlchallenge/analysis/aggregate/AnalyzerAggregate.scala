package com.crackd.lawlchallenge.analysis.aggregate

import play.api.libs.json._

/**
 * Created by trent ahrens on 4/10/15.
 */
trait AnalyzerAggregate {
  val name: String = getClass.getCanonicalName

  def apply(json: JsValue): Unit

  def capture: JsValue

  def restore(json: JsValue)
}


