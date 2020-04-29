import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { ScaUserDataTO } from '../../api/models/sca-user-data-to';
import { PaymentDetailsComponent } from '../payment-details/payment-details.component';
import { SelectScaComponent } from './select-sca.component';
import { PisService } from '../../common/services/pis.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RoutingPath } from '../../common/models/routing-path.model';
import { of } from 'rxjs';

const mockRouter = {
  navigate: (url: string) => {},
};

const mockActivatedRoute = {
  params: of({ id: '12345' }),
};
describe('SelectScaComponent', () => {
  let component: SelectScaComponent;
  let fixture: ComponentFixture<SelectScaComponent>;
  let pisService: PisService;
  let shareDataService: ShareDataService;
  let router: Router;
  let route: ActivatedRoute;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule],
      declarations: [SelectScaComponent, PaymentDetailsComponent],
      providers: [
        ShareDataService,
        PisService,
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectScaComponent);
    component = fixture.componentInstance;
    pisService = TestBed.get(PisService);
    shareDataService = TestBed.get(ShareDataService);
    router = TestBed.get(Router);
    route = TestBed.get(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the on Submit and go to TAN confirmation', () => {
    const mockResponse = {
      encryptedPaymentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
      scaMethodId: '12345',
    };
    component.selectedScaMethod = {
      scaMethod: 'EMAIL',
    };
    component.authResponse = mockResponse;
    const selectScaSpy = spyOn(pisService, 'selectScaMethod').and.returnValue(
      of(mockResponse)
    );
    const navigateSpy = spyOn(router, 'navigate');
    component.onSubmit();
    expect(selectScaSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith([
      `${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.TAN_CONFIRMATION}`,
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
      [`${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
          authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
        },
      }
    );
  });

  it('should call the on submit with no data and return', () => {
    const mockResponse = {};
    component.authResponse = mockResponse;
    const result = component.onSubmit();
    expect(result).toBeUndefined();
  });

  it('should handle Sca Method', () => {
    const mockScaMethod: ScaUserDataTO = {
      decoupled: false,
      id: '123',
      methodValue: '',
      scaMethod: 'EMAIL',
      staticTan: 'foo@foo.de',
      user: {},
      usesStaticTan: true,
      valid: false,
    };
    component.selectedScaMethod = mockScaMethod;
    component.handleMethodSelectedEvent(mockScaMethod);
    expect(component.selectedScaMethod).toEqual(mockScaMethod);
  });

  it('should throwError when sca Method is no there', () => {
    const mockScaMethod: ScaUserDataTO = {
      decoupled: false,
      id: '123',
      methodValue: '',
      scaMethod: 'EMAIL',
      staticTan: 'foo@foo.de',
      user: {},
      usesStaticTan: true,
      valid: false,
    };
    const result = component.handleMethodSelectedEvent(null);
    expect(result).toBeUndefined();
  });

  it('should call the sca Selected', () => {
    const result = component.isScaSelected();
    expect(result).toBe(true);
  });
});
