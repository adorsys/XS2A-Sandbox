import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientModule} from "@angular/common/http";
import {FileUploadModule} from "ng2-file-upload";
import {UploadOptions} from '../services/upload.service';
import {DocumentUploadComponent} from "../commons/document-upload/document-upload.component";
import {IconModule} from "../commons/icon/icon.module";
import {UploadFileComponent} from './uploadFile.component';
import {TestDataGenerationService} from "../services/test.data.generation.service";
import {InfoService} from "../commons/info/info.service";
import {SpinnerVisibilityService} from "ng-http-loader";
import {InfoModule} from "../commons/info/info.module";
import {environment} from '../../environments/environment';
import { of } from 'rxjs';

describe('UploadFileComponent', () => {
    let component: UploadFileComponent;
    let fixture: ComponentFixture<UploadFileComponent>;
    let infoService: InfoService;
    let testDataGenerationService: TestDataGenerationService;
    let spinnerService: SpinnerVisibilityService;

    let url = `${environment.tppBackend}`;
    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            imports: [
                FileUploadModule,
                RouterTestingModule,
                HttpClientModule,
                InfoModule,
                IconModule
            ],
            declarations: [UploadFileComponent, DocumentUploadComponent],
            providers: [InfoService, SpinnerVisibilityService, TestDataGenerationService]
        })
            .compileComponents();
    }));

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
        let mockUploadConfig:  UploadOptions [] = [{
            exampleFileName: 'Users-Accounts-Balances-Payments-Example.yml',
            title: 'Upload Users/Accounts/Balances/Payments',
            method: 'PUT',
            url: url + '/data/upload',
            exampleFileUrl: '/accounts/example'
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
            exampleFileUrl: '/data/example'
        }
        ];
        component.ngOnInit();
        expect(component.uploadDataConfigs).toEqual(mockUploadConfig);
    });

    it('should generate the file Example', () => {
        let mockUploadConfig:  UploadOptions  = {
            exampleFileName: 'Users-Accounts-Balances-Payments-Example.yml',
            title: 'Upload Users/Accounts/Balances/Payments',
            method: 'PUT',
            url: url + '/data/upload',
            exampleFileUrl: '/accounts/example'
        };
        let generateSpy = spyOn(testDataGenerationService, 'generateExampleTestData').and.returnValue(of(mockUploadConfig.exampleFileUrl));
        let infoSpy = spyOn(infoService, 'openFeedback');
        component.generateFileExample(mockUploadConfig);
        expect(infoSpy).toHaveBeenCalledWith('Test data has been successfully generated.');
    });
});
