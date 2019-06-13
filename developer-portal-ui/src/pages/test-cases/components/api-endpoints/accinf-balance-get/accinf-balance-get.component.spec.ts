import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccinfBalanceGetComponent } from './accinf-balance-get.component';

describe('AccinfBalanceGetComponent', () => {
  let component: AccinfBalanceGetComponent;
  let fixture: ComponentFixture<AccinfBalanceGetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccinfBalanceGetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccinfBalanceGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
