# java-was-2022
Java Web Application Server 2022

## 요구사항 1: 정적인 html 파일 응답

> [http://localhost:8080/index.html](http://localhost:8080/index.html)
로 접속했을 때 webapp 디렉토리의 index.html 파일을 읽어 클라이언트에 응답한다.
>

### 로직 순서

1. BufferedReader로 InputStream 값을 한 줄(readLine()을 사용해)씩 읽어가며 로직을 처리한다.
2. header의 첫번째 라인을 읽는 경우, `httpMethod HTTPTarget HTTP/버전` 순으로 출력되기 때문에 split을 사용해 첫 번째 URL을 가져온다.
3. 해당 URL에 해당하는 파일을 response에 담아 처리한다.

## 요구사항 2: ****GET으로 회원가입 기능 구현****

> “회원가입” 메뉴를 클릭하면 http://localhost:8080/user/form.html 으로 이동하면서 회원가입 폼을 표시한다. 이 폼을 통해서 회원가입을 할 수 있다.회원가입을 하면 다음과 같은 형태로 사용자가 입력한 값이 서버에 전달된다.

### 개선점

1. ~~첫번째 라인을 확인하기 위해 lineCount라는 변수를 사용했는데, 0일 때만 확인해서, 첫번째 url 추출하는 부분은 따로 처리하는 게 나은지에 대한 방법 고안해야 한다.~~ -> step2에서 리팩토링, 시작줄은 따로 처가

```
/create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net
```

> HTML과 URL을 비교해 보고 사용자가 입력한 값을 파싱해 model.User 클래스에 저장한다.

### 로직 순서

1. 기존 코드에서 HttpRequest 클래스를 구현해 request에 관한 값을 처리한다.
2. 이 때 해당 인스턴스를 만들기 위해 이를 파싱해주는 RequestParser를 사용한다.
3. 인스턴스가 생성되면 해당 내부에 있는 필드 값인 path값을 통해 User 클래스에 값을 저장한다.

### 개선점

1. 뭔가 지저분해보인다. 변수 변경 등이 필요하다.

## **요구사항 3: Stylesheet 파일 지원**

> 지금까지 구현한 소스 코드는 stylesheet 파일을 지원하지 못하고 있다. Stylesheet 파일을 지원하도록 구현하도록 한다.
>

### 로직 순서

1. response header를 write할 때 파싱해온 HttpRequest의 path에 따라 분기한다.
2. 정적파일이기 때문에 확장자에 따라 context-type을 리턴한다.

### 개선점

1. ~~response 타입을 좀 더 클래스로 추상화해야 한다.~~
2. 그래도.. 깨끗하지 못한……..


## 요구사항 4: POST로 회원 가입 구현
> http://localhost:8080/user/form.html 파일의 form 태그 method를 get에서 post로 수정한 후 회원가입 기능이 정상적으로 동작하도록 구현한다.가입 후 페이지 이동을 위해 redirection 기능을 구현한다.

### 로직 순서

1. form.html의 `method`를 `get`에서 `post`로 수정한다.
2. RequestHandler.getHttpRequestFromInput 메서드에 body를 읽는 코드를 추가한다.
3. processRequest에서 api request path에 따라 분기, controller가 처리한다.
4. 응답 헤더와 바디를 작성한다.

### 개선점
1. ~~생성자 빌더 패턴으로 변환~~
2. 하드 코딩 제거 enum or 추상화 등등
3. 테스트 코드 작성

## **요구사항 5: Cookie를 이용한 로그인 구현**

> “로그인” 메뉴를 클릭하면 http://localhost:8080/user/login.html 으로 이동해 로그인할 수 있다. 로그인이 성공하면 index.html로 이동하고, 로그인이 실패하면 /user/login_failed.html로 이동해야 한다.
>
>
> 앞에서 회원가입한 사용자로 로그인할 수 있어야 한다. 로그인이 성공하면 cookie를 활용해 로그인 상태를 유지할 수 있어야 한다. 로그인이 성공할 경우 요청 header의 Cookie header 값이 logined=true, 로그인이 실패하면 Cookie header 값이 logined=false로 전달되어야 한다.
>

### 로직 순서

1. api 요청이 들어오면 RequestHandler의 run을 거쳐 processRequest가 실행된다.
2. 이 때 url에 따라 각각에 맞는 Controller의 process를 실행하도록 한다.
3. 로그인의 경우 UserController로 이관한다.
4. UserController는 각 요청에 필요한 파라미터 값을 파싱한 후, UserService에 넘긴다.
5. UserService는 DB와 관련된 작업을 한 후 필요에 따라 값을 반환한다.
6. Controller는 반환된 값을 사용해 HttpResponse 객체를 만들어 반환한다.

### 개선점

1. 헤더 값 부여하는 부분, 에러 코드, 메세지 등이 아직 분리되지 않았다.
2. 뭔가 하드코딩이 되어가는 느낌