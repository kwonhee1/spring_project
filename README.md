# spring project

## project(com.example.demo)  

### 구현사항(요구사항)
1. spring security  
   1. filters  
        > 1. black list filter : ip, access token blacklist
        > 2. access token filter : check access token
        > 3. refresh token filter : if no access token check refresh
        > 4. login filter(only LoginPage) : login
        > 5. logout filter(no custom) : logout
   2. utils
        > 1. blackListService  
            setBlackList(String )  
            checkBlackList(String )
        > 2. jwtService  
            createToken(String subject, Map<String, String> cliams, List<String> authorities)  
            getAuthentication(String token, Map<String, String> validateMap)