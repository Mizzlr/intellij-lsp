package com.github.gtache.lsp.diagnostics

import scala.collection.mutable
import org.eclipse.lsp4j.Diagnostic

object DiagnosticsManager {
    private val uriToDiagnostics: mutable.Map[String, Iterable[Diagnostic]] = mutable.HashMap()

    def updateDiagnostics(uri: String, diagnostics: Iterable[Diagnostic]): Unit = {
        uriToDiagnostics.put(uri, diagnostics)
    }

    def fetchDiagnostics(uri: String): Option[Iterable[Diagnostic]] = {
        uriToDiagnostics.get(uri)
    }
}
