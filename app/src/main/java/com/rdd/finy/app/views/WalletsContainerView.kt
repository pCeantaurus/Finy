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
package com.rdd.finy.app.views

import com.rdd.finy.app.models.Wallet
import io.reactivex.disposables.Disposable
import moxy.MvpView
import moxy.viewstate.strategy.SingleStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = SingleStateStrategy::class)
interface WalletsContainerView : MvpView {

    fun setupEmptyWalletsList()

    fun setupWalletsList(wallets : List<Wallet>)

    fun updateCurrentWallet(wallet: Wallet)

    fun removeCurrentWallet(wallet: Wallet)

    fun insertCurrentWallet(wallet: Wallet)

    fun showError(message: String)

    fun attachNewDisposable(d: Disposable)

}