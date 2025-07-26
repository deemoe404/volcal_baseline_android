package com.example.client_volcal_baseline.util

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class TaskEntry(val id: String, val time: Long)

object TaskHistory {
    private const val PREFS = "task_history"
    private const val KEY = "ids"

    fun getAll(ctx: Context): List<TaskEntry> {
        val pref = ctx.getSharedPreferences(PREFS, 0)
        val json = pref.getString(KEY, "[]")
        val arr = JSONArray(json)
        val list = mutableListOf<TaskEntry>()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i)
            if (obj != null) {
                val id = obj.optString("id")
                val time = obj.optLong("time")
                if (id.isNotEmpty()) list.add(TaskEntry(id, time))
            } else {
                val id = arr.optString(i)
                if (id.isNotEmpty()) list.add(TaskEntry(id, 0L))
            }
        }
        return list.sortedBy { it.time }
    }

    fun add(ctx: Context, id: String) {
        val list = getAll(ctx).filter { it.id != id }.toMutableList()
        list.add(TaskEntry(id, System.currentTimeMillis()))
        save(ctx, list)
    }

    private fun save(ctx: Context, list: List<TaskEntry>) {
        val arr = JSONArray()
        list.sortedBy { it.time }.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("time", it.time)
            arr.put(obj)
        }
        val pref = ctx.getSharedPreferences(PREFS, 0)
        pref.edit().putString(KEY, arr.toString()).apply()
    }
}
