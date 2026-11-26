# Ręczne testowanie endpointów

Instrukcja sprawdzania API Songover krok po kroku: katalog, upload pliku audio i przypadki błędów.

## Przygotowanie

Przed testami muszą działać Docker i aplikacja. Wywołania API robisz w Swaggerze, a sam plik audio wysyłasz komendą w PowerShellu.

1. Uruchom Docker Desktop.
2. Uruchom `SongoverApplication`. Kontenery Postgresa i RustFS wystartują same. W logu musi być `Started SongoverApplication`.
3. Otwórz Swaggera: `http://localhost:8080/swagger-ui.html`. Przy każdym endpoincie klikasz **Try it out**, uzupełniasz pola i klikasz **Execute**.
4. Otwórz konsolę RustFS: `http://localhost:9001` (login `songover`, hasło `songover-secret`).
5. Przygotuj plik MP3, WAV albo FLAC do 200 MB, najlepiej muzykę na wolnej licencji.

Podczas testów zapisuj identyfikatory z odpowiedzi. Każdy kolejny krok korzysta z poprzednich.

| Zmienna | Skąd ją wziąć |
| --- | --- |
| `artistId` | pole `id` z odpowiedzi `POST /artists` |
| `albumId` | pole `id` z odpowiedzi `POST /albums` |
| `trackId` | pole `id` z odpowiedzi `POST /tracks` |
| `uploadId`, `uploadUrl` | odpowiedź `POST /tracks/{trackId}/uploads` |

## Scenariusz 1: katalog

Ten scenariusz tworzy artystę, album i utwór, a potem łączy utwór z albumem. Na końcu masz `trackId` potrzebny do uploadu.

### 1.1 Utwórz artystę

`POST /api/v1/artists`

```json
{
  "name": "Test Artist",
  "description": "Artysta do testów"
}
```

Oczekiwany wynik: **201**. Zapisz `id` jako `artistId`.

### 1.2 Utwórz album

`POST /api/v1/albums`

```json
{
  "title": "Test Album",
  "releaseDate": "2026-11-26",
  "artistId": "<artistId>"
}
```

Oczekiwany wynik: **201**, w odpowiedzi `artistName`. Zapisz `id` jako `albumId`.

### 1.3 Utwórz utwór

`POST /api/v1/tracks`

```json
{
  "title": "Moja piosenka",
  "explicit": false,
  "artistId": "<artistId>"
}
```

Oczekiwany wynik: **201**, `"status": "DRAFT"`, `albumId` równe `null`. Zapisz `id` jako `trackId`.

### 1.4 Przypisz utwór do albumu

`PUT /api/v1/tracks/{trackId}/album`

```json
{
  "albumId": "<albumId>",
  "trackNumber": 1
}
```

Oczekiwany wynik: **200**, w odpowiedzi `albumTitle` i `trackNumber: 1`.

### 1.5 Sprawdź listy i filtry

| Wywołanie | Oczekiwany wynik |
| --- | --- |
| `GET /api/v1/artists` | lista z Twoim artystą |
| `GET /api/v1/albums?artistId=<artistId>` | tylko albumy tego artysty |
| `GET /api/v1/tracks?albumId=<albumId>` | utwory albumu, posortowane po numerze |
| `GET /api/v1/tracks/{trackId}` | utwór ze wszystkimi polami |

### 1.6 Edycja (opcjonalnie)

`PATCH /api/v1/tracks/{trackId}` z `{"title": "Nowy tytuł"}`. Oczekiwany wynik: **200**. Zmienia się tylko tytuł, reszta pól zostaje bez zmian.

## Scenariusz 2: upload pliku

Upload ma trzy kroki: rezerwacja w aplikacji, wysłanie pliku prosto do RustFS i potwierdzenie w aplikacji. Na końcu utwór sam przechodzi z `DRAFT` na `PROCESSING`. Potrzebujesz `trackId` utworu w statusie `DRAFT`, na przykład ze scenariusza 1.

### 2.1 Sprawdź rozmiar pliku

W PowerShellu:

```powershell
(Get-Item "C:\sciezka\do\piosenka.mp3").Length
```

Zapisz wynik. To liczba bajtów, którą podasz jako `sizeBytes`.

### 2.2 Zarezerwuj upload

`POST /api/v1/tracks/{trackId}/uploads`

```json
{
  "contentType": "audio/mpeg",
  "sizeBytes": 4823114
}
```

| Rozszerzenie | `contentType` |
| --- | --- |
| `.mp3` | `audio/mpeg` |
| `.wav` | `audio/wav` |
| `.flac` | `audio/flac` |

Oczekiwany wynik: **201**. Zapisz `uploadId` i `uploadUrl`. Link jest ważny 15 minut, więc kolejny krok zrób od razu.

### 2.3 Wyślij plik do RustFS

Swagger tego nie zrobi, bo plik idzie prosto do RustFS, a nie do API. W PowerShellu:

```powershell
curl.exe -X PUT -H "Content-Type: audio/mpeg" --upload-file "C:\sciezka\do\piosenka.mp3" "<uploadUrl>"
```

- Pisz `curl.exe`, a nie `curl`. W PowerShellu `curl` to inne polecenie.
- `uploadUrl` musi być w cudzysłowie, bo zawiera znaki `&`.
- `Content-Type` musi być taki sam jak w kroku 2.2.
- Brak odpowiedzi oznacza sukces. Plik jest już w RustFS, ale upload ma jeszcze status `PENDING_UPLOAD`.

### 2.4 Potwierdź upload

`POST /api/v1/uploads/{uploadId}/complete` (bez body).

Oczekiwany wynik: **200**, `"status": "UPLOADED"` i wypełnione `uploadedAt`. W logu aplikacji chwilę później pojawi się linia `Track ... moved to PROCESSING after upload ...`.

### 2.5 Sprawdź status utworu

`GET /api/v1/tracks/{trackId}`

Oczekiwany wynik: `"status": "PROCESSING"`. Utwór zostaje w tym statusie, dopóki nie powstanie krok przetwarzania pliku.

## Scenariusz 3: przypadki błędów

Każdy błąd wraca jako ProblemDetail: JSON z polami `status`, `title` i `detail`. Przy walidacji dochodzi jeszcze pole `errors` z listą pól. Kody poniżej sprawdzisz w Swaggerze, poza dwoma ostatnimi wierszami, które robisz przez `curl.exe`.

| Co robisz | Oczekiwany kod | Skąd |
| --- | --- | --- |
| `POST /artists` bez `name` | 400 + `errors` | walidacja |
| `POST /artists` z nazwą, która już istnieje | 409 | artysta już istnieje |
| `GET /tracks/abc` (to nie jest UUID) | 400 | zły typ parametru |
| `POST /tracks` z uciętym JSON-em | 400 | błędny JSON |
| `GET /tracks/{losowe UUID}` | 404 | utwór nie istnieje |
| `DELETE /artists/{artistId}`, gdy artysta ma utwory | 409 | dane w użyciu |
| `POST /tracks/{trackId}/publish` dla utworu w `DRAFT` | 409 | niedozwolona zmiana statusu |
| `PUT /tracks/{trackId}/album` z albumem innego artysty | 409 | album innego artysty |
| `PUT /tracks/{trackId}/album` z zajętym numerem utworu | 409 | numer zajęty |
| `POST /tracks/{losowe UUID}/uploads` | 404 | utwór nie istnieje |
| `POST /tracks/{trackId}/uploads` dla utworu w `PROCESSING` | 409 | utwór nie przyjmuje uploadu |
| `POST /tracks/{trackId}/uploads` z `"contentType": "video/mp4"` | 400 | walidacja |
| `POST /tracks/{trackId}/uploads` z `sizeBytes` powyżej 209715200 | 400 | limit 200 MB |
| `POST /uploads/{uploadId}/complete` bez wysłania pliku | 409 | pliku nie ma w RustFS |
| `POST /uploads/{uploadId}/complete` drugi raz | 409 | upload już zakończony |
| `GET /uploads/{losowe UUID}` | 404 | upload nie istnieje |
| `curl.exe` PUT z innym `Content-Type` niż przy rezerwacji | 403 | RustFS: `SignatureDoesNotMatch` |
| `curl.exe` PUT po 15 minutach | 403 | RustFS: link wygasł |

## Weryfikacja i problemy

Po scenariuszu 2 sprawdź, czy dane zgadzają się w bazie i w RustFS.

```sql
SELECT title, status FROM catalog.track;
SELECT track_id, status, size_bytes, uploaded_at FROM media.upload;
SELECT event_type, status, completion_date FROM event_publication;
```

- `catalog.track`: utwór ma status `PROCESSING`.
- `media.upload`: upload ma status `UPLOADED` i wypełnione `uploaded_at`.
- `event_publication`: jest wpis z `UploadCompleted`, statusem `COMPLETED` i datą zakończenia. Pusta `completion_date` oznacza, że listener w `catalog` się nie wykonał. Powód będzie w logu.

W konsoli RustFS (`http://localhost:9001`) plik leży w buckecie `songover-audio`, w katalogu `tracks/{trackId}/original/`.

### Gdy coś nie działa

| Objaw | Przyczyna | Co zrobić |
| --- | --- | --- |
| `No host port mapping found for container port 5432` | kontener Postgresa się zawiesił | `docker compose down`, potem `docker compose up -d` |
| `Failed to configure a DataSource: 'url' attribute is not specified` | Postgres nie działa, często przez zajęty port 5432 | `docker ps`, zatrzymaj kontener innego projektu na 5432 i zrestartuj Songover |
| `relation ... already exists` przy migracji | tabelę utworzyło coś poza Flyway | sprawdź `application.yaml`, usuń tabelę, uruchom ponownie |
| upload zostaje w `PENDING_UPLOAD` | nie było kroku 2.4 albo poszedł ze złym id | wywołaj `complete` z `uploadId`, nie z `trackId` |
| `curl` w PowerShellu zgłasza błąd parametrów | `curl` to w PowerShellu inne polecenie | użyj `curl.exe` |
| RustFS zwraca `SignatureDoesNotMatch` | inny `Content-Type` niż przy rezerwacji | wyślij z tym samym `Content-Type` |
| aplikacja nie startuje, a w logu jest błąd Modulith | kod jednego modułu używa wewnętrznej klasy innego | log podaje klasę i zależność; popraw import albo przenieś typ do publicznego API modułu |
