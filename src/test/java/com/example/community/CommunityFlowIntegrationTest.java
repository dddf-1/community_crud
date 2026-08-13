package com.example.community;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommunityFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void cookieLoginSupportsPostCommentLikeSearchAndSoftDeleteFlow() throws Exception {
        mockMvc.perform(post("/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "flow@example.com",
                                  "password": "Password1!",
                                  "nickname": "흐름테스트"
                                }
                                """))
                .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "flow@example.com",
                                  "password": "Password1!"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        Cookie accessToken = loginResult.getResponse().getCookie("accessToken");
        assertThat(accessToken).isNotNull();
        assertThat(accessToken.isHttpOnly()).isTrue();

        mockMvc.perform(get("/v1/auth/check").cookie(accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("flow@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("흐름테스트"));

        MvcResult createPostResult = mockMvc.perform(post("/v1/posts")
                        .cookie(accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "통합 테스트 게시글",
                                  "content": "댓글과 좋아요를 검증합니다."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.author.nickname").value("흐름테스트"))
                .andReturn();

        JsonNode createdPost = objectMapper.readTree(createPostResult.getResponse().getContentAsString());
        long postId = createdPost.path("data").path("id").asLong();

        mockMvc.perform(get("/v1/posts").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(postId))
                .andExpect(jsonPath("$.data.content[0].commentCount").value(0))
                .andExpect(jsonPath("$.data.content[0].likeCount").value(0));

        mockMvc.perform(post("/v1/posts/{postId}/comments", postId)
                        .cookie(accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"commentContent\":\"첫 댓글\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.author.nickname").value("흐름테스트"));

        mockMvc.perform(post("/v1/posts/{postId}/likes", postId).cookie(accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(1));

        mockMvc.perform(get("/v1/posts/{postId}", postId).cookie(accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.viewCount").value(1))
                .andExpect(jsonPath("$.data.commentCount").value(1))
                .andExpect(jsonPath("$.data.likeCount").value(1))
                .andExpect(jsonPath("$.data.liked").value(true));

        mockMvc.perform(get("/v1/posts/search")
                        .param("keyword", "통합")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "relevance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(postId));

        mockMvc.perform(delete("/v1/posts/{postId}", postId).cookie(accessToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/posts/{postId}", postId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("POST_NOT_FOUND"));
    }

    @Test
    void protectedEndpointRejectsMissingCookie() throws Exception {
        mockMvc.perform(post("/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"제목\",\"content\":\"내용\"}"))
                .andExpect(status().isUnauthorized());
    }
}
