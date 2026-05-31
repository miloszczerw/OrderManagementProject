\# OrderManagementApp - System Zarządzania Logistyką Zamówień



Aplikacja mobilna (Android) zintegrowana z backendem w chmurze Azure, służąca do wewnętrznego zarządzania procesami logistycznymi i statystykami zamówień w przedsiębiorstwie. System implementuje architekturę opartą o podział uprawnień (RBAC) dla ról Admin oraz Worker.



\## Architektura i Technologie



Backend (.NET Core Web API):

\* Hosting: Azure App Service

\* Baza danych: Azure SQL Database (Entity Framework Core)

\* Kolejkowanie asynchroniczne: Azure Queue Service (komunikaty o zmianach statusów)

\* Autoryzacja: JSON Web Token (JWT) z obsługą ról



Frontend (Android / Kotlin):

\* Architektura: MVVM (Model-View-ViewModel)

\* Komponenty UI: RecyclerView (kafelkowa lista zamówień), CardView, niestandardowy wykres statystyk

\* Komunikacja sieciowa: Retrofit2 + Coroutines (asynchroniczne zapytania HTTP)



\## Funkcjonalności Systemu



Moduł Autoryzacji:

\* Bezpieczna rejestracja (nowe konta domyślnie otrzymują rolę Worker) oraz logowanie.

\* Przechowywanie tokenu sesji i roli użytkownika w SharedPreferences.



Zarządzanie Zamówieniami (Operacje CRUD):

\* Pobieranie danych (GET): lista zamówień prezentowana za pomocą widoku RecyclerView.

\* Tworzenie (POST): formularz dodawania nowej paczki z automatycznym wyliczaniem sumy (Price \* Quantity) na backendzie.

\* Usuwanie (DELETE): usuwanie zamówienia z bazy danych poprzez kliknięcie kafelka.



Uprawnienia Administratora (Admin):

\* Modyfikacja statusu (PATCH): długie przytrzymanie kafelka otwiera okno dialogowe pozwalające zmienić status (Pending, Shipped, Completed), co automatycznie generuje komunikat w kolejce Azure Queue Service.

\* Panel Statystyk: dedykowany ekran analityczny wyświetlający łączny obrót finansowy oraz natywny wykres słupkowy proporcji statusów zamówień.



\## Zrzuty Ekranu Aplikacji

Ekran Logowania:

<img width="385" height="793" alt="reje" src="https://github.com/user-attachments/assets/dcd947d4-7f8d-45e1-992b-70970d021860" />

Ekran Rejestracji:

<img width="383" height="794" alt="Untitled" src="https://github.com/user-attachments/assets/5dca7b0c-b281-4994-af4d-e4c9872a4d80" />

Ekran główny:

<img width="362" height="808" alt="glowny" src="https://github.com/user-attachments/assets/f151f4eb-cebc-484d-8b58-dd6a9ce2f734" />

Panel Statystyk (Admin):

<img width="363" height="804" alt="statystyki" src="https://github.com/user-attachments/assets/2c1d5057-4d4f-4e04-9fd8-0cf6a58d446d" />

Zarządzanie Statusami:

<img width="318" height="158" alt="status" src="https://github.com/user-attachments/assets/e5ab17bb-b471-4c8f-bc99-63a22ce44023" />

Usuwanie Zamówienia:

<img width="313" height="138" alt="usun" src="https://github.com/user-attachments/assets/8efaf0f2-7f67-4ba9-8925-7b6cca741f3b" />

Nowe zamówienie:

<img width="365" height="802" alt="nowezam" src="https://github.com/user-attachments/assets/7452dd00-7ece-4295-b45e-476e0e99679c" />

Profil:

<img width="364" height="806" alt="wylog" src="https://github.com/user-attachments/assets/b76792f4-6120-4a97-8f26-31ed56216e28" />


\## Instrukcja Uruchomienia



1\. Sklonuj repozytorium.

2\. Otwórz projekt w Android Studio.

3\. Dostęp do Internetu.

4\. Uruchom aplikację.

