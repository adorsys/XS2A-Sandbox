import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { RoutingPath } from '../../common/models/routing-path.model';
import { ShareDataService } from '../../common/services/share-data.service';
import { PaymentDetailsComponent } from '../payment-details/payment-details.component';
import { ConfirmCancellationComponent } from './confirm-cancellation.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { of } from 'rxjs';

const mockRouter = {
  navigate: (url: string) => {},
};

const mockActivatedRoute = {
  params: of({ id: '12345' }),
};

describe('ConfirmCancellationComponent', () => {
  let component: ConfirmCancellationComponent;
  let fixture: ComponentFixture<ConfirmCancellationComponent>;
  let router: Router;
  let route: ActivatedRoute;
  let shareDataService: ShareDataService;
  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ConfirmCancellationComponent, PaymentDetailsComponent],
      providers: [
        ShareDataService,
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmCancellationComponent);
    component = fixture.componentInstance;
    router = TestBed.get(Router);
    route = TestBed.get(ActivatedRoute);
    shareDataService = TestBed.get(ShareDataService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get the accounts', () => {
    component.authResponse = {
      accounts: [{ iban: 'DE12 1234 5678 9000 0005' }],
    };
    const result = component.accounts;
    expect(result).toBeTruthy();
  });

  it('should return empty when no auth response for accounts', () => {
    component.authResponse = null;
    const result = component.accounts;
    expect(result).toEqual([]);
  });

  it('should confirm the payment and redirect to select sca page ', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.authResponse = mockResponse;
    const navigateSpy = spyOn(router, 'navigate');
    component.onConfirm();
    expect(navigateSpy).toHaveBeenCalledWith([
      `${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.SELECT_SCA}`,
    ]);
  });

  it('should cancel and redirect to result page', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.authResponse = mockResponse;
    const navigateSpy = spyOn(router, 'navigate');
    component.onCancel();
    expect(navigateSpy).toHaveBeenCalledWith(
      [`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
          authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
        },
      }
    );
  });

  it('should call the ng on init', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
      oauth2: undefined,
    };
    const mockConsentAuthorization: ConsentAuthorizeResponse = {
      scaStatus: 'received',
    };
    shareDataService.currentData = of(mockConsentAuthorization);
    component.authResponse = mockResponse;
    component.ngOnInit();
    expect(component.authResponse).toEqual(mockConsentAuthorization);
    expect(component.authResponse).toBeDefined();
  });
});
