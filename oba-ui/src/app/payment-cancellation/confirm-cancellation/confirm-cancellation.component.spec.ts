import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ConfirmCancellationComponent} from './confirm-cancellation.component';
import {PaymentDetailsComponent} from "../payment-details/payment-details.component";
import {RouterTestingModule} from "@angular/router/testing";

describe('ConfirmCancellationComponent', () => {
  let component: ConfirmCancellationComponent;
  let fixture: ComponentFixture<ConfirmCancellationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ConfirmCancellationComponent, PaymentDetailsComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmCancellationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
