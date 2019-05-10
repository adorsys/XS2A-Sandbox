import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbPaymentCancellPutComponent } from './emb-payment-cancell-put.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbPaymentCancellPutComponent;
  let fixture: ComponentFixture<EmbPaymentCancellPutComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbPaymentCancellPutComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbPaymentCancellPutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
