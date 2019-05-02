import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbPaymentInitCreatePostComponent } from './emb-payment-init-create-post.component';

describe('RdctPaymentCancellationDeleteComponent', () => {
  let component: EmbPaymentInitCreatePostComponent;
  let fixture: ComponentFixture<EmbPaymentInitCreatePostComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmbPaymentInitCreatePostComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbPaymentInitCreatePostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
