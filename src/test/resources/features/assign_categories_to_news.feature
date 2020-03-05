Feature: Assign categories to a news

  Scenario: Assign categories to a news
    Given the listed categories already exist in the repository:
      | category-1 |
      | category-2 |
    And There are news in data repository as below:
      | caption | slug | content      | createdTime             |
      | News    | news | Some content | 2020-01-01T00:00:00.000 |
    When I assign the listed categories to news "news":
      | category-1 |
      | category-2 |
    Then I could see the news "news" has the listed category labels:
      | category-1 |
      | category-2 |
