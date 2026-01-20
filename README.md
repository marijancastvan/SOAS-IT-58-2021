**Kredencijali:**

OWNER: Email: owner@uns.ac.rs Lozinka: ownerPass

ADMIN: Email: admin@uns.ac.rs Lozinka: adminPass

USER: Email: user@uns.ac.rs Lozinka: userPass

**============================ API Putanje ============================**

**1. Users Service**

GET all users GET http://localhost:8765/users

GET korisnik po EMAIL-u GET http://localhost:8765/users/email?email=user@uns.ac.rs

Kreiranje novog admina POST http://localhost:8765/users/newAdmin

Kreiranje novog korisnika POST http://localhost:8765/users/newUser

Izmena korisnika PUT http://localhost:8765/users/6

Brisanje korisnika po ID-u DELETE http://localhost:8765/users/5

**2. Currency Exchange**

Pregled kursnog odnosa GET http://localhost:8765/currency-exchange?from=EUR&to=USD

**3. Currency Conversion**

Konverzija iznosa GET http://localhost:8765/currency-conversion-feign?from=EUR&to=USD&quantity=100

**4. Bank Account Service**

ADMIN vidi sve račune u sistemu GET http://localhost:8765/bank-accounts

ADMIN vidi račun po EMAIL-u GET http://localhost:8765/bank-accounts/user@uns.ac.rs

ADMIN kreiranje novog računa POST http://localhost:8765/bank-accounts

ADMIN izmena računa PUT http://localhost:8765/bank-accounts/marijan@uns.ac.rs

**5. Crypto Wallet Service**

ADMIN vidi sve kripto novčanike u sistemu GET http://localhost:8765/crypto-wallets

ADMIN vidi kripto novčanik po EMAIL-u GET http://localhost:8765/crypto-wallets/marijan@uns.ac.rs

ADMIN kreiranje novog crypto računa POST http://localhost:8765/crypto-wallets

ADMIN izmena crypto računa PUT http://localhost:8765/crypto-wallets/user@uns.ac.rs

**6. Crypto Exchange**

Pregled crypto kursevi GET http://localhost:8765/crypto-exchange?from=BTC&to=ETH

**7. Crypto Conversion**

Konverzija kripto valute GET http://localhost:8765/crypto-conversion-feign?from=BTC&to=ETH&quantity=0.5

**8. Trade Service**

Trgovina fiat ↔ crypto GET http://localhost:8765/trade-service?from=BTC&to=EUR&quantity=2

