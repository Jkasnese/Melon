package com.forcetower.unes.feature.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forcetower.unes.R
import com.forcetower.unes.feature.shared.UFragment

class UnesMessagesFragment: UFragment() {
    init { displayName = "UNES" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_unes_messages, container, false)
        return view
    }
}