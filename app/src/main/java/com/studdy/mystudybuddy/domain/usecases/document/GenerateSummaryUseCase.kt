import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.*

class GenerateSummaryUseCase {

    suspend operator fun invoke(context: android.content.Context, text: String): String {
        return withContext(Dispatchers.IO) {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(context.applicationContext))
            }
            val py = Python.getInstance().getModule("summarizer")
            py.callAttr("ringkas_teks", text).toString()
        }
    }
}