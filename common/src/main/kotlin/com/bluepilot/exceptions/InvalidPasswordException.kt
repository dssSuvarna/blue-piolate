package com.bluepilot.exceptions

import com.bluepilot.errors.BaseError

class InvalidPasswordException(error: BaseError) : BaseException(error)