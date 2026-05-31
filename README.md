\# OrderManagementApp - System Zarządzania Logistyką Zamówień



Aplikacja mobilna (Android) zintegrowana z backendem w chmurze \*\*Azure\*\*, służąca do wewnętrznego zarządzania procesami logistycznymi i statystykami zamówień w przedsiębiorstwie. System implementuje architekturę opartą o podział uprawnień (RBAC) dla ról \*\*Admin\*\* oraz \*\*Worker\*\*.



\## 🚀 Architektura i Technologie



\### Backend (.NET Core Web API)

\* \*\*Hosting:\*\* Azure App Service

\* \*\*Baza danych:\*\* Azure SQL Database (Entity Framework Core)

\* \*\*Kolejkowanie asynchroniczne:\*\* Azure Queue Service (komunikaty o zmianach statusów)

\* \*\*Autoryzacja:\*\* JSON Web Token (JWT) z obsługą ról



\### Frontend (Android / Kotlin)

\* \*\*Architektura:\*\* MVVM (Model-View-ViewModel)

\* \*\*Komponenty UI:\*\* RecyclerView (kafelkowa lista zamówień), CardView, niestandardowy wykres statystyk

\* \*\*Komunikacja sieciowa:\*\* Retrofit2 + Coroutines (asynchroniczne zapytania HTTP)



\---



\## 📱 Funkcjonalności Systemu



\### 🔒 Moduł Autoryzacji

\* Bezpieczna rejestracja (nowe konta domyślnie otrzymują rolę `Worker`) oraz logowanie.

\* Przechowywanie tokenu sesji i roli użytkownika w `SharedPreferences`.



\### 📦 Zarządzanie Zamówieniami (Operacje CRUD)

\* \*\*Pobieranie danych (GET):\*\* Dynamiczna lista zamówień prezentowana za pomocą wydajnego widoku `RecyclerView`.

\* \*\*Tworzenie (POST):\*\* Formularz dodawania nowej paczki z automatycznym wyliczaniem sumy (`Price \* Quantity`) na backendzie.

\* \*\*Usuwanie (DELETE):\*\* Intuicyjne usuwanie zamówienia z bazy danych poprzez pojedyncze kliknięcie kafelka.



\### 🛠️ Uprawnienia Administratora (Admin)

\* \*\*Modyfikacja statusu (PATCH):\*\* Długie przytrzymanie kafelka otwiera okno dialogowe pozwalające zmienić status (`Pending` -> `Shipped` -> `Completed`), co automatycznie generuje komunikat w kolejce \*\*Azure Queue Service\*\*.

\* \*\*Panel Statystyk:\*\* Dedykowany ekran analityczny wyświetlający łączny obrót finansowy oraz natywny wykres słupkowy proporcji statusów zamówień.



\---



\## 📸 Zrzuty Ekranu Aplikacji



| Ekran Główny (Lista Zamówień) | Panel Statystyk (Admin) |

|---|---|

| !\[Ekran Główny](sciezka\_do\_pliku/main\_activity.png) | !\[Statystyki](sciezka\_do\_pliku/stats\_activity.png) |



| Zarządzanie Statusami | Usuwanie Zamówienia |

|---|---|

| !\[Statusy](sciezka\_do\_pliku/status\_dialog.png) | !\[Usuwanie](sciezka\_do\_pliku/delete\_dialog.png) |



\---



\## 🛠️ Instrukcja Uruchomienia



1\. Sklonuj repozytorium.

2\. Otwórz projekt w \*\*Android Studio\*\*.

3\. Upewnij się, że urządzenie/emulator ma dostęp do Internetu (aplikacja komunikuje się z produkcyjnym API na Azure).

4\. Uruchom aplikację.

