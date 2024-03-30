package com.bluepilot.exceptions

import com.bluepilot.errors.BaseError

class InvalidTokenException(error: BaseError) : BaseException(error)