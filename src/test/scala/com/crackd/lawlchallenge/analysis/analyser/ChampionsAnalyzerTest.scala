package com.crackd.lawlchallenge.analysis.analyser

import com.crackd.lawlchallenge.analysis.analyzer.ChampionAnalyzer
import com.crackd.test.{JsonLoader, UnitSpec}
import play.api.libs.json.Json

/**
 * Created by trent ahrens on 4/11/15.
 */
class ChampionsAnalyzerTest extends UnitSpec {
  it should "generate correct result" in {
    val input = JsonLoader.load("/champions-input.json")
    val sut = new ChampionAnalyzer()
    val actual = Json.toJson(sut(input))
    val expected = JsonLoader.load("/champions-output.json")
    actual shouldBe expected
  }
}
