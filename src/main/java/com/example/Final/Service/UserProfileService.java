package com.example.Final.Service;

import com.example.Final.Repo.UserProfileRepository;
import com.example.Final.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {
    @Autowired
    private UserProfileRepository userProfileRepository;

    public Optional<UserProfile> getUserProfileById(Long id) {
        return userProfileRepository.findById(id);
    }

    public UserProfile updateUserProfile(Long id, UserProfile userProfile) {
        // Check if the user profile exists
        Optional<UserProfile> existingUserProfileOptional = userProfileRepository.findById(id);
        if (existingUserProfileOptional.isPresent()) {
            UserProfile existingUserProfile = existingUserProfileOptional.get();
            // Update the existing user profile with the new values
            existingUserProfile.setFullName(userProfile.getFullName());
            existingUserProfile.setEmail(userProfile.getEmail());
            existingUserProfile.setAddress(userProfile.getAddress());
            existingUserProfile.setAge(userProfile.getAge());
            existingUserProfile.setGender(userProfile.getGender());
            existingUserProfile.setPhNum(userProfile.getPhNum());
            existingUserProfile.setPinCode(userProfile.getPinCode());

            return userProfileRepository.save(existingUserProfile);
        } else {

            return null;
        }
    }

    public UserProfile createUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }
}
