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
package com.rdd.finy.app.activities

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.rdd.finy.App
import com.rdd.finy.R
import com.rdd.finy.app.adapters.WalletsAdapter
import com.rdd.finy.app.fragments.ControlMoneyDialog
import com.rdd.finy.app.fragments.SetupWalletDialog
import com.rdd.finy.app.fragments.StatsContainerFragment
import com.rdd.finy.app.fragments.WalletsContainerFragment
import com.rdd.finy.app.models.Wallet
import com.rdd.finy.app.presenters.InfoPresenter
import com.rdd.finy.app.views.InfoView
import com.rdd.finy.data.repositories.WalletRepositoryImpl
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject


class InfoActivity : MvpAppCompatActivity(), InfoView, WalletsAdapter.Callbacks {

    private val DIALOG_CONTROL_MONEY = "controlMoneyDialog"

    private val TAG = InfoActivity::class.java.simpleName

    init {
        App.app()?.appComponent()?.inject(this@InfoActivity)
    }

    @Inject
    lateinit var walletRepositoryImpl: WalletRepositoryImpl

    @InjectPresenter
    lateinit var infoPresenter: InfoPresenter

    @ProvidePresenter
    fun provideInfoPresenter(): InfoPresenter {
        return InfoPresenter(walletRepositoryImpl)
    }

    companion object {
        const val DIALOG_WALLET_STATE = "walletState"
    }

    @BindView(R.id.btn_add_money)
    lateinit var addMoneyBtn: Button
    @BindView(R.id.btn_add_wallet)
    lateinit var addWalletBtn: Button
    @BindView(R.id.btn_remove_money)
    lateinit var removeMoneyBtn: Button

    private lateinit var openAddWalletAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        ButterKnife.bind(this)

        openAddWalletAnim = AnimationUtils.loadAnimation(this, R.anim.open_add_wallet)

        infoPresenter.loadWalletsData()

        val manager = supportFragmentManager
        val walletsFragment = WalletsContainerFragment()
        val statsFragment = StatsContainerFragment()
        manager.beginTransaction()
            .add(R.id.container_wallets_fragment, walletsFragment)
            .add(R.id.container_stats_fragment, statsFragment)
            .commit()

    }

    @OnClick(R.id.btn_add_money)
    fun addMoney() {
        showControlMoneyDialog(true)
    }

    @OnClick(R.id.btn_remove_money)
    fun removeMoney() {
        showControlMoneyDialog(false)
    }

    @OnClick(R.id.btn_add_wallet)
    fun createWallet() {
        val manager = supportFragmentManager
        val dialog = SetupWalletDialog()
        dialog.show(manager, DIALOG_WALLET_STATE)
    }

    override fun showControlMoneyDialog(isAddingMoney: Boolean) {
        val manager = supportFragmentManager
        val dialog = ControlMoneyDialog.newInstance(isAddingMoney)
        dialog.show(manager, DIALOG_CONTROL_MONEY)
        overridePendingTransition(0, R.anim.open_add_wallet)
    }

    override fun updateTotalBalanceState(totalBalance: Long) {
        supportActionBar?.title = if (totalBalance % 100 > 0) {
            "Total balance ${totalBalance / 100}.${totalBalance % 100}"
        } else {
            "Total balance ${totalBalance / 100}"
        }
    }

    override fun onShowSetupWalletDialog(walletId: Long) {
        val manager = supportFragmentManager
        val dialog = SetupWalletDialog.newInstance(walletId)
        dialog.show(manager, DIALOG_WALLET_STATE)
    }

    override fun onUpdateWallet(wallet: Wallet) {
        walletRepositoryImpl.update(wallet)
    }

    override fun onDestroy() {
        super.onDestroy()
        infoPresenter.destroyDisposable()
    }
}
