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

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';
import { FileUploadModule } from 'ng2-file-upload';
import { UploadOptions } from '../services/upload.service';
import { DocumentUploadComponent } from '../commons/document-upload/document-upload.component';
import { IconModule } from '../commons/icon/icon.module';
import { UploadFileComponent } from './uploadFile.component';
import { TestDataGenerationService } from '../services/test.data.generation.service';
import { InfoService } from '../commons/info/info.service';
import { SpinnerVisibilityService } from 'ng-http-loader';
import { InfoModule } from '../commons/info/info.module';
import { environment } from '../../environments/environment';
import { of } from 'rxjs';

describe('UploadFileComponent', () => {
  let component: UploadFileComponent;
  let fixture: ComponentFixture<UploadFileComponent>;
  let infoService: InfoService;
  let testDataGenerationService: TestDataGenerationService;
  let spinnerService: SpinnerVisibilityService;

  const url = `${environment.tppBackend}`;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          FileUploadModule,
          RouterTestingModule,
          HttpClientModule,
          InfoModule,
          IconModule,
        ],
        declarations: [UploadFileComponent, DocumentUploadComponent],
        providers: [
          InfoService,
          SpinnerVisibilityService,
          TestDataGenerationService,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    infoService = TestBed.get(InfoService);
    testDataGenerationService = TestBed.get(TestDataGenerationService);
    spinnerService = TestBed.get(SpinnerVisibilityService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the ngOninit', () => {
    const mockUploadConfig: UploadOptions[] = [
      {
        exampleFileName: 'Users-Accounts-Balances-Payments-Example.yml',
        title: 'Upload Users/Accounts/Balances/Payments',
        method: 'PUT',
        url: url + '/data/upload',
        exampleFileUrl: '/accounts/example',
      },
      {
        exampleFileName: 'Consents-Example.yml',
        title: 'Upload Consents',
        method: 'PUT',
        url: url + '/consent',
        exampleFileUrl: '/consent/example',
      },
      {
        exampleFileName: 'Transactions-Example.csv',
        title: 'Upload Transactions',
        method: 'PUT',
        url: url + '/data/upload/transactions',
        exampleFileUrl: '/data/example',
      },
    ];
    component.ngOnInit();
    expect(component.uploadDataConfigs).toEqual(mockUploadConfig);
  });

  it('should generate the file Example', () => {
    const mockUploadConfig: UploadOptions = {
      exampleFileName: 'Users-Accounts-Balances-Payments-Example.yml',
      title: 'Upload Users/Accounts/Balances/Payments',
      method: 'PUT',
      url: url + '/data/upload',
      exampleFileUrl: '/accounts/example',
    };
    const generateSpy = spyOn(
      testDataGenerationService,
      'generateExampleTestData'
    ).and.returnValue(of(mockUploadConfig.exampleFileUrl));
    const infoSpy = spyOn(infoService, 'openFeedback');
    component.generateFileExample(mockUploadConfig);
    expect(infoSpy).toHaveBeenCalledWith(
      'Test data has been successfully generated.'
    );
  });
});
