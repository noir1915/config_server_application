package org.example.forbiddenwordsservice.model.controller;

import lombok.RequiredArgsConstructor;
import org.example.forbiddenwordsservice.model.service.ForbiddenWordsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/forbidden")
@RequiredArgsConstructor
public class ForbiddenWordsController {

    private final ForbiddenWordsService service;

    @GetMapping("/list")
    public List<String> list() {
        return service.getAllWords();
    }

    @GetMapping("/add")
    public ResponseEntity<?> add(@RequestBody Map<String, String> body) {
        service.addWord(body.get("word"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody Map<String, String> body) {
        service.removeWord(body.get("word"));
        return ResponseEntity.ok().build();
    }
}
