import { TestBed } from '@angular/core/testing';

import { SettingsLoadService } from './settings-load.service';

describe('SettingsLoadService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SettingsLoadService = TestBed.get(SettingsLoadService);
    expect(service).toBeTruthy();
  });
});
