package org.jetbrains.plugins.scala.lang.parser.parsing.expressions

import org.jetbrains.plugins.scala.ScalaBundle
import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.parser.ScalaElementTypes
import org.jetbrains.plugins.scala.lang.parser.parsing.builder.ScalaPsiBuilder
import org.jetbrains.plugins.scala.lang.parser.parsing.types.Type

/**
* @author Alexander Podkhalyuzin
* Date: 03.03.2008
*/
object Ascription extends Ascription {
  override protected def annotation = Annotation
  override protected def `type` = Type
}

trait Ascription {
  protected def annotation: Annotation
  protected def `type`: Type

  def parse(builder: ScalaPsiBuilder): Boolean = {
    val ascriptionMarker = builder.mark
    builder.getTokenType match {
      case ScalaTokenTypes.tCOLON =>
        builder.advanceLexer() //Ate :
      case _ =>
        ascriptionMarker.drop()
        return false
    }
    builder.getTokenType match {
      case ScalaTokenTypes.tUNDER =>
        val seqArgMarker = builder.mark
        ascriptionMarker.drop()
        builder.advanceLexer() //Ate _
        builder.getTokenText match {
          case "*" =>
            builder.advanceLexer() //Ate *
          case _ =>
            builder error ScalaBundle.message("star.expected")
        }
        seqArgMarker.done(ScalaElementTypes.SEQUENCE_ARG)
        return true
      case _ =>
    }
    if (!`type`.parse(builder)) {
      var x = 0
      val annotationsMarker = builder.mark
      while (annotation.parse(builder)) {
        x = x + 1
      }
      annotationsMarker.done(ScalaElementTypes.ANNOTATIONS)
      if (x == 0) builder error ScalaBundle.message("annotation.expected")
    }
    ascriptionMarker.drop()
    true
  }
}