package com.crackd.lawlchallenge.abstraction

import java.nio.file.Path

/**
  * Created by trent ahrens on 4/10/15.
  */
trait FileService {
  def readAllText(p: Path): String
  def delete(p: Path): Unit
  def move(source: Path, target: Path)
}
