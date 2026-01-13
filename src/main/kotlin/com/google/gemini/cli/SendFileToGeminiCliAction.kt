/*
 * Copyright 2026 thxwelchs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gemini.cli

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.wm.ToolWindowManager
import org.jetbrains.plugins.terminal.ShellTerminalWidget
import java.awt.Component
import java.nio.charset.StandardCharsets
import javax.swing.JComponent

class SendFileToGeminiCliAction : AnAction() {
  private val LOG = Logger.getInstance(SendFileToGeminiCliAction::class.java)

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
    val editor = e.getData(CommonDataKeys.EDITOR)

    // Use relative path for more concise terminal input
    val projectDir = project.guessProjectDir()
    val path = if (projectDir != null) {
      VfsUtilCore.getRelativePath(virtualFile, projectDir) ?: virtualFile.path
    } else {
      virtualFile.path
    }
    
    var textToSend = " @$path"

    if (editor != null && editor.selectionModel.hasSelection()) {
      val document = editor.document
      val selectionModel = editor.selectionModel
      val startLine = document.getLineNumber(selectionModel.selectionStart) + 1
      val endOffset = if (selectionModel.selectionEnd > selectionModel.selectionStart) {
        selectionModel.selectionEnd - 1
      } else {
        selectionModel.selectionEnd
      }
      val endLine = document.getLineNumber(endOffset) + 1

      textToSend += if (startLine == endLine) ":$startLine" else ":$startLine-$endLine"
    }

    try {
      val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Terminal")
      if (toolWindow != null && toolWindow.isVisible) {
        val content = toolWindow.contentManager.selectedContent
        val widget = findTerminalWidget(content?.component)

        if (widget != null) {
          val ttyConnector = widget.ttyConnector
          if (ttyConnector != null) {
            ttyConnector.write(textToSend.toByteArray(StandardCharsets.UTF_8))
          }
        }
      }
    } catch (e: Exception) {
      LOG.error("Failed to inject text into terminal", e)
    }
  }

  private fun findTerminalWidget(component: Component?): ShellTerminalWidget? {
    if (component == null) return null
    if (component is ShellTerminalWidget) return component
    if (component is JComponent) {
      for (child in component.components) {
        val found = findTerminalWidget(child)
        if (found != null) return found
      }
    }
    return null
  }

  override fun update(e: AnActionEvent) {
    val project = e.project
    val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
    
    if (project == null || virtualFile == null) {
      e.presentation.isEnabledAndVisible = false
      return
    }

    e.presentation.isEnabledAndVisible = hasGeminiTerminal(project)
  }

  private fun hasGeminiTerminal(project: Project): Boolean {
    val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Terminal") ?: return false
    if (!toolWindow.isAvailable) return false
    
    val contentManager = toolWindow.contentManager
    return (0 until contentManager.contentCount).any { index ->
      val content = contentManager.getContent(index)
      content?.displayName?.contains("Gemini", ignoreCase = true) == true
    }
  }
}