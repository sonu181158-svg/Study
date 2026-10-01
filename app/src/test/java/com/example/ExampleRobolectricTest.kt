package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("STUDYg", appName)
  }

  @Test
  fun `verify curriculum repository grades and questions`() {
    val grade10Subjects = com.example.data.repository.CurriculumRepository.getSubjectsForGrade(10)
    assertEquals(4, grade10Subjects.size)

    val grade12Subjects = com.example.data.repository.CurriculumRepository.getSubjectsForGrade(12)
    assertEquals(5, grade12Subjects.size)

    val chapters = com.example.data.repository.CurriculumRepository.getChaptersForSubject("c10_sci")
    val firstChapter = chapters.first()
    org.junit.Assert.assertTrue(firstChapter.competitiveQuestions.size >= 100)
  }
}
