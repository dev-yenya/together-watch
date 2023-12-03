package com.example.together_watch.schedule.updateAndDelete

class UpdateAndDeleteSchedulePresenter(
    private val model : UpdateAndDeleteScheduleContract.Model
) : UpdateAndDeleteScheduleContract.Presenter {
    override fun initialize() {
        TODO("Not yet implemented")
    }

    override fun onEditButtonClicked() {
        TODO("Not yet implemented")
    }

    override fun onDeleteButtonClickedAndCheckedDeleted() : Boolean {
        val isDeleted = model.deleteAndReturnIsDeleted()
        return isDeleted
    }

    override fun loadScheduleData() {
        TODO("Not yet implemented")
    }
}