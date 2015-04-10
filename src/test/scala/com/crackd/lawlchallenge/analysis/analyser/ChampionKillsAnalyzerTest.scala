package com.crackd.lawlchallenge.analysis.analyser

import com.crackd.lawlchallenge.analysis.analyzer.ChampionKillsAnalyzer
import com.crackd.test.{JsonLoader, UnitSpec}
import play.api.libs.json.Json

/**
*  Created by trent ahrens on 4/9/15.
*/
class ChampionKillsAnalyzerTest extends UnitSpec {
  it should "generate correct result" in {
    val input = JsonLoader.load("/championdeaths-input.json")
    val sut = ChampionKillsAnalyzer()
    val actual = Json.toJson(sut(input))
    val expected = JsonLoader.load("/championdeaths-output.json")
    actual shouldBe expected
  }
}
