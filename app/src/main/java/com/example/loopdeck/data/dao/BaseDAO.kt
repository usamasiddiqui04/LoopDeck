package com.example.loopdeck.data.dao

import androidx.room.*

@Dao
interface BaseDAO<T> {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg values: T)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(values: List<T>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWithIgnore(value: T): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWithIgnore(values: List<T>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(value: T): Long

    @Delete
    fun delete(vararg value: T)

    @Delete
    fun delete(value: T)

    @Update
    fun update(vararg value: T)

    @Update
    fun update(value: T)

}