package com.crackd.lawlchallenge.analysis.accumulate

import play.api.libs.json._

/**
 * Created by trent ahrens on 4/10/15.
 */
trait AnalysisAccumulator {
  def name: String = getClass.getCanonicalName

  def apply(json: JsValue): Unit

  def capture: JsValue

  def restore(json: JsValue)
}


