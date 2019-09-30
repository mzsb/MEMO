package hu.mobilclient.memo.network

import androidx.core.util.rangeTo
import hu.mobilclient.memo.model.Login
import hu.mobilclient.memo.model.Registration
import hu.mobilclient.memo.model.TokenHolder
import hu.mobilclient.memo.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.*

interface ApiService {

    companion object {
        val BaseURL = "https://10.0.2.2:5001/api/"
    }

    @POST("authentication/login")
    fun login(@Body login: Login): Call<TokenHolder>

    @POST("authentication/autoLogin")
    fun autoLogin(@Body token: String): Call<TokenHolder>

    @POST("authentication/registration")
    fun registration(@Body registration: Registration): Call<TokenHolder>

    @POST("authentication/refreshToken")
    fun refreshToken(@Body token: String): Call<TokenHolder>

    @GET("user/{id}")
    fun getUserById(@Path("id") id: UUID): Call<User>
}