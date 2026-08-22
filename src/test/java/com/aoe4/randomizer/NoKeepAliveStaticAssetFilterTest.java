package com.aoe4.randomizer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NoKeepAliveStaticAssetFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addsConnectionCloseHeaderForImageResources() throws Exception {
        mockMvc.perform(get("/images/civs/abbasid-dynasty.png"))
                .andExpect(status().isOk())
                .andExpect(header().string("Connection", "close"));
    }

    @Test
    void doesNotForceConnectionCloseForApiEndpoints() throws Exception {
        mockMvc.perform(get("/api/civs"))
                .andExpect(status().isOk())
                .andExpect(result -> assertNotEquals("close", result.getResponse().getHeader("Connection")));
    }
}
