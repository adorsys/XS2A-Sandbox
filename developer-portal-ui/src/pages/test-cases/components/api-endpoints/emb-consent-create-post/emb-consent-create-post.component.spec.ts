import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbConsentCreatePostComponent } from './emb-consent-create-post.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbConsentCreatePostComponent;
  let fixture: ComponentFixture<EmbConsentCreatePostComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbConsentCreatePostComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbConsentCreatePostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
