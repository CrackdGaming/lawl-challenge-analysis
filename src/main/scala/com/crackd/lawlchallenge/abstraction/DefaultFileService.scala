package com.crackd.lawlchallenge.abstraction

import java.nio.file.{Files, Path}

import org.apache.commons.io.FileUtils

/**
 * Created by trent ahrens on 4/10/15.
 */
class DefaultFileService extends FileService {
  override def readAllText(p: Path): String = FileUtils.readFileToString(p.toFile)

  override def move(source: Path, target: Path): Unit = Files.move(source, target)

  override def writeAllText(p: Path, data: String): Unit = FileUtils.writeStringToFile(p.toFile, data)

  override def delete(p: Path): Unit = Files.delete(p)
}
