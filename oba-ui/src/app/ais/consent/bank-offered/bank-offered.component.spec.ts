import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BankOfferedComponent } from './bank-offered.component';

describe('BankOfferedComponent', () => {
  let component: BankOfferedComponent;
  let fixture: ComponentFixture<BankOfferedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BankOfferedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BankOfferedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
