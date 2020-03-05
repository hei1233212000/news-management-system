Feature: Assign sub category

  Scenario: Assign category to another category
    Given the listed categories already exist in the repository:
      | parent  |
      | child-1 |
      | child-2 |
    When I assign "child-1" as a sub-category of "parent"
    When I assign "child-2" as a sub-category of "parent"
    Then I could see the category "parent" has the following sub-categories:
      | child-1 |
      | child-2 |
