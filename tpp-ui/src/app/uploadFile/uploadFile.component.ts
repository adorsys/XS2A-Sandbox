/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
import { environment } from '../../environments/environment';
import { UploadOptions } from '../services/upload.service';
import { TestDataGenerationService } from '../services/test.data.generation.service';
import { InfoService } from '../commons/info/info.service';

@Component({
  selector: 'app-upload-file',
  templateUrl: './uploadFile.component.html',
  styleUrls: ['./uploadFile.component.scss'],
})
export class UploadFileComponent implements OnInit {
  uploadDataConfigs: UploadOptions[];
  private url = `${environment.tppBackend}`;
  private message = 'Test data has been successfully generated.';

  constructor(
    private generationService: TestDataGenerationService,
    private infoService: InfoService
  ) {}

  public ngOnInit(): void {
    this.uploadDataConfigs = [
      {
        exampleFileName: 'Users-Accounts-Balances-Payments-Example.yml',
        title: 'Upload Users/Accounts/Balances/Payments',
        method: 'PUT',
        url: this.url + '/data/upload',
        exampleFileUrl: '/accounts/example',
      },
      {
        exampleFileName: 'Consents-Example.yml',
        title: 'Upload Consents',
        method: 'PUT',
        url: this.url + '/consent',
        exampleFileUrl: '/consent/example',
      },
      {
        exampleFileName: 'Transactions-Example.csv',
        title: 'Upload Transactions',
        method: 'PUT',
        url: this.url + '/data/upload/transactions',
        exampleFileUrl: '/data/example',
      },
    ];
  }

  generateFileExample(uploadDataConfig) {
    return this.generationService
      .generateExampleTestData(uploadDataConfig.exampleFileUrl)
      .subscribe((data) => {
        this.infoService.openFeedback(this.message);
        const blob = new Blob([data], { type: 'plain/text' });
        let link = document.createElement('a');
        link.setAttribute('href', window.URL.createObjectURL(blob));
        link.setAttribute('download', uploadDataConfig.exampleFileName);
        document.body.appendChild(link);
        link.click();
      });
  }
}
