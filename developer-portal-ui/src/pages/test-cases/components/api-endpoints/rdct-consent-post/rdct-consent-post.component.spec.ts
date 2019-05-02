import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RdctConsentPOSTComponent } from './rdct-consent-post.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: RdctConsentPOSTComponent;
  let fixture: ComponentFixture<RdctConsentPOSTComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RdctConsentPOSTComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RdctConsentPOSTComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
