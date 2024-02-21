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

import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-certificate',
  templateUrl: './certificate.component.html',
  styleUrls: ['../auth.component.scss'],
})
export class CertificateComponent implements OnInit {
  @Output() certificateValue = new EventEmitter();

  certificateFormGroup: UntypedFormGroup;
  rolesOptionsError = false;

  public roles: Array<string> = ['PIISP', 'PISP', 'AISP'];
  selectedOptions = ['PIISP', 'PISP', 'AISP'];

  constructor(private formBuilder: UntypedFormBuilder) {}

  ngOnInit() {
    this.initializeCertificateGeneratorForm();
    this.onChange();
  }

  addCheckboxControls() {
    const arr = this.roles.map(() => {
      return this.formBuilder.control(true);
    });
    return this.formBuilder.array(arr);
  }

  getSelectedCheckboxValue() {
    this.selectedOptions = [];
    this.checkboxArray.controls.forEach((control, i) => {
      if (control.value) {
        this.selectedOptions.push(this.roles[i]);
      }
    });
    this.rolesOptionsError = this.selectedOptions.length <= 0;
  }

  onChange() {
    this.certificateFormGroup.value.roles = this.selectedOptions;
    const status = this.certificateFormGroup.valid && !this.rolesOptionsError;
    if (status) {
      this.certificateValue.emit(this.certificateFormGroup.value);
    } else {
      this.certificateValue.emit(false);
    }
  }

  public initializeCertificateGeneratorForm(): void {
    this.certificateFormGroup = this.formBuilder.group({
      authorizationNumber: ['ID12345', Validators.required],
      organizationName: ['Awesome TPP', Validators.required],
      countryName: ['Germany', Validators.required],
      domainComponent: ['awesome-tpp.de', Validators.required],
      localityName: ['Nuremberg', Validators.required],
      organizationUnit: ['IT department', Validators.required],
      stateOrProvinceName: ['Bayern', Validators.required],
      commonName: ['XS2A Sandbox', Validators.required],
      validity: [
        '365',
        [
          Validators.required,
          Validators.pattern('^[0-9]*$'),
          Validators.min(1),
          Validators.max(999),
        ],
      ],
      roles: this.addCheckboxControls(),
    });
  }

  get checkboxArray() {
    return <UntypedFormArray>this.certificateFormGroup.get('roles');
  }
}
