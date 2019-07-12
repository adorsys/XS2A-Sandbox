import {inject, TestBed} from '@angular/core/testing';

import {SettingsLoadService} from './settings-load.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SettingsService} from './settings.service';

describe('SettingsLoadService', () => {

  const SettingsServiceStub = {
    fallbackToDefault: () => {}
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ],
      providers: [
        SettingsLoadService,
        { provide: SettingsService, useValue: SettingsServiceStub }
      ]
    });
  });

  it('should be created', inject([SettingsLoadService], (service: SettingsLoadService) => {
    expect(service).toBeTruthy();
  }));
});
