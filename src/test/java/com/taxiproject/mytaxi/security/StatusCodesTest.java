package com.taxiproject.mytaxi.security;

import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import com.taxiproject.mytaxi.repository.ManagerRepository;
import com.taxiproject.mytaxi.service.ManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
public class StatusCodesTest {

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;


    @BeforeEach
    public void setUp() {

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "SergeyIvanov", roles = "USER")
    public void testUserDeleteCar_Return403() throws Exception {
        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "SergeyIvanov", roles = "USER")
    public void testUserPostCar_Return403() throws Exception {
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"licensePlate\":\"X999XX\",\"yearOfRelease\":\"2025\",\"carMileage\":\"0\",\"price\":1000000,\"brandId\":1,\"enterpriseId\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "SergeyIvanov", roles = "USER")
    public void testUserPutCar_Return403() throws Exception {
        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"licensePlate\":\"A777AA\",\"yearOfRelease\":\"2023\",\"carMileage\":\"15000\",\"price\":2555000,\"brandId\":2,\"enterpriseId\":2}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "SergeyIvanov", roles = "USER")
    public void testUserGetCars_Return200() throws Exception {
        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUnauthenticatedUser_Return401() throws Exception {
        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"licensePlate\":\"X999XX\",\"yearOfRelease\":\"2025\",\"carMileage\":\"0\",\"price\":1000000,\"brandId\":1,\"enterpriseId\":1}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"licensePlate\":\"A777AA\",\"yearOfRelease\":\"2023\",\"carMileage\":\"15000\",\"price\":2555000,\"brandId\":2,\"enterpriseId\":2}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testWrongPassword_Return403() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"DanilKhomenko\",\"password\":\"wrongPassword\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "SergeyIvanov", roles = "USER")
    public void testAccessToInvisibleEnterprise_Return403() throws Exception {
        mockMvc.perform(get("/api/enterprises/1")) //SergeyIvanov не прикреплен к предприятию 1
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "DanilKhomenko", roles = "ADMIN")
    public void testInvalidData_Return400() throws Exception {
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"yearOfRelease\":\"2025\",\"carMileage\":\"0\",\"price\":1000000,\"brandId\":1,\"enterpriseId\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "DanilKhomenko", roles = "ADMIN")
    public void testDeleteEnterpriseWithDependencies_Return409() throws Exception {
        mockMvc.perform(delete("/api/enterprises/1")) // предприятие 1 имеет добавленные автомобили + водителей
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "DanilKhomenko", roles = "ADMIN")
    public void testPostCar_Return200() throws Exception {
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"licensePlate\":\"X888XX\",\"yearOfRelease\":\"2025\",\"carMileage\":\"0\",\"price\":1000000,\"brandId\":1,\"enterpriseId\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "DanilKhomenko", roles = "ADMIN")
    public void testDeleteCar_Return204() throws Exception {
        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isNoContent());
    }



}
