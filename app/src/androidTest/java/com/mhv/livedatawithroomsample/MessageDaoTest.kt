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

package com.mhv.livedatawithroomsample

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.mhv.livedatawithroomsample.data.MessagingDatabase
import com.mhv.livedatawithroomsample.models.Message
import com.mhv.livedatawithroomsample.models.MessageDao
import com.mhv.livedatawithroomsample.utilities.getValue
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessageDaoTest {
    private lateinit var database: MessagingDatabase
    private lateinit var messageDao: MessageDao

    private val messageOne = Message("1", "test_sender",
            "test_chat", "This is message 1", 1528702983, 0)

    private val messageTwo = Message("2", "test_sender",
            "test_chat", "This is message 2", 1528702982, 0)

    private val messageThree = Message("3", "test_sender",
            "test_chat", "This is message 3", 1528702981, 0)

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getTargetContext()
        database = Room.inMemoryDatabaseBuilder(context, MessagingDatabase::class.java).build()
        messageDao = database.messageDao()
        messageDao.insertMessages(listOf(messageTwo, messageThree, messageOne))
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetMessages() {
        val messageList = getValue(messageDao.getAllMessages())
        Assert.assertThat(messageList.size, Matchers.equalTo(3))

        // Ensure message list is sorted by timestamp (from newer to older)
        Assert.assertThat(messageList[0], Matchers.equalTo(messageOne))
        Assert.assertThat(messageList[1], Matchers.equalTo(messageTwo))
        Assert.assertThat(messageList[2], Matchers.equalTo(messageThree))
    }

    @Test
    fun testGetPlant() {
        Assert.assertThat(getValue(messageDao.getMessageById(messageOne.messageId)),
                Matchers.equalTo(messageOne))
    }
}