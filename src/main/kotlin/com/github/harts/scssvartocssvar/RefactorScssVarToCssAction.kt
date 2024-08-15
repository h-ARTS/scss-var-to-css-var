package com.github.harts.scssvartocssvar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope

class RefactorScssVarToCssAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return

        val prompt = Messages.showYesNoDialog(
            project,
            "Do you want to refactor all SCSS variables to CSS variables across the entire project?",
            "Refactor SCSS to CSS Variables",
            Messages.getQuestionIcon()
        )

        if (prompt != Messages.YES) return

        val scssFileType = FileTypeManager.getInstance().getFileTypeByExtension("scss")
        val scssFiles = FileTypeIndex.getFiles(scssFileType, GlobalSearchScope.projectScope(project))

        scssFiles.forEach { psiFile -> refactor(psiFile, project)}
    }

    private fun refactor(psiFile: VirtualFile, project: Project) {
        val document = PsiManager.getInstance(project).findFile(psiFile)?.virtualFile?.let {
            FileDocumentManager.getInstance().getDocument(it)
        } ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            val regex = """\$([a-zA-Z0-9_-]+)""".toRegex()
            val text = document.text
            val newText = regex.replace(text) { matchResult ->
                "var(--${matchResult.groupValues[1]})"
            }
            document.setText(newText)
        }
    }
}