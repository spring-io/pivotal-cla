/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.cla.mvc.admin;

import io.pivotal.cla.data.CorporateSignature;
import io.pivotal.cla.data.repository.CorporateSignatureRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rob Winch
 */
@RestController
@PreAuthorize("hasRole('CLA_AUTHOR')")
public class AdminListSignaturesController {
    final CorporateSignatureRepository cclas;

    public AdminListSignaturesController(CorporateSignatureRepository cclas) {
        this.cclas = cclas;
    }

    @GetMapping("/admin/signatures/ccla")
    Iterable<CorporateSignature> cclas() {
        Iterable<CorporateSignature> cclas = this.cclas.findAll();
        return cclas;
    }
}
