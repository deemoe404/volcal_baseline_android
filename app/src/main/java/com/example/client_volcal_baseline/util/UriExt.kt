package com.example.client_volcal_baseline.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.util.UUID

fun Uri.copyToTemp(ctx: Context): File {
    val input = ctx.contentResolver.openInputStream(this)!!
    val out = File(ctx.cacheDir, UUID.randomUUID().toString())
    input.use { it.copyTo(out.outputStream()) }
    return out
}

fun Uri.displayName(ctx: Context): String {
    if (scheme == "content") {
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
        ctx.contentResolver.query(this, projection, null, null, null)?.use { c ->
            if (c.moveToFirst()) return c.getString(0)
        }
    }

    return lastPathSegment ?: UUID.randomUUID().toString()
}
