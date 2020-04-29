import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AccountDetailsTO } from '../../api/models/account-details-to';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { RoutingPath } from '../../common/models/routing-path.model';
import { ShareDataService } from '../../common/services/share-data.service';
import { PaymentDetailsComponent } from '../payment-details/payment-details.component';
import { ConfirmCancellationComponent } from './confirm-cancellation.component';
import { ActivatedRoute, Router } from '@angular/router';

import { of, throwError } from 'rxjs';
import get = Reflect.get;

const mockRouter = {
    navigate: (url: string) => {
    }
};

const mockActivatedRoute = {
    params: of({id: '12345'})
};

describe('ConfirmCancellationComponent', () => {
  let component: ConfirmCancellationComponent;
  let fixture: ComponentFixture<ConfirmCancellationComponent>;
    let router: Router;
    let route: ActivatedRoute;
    let shareDataService: ShareDataService;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ConfirmCancellationComponent, PaymentDetailsComponent],
        providers: [ShareDataService,
            {provide: Router, useValue: mockRouter},
            {provide: ActivatedRoute, useValue: mockActivatedRoute}]
    })
      .compileComponents();
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

    it('should confirm the payment and redirect to select sca page ', () => {
        let mockResponse = {
            encryptedConsentId:'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
            authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293'
        }
        component.authResponse = mockResponse;
        let navigateSpy = spyOn(router, 'navigate');
        component.onConfirm();
        expect(navigateSpy).toHaveBeenCalledWith([`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.SELECT_SCA}`]);
    });

    it('should cancel and redirect to result page', () => {
        let mockResponse = {
            encryptedConsentId:'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
            authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293'
        }
        component.authResponse = mockResponse;
        let navigateSpy = spyOn(router, 'navigate');
        component.onCancel();
        expect(navigateSpy).toHaveBeenCalledWith([`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.RESULT}`],
            {queryParams: { encryptedConsentId:'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
                    authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293'}});
    });
});
