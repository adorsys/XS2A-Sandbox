import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientModule} from "@angular/common/http";
import {FileUploadModule} from "ng2-file-upload";

import {DocumentUploadComponent} from "../commons/document-upload/document-upload.component";
import {IconModule} from "../commons/icon/icon.module";
import {UploadFileComponent} from './uploadFile.component';
import {InfoService} from "../commons/info/info.service";
import {SpinnerVisibilityService} from "ng-http-loader";
import {InfoModule} from "../commons/info/info.module";


describe('UploadFileComponent', () => {
    let component: UploadFileComponent;
    let fixture: ComponentFixture<UploadFileComponent>;
    let infoService: InfoService;
    let spinnerService: SpinnerVisibilityService;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                FileUploadModule,
                RouterTestingModule,
                HttpClientModule,
                InfoModule,
                IconModule
            ],
            declarations: [UploadFileComponent, DocumentUploadComponent],
            providers: [InfoService, SpinnerVisibilityService]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(UploadFileComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        infoService = TestBed.get(InfoService);
        spinnerService = TestBed.get(SpinnerVisibilityService);
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
