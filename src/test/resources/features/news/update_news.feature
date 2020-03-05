Feature: Update news

  Scenario: Update the content of a news
    Given There are news in data repository as below:
      | caption | slug | content          | createdTime             | updatedTime             |
      | News    | news | Original content | 2020-01-01T00:00:01.000 | 2020-01-01T00:00:01.000 |
    And the server time is "2020-01-02T00:00:00.000"
    When I update the "news" with following information:
      | content | Updated content |
    Then I could see the news "news" with following information:
      | caption     | News                |
      | slug        | news                |
      | content     | Updated content     |
      | createdTime | 2020-01-01T00:00:01 |
      | updatedTime | 2020-01-02T00:00:00 |

  Scenario: Update the content of a news which does not exist
    Given A news "news" does not exist
    When I update the "news" with following information will cause resource not found error:
      | content | Updated content |
