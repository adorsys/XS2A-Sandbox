import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccinfTransactionGetComponent } from './accinf-transaction-get.component';

describe('AccinfTransactionGetComponent', () => {
  let component: AccinfTransactionGetComponent;
  let fixture: ComponentFixture<AccinfTransactionGetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccinfTransactionGetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccinfTransactionGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
