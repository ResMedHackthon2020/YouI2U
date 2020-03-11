package com.hiteshsahu.stt_tts.demo

class CommandClassifier {
    val commandList: MutableList<String> = mutableListOf()

    fun addCommands(commands: List<String>)
    {
        commandList.clear()
        commandList.addAll(commands)
    }
}