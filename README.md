# Songover

**Your voice over every song.**

Songover to backend (REST API) platformy streamingu muzyki z wbudowanym trybem karaoke i personalizacją dźwięku. Użytkownicy mogą słuchać muzyki, tworzyć playlisty, ustawiać 10-pasmowy equalizer i przełączyć dowolny utwór w tryb karaoke, w którym wokal jest oddzielany od podkładu przez model AI uruchamiany w Javie.

> 🚧 **Projekt w trakcie budowy.** Aktualny stan prac opisuje sekcja [Plan realizacji](#plan-realizacji).

## Architektura w skrócie

- **Modular monolith** oparty na Spring Modulith; moduły komunikują się wyłącznie zdarzeniami
- **Apache Kafka** jako strumień zdarzeń z odsłuchów, z którego korzystają historia, statystyki i ranking popularności
- **Keycloak** do uwierzytelniania i autoryzacji opartej na rolach (OAuth2 / OIDC)
- **Przetwarzanie audio** jako moduł aplikacji: transkodowanie (ffmpeg) i separacja wokalu modelem AI (ONNX Runtime for Java)
- **PostgreSQL** z osobnym schematem dla każdego modułu, **RustFS** (API S3) do przechowywania plików audio

Pełna dokumentacja projektu: [docs/SPECIFICATION.md](docs/SPECIFICATION.md).

## Technologie

Java 21 · Spring Boot 4 · Spring Modulith · PostgreSQL · Flyway · Apache Kafka · Keycloak · RustFS · Testcontainers · Docker Compose · GitHub Actions

## Uruchomienie

Wymagania: JDK 21, Docker.

```bash
./mvnw verify
```

## Plan realizacji

- [ ] M0 – Fundament: konfiguracja projektu, infrastruktura lokalna, CI, bezpieczeństwo
- [ ] M1 – Katalog i artyści
- [ ] M2 – Wgrywanie, przetwarzanie i streaming utworów
- [ ] M3 – Odtwarzacz: zdarzenia odtwarzania, historia, playlisty, biblioteka
- [ ] M4 – Statystyki i wyszukiwarka
- [ ] M5 – Karaoke i equalizer
- [ ] M6 – Dopracowanie i dokumentacja
