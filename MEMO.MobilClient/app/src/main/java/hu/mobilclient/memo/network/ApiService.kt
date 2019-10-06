package hu.mobilclient.memo.network

import hu.mobilclient.memo.model.*
import hu.mobilclient.memo.model.Login
import hu.mobilclient.memo.model.Registration
import hu.mobilclient.memo.model.TokenHolder
import hu.mobilclient.memo.model.Dictionary
import retrofit2.Call
import retrofit2.http.*
import java.util.*


interface ApiService {

    companion object {
        const val BaseURL = "https://10.0.2.2:5001/api/"
    }

    /* Authentication */

    @POST("authentication/login")
    fun login(@Body login: Login): Call<TokenHolder>

    @POST("authentication/autoLogin")
    fun autoLogin(@Body token: String): Call<TokenHolder>

    @POST("authentication/registration")
    fun registration(@Body registration: Registration): Call<TokenHolder>

    @POST("authentication/refreshToken")
    fun refreshToken(@Body token: String): Call<TokenHolder>

    /* User */

    @GET("user")
    fun getUsers(): Call<List<User>>

    @GET("user/{id}")
    fun getUserById(@Path("id") id: UUID): Call<User>

    @PUT("user")
    fun updateUser(@Body user: User): Call<Void>

    @DELETE("user/{id}")
    fun deleteUser(@Path("id") id: UUID): Call<Void>

    /* Dictionary */

    @GET("dictionary")
    fun getDictionaries(): Call<List<Dictionary>>

    @GET("dictionary/{id}")
    fun getDictionaryById(@Path("id") id: UUID): Call<Dictionary>

    @POST("dictionary")
    fun insertDictionary(@Body dictionary: Dictionary): Call<Dictionary>

    @PUT("dictionary")
    fun updateDictionary(@Body dictionary: Dictionary): Call<Void>

    @DELETE("dictionary/{id}")
    fun deleteDictionary(@Path("id") id: UUID): Call<Void>

    @GET("dictionary/user/{id}")
    fun getDictionariesByUserId(@Path("id") id: UUID): Call<List<Dictionary>>

    @GET("dictionary/public/{id}")
    fun getDictionariesPublic(@Path("id") id: UUID): Call<List<Dictionary>>

    /* Translation */

    @GET("translation")
    fun getTranslations(): Call<List<Translation>>

    @GET("translation/{id}")
    fun getTranslationById(@Path("id") id: UUID): Call<Translation>

    @POST("translation")
    fun insertTranslation(@Body translation: Translation): Call<Translation>

    @PUT("translation")
    fun updateTranslation(@Body translation: Translation): Call<Void>

    @DELETE("translation/{id}")
    fun deleteTranslation(@Path("id") id: UUID): Call<Void>

    @GET("translation/dictionary/{id}")
    fun getTranslationsByDictionaryId(@Path("id") id: UUID): Call<List<Translation>>

    /* Language */

    @GET("language")
    fun getLanguages(): Call<List<Language>>
}