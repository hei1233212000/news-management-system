Feature: Retrieve news

  Scenario: Retrieve a list of news where the news should be ordered by its time in descending order
    Given There are news in data repository as below:
      | caption | slug   | content        | createdTime             |
      | News 3  | news-3 | Some content 3 | 2020-01-01T00:00:03.000 |
      | News 4  | news-4 | Some content 4 | 2020-01-01T00:00:04.000 |
      | News 2  | news-2 | Some content 2 | 2020-01-01T00:00:02.000 |
      | News 1  | news-1 | Some content 1 | 2020-01-01T00:00:01.000 |
    When I retrieve the list of news, I should see the following result:
      | caption | slug   | content        | createdTime         |
      | News 4  | news-4 | Some content 4 | 2020-01-01T00:00:04 |
      | News 3  | news-3 | Some content 3 | 2020-01-01T00:00:03 |
      | News 2  | news-2 | Some content 2 | 2020-01-01T00:00:02 |
      | News 1  | news-1 | Some content 1 | 2020-01-01T00:00:01 |

  Scenario: Retrieve a news
    Given a news "first-news" already exist with following information:
      | caption     | First news          |
      | slug        | first-news          |
      | content     | Any content         |
      | createdTime | 2020-01-01T00:00:00 |
    When I retrieve the news "first-news", I should see the following result:
      | caption     | First news          |
      | slug        | first-news          |
      | content     | Any content         |
      | createdTime | 2020-01-01T00:00:00 |
