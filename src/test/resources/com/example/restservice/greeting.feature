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
