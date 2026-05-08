package com.example.imagegallery.di

import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import com.example.imagegallery.cache.AppDatabase
import com.example.imagegallery.cache.ImageDao
import com.example.imagegallery.cache.SearchDao
import com.example.imagegallery.detection.ObjectDetectorHelper
import com.example.imagegallery.network.PicsumApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun providePicsumApi(okHttpClient: OkHttpClient): PicsumApi {
        return Retrofit.Builder()
            .baseUrl(PicsumApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PicsumApi::class.java)
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "image_gallery_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideImageDao(database: AppDatabase): ImageDao = database.imageDao()

    @Provides
    fun provideSearchDao(database: AppDatabase): SearchDao = database.searchDao()

    @Provides
    @Singleton
    fun provideObjectDetectorHelper(@ApplicationContext context: Context): ObjectDetectorHelper {
        return ObjectDetectorHelper(context)
    }
}
