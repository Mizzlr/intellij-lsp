package com.github.mizzlr.lsp.rust

import com.github.mizzlr.lsp.client.languageserver.serverdefinition.{ExeLanguageServerDefinition, LanguageServerDefinition}
import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.progress.ProgressIndicator

class RustPreloadingActivity extends PreloadingActivity {

  override def preload(indicator: ProgressIndicator): Unit = {
    //Assume rls is on Path
    LanguageServerDefinition.register(new ExeLanguageServerDefinition("rs", "rls", Array()))
  }
}
