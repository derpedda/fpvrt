package net.pedda.fpvracetimer.helperclasses

class NoActiveRaceException: RuntimeException() {

    override val message: String
        get() = "There is currently no active Race"
}