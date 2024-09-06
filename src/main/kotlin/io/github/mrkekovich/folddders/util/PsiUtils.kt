package io.github.mrkekovich.folddders.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement

fun PsiElement.findModule(): Module? = ModuleUtilCore.findModuleForPsiElement(this)
