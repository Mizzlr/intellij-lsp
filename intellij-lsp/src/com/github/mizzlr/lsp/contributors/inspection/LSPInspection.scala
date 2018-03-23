package com.github.mizzlr.lsp.contributors.inspection

import javax.swing.JComponent

import com.github.mizzlr.lsp.PluginMain
import com.github.mizzlr.lsp.contributors.LSPQuickFix
import com.github.mizzlr.lsp.contributors.psi.LSPPsiElement
import com.github.mizzlr.lsp.editor.{DiagnosticRangeHighlighter, EditorEventManager}
import com.github.mizzlr.lsp.diagnostics.DiagnosticsManager
import com.github.mizzlr.lsp.utils.FileUtils
import com.intellij.codeInspection._
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.Diagnostic

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.Editor
import com.github.mizzlr.lsp.utils.DocumentUtils
import com.intellij.openapi.editor.impl.DocumentImpl

import com.intellij.openapi.diagnostic.Logger
import java.lang.CharSequence

/**
  * The inspection tool for LSP
  */
class LSPInspection extends LocalInspectionTool {
  private val LOG: Logger = Logger.getInstance(classOf[LSPInspection])
  override def checkFile(psiFile: PsiFile, inspectionManager: InspectionManager, isOnTheFly: Boolean): Array[ProblemDescriptor] = {
    val virtualFile = psiFile.getVirtualFile
    // LOG.info("LSP inspection tool triggered for file: " + psiFile)
    if (PluginMain.isExtensionSupported(virtualFile.getExtension)) {
      val uri = FileUtils.VFSToURI(virtualFile)
      // LOG.info("Fetching diagnostics for file: " + uri)

    def publishDiagnostics(diagnostics: Iterable[Diagnostic]): Array[ProblemDescriptor] = {
        // LOG.info("Publishing diagnostics: " + diagnostics)
        // val document: Document = PsiDocumentManager.getInstance(psiFile.getProject).getDocument(psiFile)
        val document: Document = new DocumentImpl(psiFile.getText.asInstanceOf[CharSequence], true)
        // val editor: Editor = EditorFactory.getInstance.createViewer(document)
        diagnostics.collect { case diagnostic =>
            val start = DocumentUtils.LSPPosToOffset(document, diagnostic.getRange.getStart)
            val end = DocumentUtils.LSPPosToOffset(document, diagnostic.getRange.getEnd)
            if (start < end) {
                val name = document.getText(new TextRange(start, end))
                val severity = diagnostic.getSeverity match {
                    case DiagnosticSeverity.Error => ProblemHighlightType.ERROR
                    case DiagnosticSeverity.Warning => ProblemHighlightType.GENERIC_ERROR_OR_WARNING
                    case DiagnosticSeverity.Information => ProblemHighlightType.INFORMATION
                    case DiagnosticSeverity.Hint => ProblemHighlightType.INFORMATION
                    case _ => null
                }
                val element = LSPPsiElement(name, psiFile.getProject, start, end, psiFile)
                inspectionManager.createProblemDescriptor(element, null.asInstanceOf[TextRange], diagnostic.getMessage, severity, isOnTheFly, null)
          } else null
       }.toArray.filter(d => d != null)
    }

      DiagnosticsManager.fetchDiagnostics(uri) match {
        case Some(diagnostics) =>
          publishDiagnostics(diagnostics)
        case None =>
            super.checkFile(psiFile, inspectionManager, isOnTheFly)
        }
    } else super.checkFile(psiFile, inspectionManager, isOnTheFly)
  }

  override def getDisplayName: String = getShortName

  override def createOptionsPanel(): JComponent = {
    new LSPInspectionPanel(getShortName, this)
  }

  override def getShortName: String = "LSP"

  override def getID: String = "LSP"

  override def getGroupDisplayName: String = "LSP"

  override def getStaticDescription: String = "Reports errors by the Language Server server"

  override def isEnabledByDefault: Boolean = true

}
