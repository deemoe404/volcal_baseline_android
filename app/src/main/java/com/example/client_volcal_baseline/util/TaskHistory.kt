package com.example.client_volcal_baseline.util

import android.content.Context
import org.json.JSONArray

object TaskHistory {
    private const val PREFS = "task_history"
    private const val KEY = "ids"

    fun getAll(ctx: Context): List<String> {
        val pref = ctx.getSharedPreferences(PREFS, 0)
        val json = pref.getString(KEY, "[]")
        val arr = JSONArray(json)
        return List(arr.length()) { idx -> arr.getString(idx) }
    }

    fun add(ctx: Context, id: String) {
        val list = getAll(ctx).toMutableList()
        if (!list.contains(id)) list.add(0, id)
        val arr = JSONArray()
        list.forEach { arr.put(it) }
        val pref = ctx.getSharedPreferences(PREFS, 0)
        pref.edit().putString(KEY, arr.toString()).apply()
    }
}
