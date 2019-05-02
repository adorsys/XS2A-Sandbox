import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbPaymentCancellGetComponent } from './emb-payment-cancell-get.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbPaymentCancellGetComponent;
  let fixture: ComponentFixture<EmbPaymentCancellGetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbPaymentCancellGetComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbPaymentCancellGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
