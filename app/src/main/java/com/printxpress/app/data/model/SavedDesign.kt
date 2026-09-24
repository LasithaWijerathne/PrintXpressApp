package com.printxpress.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

/** Mirrors a document in `users/{uid}/savedDesigns`. */
data class SavedDesign(
    var id: String = "",
    var fileUrl: String = "",
    var fileName: String = "",
    @ServerTimestamp var createdAt: Timestamp? = null
)
