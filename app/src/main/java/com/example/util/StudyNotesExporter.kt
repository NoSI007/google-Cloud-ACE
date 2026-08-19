package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.*
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

object StudyNotesExporter {

    fun generateCompleteStudyGuide(
        modules: List<AceModule>,
        cliCommands: List<GcloudCliCommand>,
        bestPractices: List<CloudBestPractice>,
        terms: List<GcpTerm>,
        includeLessons: Boolean = true,
        includeCli: Boolean = true,
        includeBestPractices: Boolean = true,
        includeGlossary: Boolean = true
    ): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val timestamp = dateFormat.format(Date())

        val sb = StringBuilder()
        sb.append("================================================================================\n")
        sb.append("         GOOGLE CLOUD ASSOCIATE CLOUD ENGINEER (ACE) STUDY GUIDE\n")
        sb.append("================================================================================\n")
        sb.append("Generated on: $timestamp\n")
        sb.append("Source: ACE Cloud Guide Learning Application\n\n")

        if (includeLessons) {
            sb.append("================================================================================\n")
            sb.append("SECTION 1: COMPREHENSIVE LESSON STUDY NOTES\n")
            sb.append("================================================================================\n\n")

            modules.forEachIndexed { modIndex, module ->
                sb.append("--------------------------------------------------------------------------------\n")
                sb.append("MODULE ${module.sectionNumber}: ${module.title.uppercase()}\n")
                sb.append("Exam Weight: ${module.examWeight} | ${module.summary}\n")
                sb.append("--------------------------------------------------------------------------------\n\n")

                module.lessons.forEachIndexed { lesIndex, lesson ->
                    sb.append("### LESSON ${module.sectionNumber}.${lesIndex + 1}: ${lesson.title}\n")
                    sb.append("Subtitle: ${lesson.subtitle} (Est. Reading Time: ${lesson.readingTimeMinutes} mins)\n\n")

                    lesson.contentSections.forEach { section ->
                        sb.append(">> ${section.heading}\n")
                        section.bodyParagraphs.forEach { p ->
                            sb.append("$p\n\n")
                        }
                        if (!section.codeOrConceptSnippet.isNullOrBlank()) {
                            sb.append("--- CODE / SYNTAX SNIPPET ---\n")
                            sb.append("${section.codeOrConceptSnippet}\n")
                            sb.append("-----------------------------\n\n")
                        }
                        if (!section.tableRows.isNullOrEmpty()) {
                            sb.append("--- COMPARISON & SPECS ---\n")
                            section.tableRows.forEach { (col1, col2) ->
                                sb.append("• $col1: $col2\n")
                            }
                            sb.append("--------------------------\n\n")
                        }
                    }

                    if (lesson.keyTakeaways.isNotEmpty()) {
                        sb.append("KEY TAKEAWAYS:\n")
                        lesson.keyTakeaways.forEach { takeaway ->
                            sb.append("  [✓] $takeaway\n")
                        }
                        sb.append("\n")
                    }

                    if (lesson.aceExamTips.isNotEmpty()) {
                        sb.append("ACE EXAM TIPS:\n")
                        lesson.aceExamTips.forEach { tip ->
                            sb.append("  [★] $tip\n")
                        }
                        sb.append("\n")
                    }
                    sb.append("\n")
                }
            }
        }

        if (includeCli) {
            sb.append("================================================================================\n")
            sb.append("SECTION 2: ESSENTIAL GOOGLE CLOUD CLI COMMANDS REFERENCE\n")
            sb.append("================================================================================\n\n")

            val groupedCommands = cliCommands.groupBy { it.category }
            groupedCommands.forEach { (category, commands) ->
                sb.append("--------------------------------------------------------------------------------\n")
                sb.append("CATEGORY: ${category.uppercase()}\n")
                sb.append("--------------------------------------------------------------------------------\n\n")

                commands.forEach { cmd ->
                    sb.append("COMMAND: $ ${cmd.command}\n")
                    sb.append("DESCRIPTION: ${cmd.description}\n")
                    sb.append("SYNTAX: ${cmd.syntaxBreakdown}\n")
                    if (cmd.commonFlags.isNotEmpty()) {
                        sb.append("COMMON FLAGS:\n")
                        cmd.commonFlags.forEach { (flag, meaning) ->
                            sb.append("  * $flag -> $meaning\n")
                        }
                    }
                    sb.append("EXAM TIP: ${cmd.aceExamTip}\n")
                    if (!cmd.exampleOutput.isNullOrBlank()) {
                        sb.append("SAMPLE OUTPUT: ${cmd.exampleOutput}\n")
                    }
                    sb.append("\n")
                }
            }
        }

        if (includeBestPractices) {
            sb.append("================================================================================\n")
            sb.append("SECTION 3: GOOGLE CLOUD ARCHITECTURE BEST PRACTICES\n")
            sb.append("================================================================================\n\n")

            val groupedPractices = bestPractices.groupBy { it.category }
            groupedPractices.forEach { (category, practices) ->
                sb.append("--------------------------------------------------------------------------------\n")
                sb.append("PILLAR: ${category.uppercase()}\n")
                sb.append("--------------------------------------------------------------------------------\n\n")

                practices.forEach { practice ->
                    sb.append("RULE: ${practice.title} [${practice.aceExamPriority}]\n")
                    sb.append("CORE PRINCIPLE: ${practice.rule}\n")
                    sb.append("RECOMMENDED ARCHITECTURE (DO): ${practice.actionableGuideline}\n")
                    sb.append("ANTI-PATTERN TO AVOID (DON'T): ${practice.antiPattern}\n")
                    sb.append("RATIONALE: ${practice.rationale}\n\n")
                }
            }
        }

        if (includeGlossary && terms.isNotEmpty()) {
            sb.append("================================================================================\n")
            sb.append("SECTION 4: GOOGLE CLOUD KEY TERMS & ACRONYM GLOSSARY\n")
            sb.append("================================================================================\n\n")

            terms.sortedBy { it.acronymOrTerm }.forEach { term ->
                sb.append("• ${term.acronymOrTerm} (${term.fullName}) [${term.category}]\n")
                sb.append("  Definition: ${term.definition}\n")
                sb.append("  ACE Tip: ${term.aceExamTip}\n\n")
            }
        }

        sb.append("================================================================================\n")
        sb.append("                     END OF ACE CLOUD STUDY GUIDE\n")
        sb.append("================================================================================\n")

        return sb.toString()
    }

    fun generateSingleLessonNotes(lesson: AceLesson, moduleTitle: String): String {
        val sb = StringBuilder()
        sb.append("================================================================================\n")
        sb.append("GOOGLE CLOUD ACE STUDY NOTE: ${lesson.title.uppercase()}\n")
        sb.append("Module: $moduleTitle\n")
        sb.append("================================================================================\n\n")

        sb.append("Topic Subtitle: ${lesson.subtitle}\n")
        sb.append("Estimated Reading Time: ${lesson.readingTimeMinutes} minutes\n\n")

        lesson.contentSections.forEach { section ->
            sb.append("--- ${section.heading.uppercase()} ---\n")
            section.bodyParagraphs.forEach { p ->
                sb.append("$p\n\n")
            }
            if (!section.codeOrConceptSnippet.isNullOrBlank()) {
                sb.append("Command / Syntax Snippet:\n")
                sb.append("${section.codeOrConceptSnippet}\n\n")
            }
            if (!section.tableRows.isNullOrEmpty()) {
                sb.append("Key Specifications:\n")
                section.tableRows.forEach { (k, v) ->
                    sb.append("• $k: $v\n")
                }
                sb.append("\n")
            }
        }

        if (lesson.keyTakeaways.isNotEmpty()) {
            sb.append("--- KEY TAKEAWAYS ---\n")
            lesson.keyTakeaways.forEach { takeaway ->
                sb.append("✓ $takeaway\n")
            }
            sb.append("\n")
        }

        if (lesson.aceExamTips.isNotEmpty()) {
            sb.append("--- ACE EXAM TIPS ---\n")
            lesson.aceExamTips.forEach { tip ->
                sb.append("★ $tip\n")
            }
            sb.append("\n")
        }

        return sb.toString()
    }

    fun writeTextToUri(context: Context, uri: Uri, text: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream: OutputStream ->
                outputStream.write(text.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }
            Toast.makeText(context, "Study guide exported successfully!", Toast.LENGTH_LONG).show()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Export failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            false
        }
    }

    fun shareText(context: Context, text: String, title: String = "Google Cloud ACE Study Guide") {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                this.type = "text/plain"
                this.putExtra(Intent.EXTRA_SUBJECT, title)
                this.putExtra(Intent.EXTRA_TEXT, text)
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share or Save Study Guide")
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Could not open share dialog: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
