class ProgressActivity : AppCompatActivity() {

    private lateinit var repo: ProgressRepository
    private lateinit var manager: ProgressManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        repo = ProgressRepository(this)
        manager = ProgressManager(repo)

        val total = manager.getTotalProgress()
        findViewById<TextView>(R.id.tvTotalPercent).text = "$total%"
        findViewById<ProgressBar>(R.id.progressBarTotal).progress = total

        setRow(R.id.progressUpload, "Upload File", manager.getUploadProgress())
        setRow(R.id.progressSummary, "Ringkasan", manager.getSummaryProgress())
        setRow(R.id.progressQuiz, "Quiz", manager.getQuizProgress())
        setRow(R.id.progressChatbot, "Chatbot", manager.getChatbotProgress())
    }

    private fun setRow(id: Int, title: String, progress: Int) {
        val view = findViewById<View>(id)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvPercent = view.findViewById<TextView>(R.id.tvPercent)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressItem)

        tvTitle.text = title
        tvPercent.text = "$progress%"
        progressBar.progress = progress
    }
}