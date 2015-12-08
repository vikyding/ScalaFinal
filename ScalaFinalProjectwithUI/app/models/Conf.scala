package models

import play.api.libs.json.Json

/**
  * Created by mengchending on 12/3/15.
  */
case class FileName(name:String)

object FileName{
  implicit  val personFormat =Json.format[FileName]
}

case class FilePath(name:String)

object FilePath{
  implicit  val fileFormat =Json.format[FilePath]
}

