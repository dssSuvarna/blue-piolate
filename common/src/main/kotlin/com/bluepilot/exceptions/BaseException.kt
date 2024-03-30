package com.bluepilot.exceptions

import com.bluepilot.errors.BaseError

open class BaseException(val error: BaseError) : RuntimeException()