import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RdctPaymentCancellationDeleteComponent } from './rdct-payment-cancellation-delete.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: RdctPaymentCancellationDeleteComponent;
  let fixture: ComponentFixture<RdctPaymentCancellationDeleteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RdctPaymentCancellationDeleteComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RdctPaymentCancellationDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
