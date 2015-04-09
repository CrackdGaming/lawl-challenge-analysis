package com.crackd.lawlchallenge

/**
 * Created by trent ahrens on 4/9/15.
 */
trait Statistic[A] {
  def +(o: A): A
}
