/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

import { Component, OnInit } from '@angular/core';
import { LanguageService } from '../../../../services/language.service';
import { CustomizeService } from '../../../../services/customize.service';
import { CertificateService } from '../../../../services/certificate.service';
import { DataService } from '../../../../services/data.service';

@Component({
  selector: 'app-test-values',
  templateUrl: './test-values.component.html',
  styleUrls: ['./test-values.component.scss'],
})
export class TestValuesComponent implements OnInit {
  pathToTestValues = `./assets/content/i18n/en/test-cases/predefinedTestValues.md`;
  certificate;

  constructor(
    private languageService: LanguageService,
    private customizeService: CustomizeService,
    private certificateService: CertificateService,
    private dataService: DataService
  ) {}

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe((data) => {
      this.pathToTestValues = `${this.customizeService.currentLanguageFolder}/${data}/test-cases/predefinedTestValues.md`;
    });

    this.certificate = this.certificateService.getStoredCertificate();
  }

  save() {
    this.certificateService.storeCertificate(this.certificate);
    if (this.certificateService.getStoredCertificate()) {
      this.certificateService.setDefault(false);
      this.dataService.showToast('Certificate saved', 'Success!', 'success');
    }
  }

  clear() {
    this.certificateService.removeCertificate();
    this.dataService.showToast('Certificate deleted', 'Success!', 'success');
    this.certificate = '';
  }

  default() {
    this.certificateService
      .getQwacCertificate()
      .toPromise()
      .then((data) => {
        this.certificateService.storeCertificate(data);
        this.certificateService.setDefault(false);
        this.certificate = data;
        this.dataService.showToast('Certificate restored to default', 'Certificate restored!', 'success');
      });
  }
}
