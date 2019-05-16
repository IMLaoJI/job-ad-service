package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;

@Component
@Transactional
public class SearchProfileAuthorizationService {

	private final CurrentUserContext currentUserContext;

	private final SearchProfileRepository searchProfileRepository;

	public SearchProfileAuthorizationService(CurrentUserContext currentUserContext, SearchProfileRepository searchProfileRepository) {
		this.currentUserContext = currentUserContext;
		this.searchProfileRepository = searchProfileRepository;
	}

	public boolean matchesCurrentUserId(String userId) {
		String currentUserId = this.currentUserContext.getCurrentUser().getUserId();
		return (currentUserId != null) && currentUserId.equals(userId);
	}

	public boolean isCurrentUserOwner(SearchProfileId searchProfileId) {
		Optional<SearchProfile> searchProfileById = this.searchProfileRepository.findById(searchProfileId);
		if (!searchProfileById.isPresent()) {
			return true;
		}
		SearchProfile searchProfile = searchProfileById.get();
		return searchProfile.getOwnerUserId().equals(this.currentUserContext.getCurrentUser().getUserId());
	}
}
