package com.cloudpulse.backend.service;

import com.cloudpulse.backend.entity.CloudProvider;
import com.cloudpulse.backend.entity.CloudResource;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.repository.CloudResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataSeederService {

    private final CloudResourceRepository cloudResourceRepository;

    public void seedResourcesForUserIfEmpty(User user) {
        if (cloudResourceRepository.findByUserId(user.getId()).isEmpty()) {
            cloudResourceRepository.saveAll(List.of(
                    CloudResource.builder().name("production-db").type("RDS").provider(CloudProvider.AWS).status("RUNNING").costPerHour(0.50).user(user).build(),
                    CloudResource.builder().name("web-server-1").type("EC2").provider(CloudProvider.AWS).status("RUNNING").costPerHour(0.12).user(user).build(),
                    CloudResource.builder().name("web-server-2").type("EC2").provider(CloudProvider.AWS).status("RUNNING").costPerHour(0.12).user(user).build(),
                    CloudResource.builder().name("analytics-cluster").type("GKE").provider(CloudProvider.GCP).status("RUNNING").costPerHour(0.80).user(user).build(),
                    CloudResource.builder().name("backup-storage").type("S3").provider(CloudProvider.AWS).status("ACTIVE").costPerHour(0.05).user(user).build(),
                    CloudResource.builder().name("dev-instance").type("VM").provider(CloudProvider.AZURE).status("STOPPED").costPerHour(0.08).user(user).build()
            ));
        }
    }
}
