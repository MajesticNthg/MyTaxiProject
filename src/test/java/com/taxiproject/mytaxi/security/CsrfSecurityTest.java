package com.taxiproject.mytaxi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.mock.web.MockHttpSession;
import org.junit.jupiter.api.Disabled;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled
@SpringBootTest
public class CsrfSecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    private String extractCsrfToken(String html) {
        Pattern pattern = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Test
    public void testLoginWithoutCsrf_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "DanilKhomenko")
                        .param("password", "admin123"))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    public void testLoginWithWrongCsrf_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "DanilKhomenko")
                        .param("password", "admin123")
                        .param("_csrf", "wrong-token-123456789"))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    public void testLoginWithCorrectCsrf_ShouldRedirect() throws Exception {
        MvcResult loginPage = mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andReturn();

        String csrfToken = extractCsrfToken(loginPage.getResponse().getContentAsString());

        MockHttpSession session = (MockHttpSession) loginPage.getRequest().getSession();

        mockMvc.perform(post("/login")
                        .param("username", "DanilKhomenko")
                        .param("password", "admin123")
                        .param("_csrf", csrfToken)
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cars"));
    }
}