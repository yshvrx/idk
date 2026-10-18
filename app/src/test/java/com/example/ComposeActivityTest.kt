package com.example

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class ComposeActivityTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun testAppRendersWithoutCrash() {
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().assertExists()
  }
}
