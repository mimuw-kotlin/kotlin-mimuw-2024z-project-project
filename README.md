[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/M0kyOMLZ)
# Dziennik szkolny

## Authors
- Krzysztof Lembryk

## Description
Dziennik szkolny, będzie aplikacją webową starającą się skorzystać na ostatniej fali niepopularności dziennika internetowego VULCAN.

## Features
- wielowątkowy serwer w kotlinie
- baza danych
- bezpieczne logowanie i tworzenie użytkowników
- panel admina
- Frontend w kotlnie
- dostęp do ocen, wykresów, średnich z ocen, archiwum przedmiotów, planu zajęć itp.

## Plan
- Na początek chciałbym zaimplementować serwer (przypuszczalnie REST API) komunikujący się z bazą danych
(chyba we frameworku SpringBoot)
- Następnie dorobić do tego UI (które nie będzie szkicowe) i dodać profile użytkowników, logowania, admina

## Co aktualnie udało się zrobić:
- Serwer asynchroniczny z kilkoma routami 
- Baza danych z kilkoma tabelami i komunikacja między bazą danych a serwerem
- Logowanie i rejestracja użytkowników i zapisywanie ich do bazy danych
- Mechanizm sesji
- zapisywanie hashowanych haseł do bazy danych
- szczątkowe UI aktualnie w większości budowane w serwerze a nie w thymeleafie
- zaczęcie budowania UI w thymeleafie

## Co do zrobienia:
- zrobienie całego UI w thymeleaf i dodanie np. bootstrapa żeby ładniej wyglądało
- dodanie CSRF protection
- dodanie SSL żeby https działało
- dodanie swagger file
- dodanie panelu zarządzanai dla admina, nauczyciela (zmiany klasy ucznia, nazwy klasy, zmian user_type itp, kilka opcji)
- panel admina gdzie akceptuje nowych użytkowników
- dodanie możliwości zmiany hasła, nazwy użytkownika
- dodanie wyświetlania wszystkich uczniów, wszystkich uczniów danej klasy
- (jeśli zdąże) dodanie możliwości dodawania ocen, przedmiotów, planu 

Komenda do generowania ssl key:
```
keytool -keystore keystore.jks -alias dziennikKey -genkeypair -keyalg RSA -keysize 4096 -validity 3 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US'
```

Budowanie fatJaru dla projektu:
```
./gradlew buildFatJar
```
