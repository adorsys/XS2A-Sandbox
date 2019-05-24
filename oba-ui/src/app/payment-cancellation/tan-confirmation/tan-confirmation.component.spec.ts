import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TanConfirmationComponent} from './tan-confirmation.component';
import {PaymentDetailsComponent} from "../payment-details/payment-details.component";
import {RouterTestingModule} from "@angular/router/testing";
import {ReactiveFormsModule} from "@angular/forms";

describe('TanConfirmationComponent', () => {
  let component: TanConfirmationComponent;
  let fixture: ComponentFixture<TanConfirmationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule],
      declarations: [TanConfirmationComponent, PaymentDetailsComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TanConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
