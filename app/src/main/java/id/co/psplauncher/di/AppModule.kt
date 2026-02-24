package id.co.psplauncher.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.co.psplauncher.data.local.TempPhotoChache
import id.co.psplauncher.data.network.RemoteDataSource
import id.co.psplauncher.data.network.absensi.AbsensiAPI
import id.co.psplauncher.data.network.auth.AuthApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAuthApi(
        remoteDataSource: RemoteDataSource
    ): AuthApi {
        return remoteDataSource.buildApi(AuthApi::class.java)
    }

    @Singleton
    @Provides
    fun provideAbsensiApi(
        remoteDataSource: RemoteDataSource
    ): AbsensiAPI {
        return remoteDataSource.buildApi(AbsensiAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideTempPhotoChache(
        @ApplicationContext context: Context
    ): TempPhotoChache {
        return TempPhotoChache(context)
    }

}