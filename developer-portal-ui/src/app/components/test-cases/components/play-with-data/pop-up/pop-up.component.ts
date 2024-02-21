/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbModal, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { CertificateService } from '../../../../../services/certificate.service';
import { DataService } from '../../../../../services/data.service';

@Component({
  selector: 'app-pop-up',
  templateUrl: './pop-up.component.html',
  styleUrls: ['./pop-up.component.scss'],
  providers: [NgbModalConfig, NgbModal],
})
export class PopUpComponent implements OnInit {
  @Input() certificate;
  @Input() default;
  @Output() editCertificate = new EventEmitter();

  constructor(
    config: NgbModalConfig,
    private modalService: NgbModal,
    private dataService: DataService,
    private certificateService: CertificateService
  ) {
    config.backdrop = 'static';
    config.keyboard = false;
  }

  open(content) {
    this.modalService.open(content);
  }

  restoreCertificate() {
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

  saveCertificate() {
    this.editCertificate.emit(this.certificate);
    this.certificateService.storeCertificate(this.certificate);
    this.certificateService.setDefault(false);
    this.dataService.showToast('Certificate edited and saved', 'Success!', 'success');
  }

  clearCertificate() {
    this.certificateService.removeCertificate();
    this.certificate = '';
    this.dataService.showToast('Certificate deleted', 'Success!', 'success');
  }

  ngOnInit() {}
}
