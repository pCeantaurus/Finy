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

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.rdd.finy.R
import com.rdd.finy.app.adapters.MoneyWalletsAdapter
import com.rdd.finy.app.models.Wallet
import com.rdd.finy.app.presenters.ControlMoneyPresenter
import com.rdd.finy.app.views.ControlMoneyView
import com.rdd.finy.helpers.CalculatorBeverage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter


class ControlMoneyDialog : MvpAppCompatDialogFragment(), ControlMoneyView {

    private val TAG = ControlMoneyDialog::class.java.simpleName

    @InjectPresenter
    lateinit var controlMoneyPresenter: ControlMoneyPresenter

    @BindView(R.id.edit_control_balance)
    lateinit var controlBalanceEdit: EditText
    @BindView(R.id.txt_stats)
    lateinit var controlWalletsCountTxt: TextView
    @BindView(R.id.im_stats)
    lateinit var controlWalletsCountImView: ImageView
    @BindView(R.id.stats_back)
    lateinit var controlStateViewBtn: LinearLayout
    @BindView(R.id.rv_money_wallets)
    lateinit var walletsListRv: RecyclerView

    private lateinit var walletsAdapter: MoneyWalletsAdapter

    private var rvVisibility = false

    private lateinit var openAnim: Animation
    private lateinit var closeAnim: Animation

    companion object {

        private const val ARG_CONTROL_FLAG = "controlMoneyFlag"

        fun newInstance(isAddingMoney: Boolean): ControlMoneyDialog {

            val args = Bundle()
            args.putBoolean(ARG_CONTROL_FLAG, isAddingMoney)

            val fragment = ControlMoneyDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_control, null)
        ButterKnife.bind(this, view)

        loadAnimations()

        walletsAdapter = MoneyWalletsAdapter(context!!)
        walletsListRv.adapter = walletsAdapter
        walletsListRv.layoutManager = LinearLayoutManager(context)

        controlBalanceEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        controlWalletsCountTxt.text = getString(R.string.control_wallets_count,
            0)

        controlMoneyPresenter.loadWalletsFromDB()
        controlMoneyPresenter.attachDisposable(walletsAdapter.getActiveWalletsSize()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { count ->
                controlWalletsCountTxt.text = getString(R.string.control_wallets_count,
                    count)
            })


        val builder = if (arguments!!.getBoolean("controlMoneyFlag")) {
            AlertDialog.Builder(activity, R.style.AddMoneyStyle)
        } else {
            AlertDialog.Builder(activity, R.style.RemoveMoneyStyle)
        }

        val title = if (arguments!!.getBoolean(ARG_CONTROL_FLAG))
            getString(R.string.add_money_title)
        else getString(R.string.remove_money_title)
        view.findViewById<TextView>(R.id.txt_control_money_dialog_title).text = title

        builder.setView(view)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                dismiss()
            }

        val dialog = builder.create()

        dialog.setOnShowListener {
            val positiveButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val calculator: CalculatorBeverage

//                if (arguments!!.getBoolean(ARG_CONTROL_FLAG)) {
//                    calculator = AddCalculator(walletsAdapter.getActiveWallets())
//                    Log.e(TAG, "Add money calc")
//                } else {
//                    calculator = RemoveCalculator(walletsAdapter.getActiveWallets())
//                    Log.e(TAG, "Remove money calc")
//                }
//
//                if (controlBalanceEdit.text.isNotBlank()) {
//                    calculator.userBalance = controlBalanceEdit.text.toString().toDouble()
//                    calculator.calculate()
//                }
                dismiss()
            }
        }

        return dialog
    }

    override fun loadAnimations() {
        openAnim = AnimationUtils.loadAnimation(context, R.anim.open_wallets_list)
        closeAnim = AnimationUtils.loadAnimation(context, R.anim.close_wallets_list)
    }

    @OnClick(R.id.stats_back)
    fun controlRvVisibility() {
        rvVisibility = !rvVisibility
        setupRvVisibility()
    }

    override fun setupRvVisibility() {
        if (rvVisibility) {
            walletsListRv.visibility = View.VISIBLE
            openWalletsList()
        } else {
            walletsListRv.visibility = View.GONE
            closeWalletsList()
        }
    }

    override fun openWalletsList() {
        controlWalletsCountImView.setImageResource(android.R.drawable.arrow_up_float)
        controlWalletsCountImView.startAnimation(openAnim)
    }

    override fun closeWalletsList() {
        controlWalletsCountImView.setImageResource(android.R.drawable.arrow_down_float)
        controlWalletsCountImView.startAnimation(closeAnim)
    }

    override fun setupSourceList(wallets: List<Wallet>) {
        walletsAdapter.setupWalletsList(wallets)
    }

    override fun onDestroy() {
        super.onDestroy()
        controlMoneyPresenter.destroyDisposable()
    }
}