package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.users;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.FavouriteItemApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.LoginAsTechnicalUser;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventReceiverGateway {

    private static final String UNREGISTERED_EVENT_CONDITION = "headers['event']=='UNREGISTERED'";

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventReceiverGateway.class);

    private final SearchProfileApplicationService searchProfileApplicationService;

    private final FavouriteItemApplicationService favouriteItemApplicationService;

    UserEventReceiverGateway(SearchProfileApplicationService searchProfileApplicationService, FavouriteItemApplicationService favouriteItemApplicationService) {
        this.searchProfileApplicationService = searchProfileApplicationService;
        this.favouriteItemApplicationService = favouriteItemApplicationService;
    }

    @StreamListener(value = MessageBrokerChannels.USER_EVENT_CHANNEL, condition = UNREGISTERED_EVENT_CONDITION)
    @LoginAsTechnicalUser
    public void handleUnregisteredEvent(UserEventDto event) {
        LOGGER.info("Received UserUnregisteredEvent for User: '{}' with Role: '{}'.", event.getUserInfoId(), event.getUserInfoRole());
        this.searchProfileApplicationService.deleteUserSearchProfiles(event.getUserInfoId());
        this.favouriteItemApplicationService.deleteUserFavouriteItems(event.getUserInfoId());
    }

}
