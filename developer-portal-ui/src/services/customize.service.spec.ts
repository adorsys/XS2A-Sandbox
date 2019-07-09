import { TestBed, inject } from '@angular/core/testing';

import { CustomizeService } from './customize.service';
import {HttpClient, HttpHandler} from '@angular/common/http';

describe('CustomizeService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CustomizeService,
        HttpClient,
        HttpHandler
      ]
    });
  });

  it('should be created (not all)', inject([CustomizeService], (service: CustomizeService) => {
    expect(service).toBeTruthy();
  }));
});
