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

import android.os.AsyncTask
import com.mhv.livedatawithroomsample.models.Message
import com.mhv.livedatawithroomsample.models.MessageDao

class MessageRepository private constructor(private val messageDao: MessageDao) {

    fun getAllMessages() = messageDao.getAllMessages()

    fun getMessageById(messageId: String) = messageDao.getMessageById(messageId)

    fun insert(message: Message) {
        InsertAsyncTask(messageDao).execute(message)
    }

    fun deleteAllMessages() {
        DeleteAllMessagesTask(messageDao).execute()
    }

    fun deleteMessage(message: Message) {
        DeleteMessageTask(messageDao).execute(message)
    }

    private class InsertAsyncTask(private val mAsyncTaskDao: MessageDao)
        : AsyncTask<Message, Void, Void>() {
        override fun doInBackground(vararg params: Message): Void? {
            mAsyncTaskDao.insertMessage(params[0])
            return null
        }
    }

    private class DeleteAllMessagesTask(private val mAsyncTaskDao: MessageDao)
        : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg voids: Void): Void? {
            mAsyncTaskDao.deleteAllMessages()
            return null
        }
    }

    private class DeleteMessageTask(private val mAsyncTaskDao: MessageDao)
        : AsyncTask<Message, Void, Void>() {
        override fun doInBackground(vararg params: Message): Void? {
            mAsyncTaskDao.deleteMessage(params[0])
            return null
        }
    }

    companion object {
        @Volatile private var instance: MessageRepository? = null

        fun getInstance(messageDao: MessageDao) =
                instance ?: synchronized(this) {
                    instance ?: MessageRepository(messageDao).also { instance = it }
                }
    }
}