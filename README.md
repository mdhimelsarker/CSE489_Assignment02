# CSE 489 Assignment 2 - Kotlin Android Application

A comprehensive Android application built in Kotlin using XML layouts and Material Design 3, implementing a multi-feature Navigation Drawer architecture with Broadcast Receivers, HTTPS Image Zooming, and Media3 ExoPlayer Audio/Video streaming.

---

## 📱 Features Implemented

### PART 1: Navigation Drawer & UI Architecture
- **MainActivity**: Implements `DrawerLayout`, `NavigationView`, and `ActionBarDrawerToggle` for seamless slide-out navigation.
- **Fragment Management**: Dynamic fragment swapping for all major assignment sections.
- **Back Button Handling**: Custom `OnBackPressedCallback` closes the drawer if open or returns to the default home screen before exiting.

### PART 2: Broadcast Receivers
- **Custom Broadcast Flow**: 
  1. Input custom message in `CustomMessageActivity`.
  2. Pass message via Intent to `CustomBroadcastActivity`.
  3. Send custom intent (`com.example.myapplication.ACTION_CUSTOM_BROADCAST`).
  4. Dynamically caught by `MyCustomReceiver` and displayed instantly on screen.
- **System Battery Receiver Flow**:
  - Registers dynamic broadcast receiver for `ACTION_BATTERY_CHANGED` (sticky broadcast).
  - Displays real-time battery percentage and charging status dynamically.

### PART 3: Image Scale with Pinch-to-Zoom
- **Internet Image Loading**: Uses **Glide** to load high-resolution images from publicly accessible HTTPS URLs asynchronously with progress spinners and error handling.
- **Custom `ZoomImageView`**:
  - **Pinch-to-Zoom**: Two-finger scale gesture detection (`ScaleGestureDetector`) for smooth scaling between 1.0x and 5.0x.
  - **Panning**: Single-finger drag when zoomed in (`GestureDetector`) with strict boundary constraints (`checkAndBoundMatrix()`) to prevent the image from dragging off-screen.
  - **Double-Tap & Reset**: Double-tap gesture toggles zoom between 1.0x and 2.5x. Reset zoom button restores initial layout bounds.
  - **Zoom Badge**: Real-time zoom level indicator badge (e.g. `Zoom: 1.5x`).

### PART 4: Video Player & Audio Player (AndroidX Media3 ExoPlayer)
- **Video Player Fragment**:
  - Streams HTTPS MP4 video using AndroidX Media3 `ExoPlayer` and `PlayerView`.
  - Native playback controls (play, pause, seekbar, buffering indicator, error container with retry).
  - Includes 3 reliable pre-set sample video streams (MDN Flower, VideoJS Oceans, W3Schools Big Buck Bunny).
- **Audio Player Fragment**:
  - Streams HTTPS MP3 audio tracks via `ExoPlayer`.
  - Dedicated **Play**, **Pause**, and **Stop** controls.
  - Interactive **SeekBar** with real-time progress tracking, current time (`00:00`), and total duration.
  - Lifecycle awareness (pauses playback on `onStop()`, releases audio decoder resources in `onDestroyView()`).

---

## 🛠️ Project Tech Stack & Architecture

- **Language**: Kotlin 2.2+
- **Min SDK**: API 24 (Android 7.0 Nougat)
- **Compile SDK**: API 35
- **UI Toolkit**: XML Layouts & Material Design 3 (`com.google.android.material`)
- **View Binding**: Enabled across all Activities and Fragments
- **Image Loading**: Glide (`4.16.0`)
- **Media Playback**: AndroidX Media3 ExoPlayer (`1.5.1`)
- **Permissions**: `INTERNET` permission configured in `AndroidManifest.xml`

---

## 🚀 Setup & Execution Instructions

1. **Prerequisites**:
   - Install **Android Studio** (Koala, Ladybug, or newer).
   - Ensure JDK 11 or higher is configured.
   - Android Emulator or physical Android device (API 24+).
2. **Opening the Project**:
   - Open Android Studio and select **Open**.
   - Navigate to the project root directory: `Assignment_02`.
3. **Gradle Sync**:
   - Allow Gradle to sync dependencies automatically. If needed, click **Sync Project with Gradle Files**.
4. **Running the App**:
   - Connect an emulator or device.
   - Click **Run 'app' (Shift + F10)**.

---

## 📋 Testing Checklist

| Part | Feature | Verification Steps |
| :--- | :--- | :--- |
| **Part 1** | Navigation Drawer | Tap hamburger icon or swipe right from screen edge. Select different items to switch screens. |
| **Part 2A** | Custom Broadcast | Select Custom Broadcast option, enter a message, proceed, and click "Send Broadcast" to see message received. |
| **Part 2B** | Battery Receiver | Select Battery Receiver option to view live battery percentage and charging status. |
| **Part 3** | Image Pinch-to-Zoom | Open Image Scale screen, load HTTPS image, pinch with two fingers to zoom, drag to pan, double-tap to toggle zoom. |
| **Part 4A** | Video Player | Open Video screen, tap Sample 1, 2, or 3, and use native ExoPlayer play/pause/seek controls. |
| **Part 4B** | Audio Player | Open Audio screen, tap Load Audio / Play, use Play/Pause/Stop and scrub the SeekBar. |

---

## 💡 Viva Concepts & Quick Answers

- **What is AndroidX Media3 ExoPlayer?**
  - ExoPlayer is Google's extensible, application-level media player for Android. It supersedes the legacy `MediaPlayer` API, supporting adaptive streaming (HLS/DASH) and rich player state listeners.
- **Why must we release media players?**
  - Media codecs and audio/video hardware buffers are limited system resources. Releasing them in `onStop()` / `onDestroyView()` prevents memory leaks, audio focus conflicts, and background battery drain.
- **How does Pinch-to-Zoom work?**
  - `ScaleGestureDetector` calculates multi-finger distance ratios (`scaleFactor`), which update a 3x3 transformation `Matrix` applied to a custom `ImageView` (`ScaleType.MATRIX`). Boundary checks prevent dragging the image out of viewport bounds.
- **What is a Sticky Broadcast?**
  - A sticky broadcast (e.g., `ACTION_BATTERY_CHANGED`) retains its intent data in the system cache, allowing apps to query the last broadcast value immediately upon registration without waiting for a new event.
