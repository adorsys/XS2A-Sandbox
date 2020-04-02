import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PopUpComponent } from './pop-up.component';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {CertificateService} from "../../../../../services/certificate.service";
import {DataService} from "../../../../../services/data.service";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {ToastrService} from "ngx-toastr";

describe('PopUpComponent', () => {
  let component: PopUpComponent;
  let fixture: ComponentFixture<PopUpComponent>;
  const ToastrServiceStub = {};

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PopUpComponent],
      imports: [BrowserModule, FormsModule, HttpClientTestingModule],
      providers: [CertificateService, DataService, { provide: ToastrService, useValue: ToastrServiceStub }, NgbModal ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PopUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
