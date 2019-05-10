import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbConsentPutComponent } from './emb-consent-put.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbConsentPutComponent;
  let fixture: ComponentFixture<EmbConsentPutComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbConsentPutComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbConsentPutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
