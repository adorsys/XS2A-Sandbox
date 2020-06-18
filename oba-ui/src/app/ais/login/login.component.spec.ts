import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AisService } from '../../common/services/ais.service';
import { CustomizeService } from '../../common/services/customize.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { InfoModule } from '../../common/info/info.module';
import { of, throwError } from 'rxjs';
import { InfoService } from '../../common/info/info.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RoutingPath } from '../../common/models/routing-path.model';

const mockRouter = {
  navigate: (url: string) => {},
};

const mockActivatedRoute = {
  params: of({ id: '12345' }),
};
describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let aisService: AisService;
  let infoService: InfoService;
  let customizeService: CustomizeService;
  let shareDataService: ShareDataService;
  let router: Router;
  let route: ActivatedRoute;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, InfoModule],
      declarations: [LoginComponent],
      providers: [AisService, ShareDataService, CustomizeService, InfoService],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    aisService = TestBed.get(AisService);
    infoService = TestBed.get(InfoService);
    router = TestBed.get(Router);
    route = TestBed.get(ActivatedRoute);
    customizeService = TestBed.get(CustomizeService);
    shareDataService = TestBed.get(ShareDataService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the on Submit', () => {
    const mockResponse = {
      encryptedConsentId:
        'uzf7d5PJiuoui78owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      login: 'foo',
      pin: '12345',
    };
    const aisSpy = spyOn(component, 'aisAuthorise');
    component.onSubmit();
    expect(aisSpy).toHaveBeenCalled();
  });

  it('should get the AIS Authorize', () => {
    const loginUsingPOSTParams = {
      encryptedConsentId:
        'uzf7d5PJiuoui78owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      login: 'foo',
      pin: '12345',
    };
    spyOn(component, 'getAisAuthCode').and.callFake(() => {});
    const aisAuthSpy = spyOn(aisService, 'aisAuthorise').and.returnValue(
      of(loginUsingPOSTParams)
    );
    const navigateSpy = spyOn(router, 'navigate');
    component.aisAuthorise(loginUsingPOSTParams);
    expect(aisAuthSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith([
      `${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.GRANT_CONSENT}`,
    ]);
  });

  it('should call the ais Authorize and return the feedback message when encryptedConsentId is undefined', () => {
    const loginUsingPOSTParams = {
      encryptedConsentId: undefined,
      authorisationId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      login: 'foo',
      pin: '12345',
    };
    spyOn(component, 'getAisAuthCode').and.callFake(() => {});
    const aisAuthSpy = spyOn(aisService, 'aisAuthorise').and.returnValue(
      throwError({})
    );
    const infoSpy = spyOn(infoService, 'openFeedback');
    component.aisAuthorise(loginUsingPOSTParams);
    expect(aisAuthSpy).toHaveBeenCalled();
    expect(infoSpy).toHaveBeenCalledWith(
      'Consent data is missing. Please create consent prior to login',
      {
        severity: 'error',
      }
    );
  });

  it('should get the aisAuthCode', () => {
    const mockAuthCodeResponse = {
      encryptedConsentId:
        'uzf7d5PJiuoui78owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      redirectId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      headers: {
        get: (param) => {
          return 'auth_token';
        },
      },
    };
    const codeSpy = spyOn(aisService, 'aisAuthCode').and.returnValue(
      of(mockAuthCodeResponse)
    );
    component.getAisAuthCode();
    expect(codeSpy).toHaveBeenCalled();
  });

  it('pis AuthCode should throw error ', () => {
    const errorSpy = spyOn(aisService, 'aisAuthCode').and.returnValue(
      throwError({})
    );
    component.getAisAuthCode();
    expect(errorSpy).toHaveBeenCalled();
  });
});
