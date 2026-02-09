# JetBrains IDE Companion for Gemini CLI

## Project Overview

This is a JetBrains IDE plugin that provides integration between [Gemini CLI](https://github.com/google-gemini/gemini-cli) and JetBrains IDEs. The plugin follows the [ide-companion-spec](https://github.com/google-gemini/gemini-cli/blob/main/docs/ide-integration/ide-companion-spec.md) and enables Gemini CLI to interact with the IDE environment.

### Key Features
- **Open Editor File Context**: Provides Gemini CLI with awareness of files open in the editor
- **Selection Context**: Allows Gemini CLI to access cursor position and selected text
- **Native Diffing**: View, modify, and accept code changes suggested by Gemini CLI directly in the IDE
- **Launch Gemini CLI**: Start new Gemini CLI sessions from the Tools menu or via keyboard shortcut (Ctrl+Alt+G)

### Technologies
- **Language**: Kotlin
- **Build System**: Gradle with Kotlin DSL
- **IDE Platform**: IntelliJ Platform SDK
- **Dependencies**: 
  - Model Context Protocol (MCP) Kotlin SDK (0.7.2)
  - Ktor for HTTP server (2.3.11)
  - Kotlinx Serialization (1.6.3)

## Development Setup

### Prerequisites
- JDK 17
- IntelliJ IDEA or compatible JetBrains IDE
- Gradle (wrapper included)

### Building and Running

#### Build the Plugin
```bash
./gradlew buildPlugin
```

#### Run in Development IDE
```bash
./gradlew runIde
```

#### Run Tests
```bash
./gradlew test
```

#### Clean Build
```bash
./gradlew clean
```

### Project Structure
```
src/main/kotlin/com/google/gemini/cli/
├── settings/                    # Plugin settings and configuration
│   ├── GeminiCliSettingsConfigurable.kt
│   ├── GeminiCliSettingsState.kt
│   └── OpenGeminiCliSettingsAction.kt
├── transport/                   # HTTP transport layer for MCP
│   ├── HttpExchangeAdaptor.kt
│   └── StreamableHttpServerTransport.kt
├── AcceptDiffAction.kt          # Action to accept Gemini CLI diffs
├── CloseDiffAction.kt           # Action to close diffs
├── DiffManager.kt               # Manages diff viewing and application
├── GeminiCliProjectService.kt   # Project-level service
├── GeminiCliServerState.kt      # Server state management
├── GeminiCliStartupActivity.kt  # Plugin startup activity
├── GeminiCliToolWindowContent.kt # Tool window UI
├── GeminiCliToolWindowFactory.kt # Tool window factory
├── IdeServer.kt                 # Main MCP server implementation
├── OpenFilesManager.kt          # Manages open file context
├── RunGeminiCLIAction.kt        # Action to run Gemini CLI
├── ShowNoticesAction.kt         # Shows notices and licenses
└── TerminalEnvVarCustomizer.kt  # Customizes terminal environment
```

## Development Conventions

### Code Style
- Follow standard Kotlin coding conventions
- Use 2-space indentation (as seen in existing code)
- Package structure: `com.google.gemini.cli`
- File headers include Apache 2.0 license notice

### Testing
- Tests are located in `src/test/kotlin/com/google/gemini/cli/`
- Use JUnit 4 for testing
- Test naming follows Kotlin backtick convention: `test tool window content panel is created`

### Plugin Configuration
- Plugin ID: `me.solohic.gemini-cli-companion`
- Minimum IDE version: 2024.1 (sinceBuild="241")
- Dependencies: IntelliJ Platform, Terminal plugin
- Keyboard shortcut: Ctrl+Alt+G for "Run Gemini CLI"

## Deployment

### Publishing
The plugin is published to the JetBrains Marketplace via GitHub Actions when tags are pushed:
- Tag format: `v*` (e.g., `v0.1.1`)
- CI/CD workflow: `.github/workflows/publish.yml`
- Requires environment variables: `PUBLISH_TOKEN`, `CERTIFICATE_CHAIN`, `PRIVATE_KEY`, `PRIVATE_KEY_PASSWORD`

### Installation
1. Build the plugin: `./gradlew buildPlugin`
2. Install from disk in IDE: Settings > Plugins > Install Plugin from Disk
3. Select the generated ZIP from `./build/distributions/`

## Integration with Gemini CLI

### MCP Server
The plugin implements an MCP (Model Context Protocol) server that:
- Runs on a local HTTP server
- Provides IDE context to Gemini CLI
- Handles file operations, selections, and diff management

### Environment Variables
- `GEMINI_CLI_IDE_AUTH_TOKEN`: Authentication token for IDE integration

## Contributing

### Getting Started
1. Clone the repository
2. Open in IntelliJ IDEA
3. Import as Gradle project
4. Run `./gradlew runIde` to test in a sandbox IDE

### Code Organization
- **Actions**: User-initiated operations (Run, Settings, etc.)
- **Services**: Long-running components (ProjectService, ServerState)
- **Transport**: HTTP/MCP communication layer
- **UI**: Tool window and settings panels

### Adding New Features
1. Follow existing patterns for actions, services, or UI components
2. Add corresponding tests
3. Update `plugin.xml` if adding new actions or extensions
4. Consider MCP protocol compatibility

## Troubleshooting

### Common Issues
- **Plugin not loading**: Ensure JDK 17 is used
- **MCP connection issues**: Check if the IDE server is running and accessible
- **Build failures**: Clean and rebuild: `./gradlew clean build`

### Debugging
- Check IDE logs for plugin-related errors
- Use IntelliJ's internal logging: `Help > Diagnostic Tools > Debug Log Settings`
- Test MCP server connectivity independently

## Resources
- [Gemini CLI](https://github.com/google-gemini/gemini-cli)
- [IDE Companion Specification](https://github.com/google-gemini/gemini-cli/blob/main/docs/ide-integration/ide-companion-spec.md)
- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [Model Context Protocol](https://spec.modelcontextprotocol.io/)
