import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { UserProfileUpdateComponent } from './user-profile-update.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { OnlineBankingAccountInformationService } from '../../api/services';
import { OnlineBankingService } from '../../common/services/online-banking.service';

describe('UserProfileEditComponent', () => {
  let component: UserProfileUpdateComponent;
  let fixture: ComponentFixture<UserProfileUpdateComponent>;
  let mockObaService = OnlineBankingService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserProfileUpdateComponent],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [OnlineBankingAccountInformationService],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileUpdateComponent);
    component = fixture.componentInstance;
    mockObaService = TestBed.get(OnlineBankingService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('validate setupUserFormControl method', () => {
    component.setUpEditedUserFormControl();
    expect(component.userForm).toBeDefined();
  });

  it('validate onSubmit method', () => {
    component.onSubmit();
    expect(component.userForm.valid).toBeFalsy();
  });

  it('validate formControl method', () => {
    expect(component.formControl).toEqual(component.userForm.controls);
  });
});
