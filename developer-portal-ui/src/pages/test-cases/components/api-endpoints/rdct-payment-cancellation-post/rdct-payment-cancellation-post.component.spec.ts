import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RdctPaymentCancellationPostComponent } from './rdct-payment-cancellation-post.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: RdctPaymentCancellationPostComponent;
  let fixture: ComponentFixture<RdctPaymentCancellationPostComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RdctPaymentCancellationPostComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RdctPaymentCancellationPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
