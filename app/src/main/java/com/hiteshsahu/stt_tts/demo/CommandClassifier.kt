package com.hiteshsahu.stt_tts.demo

import com.hiteshsahu.stt_tts.translation_engine.ConversionCallback
import com.hiteshsahu.stt_tts.translation_engine.TranslatorFactory
import kotlinx.android.synthetic.main.activity_home.*

class CommandClassifier {
    enum class COMMANDS{
        SET,
        GET,
        START,
        STOP,
        WHAT,
        UNDEFINED
    }
    val commandList: MutableList<String> = mutableListOf()

    fun addCommands(commands: List<String>)
    {
        commandList.clear()
        commandList.addAll(commands)
    }

    fun processCommands() : String
    {
        var result = "Undefined command"
        var command = detectCommand(commandList[0])

        // filter what is
        if (command == COMMANDS.WHAT)
        {
            if (commandList[1] != "is")
            {
                command = COMMANDS.UNDEFINED
            }
        }
        else if (command == COMMANDS.START || command == COMMANDS.STOP)
        {
            if (commandList[1] != "therapy")
            {
                command = COMMANDS.UNDEFINED
            }
        }

        if (command == COMMANDS.START)
        {
            result = "Starting therapy now!";
        }
        else if (command == COMMANDS.STOP)
        {
            result = "Stopping therapy now!";
        }
        else if (command != COMMANDS.UNDEFINED)
        {
//            detectParameters(command);
        }

        return result
    }

//    private fun detectParameter(command: CommandClassifier.COMMANDS) {
//        if (command == COMMANDS.SET)
//        {
//            val param1 = commandList[1]
//            val param2 = commandList[2]
//            if (param1 == "ramp" && param2 == "to")
//            {
//
//            }
//            else if (param1 == "humidity" && param2 == "level")
//            {
//
//            }
//        }
//    }

    private fun detectCommand(word: String): COMMANDS {
        return when(word){
            "set" -> COMMANDS.SET
            "get" -> COMMANDS.GET
            "start" -> COMMANDS.START
            "stop" -> COMMANDS.STOP
            "what" -> COMMANDS.WHAT
            else -> COMMANDS.UNDEFINED
        }
    }
}