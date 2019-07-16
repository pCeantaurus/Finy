package com.rdd.finy.data

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface WalletDao {

    @Query("SELECT * FROM Wallet WHERE id=:id")
    fun getWalletById(id: Long): Single<Wallet>

    @Query("SELECT * FROM Wallet")
    fun getAllWallets(): Flowable<List<Wallet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallet: Wallet)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(wallets:List<Wallet>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(wallet: Wallet)

    @Delete
    fun delete(wallet: Wallet)

}
