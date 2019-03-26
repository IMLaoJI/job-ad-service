package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.apiuser.ApiUserApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.apiuser.dto.*;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.EventData;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.EventStore;
import ch.admin.seco.jobs.services.jobadservice.domain.apiuser.ApiUser;
import ch.admin.seco.jobs.services.jobadservice.domain.apiuser.ApiUserId;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.apiuser.read.ApiUserSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.apiuser.read.ApiUserSearchService;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.PageResource;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/apiUsers")
public class ApiUserRestController {

    private final ApiUserApplicationService apiUserApplicationService;

    private final ApiUserSearchService apiUserSearchService;

    private final EventStore eventStore;

    public ApiUserRestController(ApiUserApplicationService apiUserApplicationService, ApiUserSearchService apiUserSearchService, EventStore eventStore) {
        this.apiUserApplicationService = apiUserApplicationService;
        this.apiUserSearchService = apiUserSearchService;
        this.eventStore = eventStore;
    }

    /**
     * Response status:
     * - 201 Created: The api-user has been successfully created
     * - 400 Bad Request: The request was malformed or invalid
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiUserDto createApiUser(@Valid @RequestBody CreateApiUserDto createApiUserDto) {
        ApiUserId apiUserId = apiUserApplicationService.create(createApiUserDto);
        return apiUserApplicationService.findById(apiUserId);
    }

    /**
     * Response status:
     * - 200 Ok: The page with api-user has been returned
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     */
    @GetMapping
    public Page<ApiUserDto> findAll(Pageable pageable) {
        return apiUserApplicationService.findAll(pageable);
    }

    /**
     * Response status:
     * - 200 Ok: The page with api-user has been returned
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     */
    @PostMapping("/_search")
    public ResponseEntity<List<ApiUserDto>> search(@Valid @RequestBody ApiUserSearchRequest searchRequest, Pageable pageable) {
        Page<ApiUserDto> resultPage = apiUserSearchService.search(searchRequest, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(resultPage, "/api/apiUsers/_search");
        return new ResponseEntity<>(resultPage.getContent(), headers, HttpStatus.OK);
    }

    /**
     * Response status:
     * - 200 Ok: The api-user has been returned
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     */
    @GetMapping("/{id}")
    public ApiUserDto findById(@PathVariable String id) {
        return apiUserApplicationService.findById(new ApiUserId(id));
    }

    /**
     * Response status:
     * - 200 Ok: The api-user has been updated
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     */
    @PutMapping("/{id}")
    public ApiUserDto changeDetails(@PathVariable String id, @Valid @RequestBody UpdateDetailsApiUserDto updateDetailsApiUserDto) {
        return apiUserApplicationService.changeDetails(new ApiUserId(id), updateDetailsApiUserDto);
    }

    /**
     * Response status:
     * - 200 Ok: The api-user has been updated
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     */
    @PutMapping("/{id}/password")
    public void changePassword(@PathVariable String id, @Valid @RequestBody UpdatePasswordApiUserDto updatePasswordApiUserDto) {
        apiUserApplicationService.changePassword(new ApiUserId(id), updatePasswordApiUserDto);
    }

    /**
     * Response status:
     * - 200 Ok: The api-user has been updated
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     */
    @PutMapping("/{id}/active")
    public void changeStatus(@PathVariable String id, @Valid @RequestBody UpdateStatusApiUserDto updateStatusApiUserDto) {
        apiUserApplicationService.changeStatus(new ApiUserId(id), updateStatusApiUserDto);
    }

    /**
     * Response status:
     * - 200 Ok: The page with events has been returned for this job ad
     * - 404 Not Found: No job ad has be found for the given id
     */
    @GetMapping("/{id}/events")
    public PageResource<EventData> getEventsOfApiUser(@PathVariable String id) throws AggregateNotFoundException {
        return PageResource.of(eventStore.findByAggregateId(id, ApiUser.class.getSimpleName(), 0, 100));
    }
}
