package com.crackd.lawlchallenge.analysis.aggregate

import com.crackd.lawlchallenge.analysis.analyzer.Analyzer
import com.crackd.test.UnitSpec
import play.api.libs.json.{JsNull, JsValue, Json}

import scalaz.Monoid

/**
 * Created by trent ahrens on 4/10/15.
 */
class DefaultAnalyzerAggregateTest extends UnitSpec {

  case class Atom(size: Int)

  implicit object AtomMonoid extends Monoid[Atom] {
    override def zero: Atom = Atom(0)

    override def append(f1: Atom, f2: => Atom): Atom = Atom(f1.size + f2.size)
  }

  implicit val atomFormat = Json.format[Atom]

  class AtomAnalyzer extends Analyzer[Atom] {
    override def apply(json: JsValue): Atom = Atom(1)
  }

  val sut = new DefaultAnalyzerAggregate(new AtomAnalyzer)

  it should "start off with an aggregate identity" in {
    sut.aggregate shouldBe AtomMonoid.zero
  }

  it should "aggregate its data across analysis invocations" in {
    sut.apply(JsNull)
    sut.apply(JsNull)
    sut.apply(JsNull)
    sut.aggregate shouldBe Atom(3)
  }

  it should "return a json formatted aggregate" in {
    val actual = sut.capture
    actual shouldBe Json.obj("size" -> 3)
  }

  it should "restore an aggregate when provided json" in {
    sut.restore(Json.obj("size" -> 5))
    sut.aggregate shouldBe Atom(5)
  }
}
