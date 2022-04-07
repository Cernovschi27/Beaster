package com.app.music.validators

private const val PHONE_NUMBER_LENGTH = 10
private const val CHAR_SEQUENCE_MIN = 3
private const val MIN_PASSWORD_LENGTH = 6
private const val MIN_NAME_LENGTH = 2

class UIValidator {
    /**
     * check if given text contains more than 3 same characters
     */
     fun contains3Same(text: CharSequence): Boolean {
        if (text.length < MIN_NAME_LENGTH)
            return false
        var maxCount = 0
        var isLowercase = false
        for (c: Char in text) {
            if (text.count { it == c } >= maxCount) {
                maxCount = text.count { it == c }
                if (c.isLowerCase())
                    isLowercase = true
                else isLowercase = false
            }
        }
        if (maxCount > CHAR_SEQUENCE_MIN && isLowercase)
            return false
        else return true
    }

    fun checkPhoneNumber(text: CharSequence): Boolean {
        //check for every character to be numeric
        text.forEach {
            if (!it.isDigit())
                return false
        }
        //check for length to be 10
        if (text.length != PHONE_NUMBER_LENGTH)
            return false

        return true
    }

    fun checkFirstName(text: CharSequence): Boolean = text.length > MIN_NAME_LENGTH
    fun checkLastName(text: CharSequence): Boolean = contains3Same(text)

    fun checkEmail(text: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

    fun checkPassword(text: CharSequence, specialChars: String): Boolean {
        //minimum length - 6, at least one capital letter, at least one lowercase letter,
        //at least one symbol and at least one digit

        if (text.length < MIN_PASSWORD_LENGTH)
            return false

        if (!Regex("[A-Z]").containsMatchIn(text.toString())) {
            println("A-Z")
            return false
        }
        if (!Regex("[a-z]").containsMatchIn(text.toString())) {
            println("a-z")
            return false
        }
        if (!Regex("[0-9]").containsMatchIn(text.toString())) {
            println("0-9")
            return false
        }
        if (!Regex("[$specialChars]").containsMatchIn(text.toString())) {
            println("[$specialChars]")
            return false
        }
        return true
    }

    fun checkPasswordsMatch(pass1: String, pass2: String): Boolean {
        println("$pass1 $pass2")
        return pass1 == pass2
    }
}