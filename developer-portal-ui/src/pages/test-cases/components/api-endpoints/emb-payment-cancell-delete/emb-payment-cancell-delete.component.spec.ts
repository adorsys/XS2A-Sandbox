import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbPaymentCancellDeleteComponent } from './emb-payment-cancell-delete.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbPaymentCancellDeleteComponent;
  let fixture: ComponentFixture<EmbPaymentCancellDeleteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbPaymentCancellDeleteComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbPaymentCancellDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
