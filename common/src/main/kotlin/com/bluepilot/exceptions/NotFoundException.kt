package com.bluepilot.exceptions

import com.bluepilot.errors.BaseError

class NotFoundException (error: BaseError) : BaseException(error)