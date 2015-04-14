package com.crackd.lawlchallenge.analysis.accumulate

import com.crackd.lawlchallenge.analysis.analyzer.Analyzer
import play.api.libs.json._

import scalaz.Monoid

/**
 * Created by trent ahrens on 4/10/15.
 */
class DefaultAnalysisAccumulator[T: Format](a: Analyzer[T], aggregateName: String = getClass.getCanonicalName)(implicit m: Monoid[T]) extends AnalysisAccumulator{
  var aggregate: T = m.zero

  override def name: String = aggregateName

  override def apply(json: JsValue): Unit = aggregate = m.append(a(json), aggregate)

  override def capture: JsValue = Json.toJson(aggregate)

  override def restore(json: JsValue): Unit = aggregate = json.as[T]
}
