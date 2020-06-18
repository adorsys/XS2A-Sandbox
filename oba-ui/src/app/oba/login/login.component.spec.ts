import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { InfoModule } from '../../common/info/info.module';
import { LoginComponent } from './login.component';
import { AuthService } from '../../common/services/auth.service';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, InfoModule],
      declarations: [LoginComponent],
      providers: [AuthService],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    authService = TestBed.get(AuthService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the on submit', () => {
    const loginSpy = spyOn(authService, 'login').and.returnValue(
      of({ success: true })
    );
    component.onSubmit();
    expect(loginSpy).toHaveBeenCalled();
  });

  it('should throw error 401', () => {
    const errorSpy = spyOn(authService, 'login').and.returnValue(
      throwError({ success: false })
    );
    component.onSubmit();
    expect(errorSpy).toHaveBeenCalled();
  });
});
