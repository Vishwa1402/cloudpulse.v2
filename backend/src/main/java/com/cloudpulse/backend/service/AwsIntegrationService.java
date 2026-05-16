package com.cloudpulse.backend.service;

import com.cloudpulse.backend.entity.CloudProvider;
import com.cloudpulse.backend.entity.CloudResource;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.repository.CloudResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsIntegrationService {

    private final CloudResourceRepository cloudResourceRepository;

    public void syncEc2Instances(User user) {
        log.info("Starting AWS EC2 Sync for user {}", user.getEmail());
        
        try (Ec2Client ec2 = Ec2Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
            DescribeInstancesResponse response = ec2.describeInstances(request);

            List<CloudResource> newResources = new ArrayList<>();

            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    // Extract Name tag if exists
                    String name = instance.tags().stream()
                            .filter(t -> "Name".equalsIgnoreCase(t.key()))
                            .map(t -> t.value())
                            .findFirst()
                            .orElse(instance.instanceId());

                    // Map AWS state to our status
                    String status = instance.state().nameAsString().toUpperCase();

                    // Rough mock of cost per hour based on instance type for simulation
                    double mockCost = instance.instanceTypeAsString().contains("micro") ? 0.01 
                                    : instance.instanceTypeAsString().contains("large") ? 0.10 
                                    : 0.05;

                    CloudResource res = CloudResource.builder()
                            .name(name)
                            .type("EC2 (" + instance.instanceTypeAsString() + ")")
                            .provider(CloudProvider.AWS)
                            .status(status)
                            .costPerHour(mockCost)
                            .user(user)
                            .build();
                            
                    newResources.add(res);
                }
            }

            // For simplicity in this phase, delete old AWS resources for this user and save new
            List<CloudResource> existing = cloudResourceRepository.findByUserId(user.getId());
            existing.removeIf(r -> r.getProvider() != CloudProvider.AWS);
            
            // In a real app we'd do a smart merge/upsert, but here we just clear their AWS resources and save fresh
            cloudResourceRepository.deleteAll(cloudResourceRepository.findByUserId(user.getId()).stream()
                    .filter(r -> r.getProvider() == CloudProvider.AWS).toList());
                    
            cloudResourceRepository.saveAll(newResources);
            log.info("Successfully synced {} EC2 instances for user {}", newResources.size(), user.getEmail());

        } catch (Exception e) {
            log.error("Failed to sync AWS resources. Are AWS credentials configured in the environment? Error: {}", e.getMessage());
        }
    }
}
