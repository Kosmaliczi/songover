# Songover – dokumentacja projektu

**Your voice over every song.**

| | |
|---|---|
| Typ systemu | Backend (REST API) platformy streamingu muzyki |
| Pakiet bazowy | `io.github.kosmaliczi.songover` |
| Repozytorium | `github.com/kosmaliczi/songover` |

## Spis treści

1. [Wprowadzenie](#1-wprowadzenie)
2. [Użytkownicy i uprawnienia](#2-użytkownicy-i-uprawnienia)
3. [Architektura](#3-architektura)
4. [Komunikacja między modułami](#4-komunikacja-między-modułami)
5. [Przetwarzanie audio](#5-przetwarzanie-audio)
6. [Bezpieczeństwo](#6-bezpieczeństwo)
7. [Wymagania funkcjonalne](#7-wymagania-funkcjonalne)
8. [Interfejs REST API](#8-interfejs-rest-api)
9. [Wymagania niefunkcjonalne](#9-wymagania-niefunkcjonalne)
10. [Technologie i środowisko](#10-technologie-i-środowisko)
11. [Struktura repozytorium](#11-struktura-repozytorium)
12. [Standardy jakości i testów](#12-standardy-jakości-i-testów)
13. [Proces wytwarzania](#13-proces-wytwarzania)
14. [Słownik pojęć](#14-słownik-pojęć)

---

## 1. Wprowadzenie

### 1.1. Opis systemu

Songover jest backendem platformy do słuchania muzyki. Udostępnia katalog utworów, streaming audio, playlisty, bibliotekę ulubionych utworów, historię słuchania, statystyki odtworzeń i wyszukiwarkę. Rozszerzeniem podstawowej funkcjonalności jest tryb karaoke, w którym utwór jest odtwarzany z wyciszonym lub ściszonym wokalem, oraz personalizacja dźwięku za pomocą equalizera.

Utwory wgrywają artyści oraz administratorzy. Każdy wgrany plik jest asynchronicznie przetwarzany: weryfikowany, transkodowany do formatów streamingowych i rozdzielany na ścieżkę wokalu i podkładu.

### 1.2. Zakres

System obejmuje:

- zarządzanie katalogiem: artyści, albumy, utwory, publikacja i moderacja,
- wgrywanie plików audio i ich przetwarzanie,
- streaming utworów w dwóch jakościach,
- zdarzenia odtwarzania, historię słuchania, statystyki i ranking popularności,
- playlisty, bibliotekę ulubionych utworów i obserwowanie artystów,
- wyszukiwanie pełnotekstowe,
- tryb karaoke: separację wokalu, streaming ścieżek, teksty utworów,
- ustawienia equalizera i karaoke.

Poza zakresem systemu są: aplikacje klienckie (web i mobilne), płatności i subskrypcje, rekomendacje oparte na uczeniu maszynowym, pobieranie utworów do odtwarzania offline, funkcje społecznościowe oraz dystrybucja treści przez CDN.

### 1.3. Podział odpowiedzialności klient – serwer

Przetwarzanie dźwięku w czasie rzeczywistym (equalizer, miksowanie głośności wokalu i podkładu) odbywa się w aplikacji klienckiej. Backend przechowuje i waliduje ustawienia użytkownika oraz udostępnia pliki: pełny miks oraz osobne ścieżki wokalu i podkładu.

---

## 2. Użytkownicy i uprawnienia

### 2.1. Role

| Rola | Opis |
|---|---|
| `LISTENER` | Słuchacz. Rola nadawana każdemu nowemu kontu. |
| `ARTIST` | Artysta. Publikuje własne utwory i albumy. Nadawana po akceptacji wniosku przez administratora. |
| `ADMIN` | Administrator. Zarządza całym katalogiem, akceptuje wnioski, moderuje treści. |

`ARTIST` i `ADMIN` posiadają wszystkie uprawnienia roli `LISTENER`.

### 2.2. Macierz uprawnień

| Akcja | LISTENER | ARTIST | ADMIN |
|---|---|---|---|
| Przeglądanie i wyszukiwanie katalogu | ✅ | ✅ | ✅ |
| Odtwarzanie, tryb karaoke | ✅ | ✅ | ✅ |
| Własne playlisty, biblioteka, ustawienia audio | ✅ | ✅ | ✅ |
| Złożenie wniosku o status artysty | ✅ | — | — |
| Tworzenie i edycja utworów i albumów | — | własnych | dowolnych |
| Wgrywanie plików audio | — | do własnych utworów | do dowolnych utworów |
| Statystyki utworów | — | własnych | dowolnych |
| Akceptacja wniosków o status artysty | — | — | ✅ |
| Blokowanie utworów | — | — | ✅ |
| Endpointy techniczne Actuatora (poza `health`) | — | — | ✅ |

---

## 3. Architektura

### 3.1. Styl architektoniczny

System jest **modularnym monolitem**: jedną aplikacją Spring Boot podzieloną na moduły o ścisłych granicach. Granice modułów są weryfikowane przez Spring Modulith testem `ApplicationModules.of(SongoverApplication.class).verify()`, którego niepowodzenie przerywa build.

Zasady modułów:

- każdy moduł posiada własny schemat w bazie PostgreSQL; między schematami nie występują klucze obce,
- moduł nie odwołuje się do encji, repozytoriów ani serwisów innych modułów,
- publicznym interfejsem modułu są wyłącznie zdarzenia z pakietu `application.event`, oznaczonego `@NamedInterface("events")`,
- dane innych modułów są przechowywane w lokalnych projekcjach budowanych na podstawie zdarzeń.

### 3.2. Moduły

| Moduł | Odpowiedzialność | Schemat |
|---|---|---|
| `identity` | lokalne profile użytkowników, wnioski o status artysty, integracja z Keycloak Admin API | `identity` |
| `catalog` | artyści, albumy, utwory, gatunki, teksty utworów, publikacja, moderacja | `catalog` |
| `media` | wgrywanie plików, warianty audio, wydawanie adresów do streamingu | `media` |
| `processing` | weryfikacja, transkodowanie i separacja wokalu | `processing` |
| `playback` | przyjmowanie zdarzeń odtwarzania, historia słuchania | `playback` |
| `playlist` | playlisty, biblioteka ulubionych utworów, obserwowani artyści | `playlist` |
| `stats` | statystyki odtworzeń, ranking popularności | `stats` |
| `search` | wyszukiwanie utworów, albumów i artystów | `search` |
| `audiosettings` | equalizer, presety, ustawienia karaoke | `audiosettings` |
| `shared` | elementy techniczne: obsługa błędów, konfiguracja bezpieczeństwa i Kafki, OpenAPI | — |

Moduł `shared` nie zawiera logiki biznesowej. Tabela Event Publication Registry Spring Modulith znajduje się w schemacie `modulith`.

### 3.3. Struktura modułu

```
<moduł>/
├── package-info.java
├── api/
│   ├── <Zasób>Controller.java
│   ├── dto/
│   └── mapper/
├── application/
│   ├── <Zasób>Service.java
│   ├── <Zasób>ServiceImpl.java
│   ├── event/
│   │   └── package-info.java
│   └── listener/
├── domain/
│   ├── model/
│   ├── repository/
│   └── exception/
└── infrastructure/
```

| Pakiet | Zawartość |
|---|---|
| `api` | kontrolery REST |
| `api.dto` | rekordy żądań i odpowiedzi |
| `api.mapper` | mappery MapStruct między encjami a DTO |
| `application` | interfejsy serwisów i ich implementacje (`package-private`) |
| `application.event` | rekordy zdarzeń publikowanych przez moduł; jedyny pakiet dostępny dla innych modułów, oznaczony `@NamedInterface("events")` |
| `application.listener` | konsumenci zdarzeń innych modułów (`@ApplicationModuleListener`, `@KafkaListener`) i projekcje |
| `domain.model` | encje, obiekty wartości, typy wyliczeniowe |
| `domain.repository` | repozytoria Spring Data |
| `domain.exception` | wyjątki biznesowe dziedziczące po `BaseBusinessException` |
| `infrastructure` | adaptery systemów zewnętrznych, np. magazyn plików w module `media`, Keycloak Admin API w module `identity`, ffmpeg i ONNX Runtime w module `processing` |

Encje nie są przekazywane poza moduł ani zwracane w odpowiedziach HTTP. Typy z pakietów `domain` nie występują w rekordach zdarzeń.

---

## 4. Komunikacja między modułami

### 4.1. Kanały

Moduły komunikują się wyłącznie zdarzeniami. Stosowane są dwa kanały:

| Kanał | Zastosowanie | Mechanizm |
|---|---|---|
| **Kafka** | zdarzenia o dużym wolumenie, zdarzenia wymagające możliwości ponownego odczytu, zadania długotrwałe | publikacja przez outbox Spring Modulith i `@Externalized` albo `KafkaTemplate`; odbiór przez `@KafkaListener` |
| **W procesie** | pozostałe zdarzenia | publikacja przez `ApplicationEventPublisher`, odbiór przez `@ApplicationModuleListener` |

Zdarzenia publikowane z transakcji biznesowej są zapisywane w Event Publication Registry w tej samej transakcji. Niedokończone publikacje są ponawiane po restarcie aplikacji.

### 4.2. Zdarzenia

| Zdarzenie | Producent | Konsumenci | Kanał |
|---|---|---|---|
| `UserRegistered` | `identity` | — | w procesie |
| `ArtistApproved` | `identity` | `catalog` | w procesie |
| `TrackSnapshot` | `catalog` | `media`, `playback`, `playlist`, `search`, `audiosettings` | Kafka: `catalog.tracks` |
| `ProcessingRequested` | `media` | `processing` | Kafka: `media.jobs` |
| `ProcessingCompleted`, `ProcessingFailed` | `processing` | `media`, `catalog` | Kafka: `media.results` |
| `PlaybackEvent` | `playback` | `playback`, `stats` | Kafka: `playback.events` |
| `PlaylistCreated`, `TrackLiked` | `playlist` | — | w procesie |

### 4.3. Tematy Kafki

| Temat | Klucz | Partycje | Retencja | Polityka |
|---|---|---|---|---|
| `catalog.tracks` | `trackId` | 3 | bez limitu | `compact` |
| `media.jobs` | `trackId` | 3 | 7 dni | `delete` |
| `media.results` | `trackId` | 3 | 7 dni | `delete` |
| `playback.events` | `userId` | 6 | 30 dni | `delete` |
| `<temat>-retry-<n>`, `<temat>-dlt` | jak temat źródłowy | jak temat źródłowy | 14 dni | `delete` |

Każda wiadomość na temacie `catalog.tracks` zawiera pełny aktualny stan utworu: identyfikator, tytuł, artystę (identyfikator i nazwę), album, gatunek, czas trwania, status oraz `karaokeAvailable`.

### 4.4. Format wiadomości

- Wartość: JSON, rekord z pakietu `application.event` modułu producenta.
- Nagłówki: `eventId` (UUID), `eventType`, `version`, `occurredAt` (ISO 8601 UTC), `correlationId`.
- Grupy konsumentów: `songover.<moduł-konsument>`.
- Wersjonowanie: dodanie pola nie zmienia wersji; konsumenci ignorują nieznane pola. Zmiana niekompatybilna wstecz jest publikowana na nowym temacie z sufiksem wersji (`catalog.tracks.v2`) równolegle do dotychczasowego.

### 4.5. Gwarancje i obsługa błędów

- Dostarczanie: *at-least-once*. Wszyscy konsumenci są idempotentni: rejestrują przetworzone `eventId` lub wykonują operacje typu upsert po kluczu biznesowym.
- Ponawianie: `@RetryableTopic`, 3 próby z wykładniczym opóźnieniem (1 s, 10 s, 60 s), następnie temat `-dlt`. Wiadomości w DLT są logowane i zliczane w metrykach.
- Odbudowa projekcji: moduły `search`, `playlist`, `stats` i `playback` posiadają procedurę odbudowy danych z tematu (wyczyszczenie tabel projekcji i przesunięcie offsetu grupy konsumentów na początek tematu).
- Spójność: projekcje są spójne ostatecznie (*eventual consistency*). Utwór opublikowany w `catalog` staje się widoczny w pozostałych modułach po przetworzeniu zdarzenia przez ich konsumentów.

### 4.6. Przepływ: wgranie i przetworzenie utworu

```
Artysta                catalog              media                Kafka                 processing
   │ POST /tracks         │                    │                    │                       │
   │─────────────────────>│ ──catalog.tracks (DRAFT)──────────────> │                       │
   │ POST /tracks/{trackId}/uploads            │                    │                       │
   │──────────────────────────────────────────>│ presigned PUT URL  │                       │
   │ PUT plik → RustFS     │                    │                    │                       │
   │ POST /uploads/{uploadId}/complete ───────>│ ──media.jobs──────>│──────────────────────>│
   │                      │                    │<──media.results────│<──────────────────────│
   │                      │<─────────────media.results──────────────│                       │
   │                      │ status: READY      │                    │                       │
   │ POST /tracks/{trackId}/publish            │                    │                       │
   │─────────────────────>│ ──catalog.tracks (PUBLISHED)──> media, playback, playlist, search
```

### 4.7. Przepływ: zdarzenia odtwarzania

```
Klient ──POST /api/v1/playback/events──► playback ──KafkaTemplate──► playback.events ──┬──► playback (historia)
                                                                                       └──► stats (statystyki)
```

Endpoint zdarzeń odtwarzania nie zapisuje danych w bazie. Zdarzenia są wysyłane bezpośrednio na temat `playback.events`, a historia słuchania i statystyki są budowane przez konsumentów tego tematu. W przypadku niedostępności Kafki endpoint zwraca 503.

---

## 5. Przetwarzanie audio

### 5.1. Konsument zadań

Moduł `processing` konsumuje temat `media.jobs` z konfiguracją:

| Parametr | Wartość |
|---|---|
| `max.poll.records` | 1 |
| `max.poll.interval.ms` | 1 800 000 (30 min) |
| Współbieżność listenera | 1 na instancję |
| Zatwierdzanie offsetu | po opublikowaniu wyniku na `media.results` |
| Wątki ONNX Runtime (`intraOpNumThreads`) | 2 |

Moduł jest włączany właściwością `songover.processing.enabled` (domyślnie `true`). Ustawienie `false` wyłącza konsumpcję zadań, co umożliwia uruchomienie przetwarzania w osobnej instancji aplikacji (`songover.processing.enabled=true`, `spring.main.web-application-type=none`).

Ponowne przetworzenie tego samego zadania nadpisuje wyniki i nie tworzy duplikatów.

### 5.2. Kroki przetwarzania

1. Weryfikacja pliku przez `ffprobe`: rzeczywisty format, czas trwania, częstotliwość próbkowania, liczba kanałów.
2. Transkodowanie pełnego miksu do AAC 128 kbps i 256 kbps (kontener M4A).
3. Przygotowanie ścieżek karaoke:
   - jeśli dostarczono ścieżki od artysty: weryfikacja ich zgodności z pełnym miksem (długość ±100 ms, częstotliwość próbkowania),
   - w przeciwnym razie: separacja wokalu (sekcja 5.3).
4. Kodowanie ścieżek `vocals` i `instrumental` do AAC 128 kbps.
5. Zapis wyników w RustFS i publikacja `ProcessingCompleted` albo `ProcessingFailed` (z kodem i opisem przyczyny).

Niepowodzenie kroku 3 przy powodzeniu kroków 1–2 kończy przetwarzanie z wynikiem `ProcessingCompleted` i `karaokeAvailable = false`.

Narzędzia `ffmpeg` i `ffprobe` są wywoływane jako procesy systemowe przez `ProcessBuilder`. Argumenty wywołania są budowane wyłącznie z wartości kontrolowanych przez aplikację (ścieżki plików tymczasowych, stałe parametry kodowania).

### 5.3. Separacja wokalu

| Element | Rozwiązanie |
|---|---|
| Model | UVR MDX-Net w formacie ONNX (licencja MIT, wymóg podania autorów w pliku `NOTICE`) |
| Środowisko uruchomieniowe | ONNX Runtime for Java (CPU; opcjonalnie GPU przez artefakt `onnxruntime_gpu`) |
| Transformata | STFT i odwrotna STFT (JTransforms) z parametrami zgodnymi z konfiguracją modelu |
| Dostarczenie modelu | plik pobierany skryptem `infra/models/download.sh` z weryfikacją sumy SHA-256; poza repozytorium |

Algorytm `OnnxSeparationEngine`:

1. Dekodowanie do PCM: 44,1 kHz, stereo, 32-bit float. Nagrania mono są duplikowane do dwóch kanałów.
2. Podział sygnału na fragmenty o długości wejścia modelu, z zakładką.
3. STFT fragmentów, przygotowanie tensora wejściowego i uruchomienie `OrtSession`.
4. Odwrotna STFT wyniku i złożenie fragmentów metodą overlap-add, czego wynikiem jest ścieżka `instrumental`.
5. Ścieżka `vocals` = sygnał oryginalny − `instrumental`.

Parametry modelu (rozmiar okna STFT, przesunięcie, liczba pasm, długość fragmentu) są przechowywane w pliku konfiguracyjnym modelu, nie w kodzie. Każdy przetworzony utwór przechowuje nazwę modelu i wersję silnika separacji.

### 5.4. Źródła ścieżek karaoke

| `karaokeSource` | Opis |
|---|---|
| `ARTIST_STEMS` | ścieżki wokalu i podkładu wgrane przez właściciela utworu lub administratora |
| `AI_SEPARATION` | ścieżki uzyskane przez separację wokalu modelem |

`ARTIST_STEMS` ma pierwszeństwo przed `AI_SEPARATION`.

---

## 6. Bezpieczeństwo

### 6.1. Uwierzytelnianie

- Dostawcą tożsamości jest Keycloak. Backend nie przechowuje haseł i nie udostępnia endpointu logowania.
- Klienci uzyskują token w przepływie Authorization Code z PKCE.
- Backend jest OAuth2 Resource Server i weryfikuje podpis tokenów JWT kluczami publicznymi Keycloaka (JWKS, `issuer-uri`).
- Identyfikatorem użytkownika w systemie jest claim `sub` (UUID Keycloaka).

### 6.2. Konfiguracja Keycloak

| Element | Wartość |
|---|---|
| Realm | `songover` |
| Klient API | `songover-api` (bearer-only) |
| Klient dokumentacji | `songover-swagger` (publiczny, PKCE) |
| Klient serwisowy | `songover-identity` (`client_credentials`, uprawnienie `manage-users` w realmie) |
| Role realmu | `LISTENER` (domyślna), `ARTIST`, `ADMIN` |
| Konfiguracja | `infra/keycloak/realm-export.json`, importowana przy starcie kontenera |

Role są odczytywane z claimu `realm_access.roles` i mapowane na uprawnienia `ROLE_<nazwa>` przez dedykowany `JwtAuthenticationConverter`.

### 6.3. Autoryzacja

- Wszystkie endpointy poza `/swagger-ui/**`, `/v3/api-docs/**` i `/actuator/health/**` wymagają uwierzytelnienia.
- Reguły ról są definiowane adnotacją `@PreAuthorize` na metodach kontrolerów.
- Własność zasobów (utwory artysty, playlisty użytkownika) jest weryfikowana w serwisach aplikacyjnych.
- Zasoby prywatne innych użytkowników są raportowane jako nieistniejące (404).

### 6.4. Dane i pliki

- Publiczne identyfikatory zasobów są typu UUID (wersja 7).
- Buckety RustFS są prywatne. Dostęp do plików odbywa się wyłącznie przez presigned URL ważny 15 minut.
- Sekrety nie są przechowywane w repozytorium. Konfiguracja wrażliwa jest dostarczana przez zmienne środowiskowe (lokalnie plik `.env`, w CI GitHub Secrets).
- Logi i zdarzenia Kafki nie zawierają danych osobowych; użytkownik jest identyfikowany przez `sub`.

---

## 7. Wymagania funkcjonalne

### 7.1. Moduł `identity`

| ID | Wymaganie |
|---|---|
| FR-ID-01 | Przy pierwszym uwierzytelnionym żądaniu użytkownika system tworzy lokalny profil (identyfikator `sub`, email, nazwa wyświetlana) i publikuje `UserRegistered`. Kolejne żądania nie tworzą duplikatów. |
| FR-ID-02 | `GET /api/v1/users/me` zwraca profil zalogowanego użytkownika wraz z rolami. |
| FR-ID-03 | `PATCH /api/v1/users/me` umożliwia zmianę nazwy wyświetlanej (2–50 znaków). |
| FR-ID-04 | Użytkownik z rolą `LISTENER` może złożyć wniosek o status artysty: nazwa artystyczna (unikalna, 2–100 znaków), opis (do 1000 znaków). Użytkownik może mieć jeden wniosek w statusie `PENDING`. |
| FR-ID-05 | Administrator przegląda wnioski (filtr po statusie, paginacja), akceptuje je albo odrzuca z uzasadnieniem. |
| FR-ID-06 | Akceptacja wniosku nadaje rolę `ARTIST` w Keycloaku i publikuje `ArtistApproved`. Niepowodzenie wywołania Keycloaka pozostawia wniosek w statusie `PENDING`. |
| FR-ID-07 | `GET /api/v1/artist-applications/me` zwraca status wniosku; dla zaakceptowanego wniosku pole `requiresTokenRefresh` ma wartość `true`. |

### 7.2. Moduł `catalog`

| ID | Wymaganie |
|---|---|
| FR-CAT-01 | Po zdarzeniu `ArtistApproved` system tworzy profil artysty powiązany z użytkownikiem. |
| FR-CAT-02 | Administrator może utworzyć profil artysty niepowiązany z kontem użytkownika. |
| FR-CAT-03 | Artysta tworzy album: tytuł, data wydania, gatunek. Administrator może utworzyć album dla dowolnego artysty. |
| FR-CAT-04 | Artysta tworzy utwór: tytuł (1–200 znaków), album (opcjonalnie), numer na płycie, gatunek, flaga `explicit`. Nowy utwór ma status `DRAFT`. |
| FR-CAT-05 | Status utworu zmienia się według przejść: `DRAFT` → `PROCESSING` → `READY` / `FAILED`; `READY` → `PUBLISHED`; `PUBLISHED` ⇄ `UNPUBLISHED`; dowolny → `BLOCKED` (administrator); `BLOCKED` → `UNPUBLISHED` (administrator). Niedozwolone przejście zwraca 409. |
| FR-CAT-06 | Każda zmiana statusu lub metadanych utworu publikuje `TrackSnapshot` na temat `catalog.tracks`. |
| FR-CAT-07 | Lista i szczegóły opublikowanych utworów, albumów i artystów są dostępne dla wszystkich zalogowanych użytkowników (paginacja, sortowanie). Utwory nieopublikowane są widoczne wyłącznie dla właściciela i administratora. |
| FR-CAT-08 | Blokada utworu wymaga uzasadnienia i jest zapisywana w historii moderacji. |
| FR-CAT-09 | Tekst utworu w formacie LRC: walidacja przy zapisie, `GET` zwraca listę linii `{ timeMs, text }`. Tekst dodaje właściciel utworu lub administrator. |

### 7.3. Moduł `media`

| ID | Wymaganie |
|---|---|
| FR-MED-01 | `POST /api/v1/tracks/{trackId}/uploads` zwraca presigned URL (PUT, ważny 15 min) do wgrania pliku do RustFS. Operacja jest dostępna dla właściciela utworu i administratora, dla utworów w statusie `DRAFT` lub `FAILED`. |
| FR-MED-02 | Akceptowane formaty: WAV, FLAC, MP3 (minimum 192 kbps). Maksymalny rozmiar: 200 MB. Maksymalny czas trwania: 15 minut. |
| FR-MED-03 | `POST /api/v1/uploads/{uploadId}/complete` weryfikuje obecność obiektu w RustFS, publikuje `ProcessingRequested` i zwraca 202 z nagłówkiem `Location` wskazującym status przetwarzania. |
| FR-MED-04 | `GET /api/v1/uploads/{uploadId}` zwraca status: `PENDING_UPLOAD`, `PROCESSING`, `COMPLETED`, `FAILED` (z przyczyną). |
| FR-MED-05 | Po `ProcessingCompleted` system zapisuje warianty audio, czas trwania, `karaokeSource` i wersję silnika separacji. |
| FR-MED-06 | `GET /api/v1/tracks/{trackId}/stream?quality=HIGH\|NORMAL` zwraca presigned URL (GET, ważny 15 min) do pełnego miksu. Dla utworów nieopublikowanych operacja jest dostępna wyłącznie dla właściciela i administratora. Pliki obsługują żądania `Range`. |
| FR-MED-07 | `GET /api/v1/tracks/{trackId}/stream/karaoke` zwraca presigned URL-e do ścieżek `vocals` i `instrumental`. Dla utworu bez ścieżek karaoke zwraca 409 z kodem `KARAOKE_NOT_AVAILABLE`. |
| FR-MED-08 | Właściciel utworu lub administrator może wgrać ścieżki wokalu i podkładu (format i limity jak w FR-MED-02). Wgranie ścieżek uruchamia ponowne przetworzenie utworu. |
| FR-MED-09 | Administrator może zlecić ponowne przetworzenie wskazanych utworów. |
| FR-MED-10 | Uploady w statusie `PENDING_UPLOAD` starsze niż 24 godziny są usuwane wraz z plikami przez zadanie cykliczne. |

### 7.4. Moduł `processing`

| ID | Wymaganie |
|---|---|
| FR-PRC-01 | Moduł konsumuje zadania z tematu `media.jobs` i publikuje wyniki na temacie `media.results` zgodnie z sekcją 5. |
| FR-PRC-02 | Rzeczywisty format pliku jest weryfikowany przez `ffprobe`. Niezgodność z wymaganiami FR-MED-02 kończy zadanie wynikiem `ProcessingFailed`. |
| FR-PRC-03 | Pełny miks jest transkodowany do AAC 128 kbps i 256 kbps. |
| FR-PRC-04 | Ścieżki karaoke są przygotowywane zgodnie z sekcjami 5.3 i 5.4. |
| FR-PRC-05 | Konsumpcja zadań jest sterowana właściwością `songover.processing.enabled`. |

### 7.5. Moduł `playback`

| ID | Wymaganie |
|---|---|
| FR-PB-01 | `POST /api/v1/playback/events` przyjmuje paczkę do 50 zdarzeń. Typy zdarzeń: `STARTED`, `PROGRESS` (co 30 s), `PAUSED`, `RESUMED`, `SEEKED`, `SKIPPED`, `COMPLETED`, `KARAOKE_ON`, `KARAOKE_OFF`. |
| FR-PB-02 | Zdarzenie zawiera: `eventId` (UUID), `playId` (UUID pojedynczego odtworzenia), `trackId`, `positionMs`, `occurredAt`. Zdarzenia dla utworów nieopublikowanych są odrzucane. |
| FR-PB-03 | Zdarzenia są publikowane na temacie `playback.events` z kluczem `userId`; endpoint zwraca 202. |
| FR-PB-04 | Odtworzenie jest zaliczone, gdy w ramach jednego `playId` odsłuchano co najmniej 30 s albo co najmniej 50% utworu krótszego niż 60 s. |
| FR-PB-05 | `GET /api/v1/playback/history` zwraca zaliczone odtworzenia użytkownika w kolejności od najnowszych, z paginacją. |
| FR-PB-06 | `GET /api/v1/playback/recent` zwraca 20 ostatnio odtwarzanych różnych utworów użytkownika. |

### 7.6. Moduł `playlist`

| ID | Wymaganie |
|---|---|
| FR-PL-01 | Użytkownik tworzy playlistę: nazwa (1–100 znaków), opis (do 300 znaków), widoczność `PRIVATE` lub `PUBLIC`. |
| FR-PL-02 | Użytkownik dodaje i usuwa utwory z playlisty. Utwór może wystąpić wielokrotnie. Maksymalna liczba pozycji: 1000. Dodać można wyłącznie utwór opublikowany. |
| FR-PL-03 | Użytkownik zmienia kolejność pozycji (przeniesienie pozycji na wskazany indeks). |
| FR-PL-04 | Playlistę modyfikuje wyłącznie jej właściciel. Playlista publiczna jest widoczna dla wszystkich zalogowanych użytkowników, prywatna wyłącznie dla właściciela. |
| FR-PL-05 | Użytkownik dodaje utwór do ulubionych i usuwa go (`PUT` / `DELETE`, operacje idempotentne). Lista ulubionych jest paginowana i posortowana od najnowszych. |
| FR-PL-06 | Pozycje odnoszące się do utworów nieopublikowanych pozostają na playlistach z oznaczeniem `available = false`. |
| FR-PL-07 | Użytkownik obserwuje artystów i przegląda listę obserwowanych. |

### 7.7. Moduł `stats`

| ID | Wymaganie |
|---|---|
| FR-ST-01 | Dla każdego utworu system zlicza: zaliczone odtworzenia (łącznie i dziennie), pominięcia, odsetek pominięć, odtworzenia w trybie karaoke. |
| FR-ST-02 | `GET /api/v1/charts/trending` zwraca 50 utworów z największą liczbą zaliczonych odtworzeń w ostatnich 24 godzinach; ranking jest aktualizowany co najmniej co 5 minut. |
| FR-ST-03 | `GET /api/v1/charts/karaoke` zwraca 50 utworów najczęściej odtwarzanych w trybie karaoke w ostatnich 7 dniach. |
| FR-ST-04 | Artysta przegląda statystyki własnych utworów: łączna liczba odtworzeń, 10 najczęściej odtwarzanych utworów, liczba odtworzeń dziennie za ostatnie 30 dni, odsetek pominięć. Administrator przegląda statystyki dowolnego artysty. |

### 7.8. Moduł `search`

| ID | Wymaganie |
|---|---|
| FR-SR-01 | `GET /api/v1/search?q=...&type=TRACK,ALBUM,ARTIST` wyszukuje wśród opublikowanych treści. Minimalna długość frazy: 2 znaki. |
| FR-SR-02 | Wyszukiwanie jest pełnotekstowe (PostgreSQL `tsvector`, rozszerzenie `unaccent`) i niewrażliwe na polskie znaki diakrytyczne. |
| FR-SR-03 | Wyniki są pogrupowane według typu, stronicowane osobno dla każdego typu i posortowane według trafności. |
| FR-SR-04 | Parametr `karaoke=true` ogranicza wyniki do utworów z dostępnym trybem karaoke. |

### 7.9. Moduł `audiosettings`

| ID | Wymaganie |
|---|---|
| FR-AUD-01 | Equalizer posiada 10 pasm: 31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000 Hz. Wzmocnienie pasma: od −12 do +12 dB z krokiem 0,5 dB. Preamp: od −12 do 0 dB. |
| FR-AUD-02 | Presety systemowe (tylko do odczytu): Flat, Bass Boost, Treble Boost, Vocal, Rock, Electronic, Acoustic. |
| FR-AUD-03 | Użytkownik tworzy, edytuje i usuwa własne presety: nazwa 1–40 znaków, unikalna w obrębie użytkownika, maksymalnie 20 presetów. |
| FR-AUD-04 | Użytkownik wybiera aktywny preset. `GET /api/v1/audio-settings/me` zwraca aktywny preset z wartościami wszystkich pasm. |
| FR-AUD-05 | Ustawienia karaoke użytkownika: głośność wokalu 0–100% (domyślnie 0), głośność podkładu 0–100% (domyślnie 100), wyświetlanie tekstu (domyślnie włączone). |

---

## 8. Interfejs REST API

### 8.1. Konwencje

| Element | Konwencja |
|---|---|
| Ścieżka bazowa | `/api/v1` |
| Zasoby | rzeczowniki w liczbie mnogiej, `kebab-case` |
| Zasoby zalogowanego użytkownika | `/me`, np. `/api/v1/users/me` |
| Akcje spoza CRUD | podzasób z czasownikiem, np. `POST /api/v1/tracks/{trackId}/publish` |
| Zmienne ścieżki | nazwane według zasobu: `{trackId}`, `{playlistId}`, `{uploadId}` |
| Format | JSON, pola w `camelCase` |
| Czas | ISO 8601 UTC; czasy trwania w milisekundach (`durationMs`) |
| Identyfikatory | UUID |
| Wersjonowanie | zmiana niekompatybilna wstecz publikowana jako `/api/v2` równolegle do `/api/v1` |

### 8.2. Metody i kody odpowiedzi

| Operacja | Metoda | Kod sukcesu |
|---|---|---|
| Lista | `GET` | 200 |
| Szczegóły | `GET` | 200 |
| Utworzenie | `POST` | 201 + nagłówek `Location` |
| Częściowa modyfikacja | `PATCH` | 200 |
| Ustawienie stanu (idempotentne) | `PUT` | 204 |
| Usunięcie | `DELETE` | 204 |
| Operacja asynchroniczna | `POST` | 202 + nagłówek `Location` |

| Kod błędu | Znaczenie |
|---|---|
| 400 | błąd walidacji danych wejściowych |
| 401 | brak lub nieprawidłowy token |
| 403 | brak uprawnień |
| 404 | zasób nie istnieje lub jest prywatnym zasobem innego użytkownika |
| 409 | konflikt stanu (niedozwolone przejście, duplikat, konflikt wersji) |
| 413 | przekroczony rozmiar danych |
| 422 | dane poprawne składniowo, lecz nieprzetwarzalne (np. niepoprawny format LRC) |
| 503 | chwilowa niedostępność zależności |
| 500 | błąd nieoczekiwany |

### 8.3. Format błędu

Odpowiedzi błędów są zgodne z RFC 9457 (`application/problem+json`), w tym odpowiedzi 401 i 403 generowane przez warstwę Spring Security.

```json
{
  "type": "https://api.songover.local/problems/invalid-track-state",
  "title": "Invalid track state",
  "status": 409,
  "detail": "Track 0192f6e4-... cannot be published from state DRAFT",
  "instance": "/api/v1/tracks/0192f6e4-.../publish",
  "code": "INVALID_TRACK_STATE",
  "traceId": "4bf92f3577b34da6"
}
```

Błędy walidacji zawierają dodatkowo pole `errors`: listę obiektów `{ "field", "message" }`.

### 8.4. Paginacja i sortowanie

- Parametry: `page` (od 0), `size` (domyślnie 20, maksymalnie 100), `sort=pole,asc|desc`.
- Sortowanie jest dozwolone wyłącznie po polach zdefiniowanych dla danego zasobu; inne pole zwraca 400.
- Format odpowiedzi:

```json
{
  "content": [],
  "page": { "number": 0, "size": 20, "totalElements": 135, "totalPages": 7 }
}
```

### 8.5. Dokumentacja API

Specyfikacja OpenAPI jest generowana przez springdoc. Swagger UI (`/swagger-ui.html`) uwierzytelnia użytkownika przez Keycloak (klient `songover-swagger`). Każdy endpoint posiada opis, przykłady oraz udokumentowane odpowiedzi błędów.

---

## 9. Wymagania niefunkcjonalne

### 9.1. Niezawodność

| ID | Wymaganie |
|---|---|
| NFR-REL-01 | Zdarzenia publikowane z transakcji biznesowej są dostarczane mimo awarii aplikacji lub brokera (outbox, ponawianie po restarcie). |
| NFR-REL-02 | Ponowne dostarczenie wiadomości nie powoduje zduplikowanych efektów. |
| NFR-REL-03 | Agregaty modyfikowane współbieżnie (utwory, playlisty) stosują optimistic locking; konflikt zwraca 409. |
| NFR-REL-04 | Niedostępność RustFS lub Keycloak Admin API nie pozostawia danych w stanie niespójnym. |
| NFR-REL-05 | Każda projekcja budowana z Kafki może zostać odbudowana z tematu źródłowego. |

### 9.2. Wydajność

Wartości dotyczą środowiska lokalnego z katalogiem 10 000 utworów.

| ID | Wymaganie |
|---|---|
| NFR-PERF-01 | p95 czasu odpowiedzi endpointów odczytu katalogu i wyszukiwania < 300 ms. |
| NFR-PERF-02 | p95 czasu wydania adresów streamingu < 100 ms. |
| NFR-PERF-03 | p95 czasu odpowiedzi `POST /api/v1/playback/events` < 50 ms. |
| NFR-PERF-04 | Transkodowanie 4-minutowego utworu < 1 min; pełne przetworzenie z separacją wokalu < 10 min (CPU, 4 rdzenie). |
| NFR-PERF-05 | W trakcie przetwarzania audio p95 endpointów odczytu nie przekracza dwukrotności wartości z NFR-PERF-01. |
| NFR-PERF-06 | Pobieranie list nie generuje zapytań N+1. |

### 9.3. Bezpieczeństwo

| ID | Wymaganie |
|---|---|
| NFR-SEC-01 | Brak sekretów w repozytorium. |
| NFR-SEC-02 | Domyślna odmowa dostępu: każdy endpoint spoza listy publicznej (sekcja 6.3) wymaga uwierzytelnienia. |
| NFR-SEC-03 | Użytkownik nie może odczytać ani zmodyfikować prywatnych zasobów innego użytkownika. |
| NFR-SEC-04 | Presigned URL-e są ważne maksymalnie 15 minut. |
| NFR-SEC-05 | Format wgrywanych plików jest weryfikowany na podstawie zawartości, nie rozszerzenia ani nagłówka `Content-Type`. |
| NFR-SEC-06 | Logi i zdarzenia nie zawierają danych osobowych. |
| NFR-SEC-07 | Zależności i obraz Dockera nie zawierają znanych podatności o poziomie HIGH lub CRITICAL. |

### 9.4. Obserwowalność

| ID | Wymaganie |
|---|---|
| NFR-OBS-01 | Actuator udostępnia `health` (z grupami `liveness` i `readiness` obejmującymi PostgreSQL, Kafkę i RustFS), `info` i `metrics`. |
| NFR-OBS-02 | Logi są zapisywane w formacie JSON i zawierają `traceId`. |
| NFR-OBS-03 | `correlationId` jest przenoszony z żądania HTTP do nagłówków zdarzeń i logów konsumentów. |
| NFR-OBS-04 | Metryki obejmują: liczbę uploadów, czas i błędy przetwarzania, opóźnienie konsumentów Kafki (consumer lag), liczbę wiadomości w tematach DLT. |

---

## 10. Technologie i środowisko

### 10.1. Stos technologiczny

| Obszar | Technologia |
|---|---|
| Język | Java 21 (Eclipse Temurin) |
| Framework | Spring Boot 4.1, Spring Modulith 2.1 |
| Budowanie | Maven (artefakt `io.github.kosmaliczi:songover`), Maven Wrapper |
| Baza danych | PostgreSQL, Flyway |
| Broker | Apache Kafka 4 (tryb KRaft), Spring for Apache Kafka |
| Tożsamość | Keycloak |
| Pliki | RustFS (API S3), AWS SDK for Java v2 |
| Audio | ffmpeg / ffprobe, ONNX Runtime for Java, JTransforms |
| Mapowanie | MapStruct, Lombok |
| Dokumentacja API | springdoc-openapi |
| Testy | JUnit 5, AssertJ, Mockito, Spring Modulith Test, Testcontainers, ArchUnit, JaCoCo |
| Jakość | Spotless, SonarCloud, CodeQL, Trivy |
| CI | GitHub Actions, Renovate |

### 10.2. Środowisko lokalne

Plik `compose.yaml` w katalogu głównym repozytorium definiuje usługi:

| Usługa | Obraz | Port |
|---|---|---|
| PostgreSQL | `postgres` | 5432 |
| Kafka | `apache/kafka` | 9092 |
| Keycloak | `quay.io/keycloak/keycloak` | 8180 |
| RustFS | `rustfs/rustfs` | 9000 (API), 9001 (konsola) |

Wersje obrazów są przypięte w `compose.yaml`. Tematy Kafki są tworzone skryptem `infra/kafka/create-topics.sh`, buckety skryptem `infra/storage/init.sh`, a realm Keycloak jest importowany z `infra/keycloak/realm-export.json`.

Aplikacja wykorzystuje Spring Boot Docker Compose Support. Realm lokalny zawiera użytkowników testowych `listener`, `artist` i `admin`.

### 10.3. Kluczowe właściwości konfiguracyjne

| Właściwość | Wartość domyślna | Opis |
|---|---|---|
| `songover.processing.enabled` | `true` | konsumpcja zadań przetwarzania audio |
| `songover.storage.presigned-url-ttl` | `15m` | czas ważności presigned URL |
| `songover.processing.onnx.intra-op-threads` | `2` | liczba wątków ONNX Runtime |
| `songover.processing.model-path` | — | ścieżka do pliku modelu separacji |
| `spring.jpa.open-in-view` | `false` | |
| `spring.jpa.hibernate.ddl-auto` | `validate` | |

---

## 11. Struktura repozytorium

```
songover/
├── .github/
│   └── workflows/ci.yml
├── .mvn/
├── docs/
│   ├── SPECIFICATION.md
│   ├── adr/
│   └── events.md
├── infra/
│   ├── kafka/create-topics.sh
│   ├── keycloak/realm-export.json
│   ├── storage/init.sh
│   └── models/download.sh
├── src/
│   ├── main/
│   │   ├── java/io/github/kosmaliczi/songover/
│   │   │   ├── SongoverApplication.java
│   │   │   ├── shared/
│   │   │   ├── identity/
│   │   │   ├── catalog/
│   │   │   ├── media/
│   │   │   ├── processing/
│   │   │   ├── playback/
│   │   │   ├── playlist/
│   │   │   ├── stats/
│   │   │   ├── search/
│   │   │   └── audiosettings/
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── db/migration/<moduł>/
│   └── test/java/io/github/kosmaliczi/songover/
├── compose.yaml
├── Dockerfile
├── pom.xml
├── mvnw, mvnw.cmd
├── NOTICE
└── README.md
```

---

## 12. Standardy jakości i testów

### 12.1. Standardy kodu

- Czas systemowy jest pobierany wyłącznie przez wstrzykiwany `Clock`.
- DTO i zdarzenia są rekordami. Encje stosują bogaty model domenowy i nie udostępniają publicznych setterów pól stanu.
- Lombok jest ograniczony do `@Getter`, `@RequiredArgsConstructor`, `@Slf4j`; na encjach nie stosuje się `@Data` ani `@Setter`.
- Przy włączonych wątkach wirtualnych synchronizacja odbywa się przez `ReentrantLock` zamiast `synchronized`.
- Migracje Flyway po scaleniu do gałęzi `main` są niemodyfikowalne.
- Formatowanie kodu jest wymuszane przez Spotless.

### 12.2. Poziomy testów

| Poziom | Zakres | Narzędzia |
|---|---|---|
| Jednostkowe | encje, maszyny stanów, reguły biznesowe, STFT | JUnit 5, AssertJ |
| Serwisów | logika aplikacyjna z zamockowanymi zależnościami | Mockito |
| Modułowe | pojedynczy moduł, zdarzenia w procesie | `@ApplicationModuleTest`, Scenario API |
| Warstwy web | kontrolery, walidacja, kody odpowiedzi, autoryzacja | `@WebMvcTest`, `spring-security-test` |
| Integracyjne | aplikacja z PostgreSQL, Kafką, RustFS i Keycloakiem | `@SpringBootTest`, Testcontainers, `@ServiceConnection` |
| Architektoniczne | granice modułów, zależności między warstwami | Spring Modulith, ArchUnit |
| Jakości separacji | SDR ścieżki `instrumental` na utworach z referencyjnymi ścieżkami | test oznaczony `@Tag("slow")` |

### 12.3. Kryteria

- Pokrycie kodu pakietów `domain` i `application`: minimum 80% linii i 70% gałęzi.
- `@DisplayName` testu zawiera identyfikator weryfikowanego wymagania.
- Wartości graniczne wymagań są pokryte testami.
- Każdy konsument Kafki posiada test idempotencji i test przekazania wiadomości do DLT.
- Każda projekcja posiada test odbudowy z tematu.
- Każdy endpoint modyfikujący posiada testy odpowiedzi 401, 403 oraz dostępu do zasobu innego użytkownika.
- Dane testowe audio pochodzą wyłącznie z utworów na wolnych licencjach; źródła są wymienione w `docs/test-data.md`.

---

## 13. Proces wytwarzania

### 13.1. Repozytorium i commity

- Model gałęzi: trunk-based. Gałęzie robocze `feat/*`, `fix/*`, `refactor/*`, `chore/*`, `docs/*` tworzone od `main`.
- Gałąź `main` jest chroniona: zmiany wyłącznie przez pull request z pozytywnym wynikiem CI, scalanie metodą squash.
- Tytuły pull requestów i commitów są zgodne z Conventional Commits, np. `feat(catalog): add track publishing`.

### 13.2. Ciągła integracja

Workflow `ci.yml` jest uruchamiany dla każdego pull requesta i każdego commita na `main`.

| Krok | Narzędzie | Warunek niepowodzenia |
|---|---|---|
| Formatowanie | `spotless:check` | niesformatowany kod |
| Budowanie i testy | `./mvnw verify` z Testcontainers | błąd kompilacji lub testu |
| Weryfikacja architektury | Spring Modulith, ArchUnit | naruszenie granic modułów |
| Pokrycie | JaCoCo | wartości poniżej progów z sekcji 12.3 |
| Analiza statyczna | SonarCloud | niespełniony quality gate |
| Analiza bezpieczeństwa kodu | CodeQL | alert o poziomie high lub wyższym |
| Nowe zależności | `dependency-review-action` | podatność o poziomie high lub wyższym |
| Skan podatności | Trivy (repozytorium i obraz) | podatność CRITICAL |
| Tytuł pull requesta | walidator Conventional Commits | niezgodny format |
| Kontrakt API | wygenerowany `openapi.json` | — (artefakt) |
| Obraz | budowa i publikacja w GitHub Container Registry | — (tylko `main`) |

### 13.3. Zarządzanie zależnościami

- Aktualizacje zależności Maven, obrazów Dockera i akcji GitHub są proponowane przez Renovate jako pull requesty.
- Minimalny wiek wersji przed aktualizacją: 3 dni dla wersji patch, 14 dni dla minor i major.
- Wersje zarządzane przez BOM Spring Boot nie są nadpisywane.
- Wersje pre-release (milestone, RC, snapshot) nie są dopuszczone na gałęzi `main`.
- Wersja Javy jest zmieniana wyłącznie decyzją architektoniczną (ADR).

### 13.4. Definition of Done

Zmiana jest ukończona, gdy:

- realizuje wymagania wskazane identyfikatorami w opisie pull requesta,
- posiada testy zgodne z sekcją 12,
- przechodzi wszystkie kroki CI,
- dokumentacja OpenAPI opisuje nowe i zmienione endpointy,
- zmiany zdarzeń i tematów są odzwierciedlone w `docs/events.md`,
- zmiany schematu bazy danych posiadają migrację Flyway,
- decyzje architektoniczne posiadają ADR,
- nie wprowadza komentarzy `TODO` bez powiązanego zgłoszenia.

---

## 14. Słownik pojęć

| Pojęcie | Definicja |
|---|---|
| ADR | Architecture Decision Record: dokument opisujący pojedynczą decyzję architektoniczną |
| DLT | Dead Letter Topic: temat Kafki przechowujący wiadomości, których nie udało się przetworzyć |
| Event Publication Registry | tabela Spring Modulith przechowująca zdarzenia do czasu ich przetworzenia (wzorzec transactional outbox) |
| Log compaction | polityka tematu Kafki zachowująca ostatnią wiadomość dla każdego klucza |
| LRC | format tekstu utworu ze znacznikami czasu poszczególnych linii |
| ONNX | otwarty format zapisu modeli uczenia maszynowego |
| Presigned URL | adres URL z podpisem, zapewniający czasowy dostęp do obiektu w RustFS bez dodatkowego uwierzytelnienia |
| Projekcja | lokalna kopia danych innego modułu, budowana na podstawie zdarzeń |
| SDR | Signal-to-Distortion Ratio: miara jakości separacji sygnału audio |
| STFT | krótkoczasowa transformata Fouriera |
| Stem | pojedyncza ścieżka składowa utworu, np. wokal lub podkład |
| Zaliczone odtworzenie | odtworzenie spełniające warunek FR-PB-04 |
