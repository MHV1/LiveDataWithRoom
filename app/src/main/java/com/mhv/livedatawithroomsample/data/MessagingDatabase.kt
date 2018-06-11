/*
 * Copyright (C) 2018 Milan Herrera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mhv.livedatawithroomsample.data

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.mhv.livedatawithroomsample.models.Message
import com.mhv.livedatawithroomsample.models.MessageDao
import com.mhv.livedatawithroomsample.utils.DATABASE_NAME
import com.mhv.livedatawithroomsample.utils.MESSAGE_DATA_FILENAME
import com.mhv.livedatawithroomsample.utils.ioThread

@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class MessagingDatabase : RoomDatabase() {

    abstract fun messageDao(): MessageDao

    companion object {
        private val TAG = MessagingDatabase::class.java.simpleName

        @Volatile
        private var instance: MessagingDatabase? = null

        fun getInstance(context: Context): MessagingDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): MessagingDatabase {
            return Room.databaseBuilder(context, MessagingDatabase::class.java, DATABASE_NAME)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            ioThread {
                                val messageType = object : TypeToken<List<Message>>() {}.type
                                var jsonReader: JsonReader? = null

                                try {
                                    val inputStream = context.applicationContext
                                            .assets.open(MESSAGE_DATA_FILENAME)
                                    jsonReader = JsonReader(inputStream.reader())

                                    val messageList: List<Message> = Gson()
                                            .fromJson(jsonReader, messageType)

                                    instance!!.messageDao().insertMessages(messageList)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error populating db", e)
                                } finally {
                                    jsonReader?.close()
                                }
                            }

                        }
                    }).build()
        }
    }
}
