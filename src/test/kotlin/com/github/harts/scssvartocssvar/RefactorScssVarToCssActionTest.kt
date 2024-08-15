package com.github.harts.scssvartocssvar

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.mockk.every
import io.mockk.mockkStatic
import javax.swing.Icon

class RefactorScssVarToCssActionTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()

        mockkStatic(Messages::class)
        every {
            Messages.showYesNoDialog(
                any<Project>(),
                any<String>(),
                any<String>(),
                any<Icon>()
            )
        } returns Messages.YES
    }

    override fun getTestDataPath(): String {
        return "src/test/resources/testScss"
    }

    fun `test refactoring scss variable to css variable`() {
        myFixture.configureByText("styles.scss", """
            ${'$'}primary-500: red;
            .button {
                background-color: ${'$'}primary-500;
            }
        """.trimIndent())

        myFixture.testAction(RefactorScssVarToCssAction())

        myFixture.checkResult("""
            :root {
                --primary-color: red;
            }
            .button {
                background-color: var(--primary-color);
            }
        """.trimIndent())
    }

    fun `test does not change unrelated code`() {
        val testScss = """
            body {
                margin: 0;
                color: ${'$'}pale
            }
        """
        val scssFile = myFixture.configureByText("styles.scss", testScss.trimIndent())

        myFixture.testAction(RefactorScssVarToCssAction())

        myFixture.checkResult(testScss.trimIndent())
    }
}