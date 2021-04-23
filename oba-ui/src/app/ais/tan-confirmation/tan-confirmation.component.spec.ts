import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AisService } from '../../common/services/ais.service';
import { CustomizeService } from '../../common/services/customize.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { TanConfirmationComponent } from './tan-confirmation.component';
import { AccountDetailsComponent } from '../account-details/account-details.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RoutingPath } from '../../common/models/routing-path.model';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import get = Reflect.get;
import { ResultPageComponent } from '../result-page/result-page.component';
import { AccountsComponent } from 'src/app/oba/accounts/accounts.component';

const mockRouter = {
  navigate: (url: string) => {},
};

const mockActivatedRoute = {
  params: of({ id: '12345' }),
};

describe('TanConfirmationComponent', () => {
  let component: TanConfirmationComponent;
  let fixture: ComponentFixture<TanConfirmationComponent>;
  let customizeService: CustomizeService;
  let shareDataService: ShareDataService;
  let aisService: AisService;
  let router: Router;
  let route: ActivatedRoute;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule, ReactiveFormsModule],
        declarations: [TanConfirmationComponent, AccountDetailsComponent],
        providers: [
          CustomizeService,
          ShareDataService,
          AisService,
          { provide: Router, useValue: mockRouter },
          { provide: ActivatedRoute, useValue: mockActivatedRoute },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TanConfirmationComponent);
    component = fixture.componentInstance;
    shareDataService = TestBed.inject(ShareDataService);
    customizeService = TestBed.inject(CustomizeService);
    aisService = TestBed.inject(AisService);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the on submit with no data', () => {
    const mockResponse = {};
    component.authResponse = mockResponse;
    const result = component.onSubmit();
    expect(result).toBeUndefined();
  });

  it('should call the on submit', () => {
    const mockResponse = {
      encryptedPaymentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.authResponse = mockResponse;
    component.tanForm.get('authCode').setValue('izsugfHZVblizdwru79348z0');
    const pisAuthSpy = spyOn(aisService, 'authrizedConsent').and.returnValue(
      of(mockResponse)
    );
    const navigateSpy = spyOn(router, 'navigate').and.returnValue(
      of(undefined).toPromise()
    );
    component.onSubmit();
    expect(navigateSpy).toHaveBeenCalledWith(
      [`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: undefined,
          authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
          oauth2: null,
        },
      }
    );
    expect(pisAuthSpy).toHaveBeenCalled();
  });

  it('should call the on submit and return to result page when you set a wrong TAN', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.authResponse = mockResponse;
    component.tanForm
      .get('authCode')
      .setValue('izsugfHZVblizdwru79348z0fHZVblizdwru793');
    component.invalidTanCount = 3;
    const pisAuthSpy = spyOn(aisService, 'authrizedConsent').and.returnValue(
      throwError(mockResponse)
    );
    const error = of(undefined).toPromise();
    const errorSpy = spyOn(error, 'then');
    const navigateSpy = spyOn(router, 'navigate').and.returnValue(error);
    component.onSubmit();
    expect(navigateSpy).toHaveBeenCalledWith(
      [`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
          authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
          oauth2: null,
        },
      }
    );
    expect(errorSpy).toHaveBeenCalled();
    expect(pisAuthSpy).toHaveBeenCalled();
  });

  it('should cancel and redirect to result page', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.authResponse = mockResponse;
    const revokeSpy = spyOn(aisService, 'revokeConsent').and.returnValue(
      of(mockResponse)
    );
    const navigateSpy = spyOn(router, 'navigate').and.returnValue(
      of(undefined).toPromise()
    );
    component.onCancel();
    expect(navigateSpy).toHaveBeenCalledWith(
      [`${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
          authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
        },
      }
    );
  });
});
