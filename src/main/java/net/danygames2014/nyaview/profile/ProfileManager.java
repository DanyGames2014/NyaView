package net.danygames2014.nyaview.profile;

import net.danygames2014.nyaview.NyaView;

import java.io.File;
import java.util.HashMap;

public class ProfileManager {
    public HashMap<String, Profile> profiles;
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
            
            for (File file : profileDir.listFiles()) {
                if (file.isFile()) {
                    String path = file.getPath();
                    NyaView.LOGGER.info("Loading profile " + path);
                    Profile profile = new Profile(path);
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
            setActiveProfile(activeProfileName);
        } else {
            if (profiles.isEmpty()) {
                // If there are no profiles, create a default one
                NyaView.LOGGER.info("No profiles found, creating default profile...");
                profiles.put("default", new Profile("profiles/default.yml"));
                setActiveProfile("default");
            } else {
                // If there are profiles, but not the active one, use the first loaded one
                NyaView.LOGGER.info("The active profile " + activeProfileName + " was not found, using first profile...");
                setActiveProfile(profiles.values().iterator().next().getId());
            }
        }
    }
    
    public void setActiveProfile(String id) {
        if (!profiles.containsKey(id)) {
            NyaView.LOGGER.warn("Tried to load non-existent profile " + id);
            activeProfile = profiles.values().iterator().next();
            return;
        }
        
        activeProfile = profiles.get(id);
        NyaView.LOGGER.info("Set active profile to " + activeProfile.getName());
    }
}
