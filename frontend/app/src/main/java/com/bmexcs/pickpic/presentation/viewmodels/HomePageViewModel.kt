package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.repositories.EventRepository
import com.bmexcs.pickpic.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomePageViewModel"

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<EventMetadata>>(emptyList())
    val events: StateFlow<List<EventMetadata>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun setEvent(event: EventMetadata) {
        eventRepository.setCurrentEvent(event)
    }

    // delete the event locally without needing to make a new network call
    fun deleteEventFromCache(eventId: String) {
        _events.value = _events.value.filter { it.id != eventId }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            eventRepository.deleteEvent(eventId)
        }
        deleteEventFromCache(eventId)
    }

    fun leaveInvitedEvent(eventId: String) {
        viewModelScope.launch {
            eventRepository.removeUserFromEvent(eventId, userRepository.getUser().id)
        }
        deleteEventFromCache(eventId)
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "Fetching events for user: ${userRepository.getUser().id}")

                // Execute the network call on the IO dispatcher
                val eventItems = eventRepository.getAllEventsMetadata()

                _events.value = eventItems
                _errorMessage.value = null

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching events", e)
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun isCurrentUserOwner(ownerId: String): Boolean {
        return userRepository.getUser().id == ownerId
    }

    private val photoQuotes = listOf(
        "Photography is the story I fail to put into words.",
        "When words become unclear, I shall focus with photographs.",
        "A photograph is a secret about a secret. The more it tells you, the less you know.",
        "Your first 10,000 photographs are your worst.",
        "Photography is an art of observation.",
        "The best thing about a picture is that it never changes, even when the people in it do.",
        "Photography is the beauty of life captured.",
        "In photography there is a reality so subtle that it becomes more real than reality.",
        "Taking pictures is savoring life intensely, every hundredth of a second.",
        "Photography is the only language that can be understood anywhere in the world.",
        "A camera is a save button for the mind's eye.",
        "Photography is a way of feeling, of touching, of loving.",
        "The picture that you took with your camera is the imagination you want to create with reality.",
        "The camera is an instrument that teaches people how to see without a camera.",
        "Photography is the art of making memories tangible.",
        "Every photo you take communicates something about the world.",
        "Photography helps people to see.",
        "A good photograph is one that communicates a fact, touches the heart, and leaves the viewer a changed person.",
        "There are always two people in every picture: the photographer and the viewer.",
        "Photography is the art of frozen time.",
        "You don't take a photograph, you make it.",
        "Photography is a love affair with life.",
        "A photograph is memory in the raw.",
        "The camera makes you forget you're there. It's not like you are hiding but you forget, you are just looking so much.",
        "Photography takes an instant out of time, altering life by holding it still.",
        "What I like about photographs is that they capture a moment that's gone forever, impossible to reproduce.",
        "Photography is truth.",
        "To me, photography is an art of observation. It's about finding something interesting in an ordinary place.",
        "Photography is more than a medium for factual communication of ideas. It is a creative art.",
        "The camera is an excuse to be someplace you otherwise don't belong.",
        "Photography is the simplest thing in the world, but it is incredibly complicated to make it really work.",
        "A photograph is usually looked at - seldom looked into.",
        "The whole point of taking pictures is so that you don't have to explain things with words.",
        "Photography is the art of capturing light.",
        "A photograph is a pause button for life.",
        "Photography is the only language that can be understood anywhere in the world.",
        "Photography is a way of shaping the human perception of reality.",
        "The best camera is the one that's with you.",
        "Photography is the beauty of life captured.",
        "A great photograph is one that fully expresses what one feels, in the deepest sense, about what is being photographed.",
        "Photography records the gamut of feelings written on the human face.",
        "It's not about the camera but the eye behind it.",
        "Photography is the poetry of the visual world.",
        "A photograph is like a recipe memory the finished dish.",
        "Photography is about finding extraordinary things in ordinary places.",
        "The camera sees more than the eye, so why not make use of it?",
        "Photography is the art of showing the world what you saw in your own unique way.",
        "Every photograph tells a story, what's yours?",
        "Life is like a camera: focus on what's important, capture the good times.",
        "Photography is the only profession where you can steal someone's soul without punishment.",
        "A moment captured is a moment that will never be forgotten.",
        "Photography is not about the thing photographed. It is about how that thing looks photographed.",
        "The camera is my tool. Through it I give a reason to everything around me.",
        "Photography is the story of a moment that would otherwise be forgotten.",
        "A photograph is a memory you can hold in your hands.",
        "Through photography, I can speak without words.",
        "Photography is my way of making sense of the world.",
        "Every photo you take reveals part of your soul.",
        "Photography is painting with light.",
        "The best photographs are the ones that leave something to the imagination.",
        "Photography is the art of capturing what is invisible to others.",
        "A photograph is a fragment of time that will never come back.",
        "Photography is the science of freezing time so we can examine it later.",
        "The camera is a sketchbook, an instrument of intuition and spontaneity.",
        "Photography is about capturing souls, not smiles.",
        "A photograph is a mirror that remembers.",
        "Photography is the art of making memories visible.",
        "The best photographs are those that tell stories without words.",
        "Photography is seeing what others don't see.",
        "A camera is a tool for learning how to see without a camera.",
        "Photography is the art of observation in its purest form.",
        "Every photograph is a self-portrait of the photographer.",
        "Photography is the art of showing people what they don't see every day.",
        "A photograph is a moment of life preserved forever.",
        "Photography is the bridge between reality and imagination.",
        "The best photographs come from the heart, not the lens.",
        "Photography is the silent poetry of the visual world.",
        "A photograph is a souvenir of a moment you can never relive.",
        "Photography is the art of finding beauty in chaos.",
        "The camera is a passport that lets you travel without moving your feet.",
        "Photography is about revealing what lies beneath the surface.",
        "A great photograph is one that makes you feel something.",
        "Photography is the art of capturing light and shadow in perfect harmony.",
        "The best photographs are those that make time stand still.",
        "Photography is my way of documenting the beauty I see in the world.",
        "A photograph is a window into another person's perspective.",
        "Photography is the art of making the ordinary extraordinary.",
        "The camera is my weapon against forgetting.",
        "Photography is the art of seeing what is invisible to others.",
        "A photograph is a moment of truth captured forever.",
        "Photography is my way of collecting moments I never want to forget.",
        "The best photographs are those that tell the truth but tell it slant.",
        "Photography is the art of capturing emotions in a frame.",
        "A camera is a tool that teaches people how to see without a camera.",
        "Photography is the art of preserving memories in pixels and prints.",
        "The best photographs are those that make you feel something you can't explain.",
        "Photography is my way of stopping time and holding onto moments forever.",
        "A photograph is a memory you can hold in your hands and heart.",
        "Photography is the art of finding beauty in unexpected places.",
        "The camera is my voice when words fail me.",
        "Photography is the art of capturing light and turning it into memories.",
        "A great photograph is one that speaks directly to your soul.",
        "Photography is my way of collecting pieces of the world that move me.",
        "The best photographs are those that capture the essence of a moment.",
        "Photography is the art of seeing the extraordinary in the ordinary."
    )

    val randomQuote: String
        get() = photoQuotes.random()
}
