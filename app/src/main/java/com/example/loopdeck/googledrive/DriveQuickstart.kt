package com.example.loopdeck.googledrive

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.DriveScopes
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


object DriveQuickstart {

    val APPLICATION_NAME = "Google Drive API Java Quickstart"
    val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    val TOKENS_DIRECTORY_PATH = "tokens"


    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES: List<String> =
        Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY)
    private val CREDENTIALS_FILE_PATH = "/credentials.json"

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
//    @Throws(IOException::class)
//     fun getCredentials(context: Context, HTTP_TRANSPORT: NetHttpTransport): Credential? {
//        // Load client secrets.
//        val `in` = DriveQuickstart::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
//            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
//        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))
//
//
////        val tokenFolder  = File(
////            Environment.getExternalStorageDirectory().absolutePath+
////                     TOKENS_DIRECTORY_PATH
////        )
//
//
//        val tokenFolder =
//            File(context.getExternalFilesDir(null)!!.absolutePath,TOKENS_DIRECTORY_PATH )
//
//        if (!tokenFolder.exists()) {
//            tokenFolder.mkdir()
//        }
//
//        val flow = GoogleAuthorizationCodeFlow.Builder(
//            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
//        )
//            .setDataStoreFactory(FileDataStoreFactory(tokenFolder))
//            .setAccessType("offline")
//            .build()
//
//
////        // Build flow and trigger user authorization request.
////        val flow = GoogleAuthorizationCodeFlow.Builder(
////            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
////        )
////            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
////            .setAccessType("offline")
////            .build()
//
//
//        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
//        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
//    }

    fun getCredentials(context: Context, HTTP_TRANSPORT: NetHttpTransport): Credential {
        val `in` = DriveQuickstart::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))


        createTokenFolderIfMissing(context)

        val authorisationFlow: GoogleAuthorizationCodeFlow =
            getAuthorisationFlow(context,HTTP_TRANSPORT, clientSecrets)

        val ab: AuthorizationCodeInstalledApp =
            object : AuthorizationCodeInstalledApp(authorisationFlow, LocalServerReceiver()) {
                @Throws(IOException::class)
                override fun onAuthorization(authorizationUrl: AuthorizationCodeRequestUrl) {
                    val url = authorizationUrl.build()
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(browserIntent)
                }
            }
        return ab.authorize("user").setAccessToken("user")
    }

    private fun createTokenFolderIfMissing(context: Context) {
        val tokenFolder = getTokenFolder(context)
        if (!tokenFolder.exists()) {
            tokenFolder.mkdir()
        }
    }


    private fun getTokenFolder(context: Context): File {
        return File(context.getExternalFilesDir("")?.absolutePath + TOKENS_DIRECTORY_PATH)
    }


    private fun getAuthorisationFlow(
        context: Context,
        HTTP_TRANSPORT: NetHttpTransport,
        clientSecrets: GoogleClientSecrets
    ): GoogleAuthorizationCodeFlow {
        return GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(getTokenFolder(context)))
            .setAccessType("offline")
            .build()
    }
}