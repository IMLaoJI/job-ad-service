package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.ws.avam.source.InsertOste;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.ws.avam.source.InsertOsteResponse;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.ws.avam.source.WSOsteEgov;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

import static org.springframework.util.StringUtils.trimWhitespace;

@Endpoint
public class AvamEndpoint {
	private static final Logger LOG = LoggerFactory.getLogger(AvamEndpoint.class);

	private static final String RESPONSE_OK = "SECO_WS: OK";

	private static final String RESPONSE_ERROR = "SECO_WS: ERROR";

	private static final String NAMESPACE_URI = "http://valueobjects.common.avam.bit.admin.ch";

	private final AvamSource avamSource;
	private final JobAdvertisementFromAvamAssembler jobAdvertisementFromAvamAssembler;

	public AvamEndpoint(AvamSource avamSource, JobAdvertisementFromAvamAssembler jobAdvertisementFromAvamAssembler) {
		this.avamSource = avamSource;
		this.jobAdvertisementFromAvamAssembler = jobAdvertisementFromAvamAssembler;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "insertOste")
	@ResponsePayload
	public InsertOsteResponse receiveJobAdvertisementFromAvam(@RequestPayload InsertOste request) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Recieved request: {}", transformToXml(request));
		}
		LOG.info("Recieved stellennummerAvam={}, stellennummerEgov={}, event={}", request.getOste().getStellennummerAvam(), request.getOste().getStellennummerEgov(), request.getOste().getEvent());

		WSOsteEgov avamJobAdvertisement = request.getOste();
		try {
			switch (Enum.valueOf(AvamEvents.class, avamJobAdvertisement.getEvent())) {
				case AKTIVIERT: {
					if (isApproved(avamJobAdvertisement)) {
						LOG.info("Approving JobAdvertisement from AVAM with EVENT: {}", avamJobAdvertisement.getEvent());
						avamSource.approve(jobAdvertisementFromAvamAssembler.createApprovalDto(avamJobAdvertisement));
						break;
					} else {
						LOG.info("Creating JobAdvertisement from AVAM with EVENT: {}", avamJobAdvertisement.getEvent());
						avamSource.create(jobAdvertisementFromAvamAssembler.createCreateJobAdvertisementAvamDto(avamJobAdvertisement));
						break;
					}
				}
				case ABGEMELDET: {
					LOG.info("Cancelling JobAdvertisement from AVAM with EVENT: {}", avamJobAdvertisement.getEvent());
					avamSource.cancel(jobAdvertisementFromAvamAssembler.createCancellationDto(avamJobAdvertisement));
					break;
				}
				case MUTIERT: {
					LOG.info("Updating JobAdvertisement from AVAM with EVENT: {}", avamJobAdvertisement.getEvent());
					avamSource.update(jobAdvertisementFromAvamAssembler.createUpdateDto(avamJobAdvertisement));
					break;
				}
				case ABGELEHNT: {
					LOG.info("Rejecting JobAdvertisement from AVAM with EVENT: {}", avamJobAdvertisement.getEvent());
					avamSource.reject(jobAdvertisementFromAvamAssembler.createRejectionDto(avamJobAdvertisement));
					break;
				}
				case INAKTIVIERT: {
					LOG.info("Inactivating JobAdvertisement from AVAM with EVENT: {}", avamJobAdvertisement.getEvent());
					avamSource.inactivate(jobAdvertisementFromAvamAssembler.createCancellationDto(avamJobAdvertisement));
					break;
				}
				case GELOESCHT: {
					LOG.info("Deleting JobAdvertisement from AVAM with EVENT: {}", avamJobAdvertisement.getEvent());
					avamSource.delete(jobAdvertisementFromAvamAssembler.createCancellationDto(avamJobAdvertisement));
					break;
				}
				case REAKTIVIERT: {
					LOG.info("Reactivating JobAdvertisement from AVAM with EVENT: {}", avamJobAdvertisement.getEvent());
					avamSource.reactivate(jobAdvertisementFromAvamAssembler.createCancellationDto(avamJobAdvertisement));
					break;
				}
				default:
					LOG.warn("Received JobAdvertisement in unknown state from AVAM: {}", transformToXml(request));
			}
		} catch (Exception e) {
			LOG.warn("Processing 'InsertOste' failed: {}", transformToXml(request), e);
			return response(RESPONSE_ERROR);
		}
		return response(RESPONSE_OK);
	}

	private String transformToXml(Object xmlRootObject) {
		try {
			JAXBContext context = JAXBContext.newInstance(xmlRootObject.getClass());
			Marshaller m = context.createMarshaller();

			StringWriter sw = new StringWriter();
			m.marshal(xmlRootObject, sw);
			return sw.toString();

		} catch (JAXBException e) {
			LOG.error("Marshalling of JaxbObject failed", e);
			return xmlRootObject.toString();
		}
	}

	private boolean isApproved(WSOsteEgov avamJobAdvertisement) {
		return isFromJobroom(avamJobAdvertisement);
	}

	private boolean isFromJobroom(WSOsteEgov avamJobAdvertisement) {
		String quelleCode = trimWhitespace(avamJobAdvertisement.getQuelleCode());
		return (AvamCodeResolver.SOURCE_SYSTEM.getLeft(SourceSystem.JOBROOM).equals(quelleCode)
				|| AvamCodeResolver.SOURCE_SYSTEM.getLeft(SourceSystem.API).equals(quelleCode));
	}

	private InsertOsteResponse response(String returnCode) {
		InsertOsteResponse insertOsteResponse = new InsertOsteResponse();
		insertOsteResponse.setReturn(returnCode);
		return insertOsteResponse;
	}

}
