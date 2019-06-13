import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccinfTransactionsGetComponent } from './accinf-transactions-get.component';

describe('AccinfTransactionsGetComponent', () => {
  let component: AccinfTransactionsGetComponent;
  let fixture: ComponentFixture<AccinfTransactionsGetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccinfTransactionsGetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccinfTransactionsGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
