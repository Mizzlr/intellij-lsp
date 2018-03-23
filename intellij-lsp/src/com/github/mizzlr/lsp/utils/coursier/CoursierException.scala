package com.github.mizzlr.lsp.utils.coursier

import com.github.mizzlr.lsp.utils.LSPException

case class CoursierException(msg: String) extends LSPException(msg) {

}
