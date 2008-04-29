package org.argouml.profile.plugin;

@SuppressWarnings("serial")
public class ErrorLoadingProfile extends Exception {

	public ErrorLoadingProfile() {
		super();
	}

	public ErrorLoadingProfile(String message, Throwable cause) {
		super(message, cause);
	}

	public ErrorLoadingProfile(String message) {
		super(message);
	}

	public ErrorLoadingProfile(Throwable cause) {
		super(cause);
	}

}
