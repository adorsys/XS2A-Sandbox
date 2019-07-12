import { TestBed, inject } from '@angular/core/testing';

import { DataService } from './data.service';
import {ToastrService} from 'ngx-toastr';

describe('DataService', () => {

  const ToastrServiceStub = {};

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        DataService,
        { provide: ToastrService, useValue: ToastrServiceStub }
      ]
    });
  });

  it('should be created', inject([DataService], (service: DataService) => {
    expect(service).toBeTruthy();
  }));
});
