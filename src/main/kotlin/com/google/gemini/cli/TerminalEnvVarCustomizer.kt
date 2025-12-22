/*
 * Copyright 2025 Charles Zhang
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

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.terminal.LocalTerminalCustomizer

class TerminalEnvVarCustomizer : LocalTerminalCustomizer() {
    override fun customizeCommandAndEnvironment(
        project: Project,
        workingDirectory: String?,
        command: Array<String>,
        env: MutableMap<String, String>
    ): Array<String> {
        val serverState = project.service<GeminiCliServerState>()
        serverState.port?.let {
            env["GEMINI_CLI_IDE_SERVER_PORT"] = it.toString()
        }
        serverState.token?.let {
            env["GEMINI_CLI_IDE_AUTH_TOKEN"] = it
        }
        serverState.workspacePath?.let {
            env["GEMINI_CLI_IDE_WORKSPACE_PATH"] = it
        }
        return command
    }
}
