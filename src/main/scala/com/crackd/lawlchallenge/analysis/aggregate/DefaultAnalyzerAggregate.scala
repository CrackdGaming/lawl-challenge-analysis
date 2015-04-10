package com.crackd.lawlchallenge.analysis.aggregate

import com.crackd.lawlchallenge.analysis.analyzer.Analyzer
import play.api.libs.json._

import scalaz.Monoid

/**
 * Created by trent ahrens on 4/10/15.
 */
class DefaultAnalyzerAggregate[T: Format](a: Analyzer[T])(implicit m: Monoid[T]) extends AnalyzerAggregate{
  var aggregate: T = m.zero

  override def apply(json: JsValue): Unit = aggregate = m.append(a(json), aggregate)

  override def capture: JsValue = Json.toJson(aggregate)

  override def restore(json: JsValue): Unit = aggregate = json.as[T]
}
