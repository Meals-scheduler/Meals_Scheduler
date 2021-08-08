package com.example.meals_schdueler

class MonthlySchedule(
    monthlyId: Int,
    ownerId: Int,
    numOfWeek: String,
    weeklyIds: String,
    totalCost: Double,
    isUpdate: Boolean

) : GetAndPost {
    override fun DoNetWorkOpreation(): String {
        TODO("Not yet implemented")
    }

    override fun getData(str: String) {
        TODO("Not yet implemented")
    }
}