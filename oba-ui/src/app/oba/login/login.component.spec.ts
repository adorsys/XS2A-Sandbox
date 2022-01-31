import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { InfoModule } from '../../common/info/info.module';
import { LoginComponent } from './login.component';
import { AuthService } from '../../common/services/auth.service';
import { of } from 'rxjs';
import { RoutingPath } from 'src/app/common/models/routing-path.model';
import { AccountDetailsComponent } from '../accounts/account-details/account-details.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let authServiceStub: Partial<AuthService>;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          MatSnackBarModule,
          RouterTestingModule.withRoutes([
            { path: RoutingPath.LOGIN, component: LoginComponent },
            { path: RoutingPath.ACCOUNTS, component: AccountDetailsComponent },
          ]),
          InfoModule,
        ],
        declarations: [LoginComponent],
        providers: [{ provide: AuthService, useValue: authServiceStub }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    authService = TestBed.inject(AuthService);
    authServiceStub = {
      login: () => of(true),
    };
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
