import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { ClipboardModule } from 'ngx-clipboard';

import { InfoModule } from '../../common/info/info.module';
import { AuthService } from '../../common/services/auth.service';
import { ConsentsComponent } from './consents.component';

describe('ConsentsComponent', () => {
  let component: ConsentsComponent;
  let fixture: ComponentFixture<ConsentsComponent>;
  const authServiceSpy = jasmine.createSpyObj('AuthService', ['getAuthorizedUser', 'isLoggedIn', 'logout']);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, InfoModule, ClipboardModule],
      declarations: [ConsentsComponent],
      providers: [TestBed.overrideProvider(AuthService, {useValue: authServiceSpy})],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
