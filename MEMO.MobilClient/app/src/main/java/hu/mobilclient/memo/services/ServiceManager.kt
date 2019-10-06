package hu.mobilclient.memo.services


import android.app.Activity

class ServiceManager
constructor(private val activity: Activity) {

    var authentication: AuthenticationService? = null
        get(){
            if(field == null){
                field = AuthenticationService(activity)
            }
            return field
        }

    var user: UserService? = null
        get(){
            if(field == null){
                field = UserService(activity)
            }
            return field
        }

    var dictionary: DictionaryService? = null
        get(){
            if(field == null){
                field = DictionaryService(activity)
            }
            return field
        }

    var translation: TranslationService? = null
        get(){
            if(field == null){
                field = TranslationService(activity)
            }
            return field
        }

    var language: LanguageService? = null
        get(){
            if(field == null){
                field = LanguageService(activity)
            }
            return field
        }

}