import { inject, TestBed } from '@angular/core/testing';
import { SettingsService } from './settings.service';

describe('SettingsService', () => {
  const environmentStub = {
    environment: () => {},
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SettingsService],
    });
  });

  it('should be created', inject(
    [SettingsService],
    (service: SettingsService) => {
      expect(service).toBeTruthy();
    }
  ));

  it('should return the local envLink', inject(
    [SettingsService],
    (service: SettingsService) => {
      service.fallbackToDefault();
      expect(service.getEnvLink('DEVELOPER_PORTAL')).toBe(
        'http://localhost:4206'
      );
    }
  ));
});
