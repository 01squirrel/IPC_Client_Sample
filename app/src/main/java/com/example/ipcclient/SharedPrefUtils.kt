package com.example.ipcclient

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections

/**
 * SP工具类
 *
 * @author guoyang
 */
class SharedPrefUtils private constructor(mContext: Context, spName: String?) {
    private val sp: SharedPreferences = mContext.getSharedPreferences(spName, Context.MODE_PRIVATE)
    private val editor: Editor = sp.edit()

    /**
     * SPUtils构造函数
     *
     * 在Application中初始化
     *
     * @param spName spName
     */
    init {
        editor.apply()
    }

    /**
     * SP中写入String类型value
     *
     * @param key   键
     * @param value 值
     */
    fun putString(key: String?, value: String?) {
        editor.putString(key, value).apply()
    }

    /**
     * SP中读取String
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`null`
     */
    fun getString(key: String?): String? {
        return getString(key, null)
    }

    /**
     * SP中读取String
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
     fun getString(key: String?, defaultValue: String?): String? {
        return sp.getString(key, defaultValue)
    }

    /**
     * SP中写入int类型value
     *
     * @param key   键
     * @param value 值
     */
    fun putInt(key: String?, value: Int) {
        editor.putInt(key, value).apply()
    }

    /**
     * SP中读取int
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    fun getInt(key: String?): Int {
        return getInt(key, -1)
    }

    /**
     * SP中读取int
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getInt(key: String?, defaultValue: Int): Int {
        return sp.getInt(key, defaultValue)
    }

    /**
     * SP中写入long类型value
     *
     * @param key   键
     * @param value 值
     */
    fun putLong(key: String?, value: Long) {
        editor.putLong(key, value).apply()
    }

    /**
     * SP中读取long
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    fun getLong(key: String?): Long {
        return getLong(key, -1L)
    }

    /**
     * SP中读取long
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getLong(key: String?, defaultValue: Long): Long {
        return sp.getLong(key, defaultValue)
    }

    /**
     * SP中写入float类型value
     *
     * @param key   键
     * @param value 值
     */
    fun putFloat(key: String?, value: Float) {
        editor.putFloat(key, value).apply()
    }

    /**
     * SP中读取float
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    fun getFloat(key: String?): Float {
        return getFloat(key, -1f)
    }

    /**
     * SP中读取float
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getFloat(key: String?, defaultValue: Float): Float {
        return sp.getFloat(key, defaultValue)
    }

    /**
     * SP中写入boolean类型value
     *
     * @param key   键
     * @param value 值
     */
    fun putBoolean(key: String?, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    /**
     * SP中读取boolean
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`false`
     */
    fun getBoolean(key: String?): Boolean {
        return getBoolean(key, false)
    }

    /**
     * SP中读取boolean
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
     fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    /**
     * SP中写入string类型set
     *
     * @param key 键
     * @param values 值
     */
    fun putSet(key: String?,values: Set<String>?) {
        editor.putStringSet(key,values).apply()
    }

    /**
     * SP中读取string类型set
     * @param key 键
     * @return 存在则返回对应值，不存在返回空set
     */
    fun getStringSet(key: String?): MutableSet<String>? {
        return getStringSet(key,Collections.emptySet())
    }

    /**
     * SP中读取string类型set
     * @param key 键
     * @param defaultValue 默认值
     */
    fun getStringSet(key: String?,defaultValue: Set<String>): MutableSet<String>? {
        return sp.getStringSet(key,defaultValue)
    }

    /**
     * SP中写入list
     * @param key 键
     * @param values 值
     */
    fun putStringList(key: String?,values: List<String>?) {
        val gson = Gson()
        val str = gson.toJson(values)
        editor.putString(key,str).apply()
    }


    fun getStringList(key: String?): List<String> {
        var list = arrayListOf<String>()
        val str = getString(key) ?: return list
        val gson = Gson()
        list = gson.fromJson(str, TypeToken.get(arrayListOf<String>()::class.java) )
        return list
    }

    /**
     * SP中获取所有键值对
     *
     * @return Map对象
     */
    val all: Map<String, *>
        get() = sp.all

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    fun remove(key: String?) {
        editor.remove(key).apply()
    }

    /**
     * SP中是否存在该key
     *
     * @param key 键
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    operator fun contains(key: String?): Boolean {
        return sp.contains(key)
    }

    /**
     * SP中清除所有数据
     */
    fun clear() {
        editor.clear().apply()
    }

    companion object {
        @Volatile
        private var mSharedPrefUtils: SharedPrefUtils? = null

        /**
         * SharedPref 工具
         */
        fun getInstance(mContext: Context): SharedPrefUtils {
            //写法一：
//            if (mSharedPrefUtils == null) {
//                synchronized(SharedPrefUtils::class.java) {
//                    if (mSharedPrefUtils == null) {
//                        mSharedPrefUtils = SharedPrefUtils(mContext, "IPC_shared")
//                    }
//                }
//            }
//            return mSharedPrefUtils
            return mSharedPrefUtils ?: synchronized(SharedPrefUtils::class.java) {
                mSharedPrefUtils?.let {
                    return it
                }
                val instance = SharedPrefUtils(mContext,"default_shared")
                mSharedPrefUtils = instance
                instance
            }
        }
    }
}