# spring project

## project(com.example.demo)  

### 구현 내용 정리(공부용)
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
   3. 기능 설명  
        > 1. security 각각의 filter들을 순서대로 실행시킴
        > 2. 각각의 filter 내부를 돌면서 authentication객체를 만들고 security context에 저장함  
         >> 1. filter별로 request에서 정보를 가져와서 authenticaion객체를 만듬
         >> 2. 각각 선언된 filter.authentic manager.provider에서 authentic을 검사하고(db 비교) isAuthentiecated상태로 변환
         >> 3. AuthenticException발생시 FailureHandler, 성공시 SuccessHandler (+ next filter 진행)
        > 3. 모든 filter를 정상적으로 돌고나서 authenticaion에서 getAuthorites를 실행시켜서 권한가져온다 (page제한에 사용됨)
        > 4. 중간에 FailureHandler or response작성시 해당 내용을 EntryPoint를 통해 처리한다(controller 로 전달 필요)