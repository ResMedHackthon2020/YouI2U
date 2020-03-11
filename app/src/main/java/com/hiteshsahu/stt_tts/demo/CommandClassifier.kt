package com.hiteshsahu.stt_tts.demo

import com.hiteshsahu.stt_tts.translation_engine.ConversionCallback
import com.hiteshsahu.stt_tts.translation_engine.TranslatorFactory
import kotlinx.android.synthetic.main.activity_home.*

class CommandClassifier {
    val commandList: MutableList<String> = mutableListOf()

    fun addCommands(commands: List<String>)
    {
        commandList.clear()
        commandList.addAll(commands)
    }

    fun processCommands() : Int
    {
        var command = firstStage(commandList[0])

        return command
    }

    private fun firstStage(word: String): Int {
        return when(word){
            "set" -> 1
            "get" -> 2
            "start" -> 3
            "stop" -> 4
            "what" -> 5
            else -> -1
        }
    }
}