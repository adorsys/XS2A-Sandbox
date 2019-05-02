import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbConsentGetComponent } from './emb-consent-get.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbConsentGetComponent;
  let fixture: ComponentFixture<EmbConsentGetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbConsentGetComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbConsentGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
