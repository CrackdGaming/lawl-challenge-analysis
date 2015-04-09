package com.crackd.lawlchallenge

import akka.util.ByteString
import com.crackd.test.JsonLoader
import org.scalatest.{Matchers, FlatSpec}

/**
*  Created by trent ahrens on 4/9/15.
*/
class ChampionDeathsTest extends FlatSpec with Matchers {
  it should "generate correct json result" in {
    val input = JsonLoader.load("/championdeaths-input.json")
    val sut = ChampionDeaths()
    val actual = sut(input)
    val expected = JsonLoader.load("/championdeaths-output.json")
    actual shouldBe expected
    ByteString
  }
}
