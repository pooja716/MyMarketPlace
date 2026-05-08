# MyMarketPlace

A native Android marketplace app built with Kotlin + Jetpack Compose. Users can browse 200 listings, mark favorites, and create new listings while offline. The app syncs automatically when connectivity returns.

---

## Features

| Requirement | Implementation |
|---|---|
| Browse listings | `LazyVerticalGrid` with 200 items, tab filters (All / Favorites / Pending Sync) |
| Mark favorites | Persisted in Room, survives offline + sync |
| Create listing offline | Saved locally with `PENDING_CREATE` status, queued for sync |
| Offline sync + conflict resolution | `SyncWorker` (WorkManager) + last-write-wins on `updatedAt` |
| Sync status indicator | Animated banner + pending badge on each card |
| Image caching | Coil 3 with 20% memory cap + 50 MB disk cache |
| Camera / photo picker | `TakePicture` + `PickVisualMedia` contracts with runtime permission |
| Background upload | WorkManager `SyncWorker` with `NETWORK_CONNECTED` constraint |
| Secure token storage | `DataStore Preferences` via `TokenManager` |
| Input validation | Double-layer: `CreateListingViewModel` + `CreateListingUseCase` |
| Mock REST API | `MockApiInterceptor` intercepts all calls — no server needed |

---

## Architecture

```
app/
├── data/
│   ├── local/          # Room DB, DAO, Entity, TypeConverters
│   ├── remote/         # Retrofit API, DTOs, MockApiInterceptor, AuthInterceptor
│   ├── mapper/         # ListingMapper — Entity/DTO ↔ Domain model conversions
│   ├── repository/     # ListingRepositoryImpl — single source of truth
│   ├── scheduler/      # SyncSchedulerImpl — WorkManager scheduling
│   └── service/        # SyncWorker (WorkManager + HiltWorkerFactory)
├── di/                 # Hilt modules (App, Network, Database, Repository, Connectivity)
├── domain/
│   ├── model/          # Pure Kotlin data classes (Listing, SyncStatus, PendingAction, etc.)
│   ├── repository/     # ListingRepository interface
│   ├── scheduler/      # SyncScheduler interface
│   └── usecase/        # CreateListing, ObserveListings, ToggleFavorite,
│                       # GetListingById, RefreshListings, SyncPendingListings
├── presentation/
│   ├── listing/        # ListingsScreen + ListingsViewModel + components
│   ├── create/         # CreateListingScreen + CreateListingViewModel
│   ├── detail/         # ListingDetailScreen + ListingDetailViewModel
│   ├── favorites/      # FavoritesScreen
│   ├── navigation/     # NavGraph (Listings → Create → Detail)
│   └── common/         # TopBar, UiSyncStatus
├── ui/theme/           # Color, Typography, Theme, Dimens
└── util/               # ConnectivityObserver, TokenManager, ImageCacheManager
```

Clean Architecture layers: `Presentation → Domain ← Data`. The Domain layer has zero Android dependencies.

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────┐
│                  Presentation Layer                  │
│  ListingsVM ── ListingsScreen                        │
│  CreateListingVM ── CreateListingScreen              │
│  ListingDetailVM ── ListingDetailScreen              │
└──────────────────────┬──────────────────────────────┘
                       │  Use Cases + SyncScheduler
┌──────────────────────▼──────────────────────────────┐
│                   Domain Layer                       │
│  ListingRepository (interface)                       │
│  SyncScheduler (interface)                           │
│  ObserveListings / CreateListing / ToggleFavorite    │
│  GetListingById / RefreshListings / SyncPending      │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                    Data Layer                        │
│  ListingRepositoryImpl                               │
│       ├── Room DB (ListingDao / ListingEntity)       │
│       ├── Retrofit API (MockApiInterceptor)          │
│       └── ListingMapper (Entity/DTO ↔ Domain)        │
│  SyncSchedulerImpl ── WorkManager                    │
│  SyncWorker ── SyncPendingListingsUseCase            │
└─────────────────────────────────────────────────────┘
```

---

## Sequence Diagram — Offline Create + Sync

```
User        CreateListingVM     UseCase        Repository       Room DB       SyncWorker     MockAPI
 │                 │               │               │               │               │             │
 │──POST listing──►│               │               │               │               │             │
 │                 │──invoke()────►│               │               │               │             │
 │                 │               │──createListing►               │               │             │
 │                 │               │               │──INSERT───────►               │             │
 │                 │               │               │   (PENDING_CREATE)            │             │
 │                 │◄──success─────│               │               │               │             │
 │◄──navigate back─│               │               │               │               │             │
 │                 │               │               │               │               │             │
 │      [Network becomes available]                │               │               │             │
 │                 │               │               │               │               │             │
 │                 │──scheduleSync()──────────────────────────────►│               │             │
 │                 │               │               │               │──doWork()────►│             │
 │                 │               │               │◄──getPending()────────────────│             │
 │                 │               │               │────entities──►│               │             │
 │                 │               │               │               │──POST /listings────────────►│
 │                 │               │               │               │◄──ListingDto───────────────│
 │                 │               │               │◄──INSERT SYNCED───────────────│             │
```

---

## Conflict Resolution

Strategy: **Last-Write-Wins** on `updatedAt` timestamp.

- On sync, server response contains `updatedAt`; this value overwrites local `updatedAt`.
- The local `isFavorite` flag is **always preserved** — user preference is never overwritten by the server.
- Items with `PENDING_CREATE` or `PENDING_UPDATE` are **skipped during refresh** — local edits are never overwritten without going through a sync cycle.
- When the server assigns a new ID to a locally-created item, the old local UUID row is deleted and replaced with the server-assigned ID.

---

## Sync Status Model

| `SyncStatus` | Meaning |
|---|---|
| `SYNCED` | In sync with server |
| `PENDING_CREATE` | Created offline, not yet pushed |
| `PENDING_UPDATE` | Edited offline, not yet pushed |
| `CONFLICT` | Reserved for future merge-strategy use |

---

## Design System

All UI constants are centralised in `ui/theme/`:

| File | Purpose |
|---|---|
| `Color.kt` | Named semantic colors (`PendingBadgeBackground`, `FavoriteIconTint`, `OnlineDot`, etc.) |
| `Dimens.kt` | Size constants (`size4`, `size8`, `size16`, etc.) — no hardcoded `.dp` in screens |
| `Type.kt` | Typography scale |
| `Theme.kt` | Material3 theme wiring |

---

## Performance Considerations (200 Listings)

| Concern | Approach |
|---|---|
| List rendering | `LazyVerticalGrid` with stable `key = { it.id }` — only changed items recompose |
| Image memory | Coil `MemoryCache` capped at 20% of heap (~50 MB on typical device) |
| Image disk | 50 MB disk cache; images sized to thumbnail resolution (320×240 px) via `.size()` |
| DB writes on refresh | Single `insertAll()` transaction instead of 200 individual inserts |
| Pending items skipped | `refreshListings()` pre-fetches all existing rows into a `Map` — O(1) lookup |
| Background sync | WorkManager ensures sync runs off the main thread with retry on failure |
| CPU | Room `Flow` uses `distinctUntilChanged` semantics; recomposition gated by `collectAsStateWithLifecycle` |

---

## Unit Tests

Located in `app/src/test/.../data/repository/ListingRepositoryTest.kt`:

1. **`syncPending creates listing on API and marks as SYNCED`** — verifies `CREATE` path calls the API and writes back `SYNCED` status.
2. **`syncPending applies last-write-wins on conflict`** — verifies server `updatedAt` and `title` win over local values.
3. **`createListing when offline saves locally with PENDING_CREATE status`** — verifies the API is never called during offline create.
4. **`syncPending deletes old local UUID row when server assigns a new ID`** — verifies stale local UUID is removed after sync.
5. **`toggleFavorite calls atomic SQL update on DAO`** — verifies atomic `CASE WHEN` SQL used instead of read-modify-write.
6. **`refreshListings preserves isFavorite for already-synced items`** — verifies local favourite flag survives a server refresh.

Run with: `./gradlew test`

---

## Security

- Auth token stored in `DataStore Preferences` — not in SharedPreferences or files.
- `AuthInterceptor` attaches Bearer token to every outbound request.
- `FileProvider` used for camera images — never exposes `file://` URIs.
- Input validated at both ViewModel and UseCase layers.
- `network_security_config.xml` scoped to system CAs.

---

## Building & Running

```bash
./gradlew assembleDebug
# or open in Android Studio Hedgehog+
```

Min SDK: 24 | Target SDK: 35 | Language: Kotlin 2.1 | UI: Jetpack Compose
