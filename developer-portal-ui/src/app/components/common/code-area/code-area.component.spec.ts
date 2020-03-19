import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CodeAreaComponent } from './code-area.component';
import { Pipe, PipeTransform } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import {DataService} from "../../../services/data.service";

describe('CodeAreaComponent', () => {
  let component: CodeAreaComponent;
  let fixture: ComponentFixture<CodeAreaComponent>;

  @Pipe({ name: 'translate' })
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[3];
    }
  }

  @Pipe({ name: 'prettyJson' })
  class PrettyJsonPipe implements PipeTransform {
    transform(value) {
      return JSON.stringify(value, null, 4);
    }
  }

  const ToastrServiceStub = {};

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CodeAreaComponent, TranslatePipe, PrettyJsonPipe],
      providers: [
        DataService,
        { provide: ToastrService, useValue: ToastrServiceStub },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CodeAreaComponent);
    component = fixture.componentInstance;
    component.id = 'Collapse-test';
    component.json = {
      endToEndIdentification: 'WBG-123456789',
      debtorAccount: {
        currency: 'EUR',
        iban: 'YOUR_USER_IBAN',
      },
      instructedAmount: {
        currency: 'EUR',
        amount: '20.00',
      },
      creditorAccount: {
        currency: 'EUR',
        iban: 'DE15500105172295759744',
      },
      creditorAgent: 'AAAADEBBXXX',
      creditorName: 'WBG',
      creditorAddress: {
        buildingNumber: '56',
        city: 'Nürnberg',
        country: 'DE',
        postalCode: '90543',
        street: 'WBG Straße',
      },
      remittanceInformationUnstructured: 'Ref. Number WBG-1222',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should collapse code area', () => {
    let collapsedArea = document.getElementById('Collapse-test').style
      .maxHeight;
    component.collapseThis('Collapse-test');

    expect(collapsedArea).not.toEqual(
      document.getElementById('Collapse-test').style.maxHeight
    );
    expect(component.shown).toBeTruthy();

    collapsedArea = document.getElementById('Collapse-test').style.maxHeight;
    component.collapseThis('Collapse-test');

    expect(collapsedArea).not.toEqual(
      document.getElementById('Collapse-test').style.maxHeight
    );
    expect(component.shown).toBeFalsy();
  });
});
