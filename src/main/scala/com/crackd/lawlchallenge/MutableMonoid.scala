package com.crackd.lawlchallenge

import scalaz.Monoid

/**
 * Created by trent ahrens on 4/14/15.
 */
trait MutableMonoid[T] extends Monoid[T]{
  def +=(left: T, right: T): T
}
