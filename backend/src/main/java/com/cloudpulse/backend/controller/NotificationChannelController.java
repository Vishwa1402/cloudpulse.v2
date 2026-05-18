package com.cloudpulse.backend.controller;

import com.cloudpulse.backend.entity.NotificationChannel;
import com.cloudpulse.backend.repository.NotificationChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications/channels")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class NotificationChannelController {

    @Autowired
    private NotificationChannelRepository channelRepo;

    @GetMapping
    public ResponseEntity<List<NotificationChannel>> getChannels() {
        return ResponseEntity.ok(channelRepo.findAll());
    }

    @PostMapping
    public ResponseEntity<NotificationChannel> createChannel(@RequestBody NotificationChannel channel) {
        return ResponseEntity.ok(channelRepo.save(channel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable Long id) {
        channelRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
