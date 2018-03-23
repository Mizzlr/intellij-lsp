package com.github.mizzlr.lsp.actions

import com.github.mizzlr.lsp.editor.EditorEventManager
import com.intellij.codeInsight.documentation.actions.ShowQuickDocInfoAction
import com.intellij.lang.LanguageDocumentation
import com.intellij.openapi.actionSystem.{AnActionEvent, CommonDataKeys}
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiManager

/**
  * Action overriding QuickDoc (CTRL+Q)
  */
class LSPQuickDocAction extends ShowQuickDocInfoAction with DumbAware {
  private val LOG: Logger = Logger.getInstance(classOf[LSPQuickDocAction])

  override def actionPerformed(e: AnActionEvent): Unit = {
    val editor = e.getData(CommonDataKeys.EDITOR)
    val file = FileDocumentManager.getInstance().getFile(editor.getDocument)
    val language = PsiManager.getInstance(editor.getProject).findFile(file).getLanguage
    if (LanguageDocumentation.INSTANCE.allForLanguage(language).isEmpty) {
      EditorEventManager.forEditor(editor) match {
        case Some(manager) => manager.quickDoc(editor)
        case None => super.actionPerformed(e)
      }
    } else super.actionPerformed(e)
  }
}
