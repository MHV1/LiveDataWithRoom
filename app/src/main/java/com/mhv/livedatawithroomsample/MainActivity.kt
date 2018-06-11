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

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import com.mhv.livedatawithroomsample.data.MessageRepository
import com.mhv.livedatawithroomsample.data.MessagingDatabase
import com.mhv.livedatawithroomsample.models.Message
import com.mhv.livedatawithroomsample.utils.MessageSpaceDecoration
import com.mhv.livedatawithroomsample.viewmodels.MessageListViewModel
import com.mhv.livedatawithroomsample.viewmodels.MessageListViewModelFactory

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MessageListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val factory = MessageListViewModelFactory(MessageRepository
                .getInstance(MessagingDatabase.getInstance(this).messageDao()))

        viewModel = ViewModelProviders.of(this, factory)
                .get(MessageListViewModel::class.java)

        val adapter = MessageListAdapter()
        message_list.adapter = adapter
        message_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        message_list.addItemDecoration(MessageSpaceDecoration(this, R.dimen.small_margin))

        viewModel.getAllMessages().observe(this, Observer { messages ->
            if (messages != null) adapter.values = messages
        })

        send_button.setOnClickListener {
            val userInput = message_input_view.text.toString()
            if (!TextUtils.isEmpty(userInput)) {
                viewModel.insertMessage(Message(
                        UUID.randomUUID().toString(),
                        "sender_id",
                        "chat_id",
                        userInput,
                        System.currentTimeMillis(),
                        0)
                )
                message_input_view.setText("")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
