# To-Do List Android App

A modern Android to-do list application built with Kotlin, Room Database, and MVVM architecture. Manage your tasks efficiently with local storage, categories, priorities, and powerful search functionality.

## Features

- ✅ **Create, Read, Update, Delete Tasks** - Full CRUD operations
- 🏷️ **Categories** - Organize tasks by category
- ⭐ **Priority Levels** - High, Medium, Low priorities
- 🔍 **Search & Filter** - Find tasks quickly
- 📊 **Statistics** - View task completion stats
- 💾 **Local Storage** - Room Database (SQLite)
- 🎨 **Material Design 3** - Modern Android UI
- 🔄 **Real-time Updates** - LiveData reactive updates
- ⏰ **Due Dates** - Set deadlines for tasks
- ✔️ **Mark Complete** - Track completed tasks

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite wrapper)
- **Reactive**: LiveData & Flow
- **UI**: Material Design 3
- **Testing**: JUnit, Espresso

## Project Structure

```
todo-app-android/
├── app/src/main/java/com/todo/
│   ├── data/
│   │   ├── database/TaskDatabase.kt
│   │   ├── dao/TaskDao.kt
│   │   ├── model/Task.kt
│   │   └── repository/TaskRepository.kt
│   ├── ui/
│   │   ├── viewmodel/TaskViewModel.kt
│   │   ├── fragment/TaskListFragment.kt
│   │   ├── fragment/AddTaskFragment.kt
│   │   └── fragment/EditTaskFragment.kt
│   ├── adapter/TaskAdapter.kt
│   └── MainActivity.kt
├── build.gradle.kts
└── README.md
```

## Getting Started

### Prerequisites
- Android Studio (latest)
- Android SDK 24+
- Kotlin 1.8+

### Installation

```bash
git clone https://github.com/heinrichringsgart-ops/todo-app-android.git
cd todo-app-android
```

1. Open project in Android Studio
2. Wait for Gradle sync
3. Click Run or press Shift+F10

## Usage

### Create a Task
1. Tap the FAB button
2. Enter title and description
3. Select category and priority
4. Tap Save

### Edit a Task
1. Tap on a task
2. Modify details
3. Tap Update

### Delete a Task
1. Swipe left on a task
2. Confirm deletion

### Search Tasks
1. Tap search icon
2. Enter query
3. Results filter in real-time

## Database Schema

### Task Table
```sql
CREATE TABLE task (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    category TEXT NOT NULL,
    priority TEXT NOT NULL,
    dueDate TEXT,
    isCompleted INTEGER DEFAULT 0,
    createdAt TEXT NOT NULL,
    updatedAt TEXT NOT NULL
)
```

### Categories
- Work
- Personal
- Shopping
- Health
- Finance
- Other

### Priority Levels
- High
- Medium
- Low

## Dependencies

```kotlin
// Room Database
implementation("androidx.room:room-runtime:2.6.0")
kapt("androidx.room:room-compiler:2.6.0")
implementation("androidx.room:room-ktx:2.6.0")

// LiveData & ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

// Material Design 3
implementation("com.google.android.material:material:1.10.0")

// Navigation
implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
implementation("androidx.navigation:navigation-ui-ktx:2.7.4")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
```

## Architecture

This app follows the **MVVM (Model-View-ViewModel)** architecture pattern:

- **Model**: Data classes (Task, Category)
- **View**: Activities, Fragments, and Layouts
- **ViewModel**: Manages UI state and business logic
- **Repository**: Provides data from Room database
- **Database**: Room with SQLite backend

## Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## Future Enhancements

- [ ] Cloud synchronization (Firebase)
- [ ] Recurring tasks
- [ ] Task reminders & notifications
- [ ] Dark mode
- [ ] Multiple task lists
- [ ] Export to PDF
- [ ] Voice input
- [ ] Widget support
- [ ] Backup & restore

## License

MIT License

## Author

**Heinrich Ringsgart**
- GitHub: [@heinrichringsgart-ops](https://github.com/heinrichringsgart-ops)

## Support

For issues and questions, open an issue on GitHub or email heinrich.ringsgart@gmail.com

---

**Status**: In Development 🚀
