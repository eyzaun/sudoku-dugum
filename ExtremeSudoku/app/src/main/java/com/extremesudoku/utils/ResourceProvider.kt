package com.extremesudoku.utils

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ResourceProvider {
    fun getString(@StringRes resId: Int, vararg args: Any): String
}

class AndroidResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceProvider {
    override fun getString(@StringRes resId: Int, vararg args: Any): String {
        return if (args.isEmpty()) {
            context.getString(resId)
        } else {
            context.getString(resId, *args)
        }
    }
}
