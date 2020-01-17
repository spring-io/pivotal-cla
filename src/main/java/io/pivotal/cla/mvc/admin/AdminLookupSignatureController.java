package io.pivotal.cla.mvc.admin;

import io.pivotal.cla.data.User;
import io.pivotal.cla.service.ClaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

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
	String index(@ModelAttribute LookupForm lookupForm) {
		lookupForm.setClaName("pivotal"); // default value
		return "admin/lookup/index";
	}

	@GetMapping("/admin/lookup/find")
	String lookup(@ModelAttribute LookupForm lookupForm, Map<String, Object> model) {
		boolean signed = this.clas.hasSigned(lookupForm.getGitHubLogin(), lookupForm.getClaName());
		model.put("signed", signed);
		return "admin/lookup/index";
	}
}
