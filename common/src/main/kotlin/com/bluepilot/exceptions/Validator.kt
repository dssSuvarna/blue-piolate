package com.bluepilot.exceptions

class Validator {
    companion object{
        fun validate(boolean: Boolean, baseException: BaseException) {
            if(boolean)
                throw baseException
        }
    }
}