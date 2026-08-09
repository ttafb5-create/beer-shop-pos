# Beer Shop POS - Android POS Application

## 🍺 Beer Shop / Bar Point of Sale System
### Phase 1 - Core POS | Offline-First Architecture

---

## 🚀 Features

### Authentication
- Role-based: Owner, Manager, Cashier
- SHA-256 password hashing
- Permission-based access control

### Dashboard
- Today's sales summary
- Open/Closed tables count
- Cash and wallet balance tracking
- Payment method breakdown
- Quick action buttons

### Table Management
- Visual table layout with color-coded status
  - 🟢 Available | 🔴 Occupied | 🟡 Reserved | 🟣 Held
- Create/manage tables with zone support
- Transfer orders between tables
- Merge tables
- Split bills to new tables
- Hold/Resume orders

### Product Management
- Full CRUD with categories: Beer, Whisky, Alcohol, Soft Drinks, Food, Snacks, Other
- Myanmar language product names
- Stock tracking with quantity
- Barcode support
- Cost and selling price
- Tax rate per product

### Order Screen
- Split view: Products (left) | Order Items (right)
- Search products by name/barcode
- Category filter chips
- Quantity +/- controls
- Item notes
- Discount (percentage) with note
- Service charge
- Tax calculation
- Subtotal and Grand Total

### Payment
- Multiple payment methods:
  - 💵 Cash | 📱 KBZPay | 🌊 Wave Money | 🅰️ AYA Pay | 💳 CB Pay | 🏦 Bank Transfer
- Mixed payment support (split payment across methods)
- Change calculation
- Reference number tracking

### Thermal Printer
- Bluetooth thermal printer support
- 58mm and 80mm paper sizes
- Receipt includes: Shop Name, Address, Phone, Date/Time, Table, Cashier, Items, Totals, Payment Method
- Kitchen order printing
- Reprint last receipt

### Offline-First
- Room database for all local storage
- Works fully without internet
- Automatic fallback to local data

### Cloud Sync
- Firebase Firestore sync
- Unique transaction IDs to prevent duplicates
- WorkManager periodic sync (15 min)
- Manual sync option
- Sync status tracking

### Reports
- Daily/Monthly sales
- Sales by product/category
- Payment method breakdown
- Profit reports
- Table usage reports

### Settings
- Shop information
- Tax & service charge defaults
- Printer settings
- User management
- Dark/Light mode
- Myanmar/English language
- Backup & Restore
- Cloud sync toggle

---

## 🏗️ Architecture

```
BeerShopPOS/
├── app/
│   ├── src/main/java/com/beershop/pos/
│   │   ├── data/
│   │   │   ├── local/
│   │   │   │   ├── entity/      # Room entities
│   │   │   │   ├── dao/         # Data access objects
│   │   │   │   ├── AppDatabase.kt
│   │   │   │   └── Converters.kt
│   │   │   ├── repository/      # Repository pattern
│   │   │   └── DataInitializer.kt
│   │   ├── di/                  # Hilt DI modules
│   │   ├── printer/             # Thermal printer manager
│   │   ├── sync/                # Cloud sync worker
│   │   └── ui/
│   │       ├── navigation/      # Compose navigation
│   │       ├── screens/         # All UI screens
│   │       ├── theme/           # Material3 theme
│   │       └── viewmodel/       # ViewModels
```

### Tech Stack
- **Kotlin** + **Jetpack Compose** for UI
- **Room Database** for offline storage
- **Hilt** for dependency injection
- **Navigation Compose** for routing
- **WorkManager** for background sync
- **Firebase Firestore** for cloud sync
- **ESCPOS-ThermalPrinter** library for Bluetooth printing
- **MPAndroidChart** for reports
- **Coroutines + Flow** for async operations
- **Material 3** design system

---

## 🔑 Default Users

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | Owner |
| manager | manager123 | Manager |
| cashier | cashier123 | Cashier |

---

## 📋 Permissions

| Action | Owner | Manager | Cashier |
|--------|-------|---------|---------|
| Manage Users | ✅ | ❌ | ❌ |
| Manage Products | ✅ | ✅ | ❌ |
| Manage Tables | ✅ | ✅ | ❌ |
| Process Orders | ✅ | ✅ | ✅ |
| Process Payment | ✅ | ✅ | ✅ |
| View Reports | ✅ | ✅ | ❌ |
| Manage Settings | ✅ | ✅ | ❌ |
| Give Discount | ✅ | ✅ | ❌ |
| Reopen Bill | ✅ | ✅ | ❌ |
| Void Order | ✅ | ✅ | ❌ |

---

## 🖨️ Printer Setup
1. Pair your Bluetooth thermal printer in Android settings
2. Open Settings > Printer Settings in the app
3. Scan for paired printers
4. Select your printer
5. Choose paper size (58mm or 80mm)
6. Test print

---

## 🔄 Offline Sync Flow
1. All data is stored locally in Room DB first
2. Each record gets a `syncStatus` flag: PENDING → SYNCING → SYNCED
3. WorkManager detects network and auto-syncs every 15 minutes
4. Manual sync available in Settings > Sync Status
5. Unique transaction IDs prevent duplicate records
6. Offline sales are never lost

---

## 🛠️ Build Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1) or newer
- Android SDK 34
- JDK 17
- Gradle 8.2+

### Build
```bash
./gradlew assembleDebug
```

### Install
```bash
./gradlew installDebug
```

---

## 📱 Minimum Requirements
- Android 8.0 (API 26) or higher
- Bluetooth for printer
- 50MB storage
---

## 🎨 Color Scheme
- Primary: Beer Red (#B71C1C)
- Secondary: Beer Brown (#5D4037)
- Gold: Beer Gold (#FFC107)
- Surface: Warm cream tones

---

## 🔮 Phase 2 (Planned)
- Customer management & loyalty
- Kitchen Display System (KDS)
- QR code ordering
- Online ordering
- Inventory management
- Staff scheduling
- Multi-branch support
- Real-time cloud dashboard
- Export to Excel/PDF

---

Built with ❤️ for Myanmar Beer Shops & Bars
