package ch.admin.seco.jobs.services.jobadservice.domain.apiuser;

public class ApiUserTestFixture {

    public static ApiUser createApiUser01() {
        return new ApiUser.Builder()
                .setId(new ApiUserId("apiUser01"))
                .setUsername("username-01")
                .setPassword("password-01")
                .setCompanyName("companyName-01")
                .setCompanyEmail("companyEmail-01@example.com")
                .setTechnicalContactName("technicalContactName-01")
                .setTechnicalContactEmail("technicalContactEmail-01@example.com")
                .setActive(true)
                .build();
    }

    public static ApiUser createApiUser02() {
        return new ApiUser.Builder()
                .setId(new ApiUserId("apiUser02"))
                .setUsername("username-02")
                .setPassword("password-02")
                .setCompanyName("companyName-02")
                .setCompanyEmail("companyEmail-02@example.com")
                .setTechnicalContactName("technicalContactName-02")
                .setTechnicalContactEmail("technicalContactEmail-02@example.com")
                .setActive(true)
                .build();
    }
}
