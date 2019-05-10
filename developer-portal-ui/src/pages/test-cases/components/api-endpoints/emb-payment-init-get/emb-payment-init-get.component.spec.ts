import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbPaymentInitGetComponent } from './emb-payment-init-get.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbPaymentInitGetComponent;
  let fixture: ComponentFixture<EmbPaymentInitGetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbPaymentInitGetComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbPaymentInitGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
