import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ConfirmPaymentComponent} from './confirm-payment.component';
import {PaymentDetailsComponent} from "../payment-details/payment-details.component";
import {RouterTestingModule} from "@angular/router/testing";

describe('ConfirmPaymentComponent', () => {
  let component: ConfirmPaymentComponent;
  let fixture: ComponentFixture<ConfirmPaymentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ConfirmPaymentComponent, PaymentDetailsComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmPaymentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
