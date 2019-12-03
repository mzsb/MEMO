package hu.mobilclient.memo.managers


import android.app.Activity
import hu.mobilclient.memo.App
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.services.*

class ServiceManager
constructor(private val activity: Activity,
            private val errorCallback: (errorMessage: String) -> Unit = fun (errorMessage){ EmotionToast.showError(errorMessage)}) {

    fun invalidateApiService(){
        authentication.apiService = App.apiService
        user.apiService = App.apiService
        dictionary.apiService = App.apiService
        language.apiService = App.apiService
        translation.apiService = App.apiService
        attribute.apiService = App.apiService
        connection.apiService = App.apiService
    }

    var connection: ConnectionService = ConnectionService(activity, errorCallback)

    var attribute: AttributeService = AttributeService(activity, errorCallback)

    var authentication: AuthenticationService = AuthenticationService(activity, errorCallback)

    var user: UserService = UserService(activity, errorCallback)

    var dictionary: DictionaryService = DictionaryService(activity, errorCallback)

    var translation: TranslationService = TranslationService(activity, errorCallback)

    var language: LanguageService = LanguageService(activity, errorCallback)

}