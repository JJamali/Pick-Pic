package com.bmexcs.pickpic.data.repositories

import android.graphics.BitmapFactory
import android.util.Log
import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.models.EventMember
import com.bmexcs.pickpic.data.models.ImageInfo
import com.bmexcs.pickpic.data.models.BitmapRanked
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.sources.EventDataSource
import com.bmexcs.pickpic.data.sources.ImageDataSource
import com.bmexcs.pickpic.data.utils.Vote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "EventRepository"

@Singleton
class EventRepository @Inject constructor(
    private val eventDataSource: EventDataSource,
    private val imageDataSource: ImageDataSource,
) {
    private val _cachedEvents = MutableStateFlow<List<EventInfo>>(emptyList())
    val cachedEvents: StateFlow<List<EventInfo>> = _cachedEvents

    private val _currentEventInfo = MutableStateFlow(EventInfo())
    val currentEvent = _currentEventInfo

    private var timestamp: Long = 0

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val mutex = Mutex()
    private var unrankedImageCount: Int = 0
    private val unrankedImageChannel = Channel<BitmapRanked>(capacity = QUEUE_SIZE)

    companion object {
        private const val QUEUE_SIZE = 5
    }

    suspend fun getEvents(): List<EventInfo> {
        if (_cachedEvents.value.isNotEmpty()) {
            return _cachedEvents.value
        }

        val events = eventDataSource.getEvents()
        _cachedEvents.value = events
        return events
    }

    suspend fun getEventInfo(eventId: String): EventInfo {
        return eventDataSource.getEventInfo(eventId)
    }

    suspend fun getEventOwnerInfo(ownerId: String): User {
        return eventDataSource.getEventOwnerInfo(ownerId)
    }

    suspend fun isUpdated(eventId: String): Boolean {
        val lastModified =  eventDataSource.getEventLastModified(eventId)

        return if (lastModified > timestamp) {
            timestamp = lastModified
            true
        } else {
            false
        }
    }

    suspend fun createEvent(name: String): EventInfo {
        return eventDataSource.createEvent(name)
    }

    suspend fun deleteEvent(id: String) {
        eventDataSource.deleteEvent(id)
    }

    suspend fun getUserEventsPending(): List<EventMember> {
        return eventDataSource.getEventsPending()
    }

    suspend fun acceptEvent(eventId: String) {
        eventDataSource.acceptEvent(eventId)
    }

    suspend fun declineEvent(eventId: String) {
        eventDataSource.declineEvent(eventId)
    }

    suspend fun addUserToEvent(eventId: String, userId: String) {
        eventDataSource.addUserToEvent(eventId, userId)
    }

    fun setCurrentEvent(eventInfo: EventInfo) {
        currentEvent.value = eventInfo
        repositoryScope.launch {
            fillUnrankedQueue()
        }
    }

    suspend fun getImages(eventId: String): List<ImageInfo> {
        return eventDataSource.getImageInfo(eventId)
    }

    suspend fun voteOnImage(imageId: String, vote: Vote) {
        eventDataSource.voteOnImage(currentEvent.value.event_id, imageId, vote)
    }

    suspend fun getUnrankedImage(): BitmapRanked {
        val image = unrankedImageChannel.receive()
        Log.d(TAG, "getUnrankedImage: received image ${image.info.image.image_id}")

        mutex.withLock { unrankedImageCount-- }

        repositoryScope.launch { fillUnrankedQueue() }
        return image
    }

    private suspend fun fillUnrankedQueue() {
        mutex.withLock {
            val countNeeded = QUEUE_SIZE - unrankedImageCount
            if (countNeeded <= 0) return

            Log.d(TAG, "Filling queue with $countNeeded images")

            val imageInfos = eventDataSource.getUnrankedImages(currentEvent.value.event_id, countNeeded)

            for (imageInfo in imageInfos) {
                val bitmapRanked =
                    imageDataSource.getImageBinary(currentEvent.value.event_id, imageInfo.image.image_id)
                        ?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                        ?.let { BitmapRanked(imageInfo, it) }

                if (bitmapRanked != null) {
                    unrankedImageChannel.send(bitmapRanked)
                    unrankedImageCount++
                }
            }
        }
    }

    suspend fun inviteUsersWithId(userIds: List<String>, eventId: String)  {
        return eventDataSource.inviteUsersFromEmail(userIds, eventId)
    }
}
