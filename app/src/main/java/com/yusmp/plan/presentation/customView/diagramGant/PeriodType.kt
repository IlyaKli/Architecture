package com.yusmp.plan.presentation.customView.diagramGant

import java.time.LocalDate
import java.time.temporal.IsoFields

enum class PeriodType {
    MONTH {
        override fun increment(date: LocalDate): LocalDate = date.plusMonths(1)

        override fun getDateString(date: LocalDate): String = date.month.name

        override fun getPercentOfPeriod(date: LocalDate): Float =
            (date.dayOfMonth - 1f) / date.lengthOfMonth()
    },
    WEEK {
        override fun increment(date: LocalDate): LocalDate = date.plusWeeks(1)

        override fun getDateString(date: LocalDate): String =
            date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR).toString()

        override fun getPercentOfPeriod(date: LocalDate): Float = (date.dayOfWeek.value - 1f) / 7
    };

    abstract fun increment(date: LocalDate): LocalDate

    abstract fun getDateString(date: LocalDate): String

    abstract fun getPercentOfPeriod(date: LocalDate): Float
}