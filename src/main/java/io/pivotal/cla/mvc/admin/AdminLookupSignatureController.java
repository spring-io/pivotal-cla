package io.pivotal.cla.mvc.admin;

import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.User;
import io.pivotal.cla.service.ClaService;
import io.pivotal.cla.service.CorporateSignatureInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @author Rob Winch
 */
@Controller
@PreAuthorize("hasRole('CLA_AUTHOR')")
public class AdminLookupSignatureController {
	final ClaService clas;

	public AdminLookupSignatureController(ClaService clas) {
		this.clas = clas;
	}

	@GetMapping("/admin/lookup")
	String index(@ModelAttribute User user) {
		return "admin/lookup/index";
	}

	@GetMapping("/admin/lookup/find")
	String lookup(@ModelAttribute User user, Map<String, Object> model) {
		String claName = "pivotal";
		IndividualSignature indivSignature = this.clas
				.findIndividualSignaturesFor(user, claName);
		CorporateSignatureInfo corpSignature = this.clas.findCorporateSignatureInfoFor(claName, user);
		model.put("ccla", corpSignature);
		model.put("icla", indivSignature);
		return "admin/lookup/index";
	}
}
