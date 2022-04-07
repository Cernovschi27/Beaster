package com.app.music

import android.content.res.Resources
import androidx.test.platform.app.InstrumentationRegistry
import com.app.music.validators.UIValidator
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val validator = UIValidator()

    @Test
    fun validate3SameSuccess(){
        assertTrue(validator.contains3Same("RaResastilean"))
    }
    @Test
    fun validate3SameMinLength(){
        assertTrue(validator.contains3Same("gabi"))
    }
    @Test
    fun validate3SameC3(){
        assertFalse(validator.contains3Same("GGaaaabi"))
    }
    @Test
    fun validate3SameC4(){
        assertFalse(validator.contains3Same(""))
    }

}