package com.onlydevs.bookstore.endpoint.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

  @GetMapping("/swagger-ui.html")
  public String swaggerUi() {
    return "redirect:/swagger.html";
  }
}
