package com.github.mizzlr.lsp.utils

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.{Editor, LogicalPosition, Document}
import com.intellij.openapi.util.TextRange
import org.eclipse.lsp4j.Position

/**
  * Various methods to convert offsets / logical position / server position
  */
object DocumentUtils {

  private val LOG: Logger = Logger.getInstance(this.getClass)

  /**
    * Gets the line at the given offset given an editor and bolds the text between the given offsets
    *
    * @param editor      The editor
    * @param startOffset The starting offset
    * @param endOffset   The ending offset
    * @return The document line
    */
  def getLineText(editor: Editor, startOffset: Int, endOffset: Int): String = {
    val doc = editor.getDocument
    val lineIdx = doc.getLineNumber(startOffset)
    val lineStartOff = doc.getLineStartOffset(lineIdx)
    val lineEndOff = doc.getLineEndOffset(lineIdx)
    val line = doc.getText(new TextRange(lineStartOff, lineEndOff))
    val startOffsetInLine = startOffset - lineStartOff
    val endOffsetInLine = endOffset - lineStartOff
    line.substring(0, startOffsetInLine) + "<b>" + line.substring(startOffsetInLine, endOffsetInLine) + "</b>" + line.substring(endOffsetInLine)
  }

  /**
    * Calculates a Position given an editor and an offset
    *
    * @param editor The editor
    * @param offset The offset
    * @return an LSP position
    */
  def offsetToLSPPos(editor: Editor, offset: Int): Position = {
    logicalToLSPPos(editor.offsetToLogicalPosition(offset))
  }

  /**
    * Transforms a LogicalPosition (IntelliJ) to an LSP Position
    *
    * @param position the LogicalPosition
    * @return the Position
    */
  def logicalToLSPPos(position: LogicalPosition): Position = {
    new Position(position.line, position.column)
  }

  /**
    * Transforms an LSP position to an editor offset
    *
    * @param editor The editor
    * @param pos    The LSPPos
    * @return The offset
    */
  def LSPPosToOffset(editor: Editor, pos: Position): Int = {
    val offset = editor.logicalPositionToOffset(LSPToLogicalPos(pos))
    val docLength = editor.getDocument.getTextLength
    if (offset > docLength) {
      LOG.warn("Offset greater than text length : " + offset + " > " + docLength)
    }
    math.min(math.max(offset, 0), docLength)
  }

  def LSPPosToOffset(document: Document, pos: Position): Int = {
    val offset = logicalPositionToOffset(document, LSPToLogicalPos(pos))
    val docLength = document.getTextLength
    if (offset > docLength) {
      LOG.warn("Offset greater than text length : " + offset + " > " + docLength)
    }
    math.min(math.max(offset, 0), docLength)
  }

  /**
    * Transforms an LSP position to a LogicalPosition
    *
    * @param position The LSPPos
    * @return The LogicalPos
    */
  def LSPToLogicalPos(position: Position): LogicalPosition = {
    new LogicalPosition(position.getLine, position.getCharacter)
  }

  def logicalPositionToOffset(document: Document, pos: LogicalPosition): Int = {
      if (document.getLineCount() == 0) return 0
      if (pos.line < 0) throw new IndexOutOfBoundsException("Wrong line: " + pos.line)
      if (pos.column < 0) throw new IndexOutOfBoundsException("Wrong column:" + pos.column)
      if (pos.line >= document.getLineCount) return document.getTextLength
      val start = document.getLineStartOffset(pos.line)
      return start + pos.column
  }
}
