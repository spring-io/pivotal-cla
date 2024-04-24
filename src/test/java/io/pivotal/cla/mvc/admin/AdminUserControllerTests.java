package io.pivotal.cla.mvc.admin;


import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.UserRepository;
import io.pivotal.cla.security.ImportSecurity;
import io.pivotal.cla.security.WithAdminUser;
import io.pivotal.cla.security.WithClaAuthorUser;
import io.pivotal.cla.security.WithSigningUser;
import io.pivotal.cla.service.ClaService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUserController.class)
@ImportSecurity
public class AdminUserControllerTests {
    @MockBean
    UserRepository users;

    @MockBean
    ClaService claService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithClaAuthorUser
    public void findByUsernameWhenClaAuthorUserThenOk() throws Exception {
        User user = new User();
        user.setAccessToken("do not display");
        user.setEmails(Collections.singleton("noreply@example.com"));
        when(this.users.findById("user")).thenReturn(Optional.of(user));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/users/user"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"emails\":[\"noreply@example.com\"]}"))
                .andExpect(jsonPath("$.accessToken", CoreMatchers.nullValue()));
    }

    @Test
    @WithAdminUser
    public void findByUsernameWhenClaAuthorUserThenNotOk() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/users/user"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithSigningUser
    public void findByUsernameWhenSigningUserThenNotOk() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/users/user"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void findByUsernameWhenAnonymousThenNotOk() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/users/user"))
                .andExpect(status().is3xxRedirection());
    }

}