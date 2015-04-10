package com.crackd.lawlchallenge.analyser

import com.crackd.test.{JsonLoader, UnitSpec}
import play.api.libs.json.Json

/**
*  Created by trent ahrens on 4/9/15.
*/
class HeatMapAnalysisTest extends UnitSpec {
  it should "generate correct result" in {
    val input = JsonLoader.load("/heatmap-input.json")
    val sut = HeatMapAnalysis()
    val actual = Json.toJson(sut(input))
    val expected = JsonLoader.load("/heatmap-output.json")
    actual shouldBe expected
  }

  it should "skip event types with no position" in {
    val input = JsonLoader.load("/heatmap-input-noposition.json")
    val sut = HeatMapAnalysis()
    val actual = Json.toJson(sut(input))
    val expected = Json.toJson(List.empty[String])
    actual shouldBe expected
  }
}
