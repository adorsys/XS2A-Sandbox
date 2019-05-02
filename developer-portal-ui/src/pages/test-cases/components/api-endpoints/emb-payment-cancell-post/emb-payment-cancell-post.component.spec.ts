import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbPaymentCancellPostComponent } from './emb-payment-cancell-post.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbPaymentCancellPostComponent;
  let fixture: ComponentFixture<EmbPaymentCancellPostComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbPaymentCancellPostComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbPaymentCancellPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
