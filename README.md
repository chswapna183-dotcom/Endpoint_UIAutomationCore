# Endpoint UI Automation Core

Maven-based Java 17 automation framework with **TestNG** for both **UI (Selenium)** and **API (RestAssured)** testing.

## Tech Stack
- Java 17 (compiled with `maven.compiler.release=17`)
- TestNG
- Selenium WebDriver + WebDriverManager
- RestAssured
- Jackson + Gson

## Project Structure
```
src/main/java/com/chswapna183_dotcom/
  api/
    base/        BaseAPITest
    client/      APIClient
    endpoints/   UserEndpoints
    payload/     UserPayload
    utils/       APIUtil
  base/          TestBase (WebDriver setup)
  config/        ConfigLoader
  pages/         LoginPage, HomePage (POM)
  util/          TestUtil

src/test/java/com/chswapna183_dotcom/
  api_test/      UserCRUDTests
  test/          LoginPageTest, HomePageTest, UITestBase
  support/       MockServer (local API + UI server used by tests)
```

## Configuration
Default config file:
- `src/main/resources/config.properties`

Supported keys:
- `baseUrl` (UI base URL)
- `browser` (`chrome`, `firefox`, `edge`)
- `timeout` (seconds)
- `headless` (`true|false`)
- `apiBaseUrl`
- `apiBasePath`

You can override any config key via JVM system properties, for example:
```
mvn test -Dheadless=false -Dbrowser=firefox -Dtimeout=15
```

## Running Tests
Run all tests:
```
mvn test
```

Run API tests only:
```
mvn test -Dgroups=api
```

Run UI tests only:
```
mvn test -Dgroups=ui
```

## Notes
- Tests use `MockServer` during execution to avoid depending on external demo sites/APIs (more stable and CI-friendly).
- A Lombok dependency exists in `pom.xml`, but annotation processing is disabled to keep builds stable with newer JDKs. If you want to use Lombok in your own classes, remove `<proc>none</proc>` from the `maven-compiler-plugin` configuration and ensure your JDK/Lombok versions are compatible.

