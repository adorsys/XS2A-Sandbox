import { TestBed } from '@angular/core/testing';

import { NavigationService } from './navigation.service';
import {RouterTestingModule} from "@angular/router/testing";

describe('NavigationService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      RouterTestingModule
    ],
  }));

  it('should be created', () => {
    const service: NavigationService = TestBed.get(NavigationService);
    expect(service).toBeTruthy();
  });
});
