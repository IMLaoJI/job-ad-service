package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.profession;

public class OccupationLabelMappingResource {

    private String id;

    private String bfsCode;

    private String avamCode;

    private String iscoCode;

    private String chIsco3Code;

    private String chIsco5Code;

    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBfsCode() {
        return bfsCode;
    }

    public void setBfsCode(String bfsCode) {
        this.bfsCode = bfsCode;
    }

    public String getAvamCode() {
        return avamCode;
    }

    public void setAvamCode(String avamCode) {
        this.avamCode = avamCode;
    }

    public String getIscoCode() {
        return iscoCode;
    }

    public void setIscoCode(String iscoCode) {
        this.iscoCode = iscoCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChIsco3Code() {
        return chIsco3Code;
    }

    public void setChIsco3Code(String chIsco3Code) {
        this.chIsco3Code = chIsco3Code;
    }

    public String getChIsco5Code() {
        return chIsco5Code;
    }

    public void setChIsco5Code(String chIsco5Code) {
        this.chIsco5Code = chIsco5Code;
    }
}
