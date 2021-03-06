/*
 * Copyright (c) 2019.
 * João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This file is part of the UNES Open Source Project.
 *
 * UNES is licensed under the MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.forcetower.uefs.feature.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.forcetower.uefs.R
import com.forcetower.uefs.core.storage.repository.MessagesRepository
import com.forcetower.uefs.core.vm.Event
import javax.inject.Inject

class MessagesViewModel @Inject constructor(
    val repository: MessagesRepository
) : ViewModel(), MessagesActions {
    val messages by lazy { repository.getMessages() }
    val unesMessages by lazy { repository.getUnesMessages() }
    var pushedTimes = 0

    private val _refreshing = MediatorLiveData<Boolean>()
    val refreshing: LiveData<Boolean>
        get() = _refreshing

    private val _messageClick = MutableLiveData<Event<String>>()
    val messageClick: LiveData<Event<String>>
        get() = _messageClick

    private val _snackMessage = MutableLiveData<Event<Int>>()
    val snackMessage: LiveData<Event<Int>>
        get() = _snackMessage

    fun onRefresh() {
        pushedTimes++
        if (pushedTimes == 3) {
            _snackMessage.value = Event(R.string.download_all_messages)
        }

        val fetchMessages = repository.fetchMessages(pushedTimes == 3)
        _refreshing.value = true
        _refreshing.addSource(fetchMessages) {
            _refreshing.removeSource(fetchMessages)
            _refreshing.value = false
        }
    }

    override fun onMessageClick(message: String) {
        _messageClick.value = Event(message)
    }
}