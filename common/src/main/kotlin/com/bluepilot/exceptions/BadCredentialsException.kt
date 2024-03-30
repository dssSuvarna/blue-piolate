package com.bluepilot.exceptions

import com.bluepilot.errors.BaseError

class BadCredentialsException(error: BaseError) : BaseException(error)
