package ru.skillbranch.gameofthrones.data.remote

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes


object NetworkService {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(AppConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getJSONApi(): JSONPlaceHolderApi {
        return retrofit.create(
            JSONPlaceHolderApi::class.java)
    }
}

interface JSONPlaceHolderApi {
    @GET("/api/houses?pageSize=50")
    abstract fun getAllHouses(
        @Query("page") pageIndex : Int,
        @Query("pageSize") pageSize : Int
    ): Call<List<HouseRes>>

    @GET("/api/characters")
    abstract fun getAllCharacters(
        @Query("page") pageIndex : Int,
        @Query("pageSize") pageSize : Int
    ): Call<List<CharacterRes>>

    @GET("/api/characters/{id}")
    abstract fun getCharacter(
        @Path("id") id : Int
    ): Call<CharacterRes>

}
