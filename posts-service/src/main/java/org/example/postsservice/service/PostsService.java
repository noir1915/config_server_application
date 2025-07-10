package org.example.postsservice.service;

import lombok.RequiredArgsConstructor;
import org.example.forbiddenwordsservice.model.service.ForbiddenWordsService;
import org.example.postsservice.model.Post;
import org.example.postsservice.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostsService {

    @Value("${post.max.length}")
    private int maxPostLength;

    final private PostRepository postRepository;

    private final ForbiddenWordsService forbiddenWordsService;

    private final RestTemplate restTemplate; // для вызова аналитики

    public Post createPost(Long userId, String content) {

        // проверка длины
        if (content.length() > maxPostLength) {
            savePost(userId, content, false, "length");
            sendToAnalytics(userId, false, "length");
            throw new RuntimeException("Post length exceeds limit");
        }

        // проверка запрещенных слов
        for (String forbidden : forbiddenWordsService.getAllWords()) {
            if (content.contains(forbidden)) {
                savePost(userId, content, false, "forbidden");
                sendToAnalytics(userId, false, "forbidden");
                throw new RuntimeException("Contains forbidden words");
            }
        }

        // публикация поста
        Post post = savePost(userId, content, true, null);
        sendToAnalytics(userId, true, null);
        return post;

    }

    private Post savePost(Long userId,String content , boolean published , String reason) {
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setPublished(published);
        post.setRejectionReason(reason);
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    private void sendToAnalytics(Long userId , boolean published , String reason) {

        Map<String,Object> payload= new HashMap<>();
        payload.put("userId", userId);
        payload.put("published", published);
        payload.put("reason", reason);

        restTemplate.postForObject("http://localhost:8084/analytics/post", payload , Void.class);

    }

    public List<Post> getPostsByUser(Long userId){
        return postRepository.findByUserId(userId);
    }
}