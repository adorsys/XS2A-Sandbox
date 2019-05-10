import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbConsentAuthPostComponent } from './emb-consent-auth-post.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbConsentAuthPostComponent;
  let fixture: ComponentFixture<EmbConsentAuthPostComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbConsentAuthPostComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbConsentAuthPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
