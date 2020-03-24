package ch.admin.seco.jobs.services.jobadservice.application.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserDetailsToCurrentUserAdapter extends User {

	private final CurrentUser currentUser;

	public UserDetailsToCurrentUserAdapter(String username, String password, CurrentUser currentUser, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, true, true, true, true, authorities);
		this.currentUser = currentUser;
	}

	CurrentUser getCurrentUser() {
		return currentUser;
	}
}
