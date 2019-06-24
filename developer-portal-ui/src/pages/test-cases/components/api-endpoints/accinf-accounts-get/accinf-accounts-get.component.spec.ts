import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccinfAccountsGetComponent } from './accinf-accounts-get.component';

describe('AccinfAccountsGetComponent', () => {
  let component: AccinfAccountsGetComponent;
  let fixture: ComponentFixture<AccinfAccountsGetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccinfAccountsGetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccinfAccountsGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
