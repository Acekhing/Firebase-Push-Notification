package com.penpab.firebasepushnotification.utils

sealed class WorkerData(val name: String){
    object Success: WorkerData("success")
    object Failure: WorkerData("error")
    object Data: WorkerData("data")
    object Token: WorkerData("token")
}
