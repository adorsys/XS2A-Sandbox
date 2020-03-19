import { TestBed, inject } from '@angular/core/testing';

import { CopyService } from './copy.service';
import {DataService} from './data.service';

describe('CopyService', () => {

  const DataServiceStub = {
    showToast: (val: string) => {}
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CopyService,
        { provide: DataService, useValue: DataServiceStub },
      ]
    });
  });

  it('should be created', inject([CopyService], (service: CopyService) => {
    expect(service).toBeTruthy();
  }));
});
