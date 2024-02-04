package com.example.restservice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@CucumberContextConfiguration
public class GreetingDefinitions {
  @Autowired protected MockMvc mockMvc;

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
