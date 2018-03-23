package com.github.gtache.lsp.diagnostics

import scala.collection.mutable
import org.eclipse.lsp4j.Diagnostic
import com.intellij.openapi.diagnostic.Logger

object DiagnosticsManager {
    private val LOG: Logger = Logger.getInstance("#com.github.gtache.lsp.diagnostics.DiagnosticsManager")
    private val uriToDiagnostics: mutable.Map[String, Iterable[Diagnostic]] = mutable.HashMap()

    def updateDiagnostics(uri: String, diagnostics: Iterable[Diagnostic]): Unit = {
        // LOG.info("Updating diagnostics uri: " + uri + " diagnostics: " + diagnostics)
        uriToDiagnostics.put(uri, diagnostics)
    }

    def fetchDiagnostics(uri: String): Option[Iterable[Diagnostic]] = {
        // LOG.info("Fetching diagnostics uri: " + uri)
        val diagnostics = uriToDiagnostics.get(uri)
        // LOG.info("Fetched diagnostics uri: " + uri + " diagnostics: " + diagnostics)
        return diagnostics
    }
}
