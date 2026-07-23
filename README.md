# 💎 Esentiele — Luxury AI Personal Stylist & Digital Wardrobe

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-000000?style=for-the-badge&logo=android&logoColor=3DDC84" alt="Platform">
  <img src="https://img.shields.io/badge/Kotlin-2.0+-000000?style=for-the-badge&logo=kotlin&logoColor=7F52FF" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-000000?style=for-the-badge&logo=jetpackcompose&logoColor=4285F4" alt="Compose">
  <img src="https://img.shields.io/badge/Architecture-MVVM_%2B_Clean-000000?style=for-the-badge&logo=android&logoColor=C9A96E" alt="Architecture">
  <img src="https://img.shields.io/badge/Data_Privacy-100%25_On--Device-000000?style=for-the-badge&logo=sqlite&logoColor=003B57" alt="Offline First">
</p>

<p align="center">
  <b>Esentiele</b> is a luxury, on-device AI personal styling and digital wardrobe management application for Android.<br>
  Built with a high-fashion <i>"Chanel-meets-Tech"</i> dark mode aesthetic, it empowers users to digitize their closet, generate AI outfits, track cost-per-wear analytics, program NFC Smart Hangers, plan outfit calendars, and export Vogue-style editorial cards — <b>100% offline with zero cloud dependencies.</b>
</p>

---

## ✨ Key Features

### 👗 Digital Wardrobe Atelier
* **Garment Cataloging**: Organize pieces into *Tops, Bottoms, Shoes, Outerwear, Accessories,* and *Dresses*.
* **Real-World Attributes**: Track price (₹), formality (*Casual, Smart Casual, Formal*), material, pattern, and color.
* **1-Tap Wear Logging**: Quick-log garments worn today directly from closet cards.

### 🪄 On-Device AI Stylist Engine
* **Wardrobe-Driven Outfits**: Generates complete outfit combinations (*Top + Bottom + Shoes + Outerwear/Accessory*) dynamically using **your actual closet items**.
* **Occasion & Weather Matrix**: Tailors recommendations based on weather (*Cold, Warm, Rain*) and occasion (*Date Night, Office, Casual, Travel*).
* **Personalized Styling Tips**: On-device style advice evaluating proportions, rule of thirds, and color harmony.

### 📊 Luxe Closet Insights & Cost-Per-Wear
* **Live Closet Valuation**: Real-time calculation of total closet investment (₹).
* **Cost-Per-Wear Analytics**: Dynamic formula calculation: $$\text{Cost Per Wear} = \frac{\text{Item Price}}{\max(\text{Times Worn}, 1)}$$
* **Color Distribution Visualizer**: Live color breakdown chart grouped by garment primary colors.
* **Wardrobe Utilization**: Track active vs. dormant/forgotten pieces (unworn in 30+ days).

### 🏷️ NFC Smart Hangers (Physical-to-Digital)
* **Tag Programming**: Program low-cost NFC tags attached to physical clothing hangers.
* **Tap-to-Scan**: Scan physical hangers with your phone to instantly view item stats and mark as worn.

### 📅 Style Calendar & Capsule Builder
* **Interactive Monthly Planner**: Schedule outfits for specific days on a visual monthly calendar.
* **Capsule Wardrobe Generator**: Select 3 to 15 garments to automatically calculate all possible versatile outfit combinations.

### 🖼️ Vogue-Style Magazine Lookbook Exporter
* **Editorial Card Design**: Render saved outfits into luxury high-contrast editorial graphics with Champagne Gold accents and style critiques.
* **1-Tap HD Export**: Export high-resolution PNG lookbook cards directly to your device gallery (`Pictures/Esentiele_Lookbook_HD.png`).

### ⚔️ Outfit Battle & AI Roast
* **Head-to-Head Showdown**: Compare two competing outfits side-by-side.
* **Sassy AI Roast Engine**: On-device roast comparisons evaluating style confidence and color coordination.

### 🎨 Color Palette AI & Visual Item Finder
* **Color Season Sampling**: Detect warm autumn, cool winter, clear spring, or soft summer undertones.
* **Closet Color Palette**: Map color seasons against your actual wardrobe garments.
* **Visual Search & Matcher**: Match uploaded photos against existing wardrobe items with compatibility scores.

### 🖌️ Background Removal Mask Editor & Glow Up
* **Canvas Mask Editor**: Touch-drag gesture canvas with eraser and restore brushes for clean garment cutouts.
* **Outfit Glow Up**: Interactive before/after split slider revealing styling upgrades.

---

## 🎨 Design System

Esentiele follows a custom high-fashion luxury dark design language:

| Token Name | Hex Code | Visual Preview |
| :--- | :---: | :---: |
| **Obsidian Dark Background** | `#0D0D0D` | ![#0D0D0D](https://via.placeholder.com/15/0D0D0D/0D0D0D.png) |
| **Charcoal Surface** | `#1A1A1A` | ![#1A1A1A](https://via.placeholder.com/15/1A1A1A/1A1A1A.png) |
| **Champagne Gold Accent** | `#C9A96E` | ![#C9A96E](https://via.placeholder.com/15/C9A96E/C9A96E.png) |
| **Ivory Cream Typography** | `#F5F0E8` | ![#F5F0E8](https://via.placeholder.com/15/F5F0E8/F5F0E8.png) |

---

## 🛠️ Tech Stack & Architecture

* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 components
* **Language**: Kotlin 2.0+
* **Architecture**: MVVM + Clean Architecture with Repository Pattern
* **Local Database**: [Room Database v2](https://developer.android.com/training/data-storage/room) with reactive `Flow` queries
* **Local Preferences**: [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Auth sessions & profile preferences)
* **Security**: SHA-256 local password hashing
* **On-Device Intelligence**: HSV color space analysis, aspect-ratio classification, and deterministic styling rule matrices (0 API costs, 100% private)

---

## 📂 Project Structure

```
com.esentiele.app/
├── MainActivity.kt               # Entry point, Theme setup & NavHost wrapper
├── EsentieleApplication.kt        # App Context Initialization
├── data/
│   ├── local/
│   │   ├── ClothingDao.kt        # Room DAO queries for garments & wear history
│   │   ├── ClothingItemEntity.kt # Database entity for clothing items
│   │   ├── EsentieleDatabase.kt   # Database database singleton (v2)
│   │   ├── OutfitDao.kt          # Room DAO queries for outfits & scheduling
│   │   ├── OutfitEntity.kt        # Database entity for saved outfit sets
│   │   └── UserPreferences.kt   # DataStore manager for Auth & Settings
│   ├── remote/
│   │   └── LocalStylingEngine.kt # On-device AI recommendation & roast engine
│   └── repository/
│       └── WardrobeRepository.kt # Repository abstraction layer
├── domain/
│   └── model/
│       ├── ClothingItem.kt        # Domain model for garments
│       └── Outfit.kt              # Domain model for outfits
└── ui/
    ├── auth/                     # Login & SignUp screens
    ├── battle/                   # Outfit Battle & AI Roast screen
    ├── calendar/                 # Outfit Calendar & Capsule Builder
    ├── color/                    # Color Season & Closet Palette screen
    ├── components/                # Reusable UI components & Bottom Navigation
    ├── dashboard/                # Main Home Dashboard & Today's Look logger
    ├── glowup/                   # Interactive Before/After style slider
    ├── insights/                 # Closet Analytics & Cost-Per-Wear
    ├── itemfinder/               # Visual Item Finder & Closet Matcher
    ├── lookbook/                 # Vogue-Style Magazine Lookbook Exporter
    ├── maskeditor/               # Background removal canvas tool
    ├── navigation/               # NavHost routes & bottom bar menu
    ├── nfc/                      # NFC Smart Hangers screen
    ├── onboarding/               # 5-step personalized style wizard
    ├── outfitcheck/              # AI outfit critique & radial scoring
    ├── profile/                  # User profile & preferences
    ├── stylist/                  # AI Outfit Generator screen
    ├── theme/                    # Color palette & Material3 styling
    ├── travel/                   # Travel Packing Assistant screen
    └── wardrobe/                 # Digital Closet & Add Item dialog
```

---

## 🚀 Getting Started

### Prerequisites
* Android Studio Ladybug (2024.2.1) or newer
* JDK 17 / JDK 21
* Android SDK 34+ (Android 14)
* Physical or virtual device running Android 8.0 (API 26) or higher

### Build & Run
1. **Clone the repository**:
   ```bash
   git clone https://github.com/Chatur7x/Esentiele.git
   cd Esentiele
   ```
2. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
3. **Install on device**:
   ```bash
   ./gradlew installDebug
   ```

The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🔒 Privacy & On-Device Guarantee

Esentiele is built with an **uncompromising privacy-first philosophy**:
* Zero server calls or analytics tracking.
* No personal data or closet photos leave your device.
* All AI style generation, color extraction, and recommendations process locally in real-time.

---


<p align="center"> Designed & Developed with ✨ for Luxury Digital Wardrobes </p>
