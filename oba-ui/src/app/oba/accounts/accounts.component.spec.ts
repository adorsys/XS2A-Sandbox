import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AuthService } from '../../common/services/auth.service';
import { AccountsComponent } from './accounts.component';

describe('AccountsComponent', () => {
  let component: AccountsComponent;
  let fixture: ComponentFixture<AccountsComponent>;
  const authServiceSpy = jasmine.createSpyObj('AuthService', ['getAuthorizedUser', 'isLoggedIn', 'logout']);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccountsComponent ],
        imports: [RouterTestingModule, HttpClientTestingModule],
        providers: [TestBed.overrideProvider(AuthService, {useValue: authServiceSpy})]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
