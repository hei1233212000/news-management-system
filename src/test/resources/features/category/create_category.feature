Feature: Create category

  Scenario: Create category
    Given A category "first-category" does not exist
    When I create a category "first-category"
    Then I could see the category "first-category" in the repository
