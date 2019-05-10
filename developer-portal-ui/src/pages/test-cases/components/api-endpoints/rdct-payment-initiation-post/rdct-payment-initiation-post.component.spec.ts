import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RdctPaymentInitiationPostComponent } from './rdct-payment-initiation-post.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: RdctPaymentInitiationPostComponent;
  let fixture: ComponentFixture<RdctPaymentInitiationPostComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RdctPaymentInitiationPostComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RdctPaymentInitiationPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
