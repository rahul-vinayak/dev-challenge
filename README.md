# dev-challenge
dev challenge for DB

Rest endpoint to transfer money between accounts:

<code>
    
POST http://localhost:18080/v1/accounts/transfer

BODY CONTENT-TYPE application/json 
    
    {
        
        "accountFromId":"200", 
        
        "accountToId":"100", 
        
        "amount": "100"
    
    }
    
Success response: 202 [ACCEPTED]

</code>

**To run the application**

<code>
    
build using ./gradlew build 

run using ./gradlew bootRun

</code>

**THE BUILD WILL FAIL BECAUSE OF THE NEW FAILING TEST ADDED TO PROVE THREADSAFETY COMPARISON.**

**TO BUILD IT SUCCESSFULLY COMMENT OUT OR IGNORE THE TESTS IN FOLLOWING FILE**

<code>AccountsRepositoryInMemoryTest.java</code>
