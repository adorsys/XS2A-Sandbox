import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ShareDataService } from '../../common/services/share-data.service';
import { PisService } from '../../common/services/pis.service';
import { SettingsService } from '../../common/services/settings.service';
import { ResultPageComponent } from './result-page.component';
import { PaymentAuthorizeResponse } from '../../api/models/payment-authorize-response';
import { of } from 'rxjs';

describe('ResultPageComponent', () => {
  let component: ResultPageComponent;
  let fixture: ComponentFixture<ResultPageComponent>;
  let shareDataService: ShareDataService;
  let pisService: PisService;
  let settingsService: SettingsService;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ResultPageComponent],
      providers: [ShareDataService, SettingsService, PisService],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResultPageComponent);
    component = fixture.componentInstance;
    shareDataService = TestBed.get(ShareDataService);
    settingsService = TestBed.get(SettingsService);
    pisService = TestBed.get(PisService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the ng on init', () => {
    const mockResponse = {
      encryptedConsentId: 'owirhJHGVSgueif98200293uwpgofowbOUIGb39845zt0',
      authorisationId: 'uwpgofowbOUIGb39845zt0owirhJHGVSgueif98200293',
      oauth2: undefined,
    };
    const mockPaymentAuthorization: PaymentAuthorizeResponse = {
      scaStatus: 'received',
    };
    shareDataService.currentData = of(mockPaymentAuthorization);
    component.authResponse = mockResponse;
    component.ngOnInit();
    expect(component.scaStatus).toEqual(mockPaymentAuthorization.scaStatus);
    expect(component.authResponse).toBeDefined();
  });
});
