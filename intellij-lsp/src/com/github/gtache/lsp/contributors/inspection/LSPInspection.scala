package com.github.gtache.lsp.contributors.inspection

import javax.swing.JComponent

import com.github.gtache.lsp.PluginMain
import com.github.gtache.lsp.contributors.LSPQuickFix
import com.github.gtache.lsp.contributors.psi.LSPPsiElement
import com.github.gtache.lsp.editor.{DiagnosticRangeHighlighter, EditorEventManager}
import com.github.gtache.lsp.diagnostics.DiagnosticsManager
import com.github.gtache.lsp.utils.FileUtils
import com.intellij.codeInspection._
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.Diagnostic

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.Editor
import com.github.gtache.lsp.utils.DocumentUtils

/**
  * The inspection tool for LSP
  */
class LSPInspection extends LocalInspectionTool {

  override def checkFile(psiFile: PsiFile, inspectionManager: InspectionManager, isOnTheFly: Boolean): Array[ProblemDescriptor] = {
    val virtualFile = psiFile.getVirtualFile
    if (PluginMain.isExtensionSupported(virtualFile.getExtension)) {
      val uri = FileUtils.VFSToURI(virtualFile)

        def descriptorsForManager(diagnostics: Iterable[Diagnostic]): Array[ProblemDescriptor] = {
            val document: Document = PsiDocumentManager.getInstance(psiFile.getProject).getDocument(psiFile)
            val editor: Editor = EditorFactory.getInstance.createViewer(document)
            diagnostics.collect { case diagnostic =>
                val start = DocumentUtils.LSPPosToOffset(editor, diagnostic.getRange.getStart)
                val end = DocumentUtils.LSPPosToOffset(editor, diagnostic.getRange.getEnd)
                if (start < end) {
                    val name = editor.getDocument.getText(new TextRange(start, end))
                    val severity = diagnostic.getSeverity match {
                        case DiagnosticSeverity.Error => ProblemHighlightType.ERROR
                        case DiagnosticSeverity.Warning => ProblemHighlightType.GENERIC_ERROR_OR_WARNING
                        case DiagnosticSeverity.Information => ProblemHighlightType.INFORMATION
                        case DiagnosticSeverity.Hint => ProblemHighlightType.INFORMATION
                        case _ => null
                    }
                    val element = LSPPsiElement(name, editor.getProject, start, end, psiFile)
                    inspectionManager.createProblemDescriptor(element, null.asInstanceOf[TextRange], diagnostic.getMessage, severity, isOnTheFly, null)
              } else null
           }.toArray.filter(d => d != null)
        }

      DiagnosticsManager.fetchDiagnostics(uri) match {
        case Some(m) =>
          descriptorsForManager(m)
        case None =>
          if (isOnTheFly) {
            super.checkFile(psiFile, inspectionManager, isOnTheFly)
          } else {
            super.checkFile(psiFile, inspectionManager, isOnTheFly)
          }
      }
    } else super.checkFile(psiFile, inspectionManager, isOnTheFly)
  }

  override def getDisplayName: String = getShortName

  override def createOptionsPanel(): JComponent = {
    new LSPInspectionPanel(getShortName, this)
  }

  override def getShortName: String = "langserver"

  override def getID: String = "langserver"

  override def getGroupDisplayName: String = "langserver"

  override def getStaticDescription: String = "Reports errors by the LSP server"

  override def isEnabledByDefault: Boolean = true

}

/**
  * Get all the ProblemDescriptor given an EditorEventManager
  * Look at the DiagnosticHighlights, create dummy PsiElement for each, create descriptor using it
  *
  * @param m The inspectionManager
  * @return The ProblemDescriptors
  */
// def descriptorsForManager(m: EditorEventManager): Array[ProblemDescriptor] = {
//   val diagnostics = m.getDiagnostics
//   diagnostics.collect { case DiagnosticRangeHighlighter(rangeHighlighter, diagnostic) =>
//     val start = rangeHighlighter.getStartOffset
//     val end = rangeHighlighter.getEndOffset
//     if (start < end) {
//       val name = m.editor.getDocument.getText(new TextRange(start, end))
//       val severity = diagnostic.getSeverity match {
//         case DiagnosticSeverity.Error => ProblemHighlightType.ERROR
//         case DiagnosticSeverity.Warning => ProblemHighlightType.GENERIC_ERROR_OR_WARNING
//         case DiagnosticSeverity.Information => ProblemHighlightType.INFORMATION
//         case DiagnosticSeverity.Hint => ProblemHighlightType.INFORMATION
//         case _ => null
//       }
//       val element = LSPPsiElement(name, m.editor.getProject, start, end, psiFile)
//       val commands = m.codeAction(element)
//       inspectionManager.createProblemDescriptor(element, null.asInstanceOf[TextRange], diagnostic.getMessage, severity, isOnTheFly,
//         (if (commands != null) commands.map(c => new LSPQuickFix(uri, c)).toArray else null): _*)
//     } else null
//   }.toArray.filter(d => d != null)
// }


/*val descriptor = new OpenFileDescriptor(inspectionManager.getProject, virtualFile)
ApplicationUtils.writeAction(() => FileEditorManager.getInstance(inspectionManager.getProject).openTextEditor(descriptor, false))
EditorEventManager.forUri(uri) match {
  case Some(m) => descriptorsForManager(m)
  case None => super.checkFile(psiFile, inspectionManager, isOnTheFly)
}*/
//TODO need dispatch thread
