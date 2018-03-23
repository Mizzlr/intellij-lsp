package com.github.gtache.lsp.contributors.inspection

import com.intellij.codeInspection.InspectionToolProvider
import com.intellij.openapi.diagnostic.Logger

/**
  * The provider for the LSP Inspection
  * Returns a single class, LSPInspection
  */
class LSPInspectionProvider extends InspectionToolProvider {
  private val LOG: Logger = Logger.getInstance(classOf[LSPInspectionProvider])

  override def getInspectionClasses: Array[Class[_]] = {
    LOG.info("Inspection class provided: " + classOf[LSPInspection])
    Array(classOf[LSPInspection])
  }
}
