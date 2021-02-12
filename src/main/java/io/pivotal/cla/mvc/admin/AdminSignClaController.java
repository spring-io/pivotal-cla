package io.pivotal.cla.mvc.admin;

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Controller
@PreAuthorize("hasRole('CLA_AUTHOR')")
public class AdminSignClaController {
    private final ContributorLicenseAgreementRepository clas;

    private final IndividualSignatureRepository individual;

    public AdminSignClaController(ContributorLicenseAgreementRepository clas, IndividualSignatureRepository individual) {
        this.clas = clas;
        this.individual = individual;
    }

    @GetMapping("/admin/sign/icla")
    String iclaForm(@ModelAttribute SignIClaForm signClaForm) {
        return "admin/cla/sign/icla";
    }

    @PostMapping("/admin/sign/icla")
    String signIcla(SignIClaForm signClaForm) {
        ContributorLicenseAgreement cla = this.clas.findOne(signClaForm.getClaId());
        if (cla == null) {
            throw new IllegalArgumentException("The cla is not found");
        }
        IndividualSignature signature = new IndividualSignature();
        signature.setCla(cla);
        signature.setName(signClaForm.getName());
        signature.setCountry(signClaForm.getCountry());
        signature.setEmail(signClaForm.getEmail());
        signature.setMailingAddress(signClaForm.getMailingAddress());
        signature.setDateOfSignature(new Date());
        signature.setTelephone(signClaForm.getTelephone());
        signature.setGitHubLogin(signClaForm.getGitHubLogin());
        individual.save(signature);
        return "redirect:/admin/sign/icla?success";
    }
}
