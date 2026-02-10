package id.co.psplauncher.data.network.response

class AbsenUploadResponse (
        val success: Boolean,
        val message: String,
        val file_url: String? = null // Opsional, jika server mengembalikan link file yang diupload
    )