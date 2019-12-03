package hu.mobilclient.memo.helpers

object DateParser {

    fun parse(date : String): String
    {
        val splitted = date.split(".")
        return if(splitted.size == 4) {
            return "${splitted[0]}.${splitted[1]}.${splitted[2]}.".replace( " ",Constants.EMPTY_STRING)
        }
        else{
            Constants.EMPTY_STRING
        }
    }
}