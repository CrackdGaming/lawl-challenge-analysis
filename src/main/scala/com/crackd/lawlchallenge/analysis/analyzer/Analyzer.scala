package com.crackd.lawlchallenge.analysis.analyzer

import play.api.libs.json.JsValue

/**
 * Created by trent ahrens on 4/9/15.
 */
trait Analyzer[A] {
  def apply(json: JsValue): A
}
