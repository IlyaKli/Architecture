package com.yusmp.plan.presentation.customView.diagramGant

import java.time.LocalDate

data class Task(
    val name: String,
    val dateStart: LocalDate,
    val dateEnd: LocalDate,
)
