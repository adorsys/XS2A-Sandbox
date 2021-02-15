import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { CustomizeService } from '../../common/services/customize.service';
import { PisService } from '../../common/services/pis.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { InfoModule } from '../../common/info/info.module';
import { LoginComponent } from './login.component';
import { InfoService } from '../../common/info/info.service';
import { of, throwError } from 'rxjs';
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
  let shareDataService: ShareDataService;
  let pisService: PisService;
  let customizeService: CustomizeService;
  let router: Router;
  let route: ActivatedRoute;
  let infoService: InfoService;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule, ReactiveFormsModule, InfoModule],
        declarations: [LoginComponent],
        providers: [
          PisService,
          ShareDataService,
          CustomizeService,
          InfoService,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    pisService = TestBed.get(PisService);
    customizeService = TestBed.get(CustomizeService);
    shareDataService = TestBed.get(ShareDataService);
    infoService = TestBed.get(InfoService);
    router = TestBed.get(Router);
    route = TestBed.get(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the on Submit', () => {
    const mockResponse = {
      encryptedPaymentId:
        'uzf7d5PJiuoui78owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      login: 'foo',
      pin: '12345',
    };
    const pisSpy = spyOn(component, 'pisAuthorise');
    component.onSubmit();
    expect(pisSpy).toHaveBeenCalled();
  });

  it('should get the PIS Authorize', () => {
    const loginUsingPOSTParams = {
      encryptedPaymentId:
        'uzf7d5PJiuoui78owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      login: 'foo',
      pin: '12345',
    };
    spyOn(component, 'getPisAuthCode').and.callFake(() => {});
    const pisAuthSpy = spyOn(pisService, 'pisLogin').and.returnValue(
      of(loginUsingPOSTParams)
    );
    const navigateSpy = spyOn(router, 'navigate');
    component.pisAuthorise(loginUsingPOSTParams);
    expect(pisAuthSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith([
      `${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.CONFIRM_PAYMENT}`,
    ]);
  });

  it('should call the pis Authorize and return the feedback message when encryptedConsentId is undefined', () => {
    const loginUsingPOSTParams = {
      encryptedPaymentId: undefined,
      authorisationId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      login: 'foo',
      pin: '12345',
    };
    spyOn(component, 'getPisAuthCode').and.callFake(() => {});
    const pisAuthSpy = spyOn(pisService, 'pisLogin').and.returnValue(
      throwError({})
    );
    const infoSpy = spyOn(infoService, 'openFeedback');
    component.pisAuthorise(loginUsingPOSTParams);
    expect(pisAuthSpy).toHaveBeenCalled();
    expect(infoSpy).toHaveBeenCalledWith(
      'Payment data is missing. Please initiate payment prior to login',
      {
        severity: 'error',
      }
    );
  });

  it('should get the pisAuthCode', () => {
    const mockAuthCodeResponse = {
      encryptedPaymentId:
        'uzf7d5PJiuoui78owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      redirectId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      headers: {
        get: param => {
          return 'auth_token';
        },
      },
    };
    const codeSpy = spyOn(pisService, 'pisAuthCode').and.returnValue(
      of<any>(mockAuthCodeResponse)
    );
    component.getPisAuthCode();
    expect(codeSpy).toHaveBeenCalled();
  });

  it('pis AuthCode should throw error ', () => {
    const errorSpy = spyOn(pisService, 'pisAuthCode').and.returnValue(
      throwError({})
    );
    component.getPisAuthCode();
    expect(errorSpy).toHaveBeenCalled();
  });
});
