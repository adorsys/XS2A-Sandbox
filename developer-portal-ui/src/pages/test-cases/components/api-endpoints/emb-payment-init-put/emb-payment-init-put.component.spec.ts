import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbPaymentInitPutComponent } from './emb-payment-init-put.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbPaymentInitPutComponent;
  let fixture: ComponentFixture<EmbPaymentInitPutComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbPaymentInitPutComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbPaymentInitPutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
