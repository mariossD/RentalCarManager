# RentalCarManager

**RentalCarManager** is an Android application developed in Kotlin that provides a complete solution for managing a car rental company. The app supports full CRUD operations (Create, Read, Update, Delete) for customers, cars, branches, and rental records. It uses Room for local data storage and Firebase Firestore for remote rental management.

## Features

- Add, view, edit, and delete:
  - Customers
  - Cars
  - Branches
  - Rentals
- Search and filter data using both Room and Firebase
- Real-time UI updates using `StateFlow` and `ViewModel`
- Live data observation and reactive UI
- Dynamic theming (Dark / Light mode)
- Local notifications on successful rental creation
- Navigation through Navigation Drawer

## Technologies Used

- Kotlin
- MVVM Architecture
- Room Database (local storage)
- Firebase Firestore (remote rentals)
- Jetpack Navigation Component
- Material Design 3
- Coroutines & Kotlin Flow
- StateFlow & LiveData
- ViewModel
- RecyclerView & AlertDialogs

## Screens

- **Home**: Welcome screen with full-width image
- **Cars**: Car fleet management (list, add, edit, delete)
- **Customers**: Customer records management
- **Branches**: Manage branch details
- **Rentals**: Create and manage car rental records
- **Queries**: Search data based on criteria (Room & Firebase)
- **Settings**: Toggle Dark / Light theme


