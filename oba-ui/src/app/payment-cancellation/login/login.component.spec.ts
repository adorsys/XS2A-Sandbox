import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { CustomizeService } from '../../common/services/customize.service';
import { PisCancellationService } from '../../common/services/pis-cancellation.service';
import { PisService } from '../../common/services/pis.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { InfoService } from '../../common/info/info.service';
import { InfoModule } from '../../common/info/info.module';
import { LoginComponent } from './login.component';
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
  let customizeService: CustomizeService;
  let pisCancellationService: PisCancellationService;
  let pisService: PisService;
  let infoService: InfoService;
  let router: Router;
  let route: ActivatedRoute;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule, ReactiveFormsModule, InfoModule],
        declarations: [LoginComponent],
        providers: [PisCancellationService, CustomizeService, PisService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    shareDataService = TestBed.get(ShareDataService);
    customizeService = TestBed.get(CustomizeService);
    pisService = TestBed.get(PisService);
    infoService = TestBed.get(InfoService);
    router = TestBed.get(Router);
    route = TestBed.get(ActivatedRoute);
    pisCancellationService = TestBed.get(PisCancellationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the onsubmit', () => {
    const mockResponse = {
      encryptedPaymentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.loginForm.get('login').setValue('foo');
    component.loginForm.get('pin').setValue('12345');
    component.encryptedPaymentId = mockResponse.encryptedPaymentId;
    component.redirectId = mockResponse.authorisationId;
    spyOn(component, 'getPisAuthCode').and.callFake(() => {});
    const pisCancelSpy = spyOn(
      pisCancellationService,
      'pisCancellationLogin'
    ).and.returnValue(of(mockResponse));
    const navigateSpy = spyOn(router, 'navigate');
    component.onSubmit();
    expect(pisCancelSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith([
      `${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.CONFIRM_CANCELLATION}`,
    ]);
  });

  it('should get the PIS AuthCode', () => {
    const mockResponse = {
      encryptedPaymentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    const pisAuthSpy = spyOn(pisService, 'pisAuthCode').and.returnValue(
      of<any>(mockResponse)
    );
    component.encryptedPaymentId = mockResponse.encryptedPaymentId;
    component.redirectId = mockResponse.authorisationId;
    component.getPisAuthCode();
    expect(pisAuthSpy).toHaveBeenCalled();
  });

  it('should call the on submit and return the feedback message whne authorised is undefined', () => {
    const mockResponse = {
      encryptedPaymentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: undefined,
    };
    component.loginForm.get('login').setValue('foo');
    component.loginForm.get('pin').setValue('12345');
    component.encryptedPaymentId = mockResponse.encryptedPaymentId;
    component.redirectId = mockResponse.authorisationId;
    spyOn(component, 'getPisAuthCode').and.callFake(() => {});
    const pisCancelSpy = spyOn(
      pisCancellationService,
      'pisCancellationLogin'
    ).and.returnValue(throwError({}));
    const infoSpy = spyOn(infoService, 'openFeedback');
    component.onSubmit();
    expect(pisCancelSpy).toHaveBeenCalled();
    expect(infoSpy).toHaveBeenCalledWith(
      'Payment data is missing. Please initiate payment cancellation prior to login',
      {
        severity: 'error',
      }
    );
  });

  it('should call the on submit and return the feedback message', () => {
    const mockResponse = {
      encryptedPaymentId: undefined,
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.loginForm.get('login').setValue('foo');
    component.loginForm.get('pin').setValue('12345');
    component.encryptedPaymentId = mockResponse.encryptedPaymentId;
    component.redirectId = mockResponse.authorisationId;
    spyOn(component, 'getPisAuthCode').and.callFake(() => {});
    const pisCancelSpy = spyOn(
      pisCancellationService,
      'pisCancellationLogin'
    ).and.returnValue(throwError({}));
    const infoSpy = spyOn(infoService, 'openFeedback');
    component.onSubmit();
    expect(pisCancelSpy).toHaveBeenCalled();
    expect(infoSpy).toHaveBeenCalledWith(
      'Payment data is missing. Please initiate payment cancellation prior to login',
      {
        severity: 'error',
      }
    );
  });

  it('pis AuthCode should throw error ', () => {
    const errorSpy = spyOn(pisService, 'pisAuthCode').and.returnValue(
      throwError({})
    );
    component.getPisAuthCode();
    expect(errorSpy).toHaveBeenCalled();
  });
});
