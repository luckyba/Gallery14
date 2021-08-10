package com.luckyba.myapplication.data.provider

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import java.util.*

class Query internal constructor(builder: Builder) {
    var uri: Uri?
    var projection: Array<String>?
    var selection: String?
    var args: Array<String?>
    var sort: String?
    var ascending: Boolean
    var limit: Int

    fun getCursor(cr: ContentResolver): Cursor? {
        return cr.query(uri!!, projection, null, null, sortBy())
    }

    private fun sortBy(): String? {
        if (sort == null && limit == -1) return null
        val builder = StringBuilder()
        if (sort != null) builder.append(sort) else builder.append(1)
        builder.append(" ")
        if (!ascending) builder.append("DESC").append(" ")
        if (limit != -1) builder.append("LIMIT").append(" ").append(limit)
        return builder.toString()
    }

    class Builder {
        var uri: Uri? = null
        var projection: Array<String>? = null
        var selection: String? = null
        var args: Array<Any>? = null
        var sort: String? = null
        var limit = -1
        var ascending = false
        fun uri(uri: Uri?): Builder {
            this.uri = uri
            return this
        }

        fun projection(`val`: Array<String>?): Builder {
            projection = `val`
            return this
        }

        fun selection(`val`: String?): Builder {
            selection = `val`
            return this
        }

        fun args(args: Array<Any>): Builder {
            this.args = args
            return this
        }

        fun sort(sort: String?): Builder {
            this.sort = sort
            return this
        }

        fun limit(limit: Int): Builder {
            this.limit = limit
            return this
        }

        fun ascending(ascending: Boolean): Builder {
            this.ascending = ascending
            return this
        }

        fun build(): Query {
            return Query(this)
        }

        val stringArgs: Array<String?>
            get() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    return args?.map {it.toString()}?.toTypedArray()!!
                val list = arrayOfNulls<String>(args!!.size)
                for (i in args!!.indices) list[i] = args!![i].toString()
                return list
            }
    }

    override fun toString(): String {
        return """
            Query{
            uri=$uri
            projection=${Arrays.toString(projection)}
            selection='$selection'
            args=${Arrays.toString(args)}
            sortMode='$sort'
            ascending='$ascending'
            limit='$limit'}
            """.trimIndent()
    }

    init {
        uri = builder.uri
        projection = builder.projection
        selection = builder.selection
        args = builder.stringArgs
        sort = builder.sort
        ascending = builder.ascending
        limit = builder.limit
    }
}