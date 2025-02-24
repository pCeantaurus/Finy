/*
Copyright 2019 Daniil Artamonov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.rdd.finy.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.rdd.finy.R
import com.rdd.finy.app.presenters.BalanceChartPresenter
import com.rdd.finy.app.views.BalanceChartView
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.view.PieChartView
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class BalanceChartFragment : MvpAppCompatFragment(), BalanceChartView {

    @InjectPresenter
    lateinit var balanceChartPresenter: BalanceChartPresenter

    @ProvidePresenter
    fun provideBalanceChartPresenter(): BalanceChartPresenter {
        return BalanceChartPresenter(context!!)
    }

    @BindView(R.id.pie_balance_view)
    lateinit var balancePieChart: PieChartView
    @BindView(R.id.txt_no_positive_balance)
    lateinit var noPositiveTxt: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_balance_chart, container, false)

        ButterKnife.bind(this, view)

        balanceChartPresenter.loadChartData()

        return view
    }

    override fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun showNoActiveWalletsWithPositiveBalance() {
        noPositiveTxt.visibility = View.VISIBLE
        balancePieChart.visibility = View.GONE
    }

    override fun updatePieData(data: PieChartData) {

        if (balancePieChart.visibility == View.GONE) {
            noPositiveTxt.visibility = View.GONE
            balancePieChart.visibility = View.VISIBLE
        }

        balancePieChart.pieChartData = data
    }

    override fun onDestroy() {
        super.onDestroy()
        balanceChartPresenter.dispose()
    }
}
