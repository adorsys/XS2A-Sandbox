import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbPaymentInitAuthPostComponent } from './emb-payment-init-auth-post.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbPaymentInitAuthPostComponent;
  let fixture: ComponentFixture<EmbPaymentInitAuthPostComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbPaymentInitAuthPostComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbPaymentInitAuthPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
