package com.cloudpulse.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.Random;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class DemoServiceApplication {

    private final Random random = new Random();

    public static void main(String[] args) {
        SpringApplication.run(DemoServiceApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from monitored demo-service!";
    }

    @GetMapping("/cpu")
    public String cpuSpike(@RequestParam(defaultValue = "10") int seconds) {
        long startTime = System.currentTimeMillis();
        long duration = seconds * 1000L;
        // Keep CPU busy for the duration
        while (System.currentTimeMillis() - startTime < duration) {
            double x = Math.sin(random.nextDouble());
        }
        return "Generated CPU load for " + seconds + " seconds";
    }

    @GetMapping("/delay")
    public String delay(@RequestParam(defaultValue = "500") int ms) throws InterruptedException {
        Thread.sleep(ms);
        return "Delayed response by " + ms + " ms";
    }

    @GetMapping("/error")
    public ResponseEntity<String> error() {
        if (random.nextBoolean()) {
            return ResponseEntity.status(500).body("Internal Server Error (Simulated Spike)");
        }
        return ResponseEntity.ok("Success (Simulated Error Bypass)");
    }
}
