package u.dev.autokt

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.widget.Toast
import com.tencent.mmkv.MMKV


@SuppressLint("StaticFieldLeak")
val app = App.context
val kv = MMKV.defaultMMKV()

fun Any.shortToast(context: Context){
    Toast.makeText(context,this.toString(),Toast.LENGTH_SHORT).show()

}


fun ApplicationInfo.loadName() = this.loadLabel(app.packageManager).toString()


