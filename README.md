# KharchPani - Modern Expense Tracker

KharchPani is a minimal, modern, and offline-first Android expense tracking application built using **Kotlin**, **Jetpack Compose**, and **Material 3**.

---

## 🚀 Key Features

- **Daily Expense Tracking**: Add, edit, and delete expenses with descriptions and dates.
- **Selection Mode**: Long-press any expense to select multiple items and mark them as "Paid" or "Unpaid".
- **Advanced History**: A dedicated History screen with date-range and single-date filtering.
- **Data Visualization**: Weekly and Monthly bar charts to visualize spending patterns.
- **Smart Formatting**: Handles very large amounts gracefully using a compact currency formatter (e.g., ₹1.2M, ₹500K).
- **Export/Import**: Backup your data as a JSON file and restore it whenever needed.
- **Multilingual Tutorial**: Integrated guide in both English and Hindi.
- **Theme Support**: Seamless switching between Light, Dark, and System default themes.

---

## 📂 Why the Storage Permission? (SAF)

When you first open KharchPani, it asks you to **"Select a Folder"**. 

### 1. Data Safety (Uninstall-Proof)
Most apps store data in a "Private Sandbox." When you delete those apps, your data is deleted too. KharchPani uses the **Android Storage Access Framework (SAF)** to store your data in a folder you choose (like your `Documents` folder).
**Result:** If you uninstall the app and reinstall it later, your expenses will reappear as soon as you select that same folder again.

### 2. Privacy
Your data never leaves your device. There is no cloud, no login, and no tracking. You own your data.

---

## 🛠 How to Set Up (First Launch)

1.  **Launch the App**: On the first run, you will see a system folder picker.
2.  **Choose a Location**: Navigate to your `Documents` folder (or create a new folder named `MyExpenseApp`).
3.  **Grant Access**: Tap **"Use this folder"** and then **"Allow"**.
4.  **Automatic Creation**: The app will automatically create an `expenses.json` file inside that folder. This is your database.

> **Note:** Do not delete or manually edit the `expenses.json` file inside this folder, as it may corrupt your data.

---

## 📖 App Tutorial

### Home Screen
- Displays totals for **Today**, **Yesterday**, **This Week**, and **This Month**.
- Quick filters for recent expenses.
- **Double-Tap**: Edit or Delete an expense.
- **Long-Press**: Enter selection mode to mark multiple items as paid.

### History Screen
- View every record ever created.
- Use the **Date Range Picker** to find exactly what you spent during a specific trip or month.

### Settings
- Toggle the Tutorial between English and Hindi.
- Change the App Theme.

---

## 📸 Visual Guide

| Home Screen | Selection Mode | Analytics |
| :---: | :---: | :---: |
| ![Home](https://via.placeholder.com/200x400?text=Home+Screen) | ![Selection](https://via.placeholder.com/200x400?text=Selection+Mode) | ![Analytics](https://via.placeholder.com/200x400?text=Analytics+Screen) |

*(Note: Replace placeholders with actual screenshots from your device for a better look!)*

---

## 💻 Tech Stack
- **UI**: Jetpack Compose (Material 3)
- **Navigation**: Compose Navigation
- **Storage**: Storage Access Framework (SAF) + Kotlinx Serialization (JSON)
- **Charts**: Vico Charting Library
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture principles
- **Preferences**: Jetpack DataStore
