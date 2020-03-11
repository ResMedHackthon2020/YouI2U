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
            if (detectParameters(command))
            {
                state = STATE.CC_SUCCESS
            }
        }

        if (command == COMMANDS.UNDEFINED)
        {
            state = STATE.WAIT_FOR_TRIGGER
        }
    }

    private fun detectParameters(command: CommandClassifier.COMMANDS): Boolean {
        var length = commandList.size
        var classified: Boolean = false

        if (length >= 3 && command == COMMANDS.SET) {
            // commands of the form
            // set ramp <value>
            // set humidity level <value>
            val param1 = commandList[1]
            val param2 = commandList[2]
            if (param1 == "ramp") {
                if (param2 == "Auto") {
                    ourResponse = "Setting ramp to auto"
                    compiledCommand = "SET RAMP AUTO"
                    classified = true
                } else {
                    val number = param2.toIntOrNull()
                    if (number != null && number % 5 == 0 && number >= 5 && number <= 45) {
                        ourResponse = "Setting ramp to $param2"
                        compiledCommand = "SET RAMP $param2"
                        classified = true
                    }
                }
            } else if (length >= 4 && param1 == "humidity" && param2 == "level") {
                val param3 = commandList[3]
                ourResponse = "Setting humidity level to $param3"
                compiledCommand = "SET HUMIDITY_LEVEL $param3"
                classified = true
            }
        }
        else if (length >= 2 && command == COMMANDS.GET)
        {
            val param1 = commandList[1]

            if (param1 == "ramp")
            {
                ourResponse = "Reading ramp value"
                compiledCommand = "GET RAMP"
                classified = true
            }
            else if (length >= 3 && param1 == "humidity")
            {
                val param2 = commandList[2]

                if(param2 == "level")
                {
                    ourResponse = "Reading humidity level"
                    compiledCommand = "GET HUMIDITY_LEVEL"
                    classified = true
                }
            }
        }
        else if (length >= 3 && command == COMMANDS.WHAT)
        {
            val param1 = commandList[2]
            val param2 = commandList[3]

            if (length >= 3 && param1 == "ramp")
            {
                ourResponse =
                        """
                    Ramp time is the duration the device will gradually ramp up the air pressure.
                    Please give the value in minutes. Minimum is 0. Maximum is 45.
                    If you want to set ramp to 5 minutes, please say, set ramp 5
                """
                classified = true
            }
            else if (length >= 4 && param1 == "humidity" && param2 == "level")
            {
                ourResponse =
                        """
                    The humidity level helps to control the dryness of the air applied through the tubes.
                    Minimum level is 1. Maximum is 8.
                    If you want to set the humidity level to 5, please say, set humidity level 5 
                """
                classified = true
            }
        }
        else
        {
            ourResponse = "Undefined command"
            compiledCommand = ""
        }

        return classified
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