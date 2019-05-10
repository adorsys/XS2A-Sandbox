import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientModule} from "@angular/common/http";
import {FileUploadModule} from "ng2-file-upload";

import {DocumentUploadComponent} from "../commons/document-upload/document-upload.component";
import {IconModule} from "../commons/icon/icon.module";
import {UploadFileComponent} from './uploadFile.component';


describe('UploadFileComponent', () => {
    let component: UploadFileComponent;
    let fixture: ComponentFixture<UploadFileComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                FileUploadModule,
                RouterTestingModule,
                HttpClientModule,
                IconModule
            ],
            declarations: [UploadFileComponent, DocumentUploadComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(UploadFileComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
