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

package com.forcetower.uefs.feature.siecomp.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forcetower.uefs.core.injection.Injectable
import com.forcetower.uefs.core.storage.eventdatabase.accessors.SessionWithData
import com.forcetower.uefs.core.util.siecomp.TimeUtils
import com.forcetower.uefs.core.util.siecomp.TimeUtils.SIECOMP_TIMEZONE
import com.forcetower.uefs.core.vm.UViewModelFactory
import com.forcetower.uefs.databinding.FragmentEventScheduleDayBinding
import com.forcetower.uefs.feature.shared.UFragment
import com.forcetower.uefs.feature.shared.clearDecorations
import com.forcetower.uefs.feature.shared.extensions.provideActivityViewModel
import com.forcetower.uefs.feature.siecomp.SIECOMPEventViewModel
import javax.inject.Inject

class ScheduleDayFragment : UFragment(), Injectable {
    companion object {
        private const val ARG_EVENT_DAY = "arg.EVENT_DAY"

        fun newInstance(day: Int): ScheduleDayFragment {
            val args = bundleOf(ARG_EVENT_DAY to day)
            return ScheduleDayFragment().apply { arguments = args }
        }
    }

    private val eventDay: Int by lazy {
        val args = arguments ?: throw IllegalStateException("No Arguments")
        args.getInt(ARG_EVENT_DAY)
    }

    @Inject
    lateinit var factory: UViewModelFactory
    private lateinit var viewModel: SIECOMPEventViewModel
    private lateinit var binding: FragmentEventScheduleDayBinding
    private lateinit var adapter: ScheduleDayAdapter
    private val tagViewPool = RecyclerView.RecycledViewPool()
    private val sessionViewPool = RecyclerView.RecycledViewPool()

    init {
        tagViewPool.setMaxRecycledViews(0, 15)
        sessionViewPool.setMaxRecycledViews(0, 10)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = provideActivityViewModel(factory)
        binding = FragmentEventScheduleDayBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ScheduleDayFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ScheduleDayAdapter(viewModel, tagViewPool, TimeUtils.SIECOMP_TIMEZONE)
        binding.recyclerDaySchedule.apply {
            setRecycledViewPool(sessionViewPool)
            adapter = this@ScheduleDayFragment.adapter
            (layoutManager as LinearLayoutManager).recycleChildrenOnDetach = true
            (itemAnimator as DefaultItemAnimator).run {
                supportsChangeAnimations = false
                addDuration = 160L
                moveDuration = 160L
                changeDuration = 160L
                removeDuration = 120L
            }
        }

        // TODO Maybe move to current event
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getSessionsFromDayLocal(eventDay).observe(this, Observer {
            it ?: return@Observer
            populateInterface(it)
        })
    }

    private fun populateInterface(data: List<SessionWithData>) {
        adapter.submitList(data)
        binding.recyclerDaySchedule.run {
            doOnNextLayout {
                clearDecorations()
                if (data.isNotEmpty()) {
                    addItemDecoration(
                        ScheduleItemHeaderDecoration(
                            it.context, data, SIECOMP_TIMEZONE
                        )
                    )
                }
            }
        }
    }
}