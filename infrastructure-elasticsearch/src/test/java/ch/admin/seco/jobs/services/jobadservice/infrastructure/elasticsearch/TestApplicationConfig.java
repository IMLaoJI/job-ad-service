package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch;

import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.domain.apiuser.ApiUserRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplicationConfig {

    @MockBean
    ApiUserRepository apiUserRepository;

    @MockBean
    JobAdvertisementRepository jobAdvertisementRepository;

    @MockBean
    FavouriteItemRepository favouriteItemRepository;

    @MockBean
    CurrentUserContext currentUserContext;

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }
}
