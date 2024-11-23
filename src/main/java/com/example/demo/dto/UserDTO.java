package com.example.demo.dto;

import com.example.demo.entity.EmailAddress;
import com.example.demo.entity.PhoneNumber;
import com.example.demo.entity.PhysicalAddress;
import com.example.demo.entity.SocialMediaHandle;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private List<PhoneNumber> phoneNumbers;
    private List<EmailAddress> emailAddresses;
    private List<SocialMediaHandle> socialMediaHandles;
    private List<PhysicalAddress> physicalAddresses;
}
