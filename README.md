[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/M0kyOMLZ)
# Dziennik szkolny

## Authors
- Krzysztof Lembryk

## Jak uruchomić Dockera
W folderze w którym mamy plik Dockerfile wpisujemy:
- ./gradlew buildFatJar
- docker-compose build
- docker-compose run  -p 8080:8080 -p 8443:8443 web (musimy zrobić run bo compose up nie expose'uje portów)
- *(Czasem run może nie zadziałać ze względu na używany już przez kogoś port 8080, wtedy albo czekamy albo sprawdzamy czy w dockerze nie ma już kontenera z tym portem i go usuwamy)*
- docker-compose down (kiedy chcemy zatrzymać i usunąć kontenery)
<br>
  <span style="color: green;"> (ALBO można użyć skryptu ./docker_rebuild_script.sh do wykonania powyższych kroków) </span>
<br>
- Uwaga - adres wyświetlany w konsoli to 0.0.0.0 ale jest to adres naszej aplikacji wewnątrz dockera,
aby wejść na stronę z naszego komputera trzeba użyć **localhost** albo **127.0.0.1**
<br>
- Uwaga - plik keystore.jks nie powinien być w repo, a powinno się
go tworzyć samemu poprzez użycie komendy
```
keytool -keystore keystore.jks -alias dziennikKey -genkeypair -keyalg RSA -keysize 4096 -validity 3 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US'
```
Jednakże w celu ułatwienia procesu ten plik już jest w repo i jest on kopiowany do kontenera.
Również application.yaml nie powinno być w repo bo mamy tam hasła, klucze prywatne itp.

<span style="color:red">UWAGA</span> -
W bazie danych defaultowo jest już jeden użytkownik: **superAdmin**
Jego hasło to: **admin**

Jeśli po zalogowaniu występuje błąd odnoszący się do bazy danych to należy usunąć cały folder tmp, usunąć powstałe 
kontenery z dockera i zbudować wszystko od nowa, powinno wtedy zadziałać.


Żeby uruchomić projekt lokalnie trzeba w pliku application.yaml w postgres w jdbcURL zmienić **db** na **localhost**

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

## Co ostatecznie udało się zrobić
- UI w thymeleaf z bootstrapem
- dodanie SSL i przekierowania wszystkich żądań http na https
- dodanie szyfrowania cookies sesyjnych
- dodanie Dockera
- dodanie kilku testów
- panel zarządzania dla admina - modyfikowanie użytkowników, aktywowanie użytkowników (czyli akceptowanie nowych użytkowników), 
usuwanie uzytkownikow, przydzielanie do klas, dodawanie przedmiotow
- Zmiana nazwy użytkownika poprzez panel admina
- możliwość wyświetlenia wszystkich użytkowników (admin), wszystkich nauczycieli i uczniów twojej klasy (nauczyciel),
wszystkich uczniów z twojej klasy i wychowawce (uczeń)
- Nauczyciele mogą przypisać się do danej klasy, przypisać sobie dany pzredmiot