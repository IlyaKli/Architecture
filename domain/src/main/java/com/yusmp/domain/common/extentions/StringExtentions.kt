package com.yusmp.domain.common.extentions

fun String.replaceParagraphs(): String = this.replace("¶¶", "\n")
