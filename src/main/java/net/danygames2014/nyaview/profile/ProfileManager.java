package net.danygames2014.nyaview.profile;

import net.danygames2014.nyaview.NyaView;

import java.io.File;
import java.util.HashMap;

public class ProfileManager {
    private HashMap<String, Profile> profiles;
    public Profile activeProfile;
    
    public void init() {
        // Load the profiles in the profiles diectory
        NyaView.LOGGER.info("Loading profiles...");
        profiles = new HashMap<>();
        try {
            File profileDir = new File("profiles");
            
            if (!profileDir.exists()) {
                profileDir.mkdir();
                NyaView.LOGGER.info("Created profiles directory");
            }
            
            for (File item : profileDir.listFiles()) {
                if (item.isDirectory()) {
                    File profileFile = new File(item.getPath() + "/" + item.getName() + ".yml");
                    
                    if (!profileFile.exists()) {
                        break;
                    }
                    
                    NyaView.LOGGER.info("Loading profile " + profileFile.getName());
                    Profile profile = new Profile(profileFile.getPath());
                    profiles.put(profile.getId(), profile);
                }    
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // Get the active profile name
        String activeProfileName = NyaView.config.getActiveProfileId();
        
        if (profiles.containsKey(activeProfileName)) {
            // If such profile exists, load it
            switchProfile(activeProfileName);
        } else {
            if (profiles.isEmpty()) {
                // If there are no profiles, create a default one
                NyaView.LOGGER.info("No profiles found, creating default profile...");
                profiles.put("default", new Profile(Profile.constructProfilePath("default")));
                switchProfile("default");
            } else {
                // If there are profiles, but not the active one, use the first loaded one
                NyaView.LOGGER.info("The active profile " + activeProfileName + " was not found, using first profile...");
                switchProfile(profiles.values().iterator().next().getId());
            }
        }
    }

    public HashMap<String, Profile> getProfiles() {
        return profiles;
    }
    
    public boolean addProfile(Profile profile) {
        if (profiles.containsKey(profile.getId())) {
            NyaView.LOGGER.warn("Tried to add profile " + profile.getId() + " but it already exists");
            return false;
        }
        
        profiles.put(profile.getId(), profile);
        NyaView.LOGGER.info("Added profile " + profile.getId());
        profile.save();
        return true;
    }
    
    public void switchProfile(String id) {
        if (!profiles.containsKey(id)) {
            NyaView.LOGGER.warn("Tried to load non-existent profile " + id);
            activeProfile = profiles.values().iterator().next();
        } else {
            activeProfile = profiles.get(id);
        }
        
        NyaView.LOGGER.info("Set active profile to " + activeProfile.getName());
        NyaView.config.setActiveProfileId(activeProfile.getId());
        
        NyaView.loadMappings();
    }
}
