import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbPaymentCancellationPostComponent } from './emb-payment-cancellation-post.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbPaymentCancellationPostComponent;
  let fixture: ComponentFixture<EmbPaymentCancellationPostComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbPaymentCancellationPostComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbPaymentCancellationPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
