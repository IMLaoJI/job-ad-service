package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.AvamCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.ApprovalDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.RejectionDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.CancellationCode;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseMatchers;

import java.io.IOException;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.JobAdvertisementAction.*;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.ACTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.ws.test.server.RequestCreators.withPayload;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
public class AvamEndpointTest {

    private MockWebServiceClient mockWebServiceClient;

    private JacksonTester<ApprovalDto> approvalDtoJacksonTester;
    private JacksonTester<RejectionDto> rejectionDtoJacksonTester;
    private JacksonTester<AvamCreateJobAdvertisementDto> createJobAdvertisementAvamDtoJacksonTester;
    private JacksonTester<AvamCancellationDto> cancellationDtoJacksonTester;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MessageCollector messageCollector;

    @Autowired
    private Source source;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("classpath:/schema/AVAMToEgov.xsd")
    private Resource secoEgovServiceXsdResource;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockWebServiceClient = MockWebServiceClient.createClient(applicationContext);
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    public void testRedirectToWsdl() throws Exception {
        // useful test to check that no spring-security is configured
        this.mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("services/SecoEgovService.wsdl"));
    }

    @Test
    public void approveJobAdvertisement() throws IOException {

        // process
        mockWebServiceClient.sendRequest(withPayload(getAsResource("soap/messages/insertOste-approve-1.xml")))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.validPayload(secoEgovServiceXsdResource));

        // assert
        Message<String> received = (Message<String>) messageCollector.forChannel(source.output()).poll();
        assertThat(received).isNotNull();
        assertThat(received.getHeaders().get(ACTION)).isEqualTo(APPROVE.name());
        ApprovalDto approvalDto = approvalDtoJacksonTester.parse(received.getPayload()).getObject();
        assertThat(approvalDto.getStellennummerEgov()).isEqualTo("EGOV-0001");
        assertThat(approvalDto.getStellennummerAvam()).isEqualTo("AVAM-0001");
        assertThat(approvalDto.getDate()).isEqualTo("2018-03-01");
    }

    @Test
    public void rejectJobAdvertisement() throws IOException {

        // process
        mockWebServiceClient.sendRequest(withPayload(getAsResource("soap/messages/insertOste-reject-1.xml")))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.validPayload(secoEgovServiceXsdResource));

        // assert
        Message<String> received = (Message<String>) messageCollector.forChannel(source.output()).poll();
        assertThat(received).isNotNull();
        assertThat(received.getHeaders().get(ACTION)).isEqualTo(REJECT.name());

        RejectionDto rejectionDto = rejectionDtoJacksonTester.parse(received.getPayload()).getObject();
        assertThat(rejectionDto.getStellennummerEgov()).isEqualTo("EGOV-0002");
        assertThat(rejectionDto.getStellennummerAvam()).isEqualTo("AVAM-0002");
        assertThat(rejectionDto.getDate()).isEqualTo("2018-03-03");
        assertThat(rejectionDto.getCode()).isEqualTo("REJECT-CODE");
        assertThat(rejectionDto.getReason()).isEqualTo("REJECT-REASON");
    }

    @Test
    public void createJobAdvertisement() throws IOException {

        // process
        mockWebServiceClient.sendRequest(withPayload(getAsResource("soap/messages/insertOste-create-1.xml")))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.validPayload(secoEgovServiceXsdResource));

        // assert
        Message<String> received = (Message<String>) messageCollector.forChannel(source.output()).poll();
        assertThat(received).isNotNull();
        assertThat(received.getHeaders().get(ACTION)).isEqualTo(CREATE_FROM_AVAM.name());

        AvamCreateJobAdvertisementDto createJobAdvertisementFromAvamDto = createJobAdvertisementAvamDtoJacksonTester.parse(received.getPayload()).getObject();
        assertThat(createJobAdvertisementFromAvamDto.getStellennummerAvam()).isEqualTo("AVAM-0003");
        assertThat(createJobAdvertisementFromAvamDto.getTitle()).isEqualTo("Test Title");
        assertThat(createJobAdvertisementFromAvamDto.getDescription()).isEqualTo("Test Description");
        assertThat(createJobAdvertisementFromAvamDto.getLanguageIsoCode()).isEqualTo("de");
    }

    @Test
    public void createAvamCreateJobAdvertisementDtoAndAndAssembleToCreateJobAdvertisementDto() throws IOException {

        // given
        mockWebServiceClient.sendRequest(withPayload(getAsResource("soap/messages/insertOste-create-1.xml")))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.validPayload(secoEgovServiceXsdResource));

        Message<String> received = (Message<String>) messageCollector.forChannel(source.output()).poll();
        assertThat(received).isNotNull();
        assertThat(received.getHeaders().get(ACTION)).isEqualTo(CREATE_FROM_AVAM.name());

        AvamCreateJobAdvertisementDto createJobAdvertisementFromAvamDto = createJobAdvertisementAvamDtoJacksonTester.parse(received.getPayload()).getObject();
        assertThat(createJobAdvertisementFromAvamDto.getStellennummerAvam()).isEqualTo("AVAM-0003");
        assertThat(createJobAdvertisementFromAvamDto.getTitle()).isEqualTo("Test Title");
        assertThat(createJobAdvertisementFromAvamDto.getDescription()).isEqualTo("Test Description");

        //when
        CreateJobAdvertisementDto createJobAdvertisementDto = toDto(createJobAdvertisementFromAvamDto);

        //then
        assertThat(createJobAdvertisementDto.getStellennummerAvam()).isEqualTo("AVAM-0003");
        assertThat(createJobAdvertisementDto.getJobDescriptions().get(0).getTitle()).isEqualTo("Test Title");
        assertThat(createJobAdvertisementDto.getJobDescriptions().get(0).getDescription()).isEqualTo("Test Description");
        assertThat(createJobAdvertisementDto.getJobCenterCode()).isEqualTo("BEA12");
        assertThat(createJobAdvertisementDto.getApprovalDate()).isEqualTo(LocalDate.of(2012,3,1));
        assertThat(createJobAdvertisementDto.getJobDescriptions().get(0).getLanguageIsoCode()).isEqualTo("de");
    }

    @Test
    public void createAvamCancellationDtoAndAssembleToCancellationDto() throws IOException {

        //given
        mockWebServiceClient.sendRequest(withPayload(getAsResource("soap/messages/insertOste-cancel-1.xml")))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.validPayload(secoEgovServiceXsdResource));

        Message<String> received = (Message<String>) messageCollector.forChannel(source.output()).poll();
        assertThat(received).isNotNull();
        assertThat(received.getHeaders().get(ACTION)).isEqualTo(CANCEL.name());

        AvamCancellationDto avamCancellationDto = cancellationDtoJacksonTester.parse(received.getPayload()).getObject();
        assertThat(avamCancellationDto.getStellennummerEgov()).isEqualTo("EGOV-0004");
        assertThat(avamCancellationDto.getStellennummerAvam()).isEqualTo("AVAM-0004");
        assertThat(avamCancellationDto.getCancellationDate()).isEqualTo("2018-03-04");
        assertThat(avamCancellationDto.getJobCenterCode()).isEqualTo("BEA12");
        assertThat(avamCancellationDto.getContactEmail()).isEqualTo("kpemail");
        assertThat(avamCancellationDto.getJobDescriptionTitle()).isEqualTo("Dies ist ein Test (Florist)");
        assertThat(avamCancellationDto.getCancellationCode()).isEqualTo(CancellationCode.OCCUPIED_JOBCENTER);
        assertThat(avamCancellationDto.getSourceSystem()).isEqualTo(SourceSystem.JOBROOM);

        //when
        CancellationDto cancellationDto = AvamCancellationDto.toDto(avamCancellationDto);

        //then
        assertThat(cancellationDto.getStellennummerEgov()).isEqualTo("EGOV-0004");
        assertThat(cancellationDto.getStellennummerAvam()).isEqualTo("AVAM-0004");
        assertThat(cancellationDto.getCancellationDate()).isEqualTo("2018-03-04");
        assertThat(cancellationDto.getCancellationCode()).isEqualTo(CancellationCode.OCCUPIED_JOBCENTER);
    }

    @Test
    public void cancelJobAdvertisement() throws IOException {

        // process
        mockWebServiceClient.sendRequest(withPayload(getAsResource("soap/messages/insertOste-cancel-1.xml")))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.validPayload(secoEgovServiceXsdResource));

        // assert
        Message<String> received = (Message<String>) messageCollector.forChannel(source.output()).poll();
        assertThat(received).isNotNull();
        assertThat(received.getHeaders().get(ACTION)).isEqualTo(CANCEL.name());

        AvamCancellationDto cancellationDto = cancellationDtoJacksonTester.parse(received.getPayload()).getObject();
        assertThat(cancellationDto.getStellennummerEgov()).isEqualTo("EGOV-0004");
        assertThat(cancellationDto.getStellennummerAvam()).isEqualTo("AVAM-0004");
        assertThat(cancellationDto.getCancellationDate()).isEqualTo("2018-03-04");
        assertThat(cancellationDto.getJobCenterCode()).isEqualTo("BEA12");
        assertThat(cancellationDto.getContactEmail()).isEqualTo("kpemail");
        assertThat(cancellationDto.getJobDescriptionTitle()).isEqualTo("Dies ist ein Test (Florist)");
        assertThat(cancellationDto.getCancellationCode()).isEqualTo(CancellationCode.OCCUPIED_JOBCENTER);
        assertThat(cancellationDto.getSourceSystem()).isEqualTo(SourceSystem.JOBROOM);
    }

    private ClassPathResource getAsResource(String payloadFile) {
        return new ClassPathResource(payloadFile);
    }
}
