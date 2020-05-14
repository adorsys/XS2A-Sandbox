import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AisService } from '../../common/services/ais.service';
import { CustomizeService } from '../../common/services/customize.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { SelectScaComponent } from './select-sca.component';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { AccountDetailsComponent } from '../account-details/account-details.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ScaUserDataTO } from '../../api/models/sca-user-data-to';
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
  let shareDataService: ShareDataService;
  let customizeService: CustomizeService;
  let aisService: AisService;
  let router: Router;
  let route: ActivatedRoute;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule],
      declarations: [SelectScaComponent, AccountDetailsComponent],
      providers: [
        ShareDataService,
        AisService,
        CustomizeService,
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectScaComponent);
    component = fixture.componentInstance;
    shareDataService = TestBed.get(ShareDataService);
    aisService = TestBed.get(AisService);
    customizeService = TestBed.get(CustomizeService);
    router = TestBed.get(Router);
    route = TestBed.get(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the on submit with no data and return', () => {
    const mockResponse = {};
    component.authResponse = mockResponse;
    const result = component.onSubmit();
    expect(result).toBeUndefined();
  });

  it('should call the on submit and go to TAN page', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
      scaMethodId: '123456',
    };
    component.authResponse = mockResponse;
    component.selectedScaMethod = {
      scaMethod: 'EMAIL',
    };
    const selectSPy = spyOn(aisService, 'selectScaMethod').and.returnValue(
      of(mockResponse)
    );
    const navigateSpy = spyOn(router, 'navigate');
    component.onSubmit();
    expect(selectSPy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith([
      `${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.TAN_CONFIRMATION}`,
    ]);
  });

  it('should cancel and redirect to result page', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
    };
    component.authResponse = mockResponse;
    const aisCancelSpy = spyOn(aisService, 'revokeConsent').and.returnValue(
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
});
