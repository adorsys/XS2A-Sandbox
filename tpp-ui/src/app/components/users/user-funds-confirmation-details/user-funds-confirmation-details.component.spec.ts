import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserFundsConfirmationDetailsComponent } from './user-funds-confirmation-details.component';

describe('UserFundsConfirmationDetailsComponent', () => {
  let component: UserFundsConfirmationDetailsComponent;
  let fixture: ComponentFixture<UserFundsConfirmationDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserFundsConfirmationDetailsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserFundsConfirmationDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
