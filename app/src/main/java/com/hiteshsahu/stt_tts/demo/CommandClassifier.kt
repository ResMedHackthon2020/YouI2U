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

    enum class STATE{
        WAIT_FOR_TRIGGER,
        WAIT_FOR_COMMAND,
        CC_SUCCESS
    }
    var commandList: MutableList<String> = mutableListOf()
    var state: STATE = STATE.WAIT_FOR_TRIGGER
    var compiledCommand: String = ""
    var ourResponse: String = "Undefined command"

    fun ackDone()
    {
        state = STATE.WAIT_FOR_TRIGGER
        commandList.clear()
    }

    fun GetOurResponse(): String
    {
        return ourResponse
    }

    fun process(text: List<String>): STATE {
        commandList.clear()
        commandList.addAll(text)

        when (state){
            STATE.WAIT_FOR_TRIGGER -> LookForTrigger()
            STATE.WAIT_FOR_COMMAND -> LookForCommand()
        }

        return state
    }

    fun LookForTrigger() {
        val iterator = commandList.listIterator()
        for (item in iterator) {
            if (item == "hey") {
                if (iterator.hasNext()) {
                    val e = iterator.next()
                    if (e == "resmed")
                        state = STATE.WAIT_FOR_COMMAND
                }
            }
        }
    }

    fun addCommands(commands: List<String>)
    {
        commandList.clear()
        commandList.addAll(commands)
    }

    fun LookForCommand()
    {
        var length = commandList.size
        var command = detectCommand(commandList[0])

        // filter what is
        if (command == COMMANDS.WHAT)
        {
            if (length > 0 && commandList[1] != "is")
            {
                command = COMMANDS.UNDEFINED
            }
        }
        else if (command == COMMANDS.START || command == COMMANDS.STOP)
        {
            if (length > 0 && commandList[1] != "therapy")
            {
                command = COMMANDS.UNDEFINED
            }
        }

        if (command == COMMANDS.START)
        {
            ourResponse = "Starting therapy now!";
            compiledCommand = "StartTherapy"
            state = STATE.CC_SUCCESS
        }
        else if (command == COMMANDS.STOP)
        {
            ourResponse = "Stopping therapy now!";
            compiledCommand = "StopTherapy"
            state = STATE.CC_SUCCESS
        }
        else if (command != COMMANDS.UNDEFINED)
        {
            detectParameters(command);
        }
    }

    private fun detectParameters(command: CommandClassifier.COMMANDS) {
        var length = commandList.size
        var classified: Boolean = false

        if (length >= 4 && command == COMMANDS.SET)
        {
            // commands of the form
            // set ramp to <value>
            // set humidity level to <value>
            val param1 = commandList[1]
            val param2 = commandList[2]
            var param3 = commandList[3]
            if (param1 == "ramp" && param2 == "to")
            {
                if (param3 == "Auto")
                {
                    ourResponse = "Setting ramp to auto"
                    compiledCommand = "SET RAMP AUTO"
                    classified = true
                }
                else
                {
                    val number = param3.toIntOrNull()
                    if (number != null && number % 5 == 0 && number >= 5 && number <= 45)
                    {
                        ourResponse = "Setting ramp to $param3"
                        compiledCommand = "SET RAMP $param3"
                        classified = true
                    }
                }
            }
            else if (length >= 5 && param1 == "humidity" && param2 == "level" && param3 == "to")
            {
                val param4 = commandList[4]
                ourResponse = "Setting humidity level to $param4"
                compiledCommand = "SET HUMIDITY_LEVEL $param4"
                classified = true
            }
        }
        else
        {
            ourResponse = "Undefined command"
            compiledCommand = ""
        }

        if (classified)
        {
            state = STATE.CC_SUCCESS
        }
    }

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