import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentUploadComponent } from './document-upload.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientModule} from "@angular/common/http";
import {FileUploadModule} from "ng2-file-upload";
import {IconModule} from "../icon/icon.module";

describe('DocumentUploadComponent', () => {
  let component: DocumentUploadComponent;
  let fixture: ComponentFixture<DocumentUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        FileUploadModule,
        RouterTestingModule,
        HttpClientModule,
        IconModule,
      ],
      declarations: [ DocumentUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

});
