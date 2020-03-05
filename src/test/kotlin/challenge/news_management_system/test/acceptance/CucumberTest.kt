package challenge.news_management_system.test.acceptance

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["classpath:features"],
    plugin = ["pretty"]
)
class CucumberTest
