Feature: Create news

  Scenario: Create news
    Given A news "what-is-covid-197" does not exist
    And the server time is "2020-01-01T00:00:00.000"
    When I create a news with following details:
      | caption | What is COVID-19?                                                                                                                 |
      | slug    | what-is-covid-197                                                                                                                 |
      | content | COVID-19 is a virus strain, first identified in Wuhan, Hubei Province, China, that has only spread in people since December 2019. |
    Then I could see the news "what-is-covid-197" with following information:
      | caption     | What is COVID-19?                                                                                                                 |
      | content     | COVID-19 is a virus strain, first identified in Wuhan, Hubei Province, China, that has only spread in people since December 2019. |
      | createdTime | 2020-01-01T00:00:00                                                                                                               |
      | updatedTime | 2020-01-01T00:00:00                                                                                                               |

  Scenario: Create news with caption longer than 80 characters should have validation error
    When I create a news with following details will cause validation error:
      | caption | This is a caption very very very very very long caption which is longer than 80 characters |
      | slug    | any-slug                                                                                   |
      | content | any content                                                                                |

  Scenario: Create news with slug having non-alphanumeric and non-hyphen character
    When I create a news with following details will cause validation error:
      | caption | more examples in unit test |
      | slug    | slug with space            |
      | content | any content                |

  Scenario Outline: Create news with slug or caption which is already in used will cause conflict error
    Given There are news in data repository as below:
      | caption | slug | content      | createdTime             |
      | News    | news | Some content | 2020-01-01T00:00:03.000 |
    When I create a news with following details will cause conflict error:
      | caption | <captionToBeUsed> |
      | slug    | <slugToBeUsed>    |
      | content | any content       |
    Examples:
      | captionToBeUsed | slugToBeUsed |
      | News            | another-slug |
      | Another caption | news         |
