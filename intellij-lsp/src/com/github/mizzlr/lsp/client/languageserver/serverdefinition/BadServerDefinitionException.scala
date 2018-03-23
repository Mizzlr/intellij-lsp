package com.github.mizzlr.lsp.client.languageserver.serverdefinition

import com.github.mizzlr.lsp.utils.LSPException

case class BadServerDefinitionException(m: String) extends LSPException(m) {

}
