# 🎮 Tic Tac Toe - Jetpack Compose

A Tic Tac Toe game built using **Jetpack Compose** with focus on 
**state management, MVVM, and UI animations**.

---

## 🚀 What I Focused On

- State handling using `mutableStateListOf`
- Game logic (win + draw detection)
- Clean separation using **ViewModel (MVVM)**
- Maintaining **Material 3 design consistency**

---

## ✨ UI & Animations

- Animated turn text
- Winning cells highlight animation
- Restart button with visibility animation
- Lottie animations for win state

---

## 🧠 Game Logic

- Board uses integer mapping:
  - `0 → empty`
  - `1 → X`
  - `-1 → O`
- Winning combinations checked after each move
- Winning path stored to highlight UI
- Draw detection when board is full

---

## 🛠 Tech Stack

- Kotlin
- Jetpack Compose
- MVVM
- Material 3
- Lottie

---

## 💡 Note

This project was built as a **learning project** to improve:
- Compose UI structuring
- State management
- Animation usage

---

## 📂 Structure

view/ → UI (Compose)  
viewmodel/ → Game logic  
