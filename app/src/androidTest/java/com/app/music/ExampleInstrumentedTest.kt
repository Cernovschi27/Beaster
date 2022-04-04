package com.app.music

import android.content.res.Resources
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.app.music.validators.UIValidator
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val validator = UIValidator()
    private val res: Resources = getInstrumentation().getTargetContext().getResources()
    private val specialChars = res.getString(R.string.allowed_password_special_chars)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.app.music", appContext.packageName)
    }

    @Test
    fun validatePasswordSuccess() {
        val response = validator.checkPassword("Parola00!",
            specialChars)
        assertTrue(response)
    }

    @Test
    fun validatePasswordFailedMinLength() {
        val response = validator.checkPassword("Par!",
            specialChars)
        assertFalse(response)
    }

    @Test
    fun validatePasswordFailedNoUpperCase() {
        val response = validator.checkPassword("parola00!",
            specialChars)
        assertFalse(response)
    }

    @Test
    fun validatePasswordFailedNoLowerCase() {
        val response = validator.checkPassword("PAROLA00!",
            specialChars)
        assertFalse(response)
    }

    @Test
    fun validatePasswordFailedNoSpecialChars() {
        val response = validator.checkPassword("Parola001",
            specialChars)
        assertFalse(response)
    }

    @Test
    fun validatePasswordFailedNoDigits() {
        val response = validator.checkPassword("Parolaaaa!",
            specialChars)
        assertFalse(response)
    }

    @Test
    fun validatePasswordFailedLowBounderMinLength() {
        val response = validator.checkPassword("Par1!",
            specialChars)
        assertFalse(response)
    }
    @Test
    fun validatePasswordFailedHighBounderMinLength() {
        val response = validator.checkPassword("Parola1!",
            specialChars)
        assertTrue(response)
    }
    @Test
    fun validatePasswordSuccessAbsolutMinLength() {
        val response = validator.checkPassword("Parola1!",
            specialChars)
        assertTrue(response)
    }
    @Test
    fun validatePasswordFailedLowBounderDigits() {
        val response = validator.checkPassword("Parolaaaa!",
            specialChars)
        assertFalse(response)
    }
    @Test
    fun validatePasswordSuccessHighBounderDigits() {
        val response = validator.checkPassword("Parolaaaa22!",
            specialChars)
        assertTrue(response)
    }
    @Test
    fun validatePasswordSuccessAbsolutBounderDigits() {
        val response = validator.checkPassword("Parolaaaa2!",
            specialChars)
        assertTrue(response)
    }
    @Test
    fun validatePasswordFailedLowBounderUpperCase() {
        val response = validator.checkPassword("parolaaaa!",
            specialChars)
        assertFalse(response)
    }
    @Test
    fun validatePasswordSuccessHighBounderUpperCase() {
        val response = validator.checkPassword("Parolaaaa22!",
            specialChars)
        assertTrue(response)
    }
    @Test
    fun validatePasswordSuccessAbsolutBounderUpperCase() {
        val response = validator.checkPassword("PArolaaaa2!",
            specialChars)
        assertTrue(response)
    }
}