import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { InfoModule } from '../../common/info/info.module';
import { LoginComponent } from './login.component';
import { AuthService } from '../../common/services/auth.service';
import { of, throwError } from 'rxjs';
import { RoutingPath } from 'src/app/common/models/routing-path.model';
import { AccountDetailsComponent } from '../accounts/account-details/account-details.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          RouterTestingModule.withRoutes([
            { path: RoutingPath.LOGIN, component: LoginComponent },
            { path: RoutingPath.ACCOUNTS, component: AccountDetailsComponent },
          ]),
          InfoModule,
        ],
        declarations: [LoginComponent],
        providers: [AuthService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    authService = TestBed.get(AuthService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /* it('should call the on submit', () => {
    const loginSpy = spyOn(authService, 'login').and.returnValue(of(true));
    component.onSubmit();
    expect(loginSpy).toHaveBeenCalled();
  }); */

  it('should throw error 401', () => {
    const errorSpy = spyOn(authService, 'login').and.returnValue(
      of<any>({ success: false })
    );
    component.onSubmit();
    expect(errorSpy).toHaveBeenCalled();
  });
});
