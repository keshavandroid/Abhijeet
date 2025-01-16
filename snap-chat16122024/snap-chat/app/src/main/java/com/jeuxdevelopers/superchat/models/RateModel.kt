package com.jeuxdevelopers.superchat.models

import java.io.Serializable

data class RateModel(
    var textCost: Double = 0.0,
    var imageCost: Double = 0.0,
) : Serializable
