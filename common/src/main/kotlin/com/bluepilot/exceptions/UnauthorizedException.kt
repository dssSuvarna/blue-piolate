package com.bluepilot.exceptions

import com.bluepilot.errors.BaseError

class UnauthorizedException(error: BaseError) : BaseException(error)