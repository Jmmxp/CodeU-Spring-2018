package codeu.model.store.basic;

import java.util.HashMap;

import codeu.model.data.Profile;
import codeu.model.store.persistence.PersistentStorageAgent;

/**
 * Store class for the profiles of users. Loads from and saves to PersistentDataStore. It's a singleton 
 * so all servlet classes can access the same instance.
 *
 */
public class ProfileStore {
	/* Singleton instance of ProfileStore */
	private static ProfileStore instance;
	
	/* The PersistentStorgeAgent responsible for loading Profiles from and saving Profiles to 
	 * Datastore. 
	 */
	private PersistentStorageAgent persistentStorageAgent;
	
	/* In-memory hash table of profiles */
	private HashMap<String, String> profiles;

	/**
	 * 
	 * @return the singleton instance of the ProfileStore
	 */
	public static ProfileStore getInstance() {
		if (instance == null) {
			instance = new ProfileStore(PersistentStorageAgent.getInstance());
		}
		return instance;
	} 
	
	  /**
	   * Instance getter function used for testing. Supply a mock for PersistentStorageAgent.
	   *
	   * @param persistentStorageAgent a mock used for testing
	   */
	  public static ProfileStore getTestInstance(PersistentStorageAgent persistentStorageAgent) {
	    return new ProfileStore(persistentStorageAgent);
	  }
	  

	/**
	 * This class is a singleton, so its constructor is private. Call getInstance()
	 * instead.
	 */
	private ProfileStore(PersistentStorageAgent persistentStorageAgent) {
		this.persistentStorageAgent = persistentStorageAgent;
		profiles = new HashMap<>();
	}

	/**
	 * Adds a profile to the ProfileStore and to the Datastore through the PersistentStorageAgent
	 * @param profile the profile to be added
	 */
	public void addProfile(Profile profile) {
		profiles.put(profile.getProfileOwner(), profile.getProfileText());
		persistentStorageAgent.writeThrough(profile);
	}
	
	/**
	 * @param profileOwner the user whose profile text is returned
	 * @return the current text of the About Me section of a specified profile
	 */
	public String getProfileText(String profileOwner) {
		return profiles.get(profileOwner);
	}
	
	/**
	 * Updates the About Me section of a user's profile
	 * 
	 * @param profileOwner the user whose profile text is updated
	 * @param text the new About Me section text
	 */
	public void setProfileText(String profileOwner, String text) {
		profiles.put(profileOwner, text);
	}

	/**
	 * Sets the hash table of profiles stored by this ProfileStore 
	 */
	public void setProfiles(HashMap<String, String> profiles) {
		this.profiles = profiles;
	}
}
