Kredencijali:

OWNER: Email: owner@uns.ac.rs Lozinka: ownerpass

ADMIN: Email: admin@uns.ac.rs Lozinka: adminpass

USER: Email: user@uns.ac.rs Lozinka: userpass

============================ API Putanje ============================
1. Users Service

GET svi korisnici GET http://localhost:8770/api/users

GET korisnik po EMAIL-u GET http://localhost:8770/api/users/email?email=user@uns.ac.rs

Kreiranje novog korisnika POST http://localhost:8770/api/users/newUser

Kreiranje novog admina POST http://localhost:8770/api/users/newAdmin

Izmena korisnika PUT http://localhost:8770/api/users

Brisanje korisnika po EMAIL-u DELETE http://localhost:8770/api/users?email=admin2@uns.ac.rs

2. Currency Exchange

Pregled kursnog odnosa GET http://localhost:8000/api/currency-exchange?from=USD&to=EUR

3. Currency Conversion

Konverzija iznosa GET http://localhost:8100/api/currency-conversion?from=eur&to=usd&quantity=100

4. Bank Account Service

ADMIN vidi sve račune u sistemu GET http://localhost:8200/api/bank-accounts

ADMIN vidi račun po EMAIL-u GET http://localhost:8200/api/bank-accounts/email?email=user@uns.ac.rs

ADMIN kreiranje novog računa POST http://localhost:8200/api/bank-accounts/createForUser?email=marijan@uns.ac.rs

ADMIN izmena računa PUT http://localhost:8200/api/bank-accounts/email?email=user@uns.ac.rs

5. Crypto Wallet Service

ADMIN vidi sve kripto novčanike u sistemu GET http://localhost:8300/api/wallet/all

ADMIN vidi kripto novčanik po EMAIL-u GET http://localhost:8300/api/wallet/all

!!!ADMIN kreiranje novog crypto računa POST http://localhost:8300/api/wallet/create?email=pavle@uns.ac.rs

ADMIN izmena crypto računa PUT http://localhost:8300/api/wallet/update/user@uns.ac.rs

ADMIN WALLET WITHDRAW POST http://localhost:8300/api/wallet/withdraw?email=user@uns.ac.rs&currency=btc&amount=0.01

ADMIN WALLET DEPOSIT POST http://localhost:8300/api/wallet/deposit?email=user@uns.ac.rs&currency=BTC&amount=0.05

ADMIN Brisanje wallet po EMAIL-u DELETE http://localhost:8300/api/wallet/marijan@uns.ac.rs

6. Crypto Exchange

Svi crypto kursevi GET http://localhost:8400/api/crypto-exchange

PAIR crypto kursa GET http://localhost:8400/api/crypto-exchange/pair?fromCurrency=BTC&toCurrency=ETH

ADMIN kreiranje nove crypto exchange POST http://localhost:8400/api/crypto-exchange

ADMIN izmena crypto exchange PUT http://localhost:8400/api/crypto-exchange/7

ADMIN brisanje crypto exchange DELETE http://localhost:8400/api/crypto-exchange/7

7. Crypto Conversion

Konverzija kripto valute POST http://localhost:8500/api/conversion

8. Trade Service

Trgovina fiat ↔ crypto POST http://localhost:8600/api/trade

