import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ResultPageComponent } from './result-page.component';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { SettingsService } from '../../common/services/settings.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { AisService } from '../../common/services/ais.service';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';

describe('ResultPageComponent', () => {
  let component: ResultPageComponent;
  let fixture: ComponentFixture<ResultPageComponent>;
  let shareDataService: ShareDataService;
  let settingsService: SettingsService;
  let aisService: AisService;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ResultPageComponent],
      providers: [SettingsService, ShareDataService, AisService],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResultPageComponent);
    component = fixture.componentInstance;
    shareDataService = TestBed.get(ShareDataService);
    settingsService = TestBed.get(SettingsService);
    aisService = TestBed.get(AisService);
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
    const mockConsentAuthorization: ConsentAuthorizeResponse = {
      scaStatus: 'received',
    };
    shareDataService.currentData = of(mockConsentAuthorization);
    component.authResponse = mockResponse;
    component.ngOnInit();
    expect(component.scaStatus).toEqual(mockConsentAuthorization.scaStatus);
    expect(component.authResponse).toBeDefined();
  });
});
