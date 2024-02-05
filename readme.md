# Spring Boot Java Project with Cucumber

On this guide, we will setup Cucumber on a Spring Boot Project and run acceptance tests with Junit Platform Engine. Cucumber Framework provides a way to write and run acceptance tests written in a behaviour-driven development (BDD) style.

## Overview

This project is based on this [example](https://github.com/spring-guides/gs-rest-service/tree/main). The project consists of a Spring Boot Application that provides a service that accept HTTP GET requests at endpoint `/greeting`. It responds with a JSON representation of a Greeting, as follows:

```json
{
  "id": 1,
  "content": "Hello, World!"
}
````

This greeting may be customized with an optional name parameter in the query string, as follows:

```
/greeting?name=Alice
```

## Setting Up End-to-End testing

### Required Dependencies

In the file `build.gradle` add the following dependencies

```gradle
dependencies {
  ...

  testImplementation(platform("org.junit:junit-bom:5.10.1"))
  testImplementation(platform("io.cucumber:cucumber-bom:7.15.0"))

  testImplementation("io.cucumber:cucumber-java")
  testImplementation("io.cucumber:cucumber-spring")
  testImplementation("io.cucumber:cucumber-junit-platform-engine")
  testImplementation("org.junit.platform:junit-platform-suite")
  testImplementation("org.junit.jupiter:junit-jupiter")
}
```

This will add Cucumber for JVM, Cucumber Spring Integration, Cucumber Junit Integration and Junit Platform Engine. Junit provides a unified platform for running tests, supporting multiple testing frameworks, in this case, Cucumber.

### Initializing Junit with Cucumber

In the directory `src/test/java/com/example/restservice/` add a runner class that will set up Junit in order to use Cucumber.

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/example/restservice")
public class RunCucumberTest {}
```

- `Suite` allows grouping multiple test classes and run them together.
- `IncludeEngines` specifies that the test suite should include tests from an engine (Cucumber in our case).
- `SelectClasspathResource` identifies a classpath resource containing test classes. In this case, it will look for test classes in the "com/example/restservice" package (We will create the definition files in this classpath).

### Cucumber Settings

We will add the following decorators to the runner class

```java
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "html:reports/cucumber-reports.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.restservice")
public class RunCucumberTest {}
```

This will configure Cucumber with two plugins:

- `pretty` is a plugin for more human-readable output.
- `html` is a plugin for that generates reports in HTML format. The reports will be placed in the output path `reports/cucumber-reports.html`.

`GLUE_PROPERTY_NAME` defines the package where Cucumber should look for step definitions and other glue code. Glue code specifically refers to the code that binds the plain-text Gherkin feature files to the Java implementation code.

## Creating a Feature Specification

In this specification we use a Scenario Outline along with Examples. This allows parameterize a scenario and define different inputs for testing the same behavior.

We will create a Feature Specification File in Gherkin Language in the directory `src/test/resources/com/example/restservice` called `greeting.feature`

```feature
Feature: Greeting

  Scenario Outline: Client makes a GET request to /greeting with different names
    When the client makes a GET request to /greeting with name param "<name>"
    Then the client receives a status code of 200
    And the client receives application/json with greeting "<expected_greeting>"

    Examples:
      | name             | expected_greeting        |
      |                  | Hello, World!            |
      | Alice            | Hello, Alice!            |
      | Bob              | Hello, Bob!              |
      | Kanye West       | Hello, Kanye West!       |
      | Taylor Swift     | Hello, Taylor Swift!     |
      | Tommy & Verónica | Hello, Tommy & Verónica! |
```

We will test the Spring Boot service that responds to the HTTP GET requests to endpoint `/greeting`. The Scenario Outline is used to define a template for the scenario, and the Examples table provides different sets of values for the parameters ("<name>" and "<expected_greeting>"). This way, we can test various cases without duplicating the common steps. 

### Creating the Glue Definition File

We will create the glue class in the directory `src/test/java/com/example/restservice`

```java
@SpringBootTest
@AutoConfigureMockMvc
@CucumberContextConfiguration
public class GreetingDefinitions {
  @Autowired private MockMvc mockMvc;
}
```

We will use three decorators

- `SpringBootTest` loads the complete Spring application context and provides a way to test Spring components.
- `AutoConfigureMockMvc` automatically configures a `MockMvc` instance to be used in the test. `MockMvc` is a part of Spring Test and allows for testing Spring MVC controllers without starting a full HTTP server.
- `CucumberContextConfiguration` specifies the configuration class to use when running Cucumber scenarios. This annotation is used on a configuration class to make the Cucumber aware of the test configuration (in this case, Spring configuration).

Now we are ready to start writing each step defined in the Greeting Feature Specification file.

### Defining Steps

In the `GreetingDefinitions` class, we will define three public functions that will be decorated as defined in the feature file. In Gherkin, each step stars with a preposition or an adverb (Given, When, Then, And, But). For further reading checkout [Cucumber API Definition](https://cucumber.io/docs/cucumber/api/?lang=java).

```java
@SpringBootTest
@AutoConfigureMockMvc
@CucumberContextConfiguration
public class GreetingDefinitions {
  @Autowired private MockMvc mockMvc;

  private ResultActions result;

  @When("the client makes a GET request to \\/greeting with name param {string}")
  public void clientCallsGreetingWithNameParam(String name) throws Exception {
    this.result = this.mockMvc.perform(get(String.format("/greeting?name=%s", name)));
  }

  @Then("the client receives a status code of {int}")
  public void clientReceivesStatusCode200(int statusCode) throws Exception {
    this.result = this.result.andExpect(status().is(statusCode));
  }

  @And("the client receives application\\/json with greeting {string}")
  public void clientReceivesJsonWithGreeting(String content) throws Exception {
    this.result.andExpect(jsonPath("$.content").value(content));
  }
}
```

Each string inside of the Steps Decorators must match with the definition in the Greeting Feature File. We define the parameters using brackets and inside them, the type of the parameter. Then, we can access to the value of these parameters in the function itself. [Step Definitions Reference](https://cucumber.io/docs/cucumber/step-definitions/?lang=java). We define a variable called `result` to maintain the state of the test. 

## Running the Tests

With gradle, we run the tests as follows,

```sh
./gradlew test
```

We can see the output of each test using two additional flags

```sh
./gradlew test --rerun-tasks --info
```

## Reviewing Reports

As we set up Cucumber's HTML plugin, we can see the results of our scenario. Open the `reports/cucumber-reports.html`

![Cucumber HTML Report](https://github.com/carlosarguelles/cucumber-spring-boot-e2e-testing/assets/70742476/a9397c14-09bf-48c1-84e7-8f6c31d1a5c1)

![Cucumber HTML Report Failed Scenario](https://github.com/carlosarguelles/cucumber-spring-boot-e2e-testing/assets/70742476/3048d5ea-d188-4ff2-add9-2224f73066a4)
