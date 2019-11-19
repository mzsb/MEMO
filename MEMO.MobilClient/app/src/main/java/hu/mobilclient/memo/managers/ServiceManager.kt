package hu.mobilclient.memo.managers


import android.app.Activity
import hu.mobilclient.memo.App
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.services.*

class ServiceManager
constructor(private val activity: Activity,
            private val errorCallback: (errorMessage: String) -> Unit = fun (errorMessage){ EmotionToast.showError(errorMessage)}) {

    fun invalidateApiService(){
        authentication?.apiService = App.apiService
        user?.apiService = App.apiService
        dictionary?.apiService = App.apiService
        language?.apiService = App.apiService
        translation?.apiService = App.apiService
        attribute?.apiService = App.apiService
    }

    var connection: ConnectionService? = null
        get(){
            if(field == null){
                field = ConnectionService(activity, errorCallback)
            }
            return field
        }

    var attribute: AttributeService? = null
        get(){
            if(field == null){
                field = AttributeService(activity, errorCallback)
            }
            return field
        }

    var authentication: AuthenticationService? = null
        get(){
            if(field == null){
                field = AuthenticationService(activity, errorCallback)
            }
            return field
        }

    var user: UserService? = null
        get(){
            if(field == null){
                field = UserService(activity, errorCallback)
            }
            return field
        }

    var dictionary: DictionaryService? = null
        get(){
            if(field == null){
                field = DictionaryService(activity, errorCallback)
            }
            return field
        }

    var translation: TranslationService? = null
        get(){
            if(field == null){
                field = TranslationService(activity, errorCallback)
            }
            return field
        }

    var language: LanguageService? = null
        get(){
            if(field == null){
                field = LanguageService(activity, errorCallback)
            }
            return field
        }

}