package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUser;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.application.security.Role;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

@Component
@Transactional
public class JobAdvertisementAuthorizationService {

	private final JobAdvertisementRepository jobAdvertisementRepository;

	private final CurrentUserContext currentUserContext;

	public JobAdvertisementAuthorizationService(JobAdvertisementRepository jobAdvertisementRepository, CurrentUserContext currentUserContext) {
		this.jobAdvertisementRepository = jobAdvertisementRepository;
		this.currentUserContext = currentUserContext;
	}

	// FIXME parameter is JobAdvertisementId and token
	public boolean canCancel(String jobAdvertisementId, String token) {
		Optional<JobAdvertisement> jobAdvertisement = this.jobAdvertisementRepository.findById(new JobAdvertisementId(jobAdvertisementId));
		if (!jobAdvertisement.isPresent()) {
			return true;
		}

		if (hasText(token)) {
			return hasToken(jobAdvertisement.get(), token);
		}

		CurrentUser currentUser = this.currentUserContext.getCurrentUser();
		// FIXME ROLE_API and from AVAM return true
		return isOwner(jobAdvertisement.get(), currentUser);

	}

	public boolean isOwner(JobAdvertisement jobAdvertisement, CurrentUser currentUser) {
		if (currentUser == null) {
			return false;
		}
		String userId = currentUser.getUserId();
		if ((userId != null) && userId.equals(jobAdvertisement.getOwner().getUserId())) {
			return true;
		}

		String companyId = currentUser.getCompanyId();
		return (companyId != null) && companyId.equals(jobAdvertisement.getOwner().getCompanyId());
	}

	public boolean canViewJob(JobAdvertisementId jobAdvertisementId) {
		return this.jobAdvertisementRepository.findById(jobAdvertisementId)
				.map(this::internalCanViewJob)
				.orElse(true);
	}

	public boolean canViewJob(String stellennummer) {
		return this.jobAdvertisementRepository.findByStellennummerAvamOrStellennummerEgov(stellennummer)
				.map(this::internalCanViewJob)
				.orElse(true);
	}

	public boolean isCurrentUserMemberOfCompany(String companyId) {
		if (companyId == null) {
			return false;
		}
		CurrentUser currentUser = this.currentUserContext.getCurrentUser();
		if (currentUser == null) {
			return false;
		}
		return companyId.equals(currentUser.getCompanyId());
	}

	private boolean hasToken(JobAdvertisement jobAdvertisement, String token) {
		return jobAdvertisement.getOwner().getAccessToken().equals(token);
	}

	private boolean internalCanViewJob(JobAdvertisement jobAdvertisement) {
		if (this.currentUserContext.hasAnyRoles(Role.SYSADMIN, Role.ADMIN)) {
			return true;
		}
		if (jobAdvertisement.getStatus() != JobAdvertisementStatus.PUBLISHED_RESTRICTED) {
			return true;
		}
		if (isOwner(jobAdvertisement, this.currentUserContext.getCurrentUser())) {
			return true;
		}
		return this.currentUserContext.getCurrentUser() != null
				&& this.currentUserContext.hasAnyRoles(Role.JOBSEEKER_CLIENT);
	}
}
