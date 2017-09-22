# dev-challenge
dev challenge for DB

Rest point to transfer money between accounts:

POST http://localhost:18080/v1/accounts/transfer

BODY application/json 
    {
        "accountFromId":"200", 
        "accountToId":"100", 
        "amount": "100"
    }
    
Success response: 202 [ACCEPTED]

**To run the application**

build using ./gradlew build 

run using ./gradlew bootRun
