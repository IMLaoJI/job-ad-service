package ch.admin.seco.jobs.services.jobadservice.core.domain;

public class AggregateNotFoundException extends RuntimeException {

    private String aggregateName;

    private IndentifierType indentifierType;

    private Object identifierValue;

    private AggregateNotFoundException(Class<?> aggregateClass, Object identifierValue) {
        super("Aggregate with ID " + identifierValue + " not found");
        this.aggregateName = aggregateClass.getSimpleName();
    }

    public AggregateNotFoundException(Class<?> aggregateClass, String entityId) {
        this(aggregateClass, IndentifierType.ID, entityId);
    }

    public AggregateNotFoundException(Class<?> aggregateClass, IndentifierType identifierType, Object identifierValue) {
        this(aggregateClass, identifierValue);
        this.indentifierType = identifierType;
        this.identifierValue = identifierValue;
    }

    public String getAggregateName() {
        return aggregateName;
    }

    public IndentifierType getIndentifierType() {
        return indentifierType;
    }

    public Object getIdentifierValue() {
        return identifierValue;
    }

    public enum IndentifierType {
        ID,
        ACCESS_TOKEN,
        EXTERNAL_ID
    }
}
