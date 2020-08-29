package com.moamen.letspractice

import com.google.firebase.Timestamp

class CompetitionTime(var time:Timestamp) {
    constructor() : this(Timestamp.now()) {
    }
}
